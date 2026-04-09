# API site-auteur

commande pour faire tourner ton application Spring Boot directement depuis Maven, donc elle lance ton backend sur le port configuré (par défaut ``8080``) :

```bash
./mvnw spring-boot:run
```

## Configuration du projet Spring Boot

1. Étapes à suivre sur Spring Initializr
- Project → Maven
- Language → Java
- Spring Boot → 3.5.13 (stable)
- Project Metadata
    - Group → com.cedric
    - Artifact → site-auteur-api
    - Package name → com.cedric.site-auteur-api
- Packaging → Jar
-Java → 21

2. Dependencies à ajouter
Add Dependencies :
- Spring Web → pour exposer tes endpoints REST
- Spring Data JPA → pour accéder à ta base PostgreSQL facilement
- PostgreSQL Driver → pour se connecter à ta BDD
- Lombok (optionnel, mais pratique pour réduire le code boilerplate)

## Configuration des variables d'environnement

- Créer un ``.env`` a la racine du projet
- Ajoute ``dotenv-java`` pour lire le ``.env`` ( dans le ficiher ``pom.xml``) :

    ```xml
    <!-- Dotenv pour variables d'environnement -->
        <dependency>
            <groupId>io.github.cdimascio</groupId>
            <artifactId>java-dotenv</artifactId>
            <version>5.3.3</version>
        </dependency>
    ```

- ``.env`` : 

    ```bash
    DB_URL=jdbc:postgresql://localhost:5432/dn_name
    DB_USER=db_user
    DB_PASSWORD=db_password
    ```

- Classe principale ``SiteAuteurApiApplication.java`` : 

Pour charger les variables d'environnement : 

- importer dotenv :

    ```java
    import io.github.cdimascio.dotenv.Dotenv;
    ```

- Lecture du ``.env`` : 

    ```java
    Dotenv dotenv = Dotenv.configure()
        .filename(".env")
        .ignoreIfMissing()
        .load();
    ```

    - ``Dotenv.configure()`` : configure le lecteur ``.env``.
    - ``.filename(".env")`` : indique le fichier à lire (ici ``.env`` à la racine du projet).
    - ``.ignoreIfMissing()`` : évite de crasher si le fichier ``.env`` n’existe pas.
    - ``.load()`` : charge les variables dans la mémoire, prêtes à être utilisées.

- Mettre les variables en system properties

    ```java
    System.setProperty("DB_URL", dotenv.get("DB_URL"));
    System.setProperty("DB_USER", dotenv.get("DB_USER"));
    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    ```

    - Spring Boot peut lire les variables système comme source de configuration.
    - Ici, on transforme les variables du .env en variables système pour que Spring Boot puisse faire :
        
        ```yaml
        spring:
        datasource:
            url: ${DB_URL}
            username: ${DB_USER}
            password: ${DB_PASSWORD}
        ```

## Configuration de PostgreSQL dans Spring Boot

modifications dans le ficiher : ``src/main/resources/application.yml``

```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nom_de_ta_bdd
    username: ton_user # ${DB_USERNAME:postgres} "postgres" par défaut si la variable n'est pas définie
    password: ton_mdp # ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
```

- datasource:
    - datasource = la source de données = la base de données.
    - Spring Boot sait grâce à ça où se connecter.

    - url: ``jdbc:postgresql://localhost:5432/nom_de_ta_bdd``
        - jdbc:postgresql → le protocole pour PostgreSQL
        - localhost → ton ordinateur local (si ta BDD est sur ton PC)
        - 5432 → port par défaut de PostgreSQL
        - nom_de_ta_bdd → remplace par le nom réel de la base
    
    - username: ``ton_user``
        - Identifiant PostgreSQL pour te connecter à la base
        - Exemple : postgres ou celui que tu utilises pour ta BDD
    
    - password: ``ton_mdp``
        - Mot de passe correspondant au username
        - Exemple : password123
    - ``driver-class-name`` : Spring Boot pourra créer le datasource et se connecter à PostgreSQL.
- jpa : 
    - JPA = **Java Persistence API**, c’est ce qui permet de **mapper les tables SQL en classes Java** automatiquement.
    - Spring Boot utilise **Hibernate** comme implémentation par défaut.

    - ``hibernate``:
        - Hibernate est le moteur qui fait le lien entre Java et SQL.
        - Ici, tu peux lui donner des instructions spécifiques.*

        - ``ddl-auto: none``
            - DDL = Data Definition Language (création/modification de tables)
            - Options possibles :
                - create → recrée toutes les tables à chaque lancement
                - update → met à jour les tables selon tes entities
                - validate → vérifie si les tables correspondent aux entities
                - none → ne touche pas à la BDD
            - Ici on met none parce que la BDD existe déjà et on ne veut rien
    - ``show-sql: true``
        - Affiche dans la console toutes les requêtes SQL générées par Hibernate
        - Pratique pour debugger et voir ce qui se passe en coulisses

- Résumé

|                            |                                             |
|---------------------------------|------------------------------------------------------|
| `spring.datasource.url`         | Où se connecter (BDD)                                |
| `spring.datasource.username`    | Nom d’utilisateur PostgreSQL                        |
| `spring.datasource.password`    | Mot de passe PostgreSQL                               |
| `spring.jpa.hibernate.ddl-auto` | Ne touche pas aux tables (`none`)                   |
| `spring.jpa.show-sql`           | Affiche les requêtes SQL dans la console            |

## Création des entité JPA (Java Persistence API)

- Création dossier entity : 

```bash
D:\cedric\prog\code\site-auteur-api\src\main\java\com\cedric\site_auteur_api\
│
├── SiteAuteurApiApplication.java        # Main class
├── entity\                              # <-- tes modèles JPA (tables)
│   └── Auteur.java
```

- Les imports :

```java
package com.cedric.site_auteur_api.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
```

- ``package ...entity`` → On met toutes nos entités JPA dans un package dédié.
- jakarta.persistence.* → Contient toutes les annotations JPA (``@Entity, @Id, @Column,`` etc.).
- java.time.ZonedDateTime → Type Java pour les timestamps avec fuseau horaire, qui correspond au timestamp with time zone de PostgreSQL.

- Déclaration de l’entité : 

    - ``@Entity`` → Dit à Spring/Hibernate que cette classe représente **une table de la BDD**.
    - ``@Table(name = "book")`` → Indique le nom exact de la table en BDD. Si on ne met pas @Table, Hibernate utilise le nom de la classe (Book) par défaut.

    ```java
    @Entity
    @Table(name = "book")
    public class Book {...}
    ```

- Clé primaire et génération automatique :

    - ``@Id`` → Champ clé primaire (``PRIMARY KEY``).
    - ``@GeneratedValue(strategy = GenerationType.IDENTITY)`` → La BDD gère l’incrément automatique (nextval('book_id_book_seq') dans PostgreSQL).
    - ``@Column(name = "id_book")`` → Le nom exact dans la BDD (différent du nom Java idBook).
    - ``private Integer idBook`` :
        - C’est le champ dans la classe Java.
        - private → visible seulement dans la classe, donc on utilise des getters/setters pour y accéder.
        - Integer → type Java qui correspond à integer dans PostgreSQL.
        - idBook → nom utilisé dans le code Java pour manipuler l’objet (tjs CamelCase).


- Champs simples avec contraintes : 

    - nullable = false → Correspond à NOT NULL dans la BDD.
    - unique = true → Correspond à UNIQUE dans la BDD.
    - length = 255 → Limite de caractères pour les champs varchar(255).

    ```java
    @Column(nullable = false, unique = true, length = 255)
    private String title;
    ```
    
    - ``columnDefinition = "TEXT"`` → permet de forcer le type SQL exact pour la colonne.

        En Java, le type est String, mais PostgreSQL a plusieurs types pour du texte :  
            varchar(n) → taille limitée  
            text → texte illimité (ou très grand)  


    ```java
    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;
    ```

- Champs dates :

    - Correspond à timestamp with time zone de PostgreSQL.
    - On utilise ZonedDateTime pour gérer le fuseau horaire, c’est plus sûr que LocalDateTime si l’appli peut être utilisée à l’international.

    - pour createdAt et updatedAt : 

        - Hibernate fait la même chose que Sequelize : auto-remplit à la création et à chaque update.
        - Contrairement à Sequelize, si on n’ajoute pas les champs dans ton entity JPA, ils ne seront pas gérés automatiquement.

- Getters et Setters : 

    Deux facons de faire :

    - Manuellement pour chaque champ un getter et un setter :

        ```java
        public Integer getIdBook() {
            return idBook;
        }
        public void setIdBook(Integer idBook) {
            this.idBook = idBook;
        }
        ```

    - En auto avec dependance ``Lombok`` : 

        - Plus besoin d’écrire tous les getters et setters à la main.
        - ``@NoArgsConstructor`` → constructeur vide requis par JPA.
        - ``@AllArgsConstructor`` → constructeur avec tous les champs si tu en as besoin.

        ```java
        import lombok.Getter;
        import lombok.Setter;
        import lombok.NoArgsConstructor;
        import lombok.AllArgsConstructor;

        import javax.persistence.*;
        import java.time.ZonedDateTime;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Entity
        @Table(name = "book")
        public class Book {
            ...
        }
        ```

- Méthodes utilitaires (optionnel)

    - ``@PrePersist`` : exécuté avant l’INSERT.

        ```java
        @PrePersist
        // méthode perso a exécuté avant l'insert
        protected void onCreate() {
            createdAt = updatedAt = ZonedDateTime.now(); // définit les dates lors de la création
            if (isActive == null) isActive = true;  // active par défaut si pas défini
            generateSlug();      
        }
        ```

        On initialise automatiquement createdAt, updatedAt et isActive.

    - ``@PreUpdate`` : exécuté avant l’UPDATE.

        ```java
        @PreUpdate
            protected void onUpdate() {
            updatedAt = ZonedDateTime.now();
            generateSlug(); // régénère le slug si le titre change
        }
        ```

## Création des Repository

1. Concept du Repository en Spring Data JPA

Un repository est une interface qui permet d’accéder à ta base de données sans écrire de SQL.

- L’équivalent en **Sequelize** serait : ``Book.findAll(), Book.create(), Book.destroy()``, etc.
- Spring Data JPA fournit ces méthodes automatiquement grâce à l’interface ``JpaRepository``

```java
@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {}
```

- Book : l’entité manipulée (la classe Book.java).
- Integer : le type de la clé primaire (idBook).

Accès à : 

| Méthode          | Équiv Sequelize                  | Description                 |
| ---------------- | -------------------------------- | --------------------------- |
| `findAll()`      | `Book.findAll()`                 | Récupère tous les livres    |
| `findById(id)`   | `Book.findByPk(id)`              | Récupère un livre par id    |
| `save(book)`     | `Book.create()` ou `book.save()` | Crée ou met à jour un livre |
| `deleteById(id)` | `Book.destroy({ where: { id }})` | Supprime un livre par id    |   


2. Ajouter des méthodes “custom”

Si on veut filtrer ou chercher autrement, on peut définir des méthodes dans l’interface.

exemple: chercher un livre par ``slug``

```java
Book findBySlug(String slug);
```

- Spring génère automatiquement le SQL correspondant.
- Pas besoin d’implémenter la méthode.

autres exemples :

```java
List<Book> findByGenre(String genre);
List<Book> findByIsActiveTrue();
```

- ``findByGenre`` → SELECT * FROM book WHERE genre = ?
- ``findByIsActiveTrue`` → SELECT * FROM book WHERE is_active = true

3. Où placer le repository dans un projet

Par bonnes pratiques :

```bash
src/main/java/com/cedric/site_auteur_api/
├─ entity/        → Book.java
├─ repository/    → BookRepository.java   <- ici
├─ service/       → BookService.java
├─ controller/    → BookController.java
```

**package** ``repository`` : toutes les interfaces qui accèdent aux entités.

4. Les imports "standars" d'un **repository Spring Data JPA**

- ``import com.cedric.site_auteur_api.entity.Book;``
    - importer **l’entité** que le repository va gérer.
    - Exemple : Ici, c’est **Book.java**.

- ``import org.springframework.data.jpa.repository.JpaRepository;``
    - ``JpaRepository`` est l’interface de **base fournie par Spring Data JPA.**
    - Elle contient toutes les méthodes CRUD classiques (``findAll()``, ``findById()``, ``save()``, ``deleteById()``).

- ``import org.springframework.stereotype.Repository;``
    - Permet à Spring de **scanner et enregistrer ton repository comme un bean.**
    - On peut techniquement le supprimer et ça marchera toujours avec Spring Data JPA, mais c’est une bonne pratique de le mettre.

- ``import java.util.List;``
    - Seulement nécessaire si on retourne une ``List`` dans une méthode custom, comme ``findByIsActiveTrue()``.

- Résumé pratique :

```java
import ton.entité;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // si nécessaire
```

## Création des Services

1. Le rôle du service

Un service contient la logique métier de l’application :  
il exécute les règles, traite les données et sert d’intermédiaire entre le controller et le repository.

```java
@Service
public class BookService {
    ...
}
```

- Le service : 
    - utilise le repository et ses méthodes
    - appelle la BDD (via repository)
    - peut ajouter de la logique métier
    - sert d’intermédiaire entre controller et DB

2. Les imports

- ``Book`` : ton entité
- ``BookRepository`` : accès DB
- @``Service`` : Spring va créer un bean automatiquement
- ``List`` : pour retourner plusieurs livres

```java
import com.cedric.site_auteur_api.entity.Book;
import com.cedric.site_auteur_api.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;
```

3. Annotation @Service

```java
@Service
public class BookService {
    
}
```

Dit à Spring : "Cette classe contient de la logique métier : gère-la automatiquement"  
Spring pourra l’injecter dans le controller plus tard.

4. Injection du repository

- ``private final BookRepository bookRepository;`` : 
    - ``private`` : accessible seulement dans cette classe
    - ``BookRepository`` : type de la variable
    - ``bookRepository`` : nom de la variable
    - ``final`` : ne peut pas être modifiée après initialisation

- Le constructeur :

appelé quand Spring crée ton service.

```java
public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
}
```

- Ce que fait Spring derrière :
    - Il voit @Service
    - Il veut créer BookService
    - Il regarde le constructeur :
        - "ok il faut un BookRepository"
    - Il trouve un bean BookRepository
    - Il l’injecte automatiquement

5. ajout logique métier / validation / erreurs

exemple getAllBooks : 

```java
// Récupérer tous les livres
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new NoSuchElementException("Aucun livre trouvé");
        }
        return books;
    }
```

## Création des Controllers

1. Rôle du Controller

Le controller est le point d’entrée des requêtes HTTP :
- Il **reçoit la requête** venant du client (front, Postman, navigateur…).
- Il **appelle le service** pour exécuter la logique métier (par exemple récupérer un livre, créer un livre…).
- Il **renvoie la réponse** au client, généralement en JSON.

En Spring Boot :
- ``@RestController`` : indique que cette classe expose des endpoints REST et que toutes les réponses sont JSON automatiquement.
- ``@RequestMapping("/books")`` : définit la route de base pour tous les endpoints de cette ressource.
- Les méthodes du controller utilisent des annotations comme ``@GetMapping, @PostMapping, @PutMapping, @DeleteMapping`` pour mapper les requêtes HTTP.

2. Les imports

- import com.cedric.site_auteur_api.entity.Book; : entité
- import com.cedric.site_auteur_api.entity.Book; : service
- import com.cedric.site_auteur_api.service.BookService;
    - import org.springframework.web.bind.annotation.*;

    | Annotation        | Rôle                                                                                        |
    | ----------------- | ------------------------------------------------------------------------------------------- |
    | `@RestController` | Transforme la classe en controller REST et toutes les réponses seront JSON automatiquement. |
    | `@RequestMapping` | Définit la route de base de la ressource (`/books`).                                        |
    | `@GetMapping`     | Mappe les requêtes HTTP GET à une méthode.                                                  |
    | `@PostMapping`    | Mappe les requêtes HTTP POST à une méthode.                                                 |
    | `@PutMapping`     | Mappe les requêtes HTTP PUT à une méthode.                                                  |
    | `@DeleteMapping`  | Mappe les requêtes HTTP DELETE à une méthode.                                               |
    | `@PathVariable`   | Lie un segment de l’URL à un paramètre de méthode.                                          |
    | `@RequestBody`    | Lie le corps JSON de la requête à un objet Java.                                            |

3. Annotation @RestController

4. Injection du Service

- ``private final BookService bookService;`` : 
    - ``private`` : accessible seulement dans cette classe
    - ``BookService`` : type de la variable
    - ``bookService`` : nom de la variable
    - ``final`` : ne peut pas être modifiée après initialisation

- Le constructeur :

appelé quand Spring crée ton service.

```java
public BookService(BookService bookService) {
    this.bookService = bookService;
}
```

- Ce que fait Spring derrière :
    - Il voit @RestController
    - Il veut créer BookController
    - Il regarde le constructeur :
        - "ok il faut un BookService"
    - Il trouve un bean BookService(annoté @BookService)
    - Il l’injecte automatiquement
    - Le controller peut maintenant appeler toutes les méthodes du service, comme ``bookService.getBookBySlug(slug)``.

5. Explications détaillées

- **``@RestController``**
    - Combinaison de ``@Controller + @ResponseBody``.
    - Tout ce que tu retournes sera converti en **JSON** automatiquement.
-** ``@RequestMapping("/books")``**
    - Définit la route de base /books.
    - Tous les endpoints dans cette classe commencent par /books.
- **``@GetMapping, @PutMapping, @DeleteMapping``**
    - Correspond aux méthodes HTTP GET, PUT, DELETE.
    - ``@GetMapping("/{slug}")`` → capture le paramètre ``slug`` depuis l’URL.
-** ``@PathVariable``**
    - Lie un segment de l’URL à une variable Java.
    - Exemple : ``/books/my-slug`` → ``slug = "my-slug``".
- **``@RequestBody``**
    - Lie le corps de la requête HTTP à un objet Java.
    - Utile pour PUT/POST pour recevoir les données JSON du front.
- **Appel au service**
    - Le controller **ne touche jamais la base** directement.
    - Il délègue toute la logique métier au BookService.
- **Retour**
    - Spring Boot utilise Jackson pour transformer automatiquement ton Book en JSON.

## Gestion des erreurs

### Les execptions :

Une exception est un événement anormal qui interrompt le déroulement normal du programme.

- Exemples :
    - un élément non trouvé → NoSuchElementException
    - une valeur nulle → NullPointerException
    - une erreur SQL → SQLException

En Java, quand une exception est lancée (throw), l’exécution de la méthode s’arrête immédiatement.

- Le service lance les exceptions : 

    Dans un service, tu utilises les exceptions pour :

    - signaler qu’une ressource n’existe pas
    - empêcher une mise à jour ou suppression invalide
    - éviter de renvoyer null
    - faire remonter l’erreur au controller   

- Spring Boot ne renvoie pas automatiquement une bonne réponse HTTP : 

    Actuellement, si ton service lance une exception :

    - Spring renvoie une erreur 500 Internal Server Error  
    - Ce n’est pas ce qu’on veut pour un “livre non trouvé”

- La bonne pratique : créer un Global Exception Handler

C’est une classe annotée avec :

```java
@RestControllerAdvice
```

Elle a pour but d'intercepter toutes les exceptions et de renvoyer une réponse HTTP propre.

### Global Exception Handler

- Ou le placer dans le projet : 

```bash
src/main/java/com/cedric/site_auteur_api/
├─ exception/
```

- les imports : 

    - HttpStatus : enum des codes HTTP (404, 500, etc.).
    - ResponseEntity : objet qui représente une réponse HTTP complète (status + body).
    - @ExceptionHandler : annotation pour dire “cette méthode gère tel type d’exception”.
    - @RestControllerAdvice : permet de créer un handler global pour tous les controllers REST.
    - LocalDateTime : pour mettre un timestamp dans la réponse.
    - NoSuchElementException : exception que tu lances dans ton service.

    ```java
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;

    import java.time.LocalDateTime;
    import java.util.NoSuchElementException;
    ```

- annotation de la classe ``@RestControllerAdvice`` : 

    @RestControllerAdvice dit à Spring :
    “Cette classe va intercepter les exceptions qui sortent les controllers REST et renvoyer des réponses HTTP à la place.”

    **C’est global** : ça marche pour **tous les controllers**, sans rien configurer de plus.

    ```java
    @RestControllerAdvice
    public class GlobalExceptionHandler {
        ...
    }
    ```

- Le ``record ErrorResponse``

    ```java
    private record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
    ) {}

    ```
    On définis ici un type de réponse d’erreur.

    - ``record`` = une sorte de mini classe immuable avec :
        - des champs
        - un constructeur
        - ``equals``, ``hashCode``, ``toString`` générés automatiquement

    - Les champs :
        - ``timestamp`` : quand l’erreur s’est produite
        - ``status`` : le code HTTP (404, 500…)
        - ``error`` : le texte du statut (ex : "Not Found")
        - ``message`` : le message d’erreur détaillé (ex : "Livre non trouvé")

    Ce ``record`` sera converti en JSON automatiquement par Spring (via Jackson).`

- Handler pour ``NoSuchElementException`` : 404

    ```java
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    ```

    - ``@ExceptionHandler(NoSuchElementException.class)``  
        - Si une ``NoSuchElementException`` est lancée quelque part dans un controller ou un service, appelle cette méthode

    - Paramètre ``NoSuchElementException ex``  
        - récupères l’exception pour lire son message (``ex.getMessage()``).

    - Tu construis un ``ErrorResponse`` avec :
        - ``LocalDateTime.now()`` : timestamp actuel
        - ``HttpStatus.NOT_FOUND.value()`` : 404
        - ``"Not Found"`` : texte du statut
        - ``ex.getMessage()`` : le message métier (ex : "Livre non trouvé avec l'ID : 3")

    - Tu renvoies :

    ```java
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    ```

    - Réponse HTTP :
        - Status : 404
        - Body : JSON avec timestamp, status, error, message
- Handler générique → 500

```java
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
```

Ici on gère **toutes les autres exceptions** qui ne sont pas ``NoSuchElementException``.

- @ExceptionHandler(Exception.class)  
    - Pour toute exception non gérée ailleurs, utilise cette méthode.

- Tu renvoies :
    - Status : 500
    - Error : ``"Internal Server Error"``
    - Message : ``ex.getMessage()`` (souvent technique)