package com.boutique.thes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le type de thé est obligatoire")
    private String typeThe; // Vert, Noir, Oolong, Blanc, Pu-erh

    @NotBlank(message = "L'origine est obligatoire")
    private String origine; // Chine, Japon, Inde, Sri Lanka, Taiwan

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "5.0", message = "Le prix doit être au minimum 5€")
    @DecimalMax(value = "100.0", message = "Le prix ne peut pas dépasser 100€")
    private BigDecimal prix;

    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantiteStock;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    private LocalDate dateReception;
}
