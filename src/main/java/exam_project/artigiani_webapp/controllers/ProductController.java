package exam_project.artigiani_webapp.controllers;

import exam_project.artigiani_webapp.entities.ArtisanEntity;
import exam_project.artigiani_webapp.entities.ProductEntity;
import exam_project.artigiani_webapp.services.ArtisanService;
import exam_project.artigiani_webapp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ArtisanService artisanService;

    @Autowired
    public ProductController(ProductService productService, ArtisanService artisanService) {
        this.productService = productService;
        this.artisanService = artisanService;
    }

    // Landing prodotti: menu radiale regioni
    @GetMapping("/products")
    public String productsHome(Model model) {
        return "products";
    }

    // ── Made in Italy: tutti i prodotti con filtri gerarchici ──
    @GetMapping("/products/all")
    public String allProducts(Model model) {
        List<ProductEntity> products = productService.findAll();

        // Regioni distinte (da artisans con region compilato)
        Set<String> regions = artisanService.findAll().stream()
                .map(ArtisanEntity::getRegion)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Città distinte
        Set<String> cities = artisanService.findAll().stream()
                .map(ArtisanEntity::getLocation)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        model.addAttribute("products", products);
        model.addAttribute("regions", regions);
        model.addAttribute("cities", cities);
        return "productsAll";
    }

    // ── Città di una regione (usa il campo region dell'artigiano) ──
    @GetMapping("/products/region")
    public String regionCities(@RequestParam("region") String region, Model model) {
        List<String> cities = artisanService.findCitiesByRegion(region);
        model.addAttribute("region", region);
        model.addAttribute("cities", cities);
        return "regionCities";
    }

    // ── Prodotti di una città specifica ──
    @GetMapping("/products/city")
    public String cityProducts(@RequestParam("region") String region,
                               @RequestParam("city") String city,
                               Model model) {
        List<ProductEntity> products = productService.findByArtisanRegionAndCity(region, city);
        model.addAttribute("region", region);
        model.addAttribute("city", city);
        model.addAttribute("products", products);
        return "cityProducts";
    }

    // ── Artigiani filtrati per luogo (mantenuto per compatibilità) ──
    @GetMapping("/products/location")
    public String artisansByLocation(@RequestParam("city") String city, Model model) {
        model.addAttribute("artisans", artisanService.findByLocation(city));
        model.addAttribute("city", city);
        return "artisansByLocation";
    }

    // ── Dettaglio prodotto ──
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Optional<ProductEntity> optProduct = productService.findById(id);
        if (optProduct.isEmpty()) {
            return "redirect:/products";
        }
        model.addAttribute("product", optProduct.get());
        return "productDetail";
    }
}
