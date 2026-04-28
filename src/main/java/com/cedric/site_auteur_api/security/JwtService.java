package com.cedric.site_auteur_api.security;

import com.cedric.site_auteur_api.entity.User;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .setSubject(user.getEmail())    // identifiant principal
            .claim("id", user.getIdUser())  // infos custom
            .claim("roles", user.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(
                Keys.hmacShaKeyFor(secret.getBytes()),
                SignatureAlgorithm.HS256
            )
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            // 1. Parse le token et vérifie la signature
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token);

            return true; // si aucune exception → token valide

        } catch (Exception e) {
            return false; // signature invalide, expiré, mal formé, etc.
        }
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

}
