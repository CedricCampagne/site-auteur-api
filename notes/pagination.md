# Pagination

## 1. DTO de pagination

record comme pour les autres DTO :
- immuable par défaut
- concis
- lisible
- parfait pour représenter une réponse API
- cohérent avec le reste de ton code

## 2. Contrat de DTO de pagination

structure standard API pro :

````java
{
  "items": [ ... ],
  "page": 0,
  "size": 10,
  "totalItems": 123,
  "totalPages": 13,
  "hasNext": true,
  "hasPrevious": false
}
````

- ``items`` : C’est la liste des chroniques de la page demandée.

    Exemple : Tu demandes page=0 et size=10 => tu reçois les 10 premières chroniques.

    C’est Spring qui te donne ça via :

    ````java
    page.getContent()
    ````

- ``page`` : C’est le numéro de la page actuelle, en 0-based.

    Donc :
    - 0 => première page
    - 1 => deuxième page
    - 2 => troisième page

    Exemple : Tu demandes /chronicles?page=0&size=10 => page = 0.

- ``size`` : C’est le nombre d’éléments par page.

    Tu le choisis dans la requête :  
    - size=10 => 10 chroniques par page
    - size=20 => 20 chroniques par page

    Exemple : /chronicles?page=0&size=10 => size = 10.

- ``totalItems`` : C’est le nombre total de chroniques en base.

    Exemple : On a 123 chroniques dans ta table => totalItems = 123.

    Spring te donne ça via :

    ````java
    page.getTotalElements()
    ````

- ``totalPages`` : C’est le nombre total de pages, calculé automatiquement par Spring.

    Formule :

    ````java
    totalPages = ceil(totalItems / size)
    ````

- ``hasNext`` : Indique si une page suivante existe.

    Exemple :
    - Tu es sur page = 0 => oui, il y a une page suivante => hasNext = true
    - Tu es sur page = 12 (la dernière) => hasNext = false

    Spring te donne ça via :

    ````java
    page.hasNext()
    ````

- ``hasPrevious`` : Indique si une page précédente existe.

    Exemple :
    - Tu es sur page = 0 => pas de page précédente => hasPrevious = false
    - Tu es sur page = 1 => oui => hasPrevious = true

    Spring te donne ça via :

    `````java
    page.hasPrevious()
    `````

### Résumé

| Clé          | Signification                     | Exemple        |
|--------------|-----------------------------------|----------------|
| items        | Les chroniques de la page         | 10 chroniques |
| page         | Numéro de page (0-based)          | 0              |
| size         | Nombre d’éléments par page        | 10             |
| totalItems   | Nombre total de chroniques        | 123            |
| totalPages   | Nombre total de pages             | 13             |
| hasNext      | Page suivante disponible ?        | true           |
| hasPrevious  | Page précédente disponible ?      | false          |


## 3. Contrat en record JAVA

````java
public record PageResponse<T>(
    List<T> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {}
````

Ce record est :
- générique
- réutilisable pour toutes tes entités
- immuable
- propre
- pro

## 4. Comment Spring va remplir ce DTO 

Spring Data JPA te renvoie un objet Page<Chronicle> quand tu fais une requête paginée.

Cet objet contient déjà :
- getContent() => les entités de la page
- getNumber() => numéro de page
- getSize() => taille
- getTotalElements()
- getTotalPages()
- hasNext()
- hasPrevious()

Donc ton service fera simplement :
- demander un Page<Chronicle> au repository
- mapper getContent() en List<ChronicleDto>
- construire un PageResponse<ChronicleDto> avec les infos du Page

C’est simple, propre, efficace.

## 5. Avant de coder question ?

- Tri par défaut
    - createdAt DESC (les plus récentes d’abord)
    - title ASC
    - idChronicle DESC

    Le tri par défaut est important pour que la pagination soit cohérente:
    - c’est ce que les utilisateurs attendent naturellement les contenus récents sont plus pertinents
    - c’est le tri utilisé par la majorité des plateformes (blogs, news, réseaux sociaux)
    - **ça évite que l’ordre change quand tu ajoutes une nouvelle chronique**, ce qui est essentiel pour une pagination stable.


## Code ordre logique

1. ``PageResponse<T>`` est la structure de pagination => c’est le “contenant”

2. ``ChronicleListDto`` est le type qu'on va mettre dedans => c’est le “contenu”

### ``PageResponse<T>``

standard pro:
- Générique ``<T>`` => tu peux l’utiliser pour ``ChronicleListDto``, ``CommentDto``, ``BookDto``, etc.
- Immuable => record = DTO propre, sans effet de bord
- Nom des champs => ``items``, ``page``, ``size``, ``totalItems``, ``totalPages`` => ultra standard

Lisible côté front => facile à consommer dans n’importe quel framework (Svelte, React, Vue…)

## Modification du service (ici ChronicleService)

1. Le service doit :
- recevoir ``page`` et ``size``
- créer un ``Pageable`` avec tri ``createdAt DESC``
- demander au repository une page de chroniques
- mapper les entités => ``ChronicleListDto``
- construire un ``PageResponse<ChronicleListDto>``
- renvoyer ça au controller

2. Le rôle du ``Pageable``

pring fournit un objet Pageable qui décrit :
la page demandée
la taille
le tri

Dans mon cas :
page = 0
size = 10
sort = createdAt DESC

Donc le service devra créer :

````java
Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
````

**C’est ce qui dit à Spring : “donne-moi les chroniques les plus récentes, page par page”.**

3. Le repository renvoie un Page<Chronicle>

Le repository hérite de JpaRepository, donc il a déjà :

````java
Page<Chronicle> findAll(Pageable pageable);
````

Ce Page<Chronicle> contient :
- getContent() => les chroniques de la page
- getTotalElements() => totalItems
- getTotalPages() => totalPages
- getNumber() => page
- getSize() => size
- hasNext()
- hasPrevious()

**Tout ce qu’il faut pour remplir ton PageResponse<T>.**

4. Mapper les entités vers ChronicleListDto

On va faire :

````java
List<ChronicleListDto> dtos = page.getContent()
    .stream()
    .map(ChronicleMapper::toListDto)
    .toList();
````

Donc il te faudra un mapper toListDto().

5. Construire le PageResponse<ChronicleListDto>

Une fois qu'on a :
- la liste des DTO
- les infos du Page

On construit :

````java
return new PageResponse<>(
    dtos,
    page.getNumber(),
    page.getSize(),
    page.getTotalElements(),
    page.getTotalPages(),
    page.hasNext(),
    page.hasPrevious()
);
````

**C’est la réponse finale envoyée au controller.**

6.Résultat final côté API

Quand le front appelle :

````java
GET /chronicles?page=0&size=10
````

Il reçoit :

````json
{
  "items": [
    { "idChronicle": 12, "title": "...", "coverUrl": "...", "publishedAt": "...", "summary": "..." },
    { "idChronicle": 11, ... },
    ...
  ],
  "page": 0,
  "size": 10,
  "totalItems": 42,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false
}
````

