package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(nullable = false, unique = true, length = 256)
    private String email;


    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, length = 64)
    private String authority; // es. ROLE_USER, ROLE_ADMIN

    public UserEntity() {}

    public UserEntity(String username, String password, String email, String authority) {
        this.username        = username;
        this.password        = password;
        this.email           = email;
        this.authority       = authority;
    }

    // Getters and Setters
    public Long getId()                          { return id; }
    public String getUsername()                  { return username; }
    public String getPassword()                  { return password; }
    public String getEmail()                     { return email; }
    public boolean isEnabled()                   { return enabled; }
    public String getAuthority()                 { return authority; }

    public void setId(Long id)                           { this.id = id; }
    public void setUsername(String username)             { this.username = username; }
    public void setPassword(String password)             { this.password = password; }
    public void setEmail(String email)                   { this.email = email; }
    public void setEnabled(boolean enabled)              { this.enabled = enabled; }
    public void setAuthority(String authority)           { this.authority = authority; }

}
