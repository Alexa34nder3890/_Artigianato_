package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "SHIPPING_ADDRESSES")
public class ShippingAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** Nome e cognome del destinatario */
    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    /** Paese (es. "Italia") */
    @Column(nullable = false, length = 64)
    private String country = "Italia";

    /** Via e numero civico */
    @Column(nullable = false, length = 256)
    private String street;

    /** App / Interno / Altro (facoltativo) */
    @Column(name = "street_extra", length = 256)
    private String streetExtra;

    /** Codice postale */
    @Column(name = "postal_code", nullable = false, length = 16)
    private String postalCode;

    /** Città */
    @Column(nullable = false, length = 128)
    private String city;

    /** Provincia */
    @Column(nullable = false, length = 64)
    private String province;

    /** Indirizzo predefinito per la spedizione */
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    public ShippingAddressEntity() {}

    // Getters
    public Long      getId()          { return id; }
    public UserEntity getUser()       { return user; }
    public String    getFullName()    { return fullName; }
    public String    getCountry()     { return country; }
    public String    getStreet()      { return street; }
    public String    getStreetExtra() { return streetExtra; }
    public String    getPostalCode()  { return postalCode; }
    public String    getCity()        { return city; }
    public String    getProvince()    { return province; }
    public boolean   isDefault()      { return isDefault; }

    // Setters
    public void setId(Long id)                       { this.id = id; }
    public void setUser(UserEntity user)             { this.user = user; }
    public void setFullName(String fullName)         { this.fullName = fullName; }
    public void setCountry(String country)           { this.country = country; }
    public void setStreet(String street)             { this.street = street; }
    public void setStreetExtra(String streetExtra)   { this.streetExtra = streetExtra; }
    public void setPostalCode(String postalCode)     { this.postalCode = postalCode; }
    public void setCity(String city)                 { this.city = city; }
    public void setProvince(String province)         { this.province = province; }
    public void setDefault(boolean isDefault)        { this.isDefault = isDefault; }
}
