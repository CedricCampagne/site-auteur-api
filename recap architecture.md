# Architecture propre d’un backend Spring Boot (mon cas)

1. **Entités (domain model)**

Ce sont les classes qui représentent tes tables SQL.

On a :
- Book
- Chronicle
- Comment
- Role
- User
- UserRole (table de liaison)
- UserRoleId (clé composite)

**Elles correspondent 1:1 à la base de données.**
**Elles ne sortent jamais directement au front.**

2. DTO (Data Transfer Objects)

Chaque ressource a ses DTO adaptés à l’usage

Exemples :
- UserDto → version simple
- UserFullDto → version enrichie (avec roles)
- UserCreateDto → pour POST
- UserUpdateDto → pour PUT/PATCH

Idem pour Chronicle, Comment, Book, etc.

**Les DTO sont ce que tu exposes au front.**  
**Tu contrôles exactement ce que tu envoies.**

3. Mapper

Chaque entité a un mapper :
- UserMapper
- RoleMapper
- ChronicleMapper
- CommentMapper
etc.

Le mapper :
- convertit **Entity → DTO**
- convertit **DTO → Entity (si besoin)**

**Le service ne manipule jamais les entités brutes pour les renvoyer.**

4. Repository

On utilise Spring Data JPA :
- UserRepository
- RoleRepository
- ChronicleRepository
- CommentRepository
- UserRoleRepository

Avec :
- méthodes automatiques (findById, findAll, save, etc.)
- méthodes custom si besoin (findByChronicleId, etc.)

**Le repository ne fait que parler à la base.**

5. Service

Le service :
- utilise les repositories
- applique la logique métier
- utilise les mappers
- renvoie des DTO, jamais des entités

Exemple :

```java
public UserFullDto getUserById(Integer id) {
    User user = userRepository.findById(id)
        .orElseThrow(...);

    return UserMapper.toFullDto(user);
}
```

**Le service est le cœur de ton application.**

6. Controller

Le controller :
expose les endpoints REST
reçoit les DTO du front
renvoie les DTO au front
appelle le service

Exemple :

```java
@GetMapping("/{id}")
public UserFullDto getUserById(@PathVariable Integer id) {
    return userService.getUserById(id);
}
```

**Le controller ne contient aucune logique métier.**