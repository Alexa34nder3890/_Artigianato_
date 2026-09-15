package exam_project.artigiani_webapp.repositories;

import exam_project.artigiani_webapp.entities.CartEntity;
import exam_project.artigiani_webapp.entities.CartItemEntity;
import exam_project.artigiani_webapp.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    Optional<CartItemEntity> findByCartAndProduct(CartEntity cart, ProductEntity product);
}
