package com.boutique.thes.repository;

import com.boutique.thes.model.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    List<Produit> findByNomContainingIgnoreCase(String nom);
    
    List<Produit> findByTypeThe(String typeThe);
    
    Page<Produit> findByNomContainingIgnoreCaseAndTypeThe(String nom, String typeThe, Pageable pageable);
    
    Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);
    
    Page<Produit> findByTypeThe(String typeThe, Pageable pageable);
}
