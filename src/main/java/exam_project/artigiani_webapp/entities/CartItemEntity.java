package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "CART_ITEMS")
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private int quantity;

    public CartItemEntity() {}

    public CartItemEntity(CartEntity cart, ProductEntity product, int quantity) {
        this.cart     = cart;
        this.product  = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId()               { return id; }
    public CartEntity getCart()       { return cart; }
    public ProductEntity getProduct() { return product; }
    public int getQuantity()          { return quantity; }

    public void setId(Long id)                    { this.id = id; }
    public void setCart(CartEntity cart)           { this.cart = cart; }
    public void setProduct(ProductEntity product)  { this.product = product; }
    public void setQuantity(int quantity)          { this.quantity = quantity; }
}
