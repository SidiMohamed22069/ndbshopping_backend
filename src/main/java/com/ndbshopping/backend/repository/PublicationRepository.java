package com.ndbshopping.backend.repository;

import com.ndbshopping.backend.entity.Publication;
import com.ndbshopping.backend.entity.enums.PublicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    Page<Publication> findByStatutOrderByDatePublicationDesc(PublicationStatus statut, Pageable pageable);

    Page<Publication> findAllByOrderByDatePublicationDesc(Pageable pageable);
}
