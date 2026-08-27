package com.ndbshopping.backend.config;

import com.ndbshopping.backend.entity.Category;
import com.ndbshopping.backend.entity.CategoryAttributeDefinition;
import com.ndbshopping.backend.entity.Product;
import com.ndbshopping.backend.entity.Publication;
import com.ndbshopping.backend.entity.User;
import com.ndbshopping.backend.entity.enums.AttributeValueType;
import com.ndbshopping.backend.entity.enums.CategoryType;
import com.ndbshopping.backend.entity.enums.ProductSource;
import com.ndbshopping.backend.entity.enums.ProductStatus;
import com.ndbshopping.backend.entity.enums.PublicationStatus;
import com.ndbshopping.backend.entity.enums.Role;
import com.ndbshopping.backend.repository.CategoryAttributeDefinitionRepository;
import com.ndbshopping.backend.repository.CategoryRepository;
import com.ndbshopping.backend.repository.ProductRepository;
import com.ndbshopping.backend.repository.PublicationRepository;
import com.ndbshopping.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryAttributeDefinitionRepository attributeRepository;
    private final ProductRepository productRepository;
    private final PublicationRepository publicationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            CategoryAttributeDefinitionRepository attributeRepository,
            ProductRepository productRepository,
            PublicationRepository publicationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.attributeRepository = attributeRepository;
        this.productRepository = productRepository;
        this.publicationRepository = publicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        if (categoryRepository.count() == 0) {
            seedCatalog();
        }
    }

    private void seedAdmin() {
        if (userRepository.existsByTelephone("37565537")) {
            return;
        }
        // Bases déjà déployées : ne pas créer un second admin si l'ancien seed (20000001) ou un ADMIN existe.
        if (userRepository.existsByTelephone("20000001") || userRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        userRepository.save(User.builder()
                .nom("Administrateur")
                .telephone("37565537")
                .passwordHash(passwordEncoder.encode("password123"))
                .telephoneVerifie(true)
                .role(Role.ADMIN)
                .build());
        log.info("Compte admin de démo créé (téléphone 37565537)");
    }

    private void seedCatalog() {
        Category vetements = categoryRepository.save(Category.builder()
                .nom("Vêtements")
                .type(CategoryType.PRODUIT)
                .build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(vetements).nomAttribut("taille").typeValeur(AttributeValueType.TEXTE).build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(vetements).nomAttribut("couleur").typeValeur(AttributeValueType.TEXTE).build());

        Category hotels = categoryRepository.save(Category.builder()
                .nom("Hôtels")
                .type(CategoryType.HOTEL)
                .build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(hotels).nomAttribut("nombre de chambres").typeValeur(AttributeValueType.NOMBRE).build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(hotels).nomAttribut("ville").typeValeur(AttributeValueType.TEXTE).build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(hotels).nomAttribut("prix/nuit").typeValeur(AttributeValueType.NOMBRE).build());

        Category voitures = categoryRepository.save(Category.builder()
                .nom("Voitures")
                .type(CategoryType.VOITURE)
                .build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(voitures).nomAttribut("marque").typeValeur(AttributeValueType.TEXTE).build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(voitures).nomAttribut("année").typeValeur(AttributeValueType.NOMBRE).build());
        attributeRepository.save(CategoryAttributeDefinition.builder()
                .category(voitures).nomAttribut("kilométrage").typeValeur(AttributeValueType.NOMBRE).build());

        productRepository.save(Product.builder()
                .nom("T-shirt coton")
                .description("T-shirt basique, idéal pour le quotidien à Nouadhibou.")
                .prix(new BigDecimal("1500.00"))
                .stock(40)
                .category(vetements)
                .sourceOrigine(ProductSource.MANUEL)
                .statut(ProductStatus.PUBLIE)
                .build());

        publicationRepository.save(Publication.builder()
                .titre("Bienvenue sur NDB SHOPPING")
                .contenu("Marketplace pour Nouadhibou. Commandez en toute simplicité.")
                .statut(PublicationStatus.PUBLIE)
                .build());

        log.info("Catégories et données de démo créées (Vêtements, Hôtels, Voitures)");
    }
}
