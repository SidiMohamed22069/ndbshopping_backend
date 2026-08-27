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
              AND (CAST(:categoryId AS long) IS NULL OR p.category.id = CAST(:categoryId AS long))
              AND (CAST(:minPrix AS big_decimal) IS NULL OR p.prix >= CAST(:minPrix AS big_decimal))
              AND (CAST(:maxPrix AS big_decimal) IS NULL OR p.prix <= CAST(:maxPrix AS big_decimal))
              AND (
                    CAST(:q AS string) IS NULL
                    OR CAST(:q AS string) = ''
                    OR LOWER(p.nom) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
                    OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
                  )
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
