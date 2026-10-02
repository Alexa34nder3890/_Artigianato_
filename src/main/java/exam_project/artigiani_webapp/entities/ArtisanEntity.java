package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ARTISANS")
public class ArtisanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 1024)
    private String biography;

    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    @Column(length = 256)
    private String location;  // città

    @Column(length = 128)
    private String region;    // regione (una delle 20 italiane)

    // Relazione One-to-Many: un artigiano ha molti prodotti
    @OneToMany(mappedBy = "artisan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    public ArtisanEntity() {}

    public ArtisanEntity(String name, String biography, String photoUrl, String location) {
        this.name      = name;
        this.biography = biography;
        this.photoUrl  = photoUrl;
        this.location  = location;
    }

    public ArtisanEntity(String name, String biography, String photoUrl, String location, String region) {
        this.name      = name;
        this.biography = biography;
        this.photoUrl  = photoUrl;
        this.location  = location;
        this.region    = region;
    }

    // Getters and Setters
    public Long getId()                            { return id; }
    public String getName()                        { return name; }
    public String getBiography()                   { return biography; }
    public String getPhotoUrl()                    { return photoUrl; }
    public String getLocation()                    { return location; }
    public String getRegion()                      { return region; }
    public List<ProductEntity> getProducts()       { return products; }

    public void setId(Long id)                             { this.id = id; }
    public void setName(String name)                       { this.name = name; }
    public void setBiography(String biography)             { this.biography = biography; }
    public void setPhotoUrl(String photoUrl)               { this.photoUrl = photoUrl; }
    public void setLocation(String location)               { this.location = location; }
    public void setRegion(String region)                   { this.region = region; }
    public void setProducts(List<ProductEntity> products)  { this.products = products; }
}
