package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.services.ArtisanService;
import exam_project.artigiani_webapp.services.CartService;
import exam_project.artigiani_webapp.services.UserService;
import exam_project.artigiani_webapp.services.VideoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private final UserService    userService;
    private final CartService    cartService;
    private final ArtisanService artisanService;
    private final VideoService   videoService;

    @Autowired
    public MainController(UserService userService, CartService cartService,
                          ArtisanService artisanService, VideoService videoService) {
        this.userService    = userService;
        this.cartService    = cartService;
        this.artisanService = artisanService;
        this.videoService   = videoService;
    }

    // Homepage
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("videos", videoService.findAll());
        return "index";
    }

    // Pagina pubblica artigiani
    @GetMapping("/artisans")
    public String artisans(Model model) {
        model.addAttribute("artisans", artisanService.findAll());
        return "artisans";
    }

    // Pagina contatti
    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

    // Informativa cookie
    @GetMapping("/cookie-policy")
    public String cookiePolicy() {
        return "cookiePolicy";
    }

    // Informativa privacy
    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

    // Termini d'uso
    @GetMapping("/termini-duso")
    public String terminiDuso() {
        return "terminiDuso";
    }

    // Login Page
    @GetMapping("/login")
    public String login(@RequestParam(value = "loginError", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenziali non valide. Riprova.");
        }
        return "login";
    }

    // Signup Page
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // Registrazione nuovo utente
    @PostMapping("/addUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          HttpSession session,
                          Model model) {

        if (userService.usernameExists(username)) {
            model.addAttribute("error", "Username '" + username + "' già in uso. Sceglierne un altro.");
            return "signup";
        }

        if (userService.emailExists(email)) {
            model.addAttribute("error", "Email '" + email + "' già registrata.");
            return "signup";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Le password non coincidono.");
            return "signup";
        }

        if (password.length() < 8) {
            model.addAttribute("error", "La password deve essere di almeno 8 caratteri.");
            return "signup";
        }

        userService.registerUser(username, email, password);

        // Trasferisci carrello ospite al nuovo account
        cartService.mergeGuestCartIntoUserCart(session, username);

        model.addAttribute("username", username);
        return "redirect:/login";
    }

    // Dopo il login, Spring Security reindirizzerà automaticamente.
    // Questo endpoint gestisce il merge carrello ospite -> utente loggato.
    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication, HttpSession session) {
        if (authentication != null) {
            cartService.mergeGuestCartIntoUserCart(session, authentication.getName());
        }
        return "redirect:/";
    }
}
