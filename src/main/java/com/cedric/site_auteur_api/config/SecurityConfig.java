package com.cedric.site_auteur_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.cedric.site_auteur_api.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // désactive pour les outils type postman, ThunderClient ...
            // active pour les formulaires HTML
            // désactiver CSRF (API REST)
            .csrf(csrf -> csrf.disable())

            // Pas de session : JWT = stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // définir les routes publiques et privées

            .authorizeHttpRequests(auth -> auth
                // défini la route et .permitAll() pour une route publique
                // Auth public
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/login").permitAll()

                // Books
                .requestMatchers("/books/**").permitAll()

                // Users
                .requestMatchers("/users/**").permitAll()

                //route protégée .authenticated()                
                .requestMatchers("/chroniques/{id}").authenticated()

                // Privé : admin uniquement
                .requestMatchers("/admin/**").hasRole("ADMIN")          // !! hasRole("ADMIN") : cherche "ROLE_ADMIN" mettre en maj si pas le cas en bdd

                // Tous le reste protégé
                .anyRequest().authenticated()
        )

        // Ajout du filtre JWT juste avant le filtre standard de Spring
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
