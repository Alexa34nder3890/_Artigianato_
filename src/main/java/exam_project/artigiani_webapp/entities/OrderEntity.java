package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS")
public class OrderEntity {

    public enum Status { PENDING, PAID, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // ID del PaymentIntent Stripe (es. "pi_3OxGbQEhz...")
    @Column(length = 256)
    private String stripePaymentIntentId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderEntity() {}

    public OrderEntity(UserEntity user, String stripePaymentIntentId,
                       BigDecimal totalAmount, Status status) {
        this.user                    = user;
        this.stripePaymentIntentId   = stripePaymentIntentId;
        this.totalAmount             = totalAmount;
        this.status                  = status;
    }

    // Getters and Setters
    public Long getId()                            { return id; }
    public UserEntity getUser()                    { return user; }
    public String getStripePaymentIntentId()       { return stripePaymentIntentId; }
    public BigDecimal getTotalAmount()             { return totalAmount; }
    public Status getStatus()                      { return status; }
    public LocalDateTime getCreatedAt()            { return createdAt; }

    public void setId(Long id)                                     { this.id = id; }
    public void setUser(UserEntity user)                           { this.user = user; }
    public void setStripePaymentIntentId(String s)                 { this.stripePaymentIntentId = s; }
    public void setTotalAmount(BigDecimal totalAmount)             { this.totalAmount = totalAmount; }
    public void setStatus(Status status)                           { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)              { this.createdAt = createdAt; }
}
