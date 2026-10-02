package exam_project.artigiani_webapp.repositories;

import exam_project.artigiani_webapp.entities.CartEntity;
import exam_project.artigiani_webapp.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findByUser(UserEntity user);
}
