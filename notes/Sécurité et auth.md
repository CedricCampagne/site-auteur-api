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

## Le ``JwtAuthenticationFilter``

1. Rôle

- lire le header Authorization
- extraire le token Bearer ...
- demander à JwtService :
    - si le token est valide
    - quel email il contient
- charger l’utilisateur depuis la base
- créer un Authentication
- le mettre dans le SecurityContext


Et ensuite laisser la requête continuer.  
On va utiliser OncePerRequestFilter (Spring Security) pour être sûr qu’il s’exécute une seule fois par requête.

2. extends ``OncePerRequestFilter``

Spring Security donne plusieurs types de filtres possibles.  
``OncePerRequestFilter`` est le bon choix pour un filtre JWT, car il garantit une chose essentielle :  
Le filtre ne s’exécute qu’une seule fois par requête HTTP.  

Obligation d'implémenter ``doFilterInternal()`` méthode qui contient toute la logique du filtre :
- lire le header Authorization
- extraire le token
- valider le token
- extraire l’email
- charger l’utilisateur
- créer l’Authentication
- remplir le SecurityContext
- laisser la requête continuer


``OncePerRequestFilter`` est conçu pour s'intègrer dans le pipeline  Spring Security

```bash
[Filtre A] → [Filtre B] → [Ton filtre JWT] → [Filtre C] → [Controller]
```

3. Résumé ultra simple

extends OncePerRequestFilter : 
- garantit que le filtre s’exécute une seule fois par requête
- impose d’implémenter doFilterInternal()
- idéal pour lire/valider un token JWT
- évite les doublons et s’intègre parfaitement dans Spring Security

3. ``doFilterInternal()``

hérite de OncePerRequestFilter, et cette classe impose une seule méthode à implémenter :

```java
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)

```

C’est le point d’entrée du filtre.  
Spring Security l’appelle une fois par requête HTTP.

``doFilterInternal()`` c'est lui qui fait tout :
- lire le header Authorization
- extraire le token
- valider le token
- extraire l’email
- charger l’utilisateur
- créer un Authentication
- le mettre dans le SecurityContext
- laisser la requête continuer

Sans cette méthode → ton JWT ne sert à rien.

### Les paramètres

- ``HttpServletRequest request`` : 
Contient :
- les headers (dont ``Authorization``)
- l’URL
- les paramètres
- le body

C’est ici que tu lis le token.

- ``HttpServletResponse response`` : 
Contient :
- les headers de réponse
- le statut HTTP
- le body de réponse

On ne l’utilise presque jamais dans un filtre JWT, mais il doit être là.

- ``FilterChain filterChain``
C’est la chaîne de filtres de Spring Security.  

Le filtre doit toujours appeler :

````java
filterChain.doFilter(request, response);
````

Sinon la requête n’arrive jamais au contrôleur.

- Pourquoi ``throws ServletException, IOException``
    - ``IOException`` : erreurs d’entrée/sortie (lecture header, réseau…)
    - ``ServletException`` : erreurs dans la chaîne de filtres ou le serveur

    Ces exceptions sont normales dans un filtre.
    Spring Security sait les gérer : on les laisse remonter.

- Les 10 étapes internes de ``doFilterInternal()`` : 
    - Lire le header Authorization
    - Si pas de token → laisser passer
    - Extraire le token après “Bearer ”
    - Valider le token
    - Extraire l’email
    - Vérifier que personne n’est déjà authentifié
    - Charger l’utilisateur depuis la base
    - Construire les rôles (``SimpleGrantedAuthority``)
    - Créer un ``UsernamePasswordAuthenticationToken``
    - Mettre l’authentification dans le ``SecurityContext``
    - Continuer la chaîne (``filterChain.doFilter()``)

- Résumé ultra-court : 
    doFilterInternal() = cœur du filtre JWT
    - appelé une fois par requête
    - lit le header Authorization
    - extrait et valide le token
    - récupère l’email
    - charge l’utilisateur
    - crée Authentication
    - remplit SecurityContext
    - laisse la requête continuer

4. ``SecurityContext``

C’est l’endroit où Spring Security stocke l’utilisateur actuellement authentifié.

Il contient :
- l’objet ``Authentication`` qui contient :
    - le principal (le ``User``)
    - les rôles (authorities)
    - l’état “authentifié”

Sans ``SecurityContext``, Spring ne sait pas :
- qui est connecté
- quels rôles il a
- s’il peut accéder à une route protégée

Il est géré automatiquement par Spring Security.

À chaque requête :
- Spring crée un ``SecurityContext`` vide
- Ton JwtAuthenticationFilter peut y mettre un ``Authentication``
- Les autres filtres et contrôleurs peuvent le lire
- À la fin de la requête, Spring le nettoie

Donc il vit uniquement pendant la requête HTTP.

Il est stocké dans une classe utilitaire statique : ``SecurityContextHolder``

on y accède via

````java
SecurityContextHolder.getContext()
````

- Résumé ultra court : 
- SecurityContext = stockage de l’utilisateur authentifié pour la requête en cours.
- SecurityContextHolder = accès global au SecurityContext.
- getAuthentication() = renvoie l’utilisateur actuel (ou null si personne).
- On vérifie getAuthentication() == null pour éviter d’écraser une authentification existante.
- setAuthentication() = on dit à Spring “cet utilisateur est connecté”.

## Mise a jour ``SecurityConfig.java``

1. ``SessionCreationPolicy.STATELESS``

````java
.sessionManagement(session -> 
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
````

On dit à Spring :
- Ne crée jamais de session serveur.
- L’utilisateur doit être authentifié à chaque requête via le token.”

C’est **obligatoire** pour une API JWT.

2. Ajouter le filtre JWT

````java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
````

On veut lire le token avant que Spring décide si la requête est authentifiée.