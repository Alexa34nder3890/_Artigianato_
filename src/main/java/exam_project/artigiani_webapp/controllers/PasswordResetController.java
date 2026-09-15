package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // ── Mostra form "hai dimenticato la password?" ──
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgotPassword";
    }

    // ── Riceve l'email e invia il link ──
    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@RequestParam String email, Model model) {
        passwordResetService.sendResetEmail(email);
        // Messaggio generico per non rivelare se l'email è registrata
        model.addAttribute("sent", true);
        return "forgotPassword";
    }

    // ── Mostra form di reset (con token dall'URL) ──
    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        boolean valid = passwordResetService.validateToken(token).isPresent();
        if (!valid) {
            model.addAttribute("error", "Il link di recupero non è valido o è scaduto. Richiedine uno nuovo.");
            return "resetPassword";
        }
        model.addAttribute("token", token);
        return "resetPassword";
    }

    // ── Esegue il reset ──
    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token,
                                      @RequestParam String newPassword,
                                      @RequestParam String confirmPassword,
                                      Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Le password non coincidono.");
            return "resetPassword";
        }
        if (newPassword.length() < 8) {
            model.addAttribute("token", token);
            model.addAttribute("error", "La password deve essere di almeno 8 caratteri.");
            return "resetPassword";
        }

        boolean ok = passwordResetService.resetPassword(token, newPassword);
        if (!ok) {
            model.addAttribute("error", "Il link di recupero non è valido o è scaduto. Richiedine uno nuovo.");
            return "resetPassword";
        }

        model.addAttribute("success", true);
        return "resetPassword";
    }
}
