package com.ndbshopping.backend.dto.product;

import com.ndbshopping.backend.entity.Product;
import com.ndbshopping.backend.entity.ProductAttributeValue;
import com.ndbshopping.backend.entity.ProductImage;
import com.ndbshopping.backend.entity.enums.ProductSource;
import com.ndbshopping.backend.entity.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductResponse(
        Long id,
        String nom,
        String description,
        BigDecimal prix,
        Integer stock,
        Long categoryId,
        String categoryNom,
        ProductSource sourceOrigine,
        String sourceUrl,
        ProductStatus statut,
        Instant createdAt,
        Instant updatedAt,
        List<ProductImageResponse> images,
        List<ProductAttributeResponse> attributs
) {
    public static ProductResponse from(Product product) {
        List<ProductImageResponse> images = product.getImages() == null ? List.of()
                : product.getImages().stream()
                .map(img -> new ProductImageResponse(img.getId(), toMediaUrl(img)))
                .toList();
        List<ProductAttributeResponse> attributs = product.getAttributes() == null ? List.of()
                : product.getAttributes().stream()
                .map(ProductResponse::toAttr)
                .toList();
        return new ProductResponse(
                product.getId(),
                product.getNom(),
                product.getDescription(),
                product.getPrix(),
                product.getStock(),
                product.getCategory().getId(),
                product.getCategory().getNom(),
                product.getSourceOrigine(),
                product.getSourceUrl(),
                product.getStatut(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                images,
                attributs
        );
    }

    private static ProductAttributeResponse toAttr(ProductAttributeValue value) {
        return new ProductAttributeResponse(
                value.getAttributeDefinition().getId(),
                value.getAttributeDefinition().getNomAttribut(),
                value.getAttributeDefinition().getTypeValeur(),
                value.getValeur()
        );
    }

    private static String toMediaUrl(ProductImage image) {
        String path = image.getRelativePath().replace("\\", "/");
        return path.startsWith("/") ? "/media" + path : "/media/" + path;
    }
}
