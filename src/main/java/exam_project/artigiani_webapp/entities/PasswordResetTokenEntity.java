package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PASSWORD_RESET_TOKENS")
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    public PasswordResetTokenEntity() {}

    public PasswordResetTokenEntity(String token, UserEntity user, LocalDateTime expiresAt) {
        this.token     = token;
        this.user      = user;
        this.expiresAt = expiresAt;
    }

    public Long          getId()        { return id; }
    public String        getToken()     { return token; }
    public UserEntity    getUser()      { return user; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean       isUsed()       { return used; }

    public void setUsed(boolean used) { this.used = used; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
