package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.PasswordResetTokenEntity;
import exam_project.artigiani_webapp.entities.UserEntity;
import exam_project.artigiani_webapp.repositories.PasswordResetTokenRepository;
import exam_project.artigiani_webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository               userRepository;
    private final JavaMailSender               mailSender;
    private final PasswordEncoder              passwordEncoder;

    /** URL base del sito (configurabile in application.properties) */
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /** Indirizzo mittente delle email */
    @Value("${spring.mail.username:noreply@artigiani.it}")
    private String fromEmail;

    @Autowired
    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                JavaMailSender mailSender,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository  = userRepository;
        this.mailSender      = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Genera un token di reset e invia l'email.
     * Non rivela se l'email esiste (sicurezza anti-enumeration).
     */
    public void sendResetEmail(String email) {
        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return; // silenzioso — non riveliamo se l'email esiste

        UserEntity user  = optUser.get();
        String token     = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime exp = LocalDateTime.now().plusHours(1);

        tokenRepository.save(new PasswordResetTokenEntity(token, user, exp));

        String resetLink = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(email);
        msg.setSubject("Recupero password — Artigiani Bottega");
        msg.setText(
            "Ciao " + user.getUsername() + ",\n\n" +
            "Hai richiesto il recupero della tua password.\n\n" +
            "Clicca sul link seguente per impostarne una nuova (valido per 1 ora):\n" +
            resetLink + "\n\n" +
            "Se non hai richiesto tu questo reset, ignora questa email — il tuo account è al sicuro.\n\n" +
            "— Il team di Artigiani Bottega"
        );
        mailSender.send(msg);
    }

    /**
     * Valida il token e restituisce l'utente associato se valido.
     */
    public Optional<UserEntity> validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && !t.isExpired())
                .map(PasswordResetTokenEntity::getUser);
    }

    /**
     * Esegue il reset della password e marca il token come usato.
     * Restituisce false se il token è invalido/scaduto.
     */
    public boolean resetPassword(String token, String newRawPassword) {
        Optional<PasswordResetTokenEntity> optToken = tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && !t.isExpired());

        if (optToken.isEmpty()) return false;

        PasswordResetTokenEntity prt  = optToken.get();
        UserEntity user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);
        return true;
    }
}
