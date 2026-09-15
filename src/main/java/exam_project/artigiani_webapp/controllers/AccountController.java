package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.services.ShippingAddressService;
import exam_project.artigiani_webapp.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AccountController {

    private final UserService            userService;
    private final ShippingAddressService addressService;

    @Autowired
    public AccountController(UserService userService,
                             ShippingAddressService addressService) {
        this.userService    = userService;
        this.addressService = addressService;
    }

    // ----------------------------------------------------------------
    // PAGINA PRINCIPALE ACCOUNT
    // ----------------------------------------------------------------

    @GetMapping("/account")
    public String account(Authentication authentication, Model model) {
        String email = authentication.getName();
        userService.findByEmail(email).ifPresent(u -> model.addAttribute("user", u));
        model.addAttribute("addresses", addressService.findAllByEmail(email));
        return "account";
    }

    // ----------------------------------------------------------------
    // MODIFICA USERNAME
    // ----------------------------------------------------------------

    @PostMapping("/account/update-username")
    public String updateUsername(Authentication authentication,
                                 @RequestParam String newUsername,
                                 RedirectAttributes ra) {
        if (newUsername == null || newUsername.isBlank()) {
            ra.addFlashAttribute("error", "Il nuovo username non può essere vuoto.");
            return "redirect:/account";
        }
        boolean ok = userService.updateUsername(authentication.getName(), newUsername.trim());
        if (!ok) {
            ra.addFlashAttribute("error", "Username '" + newUsername.trim() + "' già in uso.");
        } else {
            ra.addFlashAttribute("success", "Username aggiornato con successo.");
        }
        return "redirect:/account";
    }

    // ----------------------------------------------------------------
    // MODIFICA EMAIL
    // ----------------------------------------------------------------

    @PostMapping("/account/update-email")
    public String updateEmail(Authentication authentication,
                              @RequestParam String newEmail,
                              RedirectAttributes ra) {
        userService.updateEmail(authentication.getName(), newEmail);
        ra.addFlashAttribute("success", "Email aggiornata con successo.");
        return "redirect:/account";
    }

    // ----------------------------------------------------------------
    // CAMBIO PASSWORD
    // ----------------------------------------------------------------

    @PostMapping("/account/change-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Le nuove password non coincidono.");
            return "redirect:/account";
        }
        boolean changed = userService.changePassword(authentication.getName(), oldPassword, newPassword);
        if (!changed) {
            ra.addFlashAttribute("error", "La password attuale non è corretta.");
        } else {
            ra.addFlashAttribute("success", "Password cambiata con successo.");
        }
        return "redirect:/account";
    }

    // ----------------------------------------------------------------
    // INDIRIZZI – AGGIUNTA
    // ----------------------------------------------------------------

    @PostMapping("/account/addresses/add")
    public String addAddress(Authentication authentication,
                             @RequestParam String fullName,
                             @RequestParam(defaultValue = "Italia") String country,
                             @RequestParam String street,
                             @RequestParam(required = false) String streetExtra,
                             @RequestParam String postalCode,
                             @RequestParam String city,
                             @RequestParam String province,
                             @RequestParam(defaultValue = "false") boolean setAsDefault,
                             RedirectAttributes ra) {
        addressService.addAddress(authentication.getName(),
                fullName, country, street, streetExtra, postalCode, city, province, setAsDefault);
        ra.addFlashAttribute("success", "Indirizzo aggiunto con successo.");
        return "redirect:/account";
    }

    // ----------------------------------------------------------------
    // INDIRIZZI – MODIFICA
    // ----------------------------------------------------------------

    @PostMapping("/account/addresses/{id}/update")
    public String updateAddress(Authentication authentication,
                                @PathVariable Long id,
                                @RequestParam String fullName,
                                @RequestParam(defaultValue = "Italia") String country,
                                @RequestParam String street,
                                @RequestParam(required = false) String streetExtra,
                                @RequestParam String postalCode,
                                @RequestParam String city,
                                @RequestParam String province,
                                @RequestParam(defaultValue = "false") boolean setAsDefault,
                                RedirectAttributes ra) {
        boolean ok = addressService.updateAddress(authentication.getName(), id,
                fullName, country, street, streetExtra, postalCode, city, province, setAsDefault);
        if (ok) {
            ra.addFlashAttribute("success", "Indirizzo aggiornato con successo.");
        } else {
            ra.addFlashAttribute("error", "Indirizzo non trovato.");
        }
        return "redirect:/account";
    }

    // ----------------------------------------------------------------
    // INDIRIZZI – IMPOSTA PREDEFINITO
    // ----------------------------------------------------------------

    @PostMapping("/account/addresses/{id}/set-default")
    public String setDefaultAddress(Authentication authentication,
                                    @PathVariable Long id,
                                    RedirectAttributes ra) {
        addressService.setDefault(authentication.getName(), id);
        ra.addFlashAttribute("success", "Indirizzo predefinito aggiornato.");
        return "redirect:/account";
    }

    // ----------------------------------------------------------------
    // INDIRIZZI – ELIMINAZIONE
    // ----------------------------------------------------------------

    @PostMapping("/account/addresses/{id}/delete")
    public String deleteAddress(Authentication authentication,
                                @PathVariable Long id,
                                RedirectAttributes ra) {
        addressService.deleteAddress(authentication.getName(), id);
        ra.addFlashAttribute("success", "Indirizzo eliminato.");
        return "redirect:/account";
    }

    // ----------------------------------------------------------------
    // ELIMINAZIONE ACCOUNT (diritto all'oblio)
    // ----------------------------------------------------------------

    @PostMapping("/account/delete-account")
    public String deleteAccount(Authentication authentication,
                                @RequestParam String confirmPassword,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes ra) {
        boolean deleted = userService.deleteAccount(authentication.getName(), confirmPassword);
        if (!deleted) {
            ra.addFlashAttribute("error", "Password non corretta. Account non eliminato.");
            return "redirect:/account";
        }

        // Logout immediato dopo l'eliminazione
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/?accountDeleted=true";
    }
}
