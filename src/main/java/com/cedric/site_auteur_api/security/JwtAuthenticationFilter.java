package com.cedric.site_auteur_api.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.cedric.site_auteur_api.repository.UserRepository;
import com.cedric.site_auteur_api.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // A chaque requete Spring Security appelle cette méthode
    @Override
    protected void doFilterInternal(
        // recuperer la requete HTTP objet contient : headers, cookies, URL, paramètres, body
        HttpServletRequest request,                     
        // Récupérer la réponse HTTP sert a  : renvoyer une erreur,ajouter des headers, gérer des cas particuliers
        HttpServletResponse response,
        // C’est la chaîne de filtres de Spring Security fonctione pipeline pour arriver au controller
        FilterChain filterChain
    //  IOException : erreur liée aux entrées/sories
    //  ServletException : erreur liée au fonctionnement interne du serveur web 
    ) throws ServletException, IOException {

        // 1. Récupérer le header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. vérification si l eheader commence pas par Bearer on stop
        if( authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. extraire le token après "Bearer " donc 7 caractères
        String token = authHeader.substring(7);

        // 4. Validrle token
        if(!jwtService.validateToken(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Extraire Email (le subject) depuis le token
        String email = jwtService.extractEmail(token);

        // 6. Vérifier si il n'y a pas déjà ne authentification dans me contexte
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            //7. Charge le user depuis la bdd
            User user = userRepository.findByEmail(email).orElse(null);

            if(user != null) {
                //8. Construire les autorités (roles -> SimpleGrantedAuthority)
                List<SimpleGrantedAuthority> authorities = user.getUserRoles()
                    .stream()
                    // !! hasRole("ADMIN") : cherche "ROLE_ADMIN" mettre en maj si pas le cas en bdd
                    .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getRoleName().toUpperCase()))
                    .toList();

                //9. Création d'un objet Authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    authorities
                );

                //10. Mettre l'authentification dans le SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            //11. Continuer la chaine de filtres
            filterChain.doFilter(request, response);
        } 
    }

}
