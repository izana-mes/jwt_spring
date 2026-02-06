package com.example.app.modules.auth;

import com.example.app.common.exception.TokenRefreshException;
import com.example.app.modules.auth.dto.AuthenticationResponse;
import com.example.app.modules.auth.dto.LoginRequest;
import com.example.app.modules.auth.dto.RegisterRequest;
import com.example.app.modules.auth.security.JwtService;
import com.example.app.modules.role.Role;
import com.example.app.modules.role.RoleRepository;
import com.example.app.modules.token.RefreshToken;
import com.example.app.modules.token.RefreshTokenService;
import com.example.app.modules.user.User;
import com.example.app.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final RefreshTokenService refreshTokenService;

        @Value("${application.security.jwt.expiration}")
        private Long jwtExpiration;

        @Transactional
        public AuthenticationResponse register(RegisterRequest request) {
                // Check if user already exists
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new IllegalStateException("User with email " + request.getEmail() + " already exists");
                }

                // Get or create default role
                Role userRole = roleRepository.findByName("ROLE_USER")
                                .orElseGet(() -> roleRepository.save(
                                                Role.builder()
                                                                .name("ROLE_USER")
                                                                .build()));

                // Create user with default role
                var user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .isActive(true)
                                .isEmailVerified(false)
                                .roles(List.of(userRole))
                                .build();

                User savedUser = userRepository.save(user);

                // Generate tokens
                String accessToken = jwtService.generateTokenWithClaims(savedUser);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

                return AuthenticationResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getRefreshTokenHash())
                                .tokenType("Bearer")
                                .expiresIn(jwtExpiration)
                                .build();
        }

        public AuthenticationResponse authenticate(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new IllegalStateException("User not found"));

                // Generate tokens
                String accessToken = jwtService.generateTokenWithClaims(user);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                return AuthenticationResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getRefreshTokenHash())
                                .tokenType("Bearer")
                                .expiresIn(jwtExpiration)
                                .build();
        }

        public AuthenticationResponse refreshToken(String refreshTokenStr) {
                return refreshTokenService.findByToken(refreshTokenStr)
                                .map(refreshTokenService::verifyExpiration)
                                .map(RefreshToken::getUser)
                                .map(user -> {
                                        String accessToken = jwtService.generateTokenWithClaims(user);
                                        return AuthenticationResponse.builder()
                                                        .accessToken(accessToken)
                                                        .refreshToken(refreshTokenStr)
                                                        .tokenType("Bearer")
                                                        .expiresIn(jwtExpiration)
                                                        .build();
                                })
                                .orElseThrow(() -> new TokenRefreshException(refreshTokenStr,
                                                "Refresh token is not in database!"));
        }
}
