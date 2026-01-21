package com.boutique.thes.repository;

import com.boutique.thes.model.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    List<Produit> findByNomContainingIgnoreCase(String nom);
    
    List<Produit> findByTypeThe(String typeThe);
    
    Page<Produit> findByNomContainingIgnoreCaseAndTypeThe(String nom, String typeThe, Pageable pageable);
    
    Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);
    
    Page<Produit> findByTypeThe(String typeThe, Pageable pageable);
    
    /**
     * Récupère tous les types de thé distincts présents en base de données.
     * 
     * @Query est NÉCESSAIRE ici car Spring Data JPA ne peut pas projeter automatiquement
     * un seul champ (typeThe) en String avec la naming convention seule.
     * Sans @Query, Spring retourne des entités Produit complètes et échoue lors de la conversion vers String,
     * causant l'erreur : "No converter found capable of converting from type [Produit] to type [String]".
     * 
     * La query JPQL projette directement le champ typeThe en tant que String.
     */
    @Query("SELECT DISTINCT p.typeThe FROM Produit p ORDER BY p.typeThe")
    List<String> findDistinctTypeTheBy();
}
