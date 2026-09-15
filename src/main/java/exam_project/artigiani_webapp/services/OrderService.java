package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.OrderEntity;
import exam_project.artigiani_webapp.entities.UserEntity;
import exam_project.artigiani_webapp.repositories.OrderRepository;
import exam_project.artigiani_webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository  userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository  = userRepository;
    }

    /**
     * Crea un ordine in stato PENDING.
     * L'ordine diventa PAID solo dopo la ricezione del webhook Stripe.
     */
    @Transactional
    public OrderEntity createPendingOrder(String username, String stripePaymentIntentId, BigDecimal total) {
        UserEntity user = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utente non trovato: " + username));
        OrderEntity order = new OrderEntity(user, stripePaymentIntentId, total, OrderEntity.Status.PENDING);
        return orderRepository.save(order);
    }

    /**
     * Conferma l'ordine come PAID dopo la ricezione dell'evento
     * "payment_intent.succeeded" dal webhook Stripe.
     */
    @Transactional
    public void confirmOrder(String stripePaymentIntentId) {
        Optional<OrderEntity> optOrder = orderRepository.findByStripePaymentIntentId(stripePaymentIntentId);
        optOrder.ifPresent(order -> {
            order.setStatus(OrderEntity.Status.PAID);
            orderRepository.save(order);
        });
    }

    /**
     * Marca l'ordine come FAILED in caso di pagamento fallito.
     */
    @Transactional
    public void failOrder(String stripePaymentIntentId) {
        orderRepository.findByStripePaymentIntentId(stripePaymentIntentId).ifPresent(order -> {
            order.setStatus(OrderEntity.Status.FAILED);
            orderRepository.save(order);
        });
    }
}
