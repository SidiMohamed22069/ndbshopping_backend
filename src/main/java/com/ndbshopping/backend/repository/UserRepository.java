package com.ndbshopping.backend.repository;

import com.ndbshopping.backend.entity.User;
import com.ndbshopping.backend.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTelephone(String telephone);

    boolean existsByTelephone(String telephone);

    boolean existsByRole(Role role);
}
