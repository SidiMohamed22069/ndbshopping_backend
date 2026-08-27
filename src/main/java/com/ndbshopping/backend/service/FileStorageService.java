package com.ndbshopping.backend.service;

import com.ndbshopping.backend.config.AppProperties;
import com.ndbshopping.backend.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private static final Map<String, String> EXT_BY_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/jpg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final Path root;

    public FileStorageService(AppProperties appProperties) {
        this.root = Path.of(appProperties.upload().dir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de créer le dossier d'upload: " + this.root, e);
        }
    }

    public String storeProductImage(Long productId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("Fichier image manquant");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw ApiException.badRequest("Format d'image non autorisé (jpg, png, webp uniquement)");
        }

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extFromName = extensionOf(original);
        String ext = EXT_BY_TYPE.getOrDefault(contentType, extFromName.isBlank() ? ".jpg" : extFromName);
        String filename = UUID.randomUUID() + ext;
        Path dir = root.resolve("products").resolve(String.valueOf(productId));
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target);
            return "products/" + productId + "/" + filename;
        } catch (IOException e) {
            log.error("Échec de sauvegarde de l'image produit {}", productId, e);
            throw ApiException.serviceUnavailable("Impossible d'enregistrer l'image");
        }
    }

    public void deleteIfExists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        Path target = root.resolve(relativePath).normalize();
        if (!target.startsWith(root)) {
            log.warn("Tentative de suppression hors dossier d'upload: {}", relativePath);
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Impossible de supprimer le fichier {}", target, e);
        }
    }

    private static String extensionOf(String filename) {
        int i = filename.lastIndexOf('.');
        if (i < 0) {
            return "";
        }
        return filename.substring(i).toLowerCase(Locale.ROOT);
    }
}
