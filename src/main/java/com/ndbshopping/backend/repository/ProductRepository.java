package com.ndbshopping.backend.repository;

import com.ndbshopping.backend.entity.Product;
import com.ndbshopping.backend.entity.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE (:status IS NULL OR p.statut = :status)
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:minPrix IS NULL OR p.prix >= :minPrix)
              AND (:maxPrix IS NULL OR p.prix <= :maxPrix)
              AND (:q IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY p.createdAt DESC
            """)
    Page<Product> search(
            @Param("status") ProductStatus status,
            @Param("categoryId") Long categoryId,
            @Param("minPrix") BigDecimal minPrix,
            @Param("maxPrix") BigDecimal maxPrix,
            @Param("q") String q,
            Pageable pageable
    );

    boolean existsByCategoryId(Long categoryId);
}
