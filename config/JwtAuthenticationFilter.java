package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, //la nostra richiesta, estraiamo i dati per metterli nel response
        @NonNull HttpServletResponse response,  //la nostra risposta
        @NonNull FilterChain filterChain //contiene la lista dei filtri che dobbiamo eseguire
    )     
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); //quando facciamo una chiamata dobbiamo passare un Jwt token all'interno dell'header
        final String jwt;
        final String userEmail;
        if(authHeader == null  || !authHeader.startsWith("Bearer ")) {
            //non voglio contiruare con l'esecuzione
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); //per il prefisso "Bearer "
        userEmail = jwtService.extractUsername(jwt); // todo extract userEmail from the token

        //la mail è valida, e lo user non è autenticato
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            //se il token è valido, devo aggiornare il security context
            if(jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, //non abbiamo le credenziali quando creiamo un utente
                    userDetails.getAuthorities()
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                    .buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
