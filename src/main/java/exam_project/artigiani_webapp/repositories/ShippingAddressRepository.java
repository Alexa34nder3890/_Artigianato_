package exam_project.artigiani_webapp.repositories;

import exam_project.artigiani_webapp.entities.ShippingAddressEntity;
import exam_project.artigiani_webapp.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddressEntity, Long> {

    List<ShippingAddressEntity> findByUser(UserEntity user);

    Optional<ShippingAddressEntity> findByUserAndIsDefaultTrue(UserEntity user);

    Optional<ShippingAddressEntity> findByIdAndUser(Long id, UserEntity user);

    void deleteByUser(UserEntity user);
}
