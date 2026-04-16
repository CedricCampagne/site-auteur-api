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

Dans config/SecurityConfig.java :

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

## Étape 2 : LOGIN

1. Objectif du login

- Vérifier email + password
- Si OK → générer un JWT
- Retourner { token, user }
- Le front stocke le token (localStorage)
- Le token sera utilisé pour accéder aux routes protégées

2. DTO nécessaires

- LoginDto

```java
public record LoginDto(
    String email,
    String password
) {}
```

- AuthResponseDto

```java
public record AuthResponseDto(
    String token,
    UserFullDto user
) {}
```

3. Méthodes nécessaires dans UserRepository

```java
Optional<User> findByEmail(String email);
```

- Optional = peut contenir un User ou être vide.
- évite les null dangereux.

4. Étapes du LOGIN dans UserService

