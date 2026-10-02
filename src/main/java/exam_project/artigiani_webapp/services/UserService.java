package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.UserEntity;
import exam_project.artigiani_webapp.repositories.CartRepository;
import exam_project.artigiani_webapp.repositories.OrderRepository;
import exam_project.artigiani_webapp.repositories.PasswordResetTokenRepository;
import exam_project.artigiani_webapp.repositories.ShippingAddressRepository;
import exam_project.artigiani_webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository               userRepository;
    private final PasswordEncoder              passwordEncoder;
    private final CartRepository               cartRepository;
    private final OrderRepository              orderRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final ShippingAddressRepository    addressRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CartRepository cartRepository,
                       OrderRepository orderRepository,
                       PasswordResetTokenRepository tokenRepository,
                       ShippingAddressRepository addressRepository) {
        this.userRepository    = userRepository;
        this.passwordEncoder   = passwordEncoder;
        this.cartRepository    = cartRepository;
        this.orderRepository   = orderRepository;
        this.tokenRepository   = tokenRepository;
        this.addressRepository = addressRepository;
    }

    public void registerUser(String username, String email, String rawPassword) {
        UserEntity user = new UserEntity(username, passwordEncoder.encode(rawPassword), email, "ROLE_USER");
        userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Ricerca per email (authentication.getName() restituisce l'email)
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Mantenuto per compatibilità
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Aggiorna username
    public boolean updateUsername(String email, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) return false;
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setUsername(newUsername);
            userRepository.save(user);
        });
        return true;
    }

    public void updateEmail(String email, String newEmail) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setEmail(newEmail);
            userRepository.save(user);
        });
    }

    public boolean changePassword(String email, String oldRawPassword, String newRawPassword) {
        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return false;

        UserEntity user = optUser.get();
        if (!passwordEncoder.matches(oldRawPassword, user.getPassword())) return false;

        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * Elimina definitivamente l'account (diritto all'oblio).
     * Sequenza: verifica password → token reset → indirizzi → ordini → cart → utente.
     *
     * @return true se eliminato, false se la password è errata
     */
    @Transactional
    public boolean deleteAccount(String email, String rawPassword) {
        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return false;

        UserEntity user = optUser.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) return false;

        // 1. Token reset password
        tokenRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .forEach(tokenRepository::delete);

        // 2. Indirizzi di spedizione
        addressRepository.deleteByUser(user);

        // 3. Ordini (cancellazione cascata)
        orderRepository.findByUserOrderByCreatedAtDesc(user)
                .forEach(orderRepository::delete);

        // 4. Carrello (con suoi CartItem grazie a CascadeType.ALL)
        cartRepository.findByUser(user).ifPresent(cartRepository::delete);

        // 5. Utente
        userRepository.delete(user);
        return true;
    }
}

