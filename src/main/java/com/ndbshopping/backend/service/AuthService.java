package com.ndbshopping.backend.service;

import com.ndbshopping.backend.dto.auth.AuthResponse;
import com.ndbshopping.backend.dto.auth.RegisterOrLoginRequest;
import com.ndbshopping.backend.dto.auth.UserResponse;
import com.ndbshopping.backend.dto.auth.VerifyOtpRequest;
import com.ndbshopping.backend.entity.User;
import com.ndbshopping.backend.entity.enums.Role;
import com.ndbshopping.backend.exception.ApiException;
import com.ndbshopping.backend.repository.UserRepository;
import com.ndbshopping.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void registerOrLogin(RegisterOrLoginRequest request) {
        User user = userRepository.findByTelephone(request.telephone()).orElse(null);
        if (user == null) {
            user = User.builder()
                    .nom(request.nom().trim())
                    .telephone(request.telephone())
                    .passwordHash(passwordEncoder.encode(request.password()))
                    .telephoneVerifie(false)
                    .role(Role.CLIENT)
                    .build();
            userRepository.save(user);
        } else if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Mot de passe incorrect");
        }

        otpService.sendOtp(request.telephone(), request.langOrDefault());
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        otpService.verifyOrThrow(request.telephone(), request.code());
        User user = userRepository.findByTelephone(request.telephone())
                .orElseThrow(() -> ApiException.notFound("Utilisateur introuvable"));
        user.setTelephoneVerifie(true);
        String token = jwtUtil.generateToken(user.getId(), user.getTelephone(), user.getRole().name());
        return new AuthResponse(token, UserResponse.from(user));
    }
}
