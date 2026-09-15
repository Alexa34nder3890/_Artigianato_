package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCTS")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 2048)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 64)
    private String category;

    // URL/percorso immagine anteprima salvata su file system
    @Column(length = 512)
    private String imageUrl;

    // URL/percorso modello 3D (.glb/.gltf) salvato su file system
    @Column(length = 512)
    private String model3dUrl;

    // Relazione Many-to-One: molti prodotti appartengono a un artigiano
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id", nullable = false)
    private ArtisanEntity artisan;

    public ProductEntity() {}

    public ProductEntity(String name, String description, BigDecimal price,
                         String category, String imageUrl, String model3dUrl,
                         ArtisanEntity artisan) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.category    = category;
        this.imageUrl    = imageUrl;
        this.model3dUrl  = model3dUrl;
        this.artisan     = artisan;
    }

    // Getters and Setters
    public Long getId()                  { return id; }
    public String getName()              { return name; }
    public String getDescription()       { return description; }
    public BigDecimal getPrice()         { return price; }
    public String getCategory()          { return category; }
    public String getImageUrl()          { return imageUrl; }
    public String getModel3dUrl()        { return model3dUrl; }
    public ArtisanEntity getArtisan()    { return artisan; }

    public void setId(Long id)                     { this.id = id; }
    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price)         { this.price = price; }
    public void setCategory(String category)       { this.category = category; }
    public void setImageUrl(String imageUrl)       { this.imageUrl = imageUrl; }
    public void setModel3dUrl(String model3dUrl)   { this.model3dUrl = model3dUrl; }
    public void setArtisan(ArtisanEntity artisan)  { this.artisan = artisan; }
}
