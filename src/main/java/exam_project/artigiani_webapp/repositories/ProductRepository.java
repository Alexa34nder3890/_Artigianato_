package exam_project.artigiani_webapp.repositories;

import exam_project.artigiani_webapp.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategory(String category);

    List<ProductEntity> findAllByOrderByNameAsc();

    List<ProductEntity> findByCategoryOrderByNameAsc(String category);

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.artisan.location) LIKE LOWER(CONCAT('%', :location, '%')) ORDER BY p.name ASC")
    List<ProductEntity> findByArtisanLocationContaining(@Param("location") String location);

    /** Prodotti di una regione intera */
    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.artisan.region) = LOWER(:region) ORDER BY p.name ASC")
    List<ProductEntity> findByArtisanRegion(@Param("region") String region);

    /** Prodotti di una specifica città all'interno di una regione */
    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.artisan.region) = LOWER(:region) " +
           "AND LOWER(p.artisan.location) = LOWER(:city) ORDER BY p.name ASC")
    List<ProductEntity> findByArtisanRegionAndCity(@Param("region") String region,
                                                    @Param("city") String city);
}
