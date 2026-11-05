# Rendu projet WEB Services 2026-2027
## Fonctionnalités exposées par l'API
### Côté restaurants
- La possibilité de récupérer tous les restaurants
  - Route : `GET /restaurants`
  - La réponse contient la moyenne des notes de chaque restaurant.

- La possibilité de récupérer un restaurant en particulier
  - Route : `GET /restaurants/{id}`
  - La réponse contient la moyenne des notes du restaurant.

- La possibilité de créer un restaurant (par les utilisateurs ayant le rôle **ADMIN**)
  - Route : `POST /restaurants`
  - Utilise les données RequestBody au format JSON (nom, adresse) pour créer un restaurant.
  - Champs obligatoires : nom, adresse
  - Champs optionnels : image via la route `PUT /restaurants/{id}/image` qui génère une URL présignée pour l'envoi de l'image.

- La possibilité de mettre à jour le nom et l'adresse d'un restaurant (par les utilisateurs ayant le rôle **ADMIN**)
  - Route : `PUT /restaurants/{id}`
  - Utilise les données RequestBody au format JSON (nom, adresse) pour modifier un restaurant.
  - Uniquement les champs nom et adresse peuvent être modifiés. L'image ne peut pas être modifiée via cette route.
  - Si un champ n'est pas fourni, il ne sera pas modifié.

- La possibilité de supprimer un restaurant (par les utilisateurs ayant le rôle **ADMIN**)
  - Route : `DELETE /restaurants/{id}`

- La possibilité de récupérer l'image (URL présignée) d'un restaurant
  - Route : `GET /restaurants/{id}/image`


### Côté évaluations
- La possibilité d'ajouter une évaluation sur un restaurant (par les utilisateurs étant authentifiés)
  - Route : `POST /restaurants/{id}/evaluations`
  - Utilise les données RequestBody au format JSON (note, commentaire) pour créer une évaluation.
  - Champs obligatoires : note, commentaire
  - La note est comprise entre 0 et 3.
  - Champs optionnels : photos via la route `PUT /evaluations/{id}/images?nbImgs={n}` qui génère une URL présignée pour chaque image à envoyer (avec n <=5 via un RequestParam nbImgs).

- La possibilité de mettre à jour une évaluation (par les utilisateurs ayant le rôle **ADMIN** ou par l'utilisateur ayant créé l'évaluation)
  - Route : `PUT /evaluations/{id}`
  - Utilise les données RequestBody au format JSON (note, commentaire) pour modifier une évaluation.
  - Uniquement les champs note et commentaire peuvent être modifiés. Les photos ne peuvent pas être modifiées via cette route.
  - Si un champ n'est pas fourni, il ne sera pas modifié.

- La possibilité de récupérer les évaluations d'un restaurant créées par l'utilisateur connecté
  - Route : `GET /restaurants/{id}/evaluations`

- La possibilité de récupérer toutes les évaluations d'un restaurant
  - Route : `GET /restaurants/{id}/evaluations/all`

- La possibilité de supprimer une évaluation (par les utilisateurs ayant le rôle **ADMIN** ou par l'utilisateur ayant créé l'évaluation)
  - Route : `DELETE /evaluations/{id}`
  - Supprime également les images associées à l'évaluation.

- La possibilité de récupérer les évaluations en fonction d'un (ou plusieurs) mots clefs
  - Route : `GET /evaluations/index?query={mot-cle}`
  - Recherche globale dans les commentaires avec Lucene.
  - Recherche limitée à un restaurant : `GET /restaurants/{id}/evaluations/index?query={mot-cle}`

- La possibilité pour un utilisateur de récupérer toutes les évaluations qu'il a lui même créé.
  - Route : `GET /evaluations`

- La possibilité de récupérer toutes les évaluations
  - Route : `GET /evaluations/all`

- La possibilité de récupérer les images (URLs présignées) d'une évaluation
  - Route : `GET /evaluations/{id}/images`

### Entités
- **Restaurant**
  - id : identifiant unique (un nombre entier positif)
  - nom : nom du restaurant (longueur max de 90 caractères)
  - adresse : adresse du restaurant (longueur max de 255 caractères)
  - moyenne : moyenne des notes du restaurant (float, -1 si aucune évaluation)
  - image : nom de l'image du restaurant utilisée pour générer les URLs présignée

- **Evaluation**
  - id : identifiant unique (un nombre entier positif)
  - auteur : nom de l'évaluateur
  - contenu : commentaire de l'évaluation (longueur max de 255 caractères)
  - note : nombre d'étoiles recommandé (0,1,2 ou 3)
  - restaurantId : identifiant du restaurant auquel l'évaluation est associée

- **Images_eval**
  - Eval_id : identifiant de l'évaluation
  - images : nom de l'image utilisée pour générer les URLs présignée

## Gestion des erreurs
Les erreurs sont renvoyées sous la forme suivante :

```json
{
  "code": 404,
  "message": "Restaurant avec l'id 1 non trouvé."
}
```

Les principaux cas gérés sont :
- `400 Bad Request` pour les données invalides ou les paramètres mal formés ;
- `403 Forbidden` pour un utilisateur authentifié mais insuffisamment autorisé ;
- `404 Not Found` lorsqu'un restaurant, une évaluation ou une image n'existe pas ;
- `500 Internal Server Error` lorsqu'une erreur interne survient.

## Tests
- Les services `RestaurantService` et `EvaluationService` sont testés avec des tests unitaires Mockito.
- Les tests couvrent les cas nominaux et les cas d'erreur :
  - création, modification, récupération et suppression ;
  - restaurant ou évaluation inexistant ;
  - calcul des moyennes avec et sans évaluations ;
  - filtrage des évaluations par utilisateur et par restaurant ;
  - gestion des images ;
  - vérification des appels aux repositories.

## Documentation
- L'API expose une interface Swagger/OpenAPI grâce à Springdoc 
  - URL : `http://localhost:8080/swagger-ui/index.html`


## Précisions sur certains choix
- Malgré une contrainte imposant de retourner la moyenne (-1) à la création d'un restaurant, j'ai décidé de ne renvoyer que l'id du restaurant créé. Il n'y a pas de raison de renvoyer la moyenne à ce moment-là selon moi.
- Le choix des routes pour les évaluations peut sembler étrange, mais il me semble cohérent d'avoir une route pour récupérer toutes les évaluations d'un restaurant et une autre pour récupérer uniquement celles de l'utilisateur connecté sur un restaurant. Idem pour les évaluations dans leur globalité. Je trouve que ça peut avoir un intéret côté client.

## Retour d'expérience
- Le projet m'a permis d'approfondir mes connaissance en Spring Boot et la création d'API REST. 
- J'ai pu prendre en main l'authentification et l'autorisation avec OpenID Connect, ainsi que la manipulation de fichiers via des URLs présignées. 
- La gestion des erreurs et la documentation avec Swagger ont également été des points à travailler.
