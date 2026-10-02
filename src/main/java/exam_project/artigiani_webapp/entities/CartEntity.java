package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CARTS")
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Null se il carrello appartiene a un ospite (gestito in sessione)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items = new ArrayList<>();

    public CartEntity() {}

    public CartEntity(UserEntity user) {
        this.user = user;
    }

    // Getters and Setters
    public Long getId()                        { return id; }
    public UserEntity getUser()                { return user; }
    public List<CartItemEntity> getItems()     { return items; }

    public void setId(Long id)                         { this.id = id; }
    public void setUser(UserEntity user)               { this.user = user; }
    public void setItems(List<CartItemEntity> items)   { this.items = items; }
}
