package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.*;
import exam_project.artigiani_webapp.repositories.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CartService {

    // Chiave usata nella sessione HTTP per il carrello ospite
    private static final String SESSION_CART_KEY = "guest_cart";

    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository  productRepository;
    private final UserRepository     userRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository     = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository  = productRepository;
        this.userRepository     = userRepository;
    }

    // -------------------------------------------------------
    // CARRELLO OSPITE (sessione HTTP)
    // -------------------------------------------------------

    /** Ritorna la mappa prodotto→quantità dalla sessione ospite */
    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getGuestCart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute(SESSION_CART_KEY, cart);
        }
        return cart;
    }

    /** Aggiunge un prodotto al carrello ospite in sessione */
    public void addToGuestCart(HttpSession session, Long productId, int quantity) {
        Map<Long, Integer> cart = getGuestCart(session);
        cart.merge(productId, quantity, Integer::sum);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    /** Rimuove un prodotto dal carrello ospite */
    public void removeFromGuestCart(HttpSession session, Long productId) {
        Map<Long, Integer> cart = getGuestCart(session);
        cart.remove(productId);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    // -------------------------------------------------------
    // CARRELLO UTENTE LOGGATO (database)
    // -------------------------------------------------------

    /** Recupera o crea il carrello persistente dell'utente */
    @Transactional
    public CartEntity getOrCreateCart(String username) {
        UserEntity user = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utente non trovato: " + username));
        return cartRepository.findByUser(user)
            .orElseGet(() -> cartRepository.save(new CartEntity(user)));
    }

    /** Aggiunge o incrementa un prodotto nel carrello DB */
    @Transactional
    public void addToCart(String username, Long productId, int quantity) {
        CartEntity    cart    = getOrCreateCart(username);
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Prodotto non trovato: " + productId));

        Optional<CartItemEntity> existing = cartItemRepository.findByCartAndProduct(cart, product);
        if (existing.isPresent()) {
            CartItemEntity item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            cartItemRepository.save(new CartItemEntity(cart, product, quantity));
        }
    }

    /** Rimuove un prodotto dal carrello DB */
    @Transactional
    public void removeFromCart(String username, Long productId) {
        CartEntity    cart    = getOrCreateCart(username);
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Prodotto non trovato: " + productId));
        cartItemRepository.findByCartAndProduct(cart, product)
            .ifPresent(cartItemRepository::delete);
    }

    /** Svuota il carrello DB */
    @Transactional
    public void clearCart(String username) {
        CartEntity cart = getOrCreateCart(username);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Trasferisce il carrello sessione ospite al carrello DB dell'utente loggato.
     * Chiamato dopo il login con successo.
     */
    @Transactional
    public void mergeGuestCartIntoUserCart(HttpSession session, String username) {
        Map<Long, Integer> guestCart = getGuestCart(session);
        if (guestCart.isEmpty()) return;

        for (Map.Entry<Long, Integer> entry : guestCart.entrySet()) {
            addToCart(username, entry.getKey(), entry.getValue());
        }
        session.removeAttribute(SESSION_CART_KEY);
    }

    /** Ritorna gli item del carrello DB per la vista */
    @Transactional
    public List<CartItemEntity> getCartItems(String username) {
        CartEntity cart = getOrCreateCart(username);
        return cart.getItems();
    }
}
