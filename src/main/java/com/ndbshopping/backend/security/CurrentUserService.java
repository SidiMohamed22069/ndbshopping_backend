package com.ndbshopping.backend.security;

import com.ndbshopping.backend.entity.User;
import com.ndbshopping.backend.exception.ApiException;
import com.ndbshopping.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails details)) {
            throw ApiException.unauthorized("Authentification requise");
        }
        return userRepository.findByTelephone(details.getUsername())
                .orElseThrow(() -> ApiException.unauthorized("Utilisateur introuvable"));
    }
}
