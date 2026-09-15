package exam_project.artigiani_webapp.repositories;

import exam_project.artigiani_webapp.entities.ArtisanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtisanRepository extends JpaRepository<ArtisanEntity, Long> {

    List<ArtisanEntity> findAllByOrderByNameAsc();

    List<ArtisanEntity> findByLocationContainingIgnoreCaseOrderByNameAsc(String location);

    /** Tutti gli artigiani di una regione (usato da regionCities) */
    List<ArtisanEntity> findByRegionIgnoreCaseOrderByLocationAsc(String region);

    /** Artigiani di una regione in una specifica città */
    List<ArtisanEntity> findByRegionIgnoreCaseAndLocationIgnoreCaseOrderByNameAsc(
            String region, String location);

    /** Città distinte presenti in una regione (per le card città) */
    @Query("SELECT DISTINCT a.location FROM ArtisanEntity a " +
           "WHERE LOWER(a.region) = LOWER(:region) AND a.location IS NOT NULL " +
           "ORDER BY a.location")
    List<String> findDistinctCitiesByRegion(@Param("region") String region);
}
