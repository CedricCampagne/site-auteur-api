package com.cedric.site_auteur_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // désactive pour les outils type postman, ThunderClient ...
            // active pour les formulaires HTML
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Auth
                // défini la route et .permitAll() pour une route publique
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/login").permitAll()
                // Books
                .requestMatchers("/books/**").permitAll()

                // Users
                .requestMatchers("/users/**").permitAll()

                // Chroniques privées
                // .authenticated() pour une route privée
                .requestMatchers("/chroniques/{id}").authenticated()

                // Tous le reste protégé
                .anyRequest().authenticated()
        );

        return http.build();
    }
}
