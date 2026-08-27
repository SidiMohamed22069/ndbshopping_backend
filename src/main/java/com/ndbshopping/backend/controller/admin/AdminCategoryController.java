package com.ndbshopping.backend.controller.admin;

import com.ndbshopping.backend.dto.category.AttributeDefinitionRequest;
import com.ndbshopping.backend.dto.category.AttributeDefinitionResponse;
import com.ndbshopping.backend.dto.category.CategoryRequest;
import com.ndbshopping.backend.dto.category.CategoryResponse;
import com.ndbshopping.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Admin — Catégories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une catégorie")
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une catégorie")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une catégorie vide")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de l'image de catégorie (jpg/png/webp, max 5 Mo)")
    public CategoryResponse uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        return categoryService.uploadImage(id, image);
    }

    @PostMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter un attribut dynamique à une catégorie")
    public AttributeDefinitionResponse addAttribute(
            @PathVariable Long id,
            @Valid @RequestBody AttributeDefinitionRequest request
    ) {
        return categoryService.addAttribute(id, request);
    }

    @DeleteMapping("/{id}/attributes/{attributeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttribute(@PathVariable Long id, @PathVariable Long attributeId) {
        categoryService.deleteAttribute(id, attributeId);
    }
}
