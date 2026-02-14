package com.example.demo.config;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler{
 private final TokenRepository tokenRepository;

    @Override
    public void logout(
        HttpServletRequest request, 
        HttpServletResponse response, 
        @Nullable Authentication auth
    ) {
      final String authHeader = request.getHeader("Authorization");
      final String jwt;
      
      if(authHeader == null || !authHeader.startsWith("Bearer ")) {
        return;
      }

      jwt = authHeader.substring(7);
      var storedToken = tokenRepository.findByToken(jwt).orElseThrow(() -> new NoEntityFoundException("Nessun token trovato"));
      if(storedToken != null) {
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
        SecurityContextHolder.clearContext();
      }
    }
}
