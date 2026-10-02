package exam_project.artigiani_webapp.repositories;

import exam_project.artigiani_webapp.entities.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {

    List<VideoEntity> findAllByOrderByIdDesc();
}
