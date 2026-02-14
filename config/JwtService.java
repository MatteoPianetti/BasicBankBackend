package com.example.demo.config;

import java.security.Key;
//import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private static final String secretKey = "I5KRi1YOu+vgMVz0T7Z7td2dYrMk/GxeSNNtZjGWE3Q=";

    /* 
    public void generateAndPrintNewKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("NUOVA CHIAVE SEGRETA (Base64): " + base64Key);
        // Copia questa stringa e sostituisci il valore di 'secretKey'
    } */

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }

    public String generatedToken(
        UserDetails userDetails
    ) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
        Map<String, Object> extractClaims,
        UserDetails userDetails
    ) {
        return Jwts
            .builder()
            .setClaims(extractClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis())) //per sapere quando ho generato il token
            .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 24))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            System.out.println("DEBUG - Token in ingresso: " + token);
            System.out.println("DEBUG - Token starts with: " + (token.length() > 10 ? token.substring(0, 10) : token));
            
            return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (MalformedJwtException e) {
            System.err.println("Token malformato: " + token);
            System.err.println("Dettaglio errore: " + e.getMessage());
            throw e;
        }
    }

    public Key getSigningKey() {
       byte[] keyBytes = Decoders.BASE64.decode(secretKey);
       return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateRefreshToken(UserDetails user) {
        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) 
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
