package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.entities.ArtisanEntity;
import exam_project.artigiani_webapp.entities.VideoEntity;
import exam_project.artigiani_webapp.services.ArtisanService;
import exam_project.artigiani_webapp.services.FileStorageService;
import exam_project.artigiani_webapp.services.ProductService;
import exam_project.artigiani_webapp.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /** Le 20 regioni italiane in ordine alfabetico */
    private static final List<String> REGIONI_ITALIANE = List.of(
            "Abruzzo", "Basilicata", "Calabria", "Campania", "Emilia-Romagna",
            "Friuli-Venezia Giulia", "Lazio", "Liguria", "Lombardia", "Marche",
            "Molise", "Piemonte", "Puglia", "Sardegna", "Sicilia",
            "Toscana", "Trentino-Alto Adige", "Umbria", "Valle d'Aosta", "Veneto"
    );

    private final ProductService     productService;
    private final ArtisanService     artisanService;
    private final FileStorageService fileStorageService;
    private final VideoService       videoService;

    @Autowired
    public AdminController(ProductService productService,
                           ArtisanService artisanService,
                           FileStorageService fileStorageService,
                           VideoService videoService) {
        this.productService     = productService;
        this.artisanService     = artisanService;
        this.fileStorageService = fileStorageService;
        this.videoService       = videoService;
    }

    // ── Dashboard admin ──
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("artisans", artisanService.findAll());
        model.addAttribute("videos", videoService.findAll());
        return "admin/adminDashboard";
    }

    // ── Form inserimento nuovo prodotto ──
    @GetMapping("/product/new")
    public String newProductForm(Model model) {
        model.addAttribute("artisans", artisanService.findAll());
        return "admin/newProduct";
    }

    // ── Salva nuovo prodotto ──
    @PostMapping("/product/save")
    public String saveProduct(@RequestParam String name,
                              @RequestParam String description,
                              @RequestParam BigDecimal price,
                              @RequestParam String category,
                              @RequestParam Long artisanId,
                              @RequestParam(required = false) MultipartFile imageFile,
                              @RequestParam(required = false) MultipartFile model3dFile,
                              Model model) {

        Optional<ArtisanEntity> optArtisan = artisanService.findById(artisanId);
        if (optArtisan.isEmpty()) {
            model.addAttribute("error", "Artigiano non trovato.");
            model.addAttribute("artisans", artisanService.findAll());
            return "admin/newProduct";
        }

        try {
            String imageUrl   = fileStorageService.saveFile(imageFile);
            String model3dUrl = fileStorageService.saveFile(model3dFile);
            productService.save(name, description, price, category, imageUrl, model3dUrl, optArtisan.get());
            return "redirect:/admin";
        } catch (IOException e) {
            model.addAttribute("error", "Errore durante il caricamento del file: " + e.getMessage());
            model.addAttribute("artisans", artisanService.findAll());
            return "admin/newProduct";
        }
    }

    // ── Elimina prodotto ──
    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/admin";
    }

    // ── Form inserimento nuovo artigiano ──
    @GetMapping("/artisan/new")
    public String newArtisanForm(Model model) {
        model.addAttribute("regioni", REGIONI_ITALIANE);
        return "admin/newArtisan";
    }

    // ── Salva nuovo artigiano ──
    @PostMapping("/artisan/save")
    public String saveArtisan(@RequestParam String name,
                              @RequestParam String biography,
                              @RequestParam String region,
                              @RequestParam String location,
                              @RequestParam(required = false) MultipartFile photoFile,
                              Model model) {
        try {
            String photoUrl = fileStorageService.saveFile(photoFile);
            ArtisanEntity artisan = new ArtisanEntity(name, biography, photoUrl, location, region);
            artisanService.save(artisan);
            return "redirect:/admin";
        } catch (IOException e) {
            model.addAttribute("error", "Errore durante il caricamento della foto: " + e.getMessage());
            model.addAttribute("regioni", REGIONI_ITALIANE);
            return "admin/newArtisan";
        }
    }

    // ── Form modifica artigiano esistente ──
    @GetMapping("/artisan/edit/{id}")
    public String editArtisanForm(@PathVariable Long id, Model model) {
        Optional<ArtisanEntity> opt = artisanService.findById(id);
        if (opt.isEmpty()) return "redirect:/admin";
        model.addAttribute("artisan", opt.get());
        model.addAttribute("regioni", REGIONI_ITALIANE);
        return "admin/editArtisan";
    }

    // ── Salva modifiche artigiano ──
    @PostMapping("/artisan/edit/{id}")
    public String saveEditArtisan(@PathVariable Long id,
                                  @RequestParam String name,
                                  @RequestParam String biography,
                                  @RequestParam String region,
                                  @RequestParam String location,
                                  @RequestParam(required = false) MultipartFile photoFile,
                                  Model model) {
        Optional<ArtisanEntity> opt = artisanService.findById(id);
        if (opt.isEmpty()) return "redirect:/admin";

        ArtisanEntity artisan = opt.get();
        artisan.setName(name);
        artisan.setBiography(biography);
        artisan.setRegion(region);
        artisan.setLocation(location);

        try {
            String newPhotoUrl = fileStorageService.saveFile(photoFile);
            if (newPhotoUrl != null) artisan.setPhotoUrl(newPhotoUrl);
            artisanService.save(artisan);
            return "redirect:/admin";
        } catch (IOException e) {
            model.addAttribute("error", "Errore durante il caricamento della foto: " + e.getMessage());
            model.addAttribute("artisan", artisan);
            model.addAttribute("regioni", REGIONI_ITALIANE);
            return "admin/editArtisan";
        }
    }

    // ── Elimina artigiano ──
    @PostMapping("/artisan/delete/{id}")
    public String deleteArtisan(@PathVariable Long id) {
        artisanService.deleteById(id);
        return "redirect:/admin";
    }

    // ── Form inserimento nuovo video ──
    @GetMapping("/video/new")
    public String newVideoForm() {
        return "admin/newVideo";
    }

    // ── Salva nuovo video ──
    @PostMapping("/video/save")
    public String saveVideo(@RequestParam String title,
                            @RequestParam String description,
                            @RequestParam(required = false) MultipartFile videoFile,
                            Model model) {
        try {
            String videoUrl = fileStorageService.saveFile(videoFile);
            if (videoUrl == null) {
                model.addAttribute("error", "Il file video è obbligatorio.");
                return "admin/newVideo";
            }
            videoService.save(new VideoEntity(title, description, videoUrl));
            return "redirect:/admin";
        } catch (IOException e) {
            model.addAttribute("error", "Errore durante il caricamento del video: " + e.getMessage());
            return "admin/newVideo";
        }
    }

    // ── Elimina video ──
    @PostMapping("/video/delete/{id}")
    public String deleteVideo(@PathVariable Long id) {
        videoService.deleteById(id);
        return "redirect:/admin";
    }
}
