# Ajouter JWT au Login (Spring Security 6)

1. Concept JWT (ultra simple)

- Token signé → prouve l’identité de l’utilisateur
- Contient : id, email, rôles, date d’expiration
- Le front le stocke (localStorage ou cookie)
- Le front l’envoie dans Authorization: Bearer <token>
- Le backend vérifie la signature → autorise ou refuse

2. Bonnes pratiques

- Ne jamais stocker le mot de passe dans le token
- Ajouter id + email + roles
- Expiration courte (ex : 15 minutes)
- Utiliser une clé secrète dans ``application.yamal`` via ``.env``
- Utiliser une classe dédiée : ``JwtService`` (dossier /security/JwtService.java)
- Le login renvoie :
    - user DTO
    - token JWT

3. Fichiers à créer

- JwtService
    génère et valide les tokens

- JwtAuthenticationFilter
    intercepte les requêtes, lit le token, charge l’utilisateur

- SecurityConfig
    configure Spring Security (routes publiques / privées)

4. Exemple minimal — JwtService

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("id", user.getIdUser())
            .claim("roles", user.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 min
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }
}
```
- Le subject (sub) est l’info principale du token.
    - Ici, on choisit de mettre l’email de l’utilisateur.
    - On aurait pu mettre l’ID, mais l’email est souvent pratique.
- Les claims personnalisés
    - les roles c'est une List de roles donc stream() > map() > toList()
- Les dates
    - setIssuedAt(now) : ajoute le champ iat (issued at).
    - setExpiration(expiry) : ajoute le champ exp.
- La signature
    - secret.getBytes() : on convertit la clé secrète (String) en tableau de bytes.
    - Keys.hmacShaKeyFor(...) : JJWT construit une clé HMAC à partir de ces bytes.
        - Ça vérifie aussi que la clé est assez longue pour l’algorithme choisi.
    - SignatureAlgorithm.HS256 : on signe en HMAC-SHA256 (classique pour JWT).

Avec ce code, le JWT contiendra :
- Standard claims :
    - sub → user.getEmail()
    - iat → date de création
    - exp → date d’expiration
- Custom claims :
    - id → user.getIdUser()
    - roles → liste des rôles de l’utilisateur

- Signé avec ta clé secrète, en HS256.

5. Modifier le login pour renvoyer un token

Version avec token :

```java
public AuthResponse login( LoginDto dto ) {
        // Cherche le user avec le email d'entrée
        User user = userRepository.findByEmail(dto.email())
           .orElseThrow(()-> new RuntimeException("Email incorrecte"));

        // Vérifie le mot de pass
        if(!passwordEncoder.matches(dto.password(),user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrecte");
        }

        // Génère le token
        String token = jwtService.generateToken(user);

        return new AuthResponse(
            token,
            UserMapper.toFullDto(user)
        );
    }
```
On renvoi plus un UserFullDto mais un autre DTO AutResponse

```java
    public record AuthResponse (
        String token,
        UserFullDto user
    ) {}
```

🟦 9) Ce qu’il reste à faire après
Ajouter un filter JWT

Ajouter une SecurityConfig

Protéger les routes :

/auth/** → public

/admin/** → admin

/user/** → user