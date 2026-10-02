package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.ArtisanEntity;
import exam_project.artigiani_webapp.repositories.ArtisanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtisanService {

    private final ArtisanRepository artisanRepository;

    @Autowired
    public ArtisanService(ArtisanRepository artisanRepository) {
        this.artisanRepository = artisanRepository;
    }

    public List<ArtisanEntity> findAll() {
        return artisanRepository.findAllByOrderByNameAsc();
    }

    public List<ArtisanEntity> findByLocation(String location) {
        return artisanRepository.findByLocationContainingIgnoreCaseOrderByNameAsc(location);
    }

    public List<ArtisanEntity> findByRegion(String region) {
        return artisanRepository.findByRegionIgnoreCaseOrderByLocationAsc(region);
    }

    public List<String> findCitiesByRegion(String region) {
        return artisanRepository.findDistinctCitiesByRegion(region);
    }

    public Optional<ArtisanEntity> findById(Long id) {
        return artisanRepository.findById(id);
    }

    public ArtisanEntity save(ArtisanEntity artisan) {
        return artisanRepository.save(artisan);
    }

    public void deleteById(Long id) {
        artisanRepository.deleteById(id);
    }
}
