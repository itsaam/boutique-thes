package com.boutique.thes.controller;

import com.boutique.thes.model.Produit;
import com.boutique.thes.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ProduitController {
    
    private final ProduitService produitService;
    
    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }
    
    /**
     * Liste des produits avec pagination, tri et recherche
     */
    @GetMapping("/")
    public String listeProduits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) String typeThe,
            Model model) {
        
        Page<Produit> pageProduits;
        
        // Recherche avec filtres ou liste complète
        if ((recherche != null && !recherche.isEmpty()) || (typeThe != null && !typeThe.isEmpty())) {
            pageProduits = produitService.search(recherche, typeThe, page, size, sort, direction);
        } else {
            pageProduits = produitService.findAll(page, size, sort, direction);
        }
        
        // Ajouter les attributs au modèle
        model.addAttribute("produits", pageProduits);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProduits.getTotalPages());
        model.addAttribute("sortBy", sort);
        model.addAttribute("sortDirection", direction);
        model.addAttribute("recherche", recherche != null ? recherche : "");
        model.addAttribute("typeThe", typeThe != null ? typeThe : "");
        
        return "index";
    }
    
    /**
     * Affiche le formulaire d'ajout d'un nouveau produit
     */
    @GetMapping("/nouveau")
    public String nouveauProduit(Model model) {
        model.addAttribute("produit", new Produit());
        return "formulaire-produit";
    }
    
    /**
     * Enregistre un nouveau produit
     */
    @PostMapping("/enregistrer")
    public String enregistrerProduit(@Valid @ModelAttribute("produit") Produit produit, BindingResult result) {
        if (result.hasErrors()) {
            return "formulaire-produit";
        }
        produitService.save(produit);
        return "redirect:/";
    }
    
    /**
     * Affiche le formulaire de modification d'un produit existant
     */
    @GetMapping("/modifier/{id}")
    public String modifierProduit(@PathVariable Long id, Model model) {
        Optional<Produit> produit = produitService.findById(id);
        if (produit.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("produit", produit.get());
        return "formulaire-produit";
    }
    
    /**
     * Met à jour un produit existant
     */
    @PostMapping("/modifier/{id}")
    public String mettreAJourProduit(@PathVariable Long id, 
                                      @Valid @ModelAttribute("produit") Produit produit, 
                                      BindingResult result) {
        if (result.hasErrors()) {
            return "formulaire-produit";
        }
        
        // Vérification de l'existence du produit
        if (!produitService.findById(id).isPresent()) {
            return "redirect:/";
        }
        
        produit.setId(id);
        produitService.save(produit);
        return "redirect:/";
    }
    
    /**
     * Supprime un produit
     */
    @PostMapping("/supprimer/{id}")
    public String supprimerProduit(@PathVariable Long id) {
        produitService.deleteById(id);
        return "redirect:/";
    }
    
    /**
     * Exporte les produits au format CSV
     */
    @GetMapping("/export-csv")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) String typeThe) {
        
        // Récupérer tous les produits correspondant aux critères de recherche
        List<Produit> produits;
        if ((recherche != null && !recherche.isEmpty()) || (typeThe != null && !typeThe.isEmpty())) {
            // Récupérer tous les résultats sans pagination pour l'export
            Page<Produit> page = produitService.search(recherche, typeThe, 0, Integer.MAX_VALUE, "nom", "asc");
            produits = page.getContent();
        } else {
            Page<Produit> page = produitService.findAll(0, Integer.MAX_VALUE, "nom", "asc");
            produits = page.getContent();
        }
        
        // Générer le CSV
        String csv = produitService.exportToCsv(produits);
        
        // Préparer la réponse HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "produits.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }
}
