package exam_project.artigiani_webapp.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Stub temporaneo: Stripe rimosso per permettere l'avvio dell'applicazione.
 * Da reimplementare quando la dipendenza stripe-java sarà disponibile.
 */
@Service
public class StripeService {

    public String createPaymentIntent(BigDecimal amount) {
        // TODO: reimplementare con Stripe SDK
        throw new UnsupportedOperationException("Stripe non configurato.");
    }

    public Object constructWebhookEvent(String payload, String sigHeader) {
        // TODO: reimplementare con Stripe SDK
        throw new UnsupportedOperationException("Stripe non configurato.");
    }
}
