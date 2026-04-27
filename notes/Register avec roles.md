# Gestion des rôles avec clé composite (User / Role / UserRole)

1. Tables utilisées

- user
- role
- user_role (table de liaison)
- user_role = clé composite : (id_user, id_role)

2. Entités Java

- User
    - contient : idUser, username, email, password, isActive, createdAt, updatedAt
    - relation : @OneToMany(mappedBy="user") vers UserRole

- Role
    - contient : idRole, roleName
    - relation : @OneToMany(mappedBy="role") vers UserRole

- UserRoleId
classe clé composite
contient : Integer userId, Integer roleId
annotée avec @Embeddable

- UserRole
    -contient : @EmbeddedId UserRoleId id
    - relations :
        - @ManyToOne @MapsId("userId") → User
        - @ManyToOne @MapsId("roleId") → Role

    - contient aussi : createdAt, updatedAt

3. Repositories

-UserRepository
    → gère la table user

- RoleRepository
    → gère la table role

UserRoleRepository

```java
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {}
```

4. Injection dans UserService (par constructeur)

```java
private final UserRepository userRepository;
private final RoleRepository roleRepository;
private final UserRoleRepository userRoleRepository;
private final PasswordEncoder passwordEncoder;
```

5. Étapes du REGISTER (ordre obligatoire)

    1. Vérifier email

    ```java
    if (userRepository.existsByEmail(dto.email())) throw ...
    ```

    2. Récupérer le rôle “user”

    ```java
    Role roleUser = roleRepository.findByRoleName("user").orElseThrow();
    ```

    3. Créer User

        ```java
        User user = new User();
        user.setUsername(...);
        user.setEmail(...);
        user.setPassword(...);
        user.setCreatedAt(...);
        user.setUpdatedAt(...);
        ```

    4. Sauvegarder User
        → pour obtenir son ID

        ```java
        User saved = userRepository.save(user);
    ```

    5. Créer UserRole

    ```java
    UserRole userRole = new UserRole();
    userRole.getId().setUserId(saved.getIdUser());
    userRole.getId().setRoleId(roleUser.getIdRole());
    userRole.setUser(saved);
    userRole.setRole(roleUser);
    userRole.setCreatedAt(...);
    userRole.setUpdatedAt(...);
    ```
    6. Sauvegarder UserRole

    ```java
    userRoleRepository.save(userRole);
    ```

    7. (Optionnel) Ajouter dans la liste du user

    ```java
    saved.getUserRoles().add(userRole);
    ```

    8. Retourner DTO

    ```java
    return UserMapper.toFullDto(saved);*
    ```

6. Pourquoi cet ordre ?

    - clé composite = besoin de l’ID du user → donc save user d’abord
    - éviter erreur Hibernate “different object with same identifier”
    - éviter erreur “transient value”
    - éviter erreur “not-null property”

7. Exemple minimal de UserRoleId

    ```java
    @Embeddable
    public class UserRoleId implements Serializable {
        private Integer userId;
        private Integer roleId;
    }
    ```

8. Exemple minimal de UserRole

    ```java
    @Entity
    @Table(name="user_role")
    public class UserRole {

        @EmbeddedId
        private UserRoleId id = new UserRoleId();

        @ManyToOne
        @MapsId("userId")
        @JoinColumn(name="id_user")
        private User user;

        @ManyToOne
        @MapsId("roleId")
        @JoinColumn(name="id_role")
        private Role role;

        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
    }
    ```