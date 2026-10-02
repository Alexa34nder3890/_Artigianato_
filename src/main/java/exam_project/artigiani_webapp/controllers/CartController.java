package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.entities.CartItemEntity;
import exam_project.artigiani_webapp.services.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Visualizza il carrello
    @GetMapping("/cart")
    public String viewCart(Authentication authentication, HttpSession session, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Utente loggato: carica carrello dal DB
            List<CartItemEntity> items = cartService.getCartItems(authentication.getName());
            // Calcola il totale qui (le lambda Java non sono supportate in Thymeleaf/SpEL)
            BigDecimal total = items.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("cartItems", items);
            model.addAttribute("total", total);
            model.addAttribute("isGuest", false);
        } else {
            // Ospite: carica carrello dalla sessione
            Map<Long, Integer> guestCart = cartService.getGuestCart(session);
            model.addAttribute("guestCart", guestCart);
            model.addAttribute("isGuest", true);
        }
        return "cart";
    }

    // Aggiungi prodotto al carrello
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Authentication authentication,
                            HttpSession session) {
        if (authentication != null && authentication.isAuthenticated()) {
            cartService.addToCart(authentication.getName(), productId, quantity);
        } else {
            cartService.addToGuestCart(session, productId, quantity);
        }
        return "redirect:/cart";
    }

    // Rimuovi prodotto dal carrello
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 Authentication authentication,
                                 HttpSession session) {
        if (authentication != null && authentication.isAuthenticated()) {
            cartService.removeFromCart(authentication.getName(), productId);
        } else {
            cartService.removeFromGuestCart(session, productId);
        }
        return "redirect:/cart";
    }
}
