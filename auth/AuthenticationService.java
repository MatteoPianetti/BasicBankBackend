package com.example.demo.auth;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtService;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.exception.UnauthorizeException;
import com.example.demo.model.token.Token;
import com.example.demo.model.token.TokenType;
import com.example.demo.model.user.User;
import com.example.demo.model.user.enumerated.Role;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final int MAX_FAILED_ATTEMPTS = 20;

     public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole() != null ? request.getRole() : Role.USER)
            .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generatedToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        User user = repository.findByEmail(request.getEmail())
            .orElseThrow();
        var jwtToken = jwtService.generatedToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
        return;
        validUserTokens.forEach(token -> {
        token.setExpired(true);
        token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try{
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String refreshToken;
            final String userEmail;
            if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
            }
            refreshToken = authHeader.substring(7);
            userEmail = jwtService.extractUsername(refreshToken);
            if (userEmail != null) {
                var user = this.repository.findByEmail(userEmail)
                        .orElseThrow();
                if (jwtService.isTokenValid(refreshToken, user)) {
                    var accessToken = jwtService.generatedToken(user);
                    revokeAllUserTokens(user);
                    saveUserToken(user, accessToken);
                    var authResponse = AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }   
            }
        } catch(java.io.IOException e) {
            System.out.println("Errore durante il refresh del token");
        }
    }

    public void changePassword(
        ChangePasswordRequest request,
        Principal principal
    ) {
        String email = principal.getName();
        User currentUser = repository.findByEmail(email)
                .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));

        if(!currentUser.getPassword().equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException("La password inserita non è corretta");
        }

        if(request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("Stai riutilizzando la stessa password");
        }

        if(!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Le password non coincidono");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(currentUser);
    }

    public void lockUser(LockRequest request, Principal principal) {
        User user = repository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));

        if(user.getRole() == Role.TELLER || user.getRole() == Role.BANK_MANAGER) {
            throw new UnauthorizeException("Non puoi bloccare un lavoratore della banca");
        }

        user.setAccountNonLocked(false);
        user.setReason(request.getReason());
        user.setLockedAt(LocalDate.now());
        user.setLockedBy(principal.getName());

        repository.save(user);
    }

    public void unlockUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NoEntityFoundException("Utente non trovato"));
        
        user.setAccountNonLocked(true);
        user.setFailedLoginAttempts(0); // Resetta contatore
        user.setReason(null);
        user.setLockedAt(null);
        
        repository.save(user);
    }

    public void autoLockUserAfterFailedAttempts(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new NoEntityFoundException("Utente non trovato"));
        
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        
        if(user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setReason("Troppi tentativi di login falliti");
            user.setLockedAt(LocalDate.now());
        }
        
        repository.save(user);
    }
}
