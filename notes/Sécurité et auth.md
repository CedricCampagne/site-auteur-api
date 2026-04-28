# Sécurité et authentification

## PLAN DE LA FORMATION AUTH (Spring Boot + JWT)

On va suivre exactement la logique Node :

1. REGISTER (inscription)

- route : POST /auth/register
- on crée un user
- on hash le password (BCrypt)
- on renvoie un UserDto (pas de token ici)

2. LOGIN

- route : POST /auth/login
- on vérifie email + password
- si OK → on génère un JWT
- on renvoie { token, user }

- 3. JWT FILTER

un filtre Spring Security qui :
- lit le header Authorization
- extrait le token
- valide le token
- charge l’utilisateur
- ajoute l’authentification dans le contexte

4. PROTECTION DES ROUTES

- /auth/** → public
- /users/** → authentifié
- /admin/** → rôle ADMIN

5. ROLE-BASED ACCESS

- annotation @PreAuthorize("hasRole('ADMIN')")
- ou config dans SecurityFilterChain

6. LOGOUT (optionnel)

- côté JWT, c’est juste côté front (on supprime le token)

Ce qu’on va utiliser :
- Spring Security 6
- JWT (jjwt ou java-jwt)
- BCryptPasswordEncoder
- Un filtre custom (comme un middleware Node)

c’est la même logique que Node, juste plus typée et plus propre.

## Étape 1 : REGISTER

1. Objectif du REGISTER

- Créer un nouvel utilisateur :
- vérifier que l’email n’existe pas déjà
- hasher le mot de passe
- sauvegarder l’utilisateur
- renvoyer un DTO (jamais l’entité)

Aucun token ici.  
C’est juste l’inscription.  

2. DTO pour l’inscription

```java
public record RegisterDto(
    String username,
    String email,
    String password
) {}
```

3. Méthodes nécessaires dans UserRepository

```java
boolean existsByEmail(String email);
```

Permet de vérifier si l’email est déjà utilisé.

4. PasswordEncoder (obligatoire pour hasher)

- Dans config/SecurityConfig.java :

    ```java
    @Configuration
    public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
    ```

    bean a injecter via constructeur dans le service.

    ```java
    private final PasswordEncoder passwordEncoder;
    ```
- Config minimale pour pas avoir 401 lors des test :

    - Le ``SecurityFilterChain`` (le cœur de la config)
        Spring Security 6 (Spring Boot 3) utilise ``SecurityFilterChain`` pour définir les règles de sécurité.

        ```java
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            ....
        }
        ```

        - ``HttpSecurity`` est l’objet principal que Spring Security te donne pour configurer :
            - les routes sont publiques
            - quelles routes sont protégées
            - comment gérer l’authentification
            - comment gérer les filtres (comme le JWT)
            - comment gérer CSRF, CORS, sessions, etc.

            On peut le voir comme le “pare‑feu” de l'API.

            C’est lui qui décide :“Cette route est libre, celle‑ci demande un token, celle‑ci demande un rôle admin…”

    - Désactiver CSRF (protection contre les attaques sur les formulaires HTML) pour les API
        - CSRF est utile pour les formulaires HTML.
        - Pour une API REST (Postman, React, mobile), on le désactive.
        - Sinon, POST/PUT/DELETE seraient bloqués.

    - Définir les routes publiques et protégées
        - ``requestMatchers("/auth/register").permitAll()``
            - Cette route est publique
            - Pas besoin de token
            - Tu peux tester depuis Postman
        - ``anyRequest().authenticated()``
            _ Toutes les autres routes nécessitent une authentification
            - Comme pas encore mis le JWT → elles renvoient 401 (normal, prévu)

    ```java
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/register").permitAll() // PUBLIC
        .anyRequest().authenticated() // tout le reste protégé
    );
    ```

    - Retourner la config

    ```java
    return http.build();
    ```

5. REGISTER dans UserService

```java
public UserFullDto register(RegisterDto dto) {

    if (userRepository.existsByEmail(dto.email())) {
        throw new RuntimeException("Email déjà utilisé");
    }

    User user = new User();
    user.setUsername(dto.username());
    user.setEmail(dto.email());
    user.setPassword(passwordEncoder.encode(dto.password())); // hash
    user.setIsActive(true);
    user.setCreatedAt(OffsetDateTime.now());
    user.setUpdatedAt(OffsetDateTime.now());

    User saved = userRepository.save(user);

    return UserMapper.toFullDto(saved);
}
```

6. AuthController : route REGISTER

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;

    public AuthController (UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserFullDto register (@RequestBody RegisterDto dto) {
        return userService.register(dto);
    }
}
```

7. Résumé ultra simple 

    1. Vérifier email unique (existsByEmail)
    2. Hasher password (passwordEncoder.encode)
    3. Créer User
    4. Sauvegarder
    5. Retourner UserFullDto

## Étape 2 : LOGIN avec token

1. Ajouter la lib JWT dans ton projet :

```java
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
```

2. Définir une clé secrète dans la config

Dans application.yaml et les données dans le ``.env``

```java
jwt:
    secret: ${JWT_Secret}
    expiration: ${JWT_EXPIRATION}
```

3. Créer le service JWT (JwtService)

- Concept :  
    On crée une classe qui sait générer un token à partir d’un User ( dans security/JwtService.java)

```java
    @Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // méthode qui générera le token (on la remplira après)
    public String generateToken(User user) {
        return null; // temporaire
    }
}
```

On vérifie que l'application démarre

4. Ajouter la logique de génération du token

    Voir notes Token.md

## Valider un token recu

```java
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
```

## Le rôle du ``JwtAuthenticationFilter``


