package exam_project.artigiani_webapp.repositories;

import exam_project.artigiani_webapp.entities.OrderEntity;
import exam_project.artigiani_webapp.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    Optional<OrderEntity> findByStripePaymentIntentId(String stripePaymentIntentId);
}
