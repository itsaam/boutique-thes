package com.boutique.thes.service;

import com.boutique.thes.model.Produit;
import com.boutique.thes.repository.ProduitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    
    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }
    
    /**
     * Récupère tous les produits avec pagination et tri
     */
    public Page<Produit> findAll(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return produitRepository.findAll(pageable);
    }
    
    /**
     * Recherche des produits par nom et/ou type avec pagination et tri
     */
    public Page<Produit> search(String nom, String typeThe, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Cas 1 : Recherche par nom ET type
        if (nom != null && !nom.isEmpty() && typeThe != null && !typeThe.isEmpty()) {
            return produitRepository.findByNomContainingIgnoreCaseAndTypeThe(nom, typeThe, pageable);
        }
        // Cas 2 : Recherche par nom seulement
        else if (nom != null && !nom.isEmpty()) {
            return produitRepository.findByNomContainingIgnoreCase(nom, pageable);
        }
        // Cas 3 : Recherche par type seulement
        else if (typeThe != null && !typeThe.isEmpty()) {
            return produitRepository.findByTypeThe(typeThe, pageable);
        }
        // Cas 4 : Aucun filtre, retourner tous les produits
        else {
            return findAll(page, size, sortBy, direction);
        }
    }
    
    /**
     * Trouve un produit par son ID
     */
    public Optional<Produit> findById(Long id) {
        return produitRepository.findById(id);
    }
    
    /**
     * Sauvegarde ou met à jour un produit
     */
    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }
    
    /**
     * Supprime un produit par son ID
     */
    public void deleteById(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new IllegalArgumentException("Produit avec l'ID " + id + " n'existe pas");
        }
        produitRepository.deleteById(id);
    }
    
    /**
     * Exporte une liste de produits au format CSV
     */
    public String exportToCsv(List<Produit> produits) {
        StringBuilder csv = new StringBuilder();
        
        // En-tête CSV
        csv.append("\"id\",\"nom\",\"typeThe\",\"origine\",\"prix\",\"quantiteStock\",\"description\",\"dateReception\"\n");
        
        // Lignes de données
        for (Produit produit : produits) {
            csv.append("\"").append(escapeValue(produit.getId())).append("\",");
            csv.append("\"").append(escapeValue(produit.getNom())).append("\",");
            csv.append("\"").append(escapeValue(produit.getTypeThe())).append("\",");
            csv.append("\"").append(escapeValue(produit.getOrigine())).append("\",");
            csv.append("\"").append(escapeValue(produit.getPrix())).append("\",");
            csv.append("\"").append(escapeValue(produit.getQuantiteStock())).append("\",");
            csv.append("\"").append(escapeValue(produit.getDescription())).append("\",");
            csv.append("\"").append(escapeValue(produit.getDateReception())).append("\"\n");
        }
        
        return csv.toString();
    }
    
    /**
     * Échappe les valeurs pour le format CSV (gère les guillemets et les valeurs nulles)
     */
    private String escapeValue(Object value) {
        if (value == null) {
            return "";
        }
        String stringValue = value.toString();
        // Échapper les guillemets en les doublant
        return stringValue.replace("\"", "\"\"");
    }
}
