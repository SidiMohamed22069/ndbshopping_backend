package com.ndbshopping.backend.dto.category;

import com.ndbshopping.backend.entity.Category;
import com.ndbshopping.backend.entity.enums.CategoryType;

import java.util.List;

public record CategoryResponse(
        Long id,
        String nom,
        CategoryType type,
        Long parentId,
        String imageUrl,
        List<CategoryResponse> children
) {
    public static CategoryResponse from(Category category, List<CategoryResponse> children) {
        return new CategoryResponse(
                category.getId(),
                category.getNom(),
                category.getType(),
                category.getParent() == null ? null : category.getParent().getId(),
                category.getImageUrl(),
                children
        );
    }

    public static CategoryResponse leaf(Category category) {
        return from(category, List.of());
    }
}
