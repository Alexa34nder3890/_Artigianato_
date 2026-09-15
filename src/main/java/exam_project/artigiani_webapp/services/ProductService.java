package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.ArtisanEntity;
import exam_project.artigiani_webapp.entities.ProductEntity;
import exam_project.artigiani_webapp.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductEntity> findAll() {
        return productRepository.findAllByOrderByNameAsc();
    }

    public List<ProductEntity> findByCategory(String category) {
        return productRepository.findByCategoryOrderByNameAsc(category);
    }

    public List<ProductEntity> findByArtisanLocation(String location) {
        return productRepository.findByArtisanLocationContaining(location);
    }

    public List<ProductEntity> findByArtisanRegion(String region) {
        return productRepository.findByArtisanRegion(region);
    }

    public List<ProductEntity> findByArtisanRegionAndCity(String region, String city) {
        return productRepository.findByArtisanRegionAndCity(region, city);
    }

    public Optional<ProductEntity> findById(Long id) {
        return productRepository.findById(id);
    }

    public ProductEntity save(String name, String description, BigDecimal price,
                              String category, String imageUrl, String model3dUrl,
                              ArtisanEntity artisan) {
        ProductEntity product = new ProductEntity(name, description, price, category, imageUrl, model3dUrl, artisan);
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
