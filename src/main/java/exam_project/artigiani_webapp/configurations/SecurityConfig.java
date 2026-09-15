package exam_project.artigiani_webapp.configurations;

import exam_project.artigiani_webapp.entities.UserEntity;
import exam_project.artigiani_webapp.pojos.SecurityUser;
import exam_project.artigiani_webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@ComponentScan("exam_project.artigiani_webapp")
public class SecurityConfig {

    private final UserRepository userRepository;

    @Autowired
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Carica utente dal DB tramite email (usata come principal)
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));
            return new SecurityUser(user);
        };
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security chain
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        // Authentication — il campo del form si chiama "email"
        http.formLogin(c -> c
            .loginPage("/login")
            .usernameParameter("email")   // corrisponde a name="email" nel form
            .defaultSuccessUrl("/", true)
            .failureUrl("/login?loginError")
        );

        // Authorization
        http.authorizeHttpRequests(c -> c
            // Pannello Admin: solo ADMIN
            .requestMatchers("/admin/**").hasRole("ADMIN")
            // Checkout e account: utenti autenticati
            .requestMatchers("/checkout/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/account/**").hasAnyRole("USER", "ADMIN")
            // Tutto il resto è pubblico
            .anyRequest().permitAll()
        );

        // Logout
        http.logout(c -> c
            .logoutUrl("/perform_logout")
            .logoutSuccessUrl("/")
        );

        // TODO: DA ABILITARE??
        // CSRF disabilitato (coerente con main_webapp)
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
