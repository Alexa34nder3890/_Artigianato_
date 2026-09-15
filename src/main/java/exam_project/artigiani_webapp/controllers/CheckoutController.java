package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.entities.CartItemEntity;
import exam_project.artigiani_webapp.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CheckoutController {

    private final CartService cartService;

    @Autowired
    public CheckoutController(CartService cartService) {
        this.cartService = cartService;
    }

    // Pagina checkout: richiede login obbligatorio (gestito da SecurityConfig)
    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<CartItemEntity> items = cartService.getCartItems(username);

        if (items.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal total = items.stream()
            .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);
        // TODO: integrare Stripe per il pagamento reale
        model.addAttribute("stripeDisabled", true);

        return "checkout";
    }

    // Pagina di conferma dopo checkout
    @GetMapping("/checkout/success")
    public String checkoutSuccess(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        cartService.clearCart(authentication.getName());
        return "checkoutSuccess";
    }
}
