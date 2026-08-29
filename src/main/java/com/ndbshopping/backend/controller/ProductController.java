package com.ndbshopping.backend.controller;

import com.ndbshopping.backend.dto.common.PageResponse;
import com.ndbshopping.backend.dto.product.ProductImageResponse;
import com.ndbshopping.backend.dto.product.ProductRequest;
import com.ndbshopping.backend.dto.product.ProductResponse;
import com.ndbshopping.backend.security.CurrentUserService;
import com.ndbshopping.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Produits (public)")
public class ProductController {

    private final ProductService productService;
    private final CurrentUserService currentUserService;

    public ProductController(ProductService productService, CurrentUserService currentUserService) {
        this.productService = productService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "Liste paginée des produits publiés")
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrix,
            @RequestParam(required = false) BigDecimal maxPrix,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productService.searchPublic(categoryId, minPrix, maxPrix, q, pageable);
    }

    @GetMapping("/me")
    @Operation(summary = "Produits soumis par l'utilisateur connecté")
    public PageResponse<ProductResponse> mine(@PageableDefault(size = 20) Pageable pageable) {
        return productService.listMine(currentUserService.requireUser(), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Soumet un produit (statut forcé EN_ATTENTE, visible après validation admin)")
    public ProductResponse submit(@Valid @RequestBody ProductRequest request) {
        return productService.submit(currentUserService.requireUser(), request);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload d'une image : propriétaire tant que EN_ATTENTE, ou admin")
    public ProductImageResponse uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return productService.addImage(id, file, currentUserService.requireUser());
    }

    @PatchMapping("/{id}/vendu")
    @Operation(summary = "Marque l'annonce comme vendue (PUBLIE → VENDU)")
    public ProductResponse markSold(@PathVariable Long id) {
        return productService.markSold(id, currentUserService.requireUser());
    }

    @PatchMapping("/{id}/archiver")
    @Operation(summary = "Archive l'annonce (PUBLIE → ARCHIVE uniquement, pas depuis VENDU)")
    public ProductResponse archive(@PathVariable Long id) {
        return productService.archive(id, currentUserService.requireUser());
    }

    @PatchMapping("/{id}/reactiver")
    @Operation(summary = "Réactive une annonce vendue ou archivée (→ PUBLIE)")
    public ProductResponse reactivate(@PathVariable Long id) {
        return productService.reactivate(id, currentUserService.requireUser());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un produit publié (404 si non public, sauf propriétaire/admin)")
    public ProductResponse get(@PathVariable Long id) {
        return productService.getVisible(id, currentUserService.findUser().orElse(null));
    }
}
