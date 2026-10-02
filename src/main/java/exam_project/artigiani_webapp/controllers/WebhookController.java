package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Stub temporaneo: webhook Stripe rimosso.
 * Risponde sempre 503 se invocato.
 */
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    public WebhookController(OrderService orderService) {
        // OrderService mantenuto per compatibilità futura
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Stripe non configurato.");
    }
}
