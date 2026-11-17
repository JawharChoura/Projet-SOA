# Guide des fichiers de configuration

Ce document explique les fichiers de configuration du projet **Billetterie Transport**, leur emplacement et leur contenu.

## 1. Vue d'ensemble

Le projet est composé de cinq microservices Spring Boot :

| Service | Rôle | Port principal |
|---|---|---:|
| `api-gateway` | Point d'entrée HTTP et routage | `8080` |
| `auth-service` | Authentification et génération des JWT | `8081` |
| `booking-service` | Trajets, réservations et voyageurs | `8082` |
| `place-service` | Gestion des places avec gRPC | `9090` gRPC, `8083` HTTP |
| `partner-service` | Communication partenaire avec SOAP | `8084` |

Chaque service possède sa propre base PostgreSQL. Les services communiquent sur le réseau Docker `billetterie-network`.

## 2. Fichiers de configuration à la racine

### `.env`

**Emplacement :** `.env`

Contient les valeurs utilisées par Docker Compose :

- paramètres des quatre bases PostgreSQL ;
- URLs des microservices ;
- ports gRPC et HTTP ;
- secret et durée de validité des JWT ;
- règles métier des réservations ;
- paramètres de retry et de circuit breaker ;
- délais d'attente gRPC et SOAP ;
- mode du partenaire (`SIMULATED` ou `REAL`) ;
- niveaux de logs et paramètres de supervision.

Ce fichier est ignoré par Git, car il peut contenir des mots de passe et des secrets. Il doit être rempli localement et ne doit pas être publié.

### `.env.example`

**Emplacement :** `.env.example`

Modèle des variables nécessaires au projet. Pour démarrer localement :

```powershell
Copy-Item .env.example .env
```

Il faut ensuite modifier au minimum `JWT_SECRET` et les mots de passe avant une utilisation réelle.

### `docker-compose.yml`

**Emplacement :** `docker-compose.yml`

Décrit l'environnement complet lancé par Docker Compose :

- quatre conteneurs PostgreSQL : `auth-db`, `booking-db`, `place-db` et `partner-db` ;
- cinq conteneurs applicatifs ;
- les ports exposés vers la machine locale ;
- les variables transmises aux conteneurs ;
- les dépendances entre services ;
- les healthchecks Spring Actuator et PostgreSQL ;
- le réseau `billetterie-network` ;
- les volumes persistants des bases de données.

Les expressions comme `${AUTH_DB_NAME:-auth}` utilisent une variable du fichier `.env` et une valeur par défaut si elle n'existe pas.

### `.gitignore`

**Emplacement :** `.gitignore`

Configure les fichiers ignorés par Git, notamment :

- les secrets `.env` ;
- les résultats Maven `target/` ;
- les fichiers compilés et les JAR ;
- les fichiers IDE (`.idea`, `.vscode`) ;
- les fichiers générés par Protobuf et JAXB ;
- les logs et fichiers temporaires.

## 3. Configuration Spring Boot

Chaque service utilise le fichier suivant :

```text
<service>/src/main/resources/application.yml
```

Le profil actif est défini par `SPRING_PROFILES_ACTIVE`. Si cette variable n'est pas définie, le profil `local` est utilisé. Docker Compose active le profil `docker`.

Les valeurs écrites sous la forme `${NOM_VARIABLE:valeur-par-defaut}` peuvent être remplacées par une variable d'environnement.

### `auth-service/src/main/resources/application.yml`

Configure :

- le nom `auth-service` ;
- la connexion PostgreSQL de la base `auth` ;
- HikariCP et la taille du pool de connexions ;
- JPA/Hibernate avec `ddl-auto: validate` ;
- Flyway pour les migrations de base de données ;
- le port HTTP `8081` ;
- la signature et l'expiration des JWT ;
- les endpoints Actuator et Prometheus ;
- les niveaux de logs.

Le profil `docker` réduit les niveaux de logs pour les conteneurs.

### `booking-service/src/main/resources/application.yml`

Configure :

- la base PostgreSQL `booking` ;
- JPA, Hibernate et Flyway ;
- la validation des JWT avec l'URL JWKS de `auth-service` ;
- le port HTTP `8082` ;
- les règles de réservation : durée de maintien et nombre maximal de places ;
- le client gRPC vers `place-service` ;
- le client SOAP vers `partner-service` ;
- les retries et le circuit breaker Resilience4j ;
- les endpoints Actuator et Prometheus ;
- les logs.

Le job `expiration-job-cron` vérifie régulièrement les réservations expirées.

### `place-service/src/main/resources/application.yml`

Configure :

- la base PostgreSQL `places` ;
- JPA, Hibernate et Flyway ;
- le port HTTP `8083` pour Actuator ;
- le port gRPC `9090` ;
- la taille maximale des messages gRPC et les paramètres keep-alive ;
- la durée de maintien d'une place et le job de libération ;
- le flux de disponibilité des places ;
- les endpoints Actuator et Prometheus ;
- les logs.

Dans le profil `docker`, la réflexion gRPC est désactivée pour éviter de l'exposer en production.

### `partner-service/src/main/resources/application.yml`

Configure :

- la base PostgreSQL `partner` ;
- JPA, Hibernate et Flyway ;
- le port HTTP `8084` ;
- le chemin du service SOAP : `/ws/partner` ;
- le namespace et la taille maximale des messages SOAP ;
- le mode partenaire `SIMULATED` ou `REAL` ;
- l'URL, l'utilisateur et le mot de passe du partenaire externe ;
- les délais d'attente SOAP ;
- les endpoints Actuator et Prometheus ;
- les logs.

Le mode `SIMULATED` est utilisé par défaut et évite d'appeler un partenaire externe.

### `api-gateway/src/main/resources/application.yml`

Configure :

- le nom `api-gateway` ;
- la validation des JWT à partir de l'endpoint JWKS ;
- le port HTTP `8080` ;
- les règles CORS ;
- les routes vers `auth-service` et `booking-service` ;
- les routes de healthcheck ;
- les endpoints Actuator, Prometheus et Gateway ;
- les logs.

Les routes publiques et protégées sont définies par les chemins :

- `/api/v1/auth/**` ;
- `/api/v1/trajets/**` ;
- `/api/v1/reservations/**` ;
- `/api/v1/voyageurs/**`.

## 4. Configuration Maven

Chaque microservice possède un `pom.xml` dans son dossier :

```text
auth-service/pom.xml
booking-service/pom.xml
place-service/pom.xml
partner-service/pom.xml
api-gateway/pom.xml
```

Ces fichiers configurent :

- Java `17` ;
- Spring Boot `3.3.5` ;
- les dépendances de chaque service ;
- le pilote PostgreSQL ;
- JPA/Hibernate et Flyway ;
- Spring Security et JWT pour les services concernés ;
- Actuator et Prometheus ;
- le plugin Spring Boot Maven ;
- les dépendances de test.

Configurations spécifiques :

- `auth-service/pom.xml` : bibliothèques JJWT pour créer et vérifier les JWT ;
- `booking-service/pom.xml` : client gRPC, client SOAP et Resilience4j ;
- `place-service/pom.xml` : génération du code Java depuis le fichier Protobuf ;
- `partner-service/pom.xml` : génération du code Java depuis le fichier XSD avec JAXB ;
- `api-gateway/pom.xml` : Spring Cloud Gateway et gestion des routes.

## 5. Configuration Docker des services

Chaque service possède un `Dockerfile` :

```text
auth-service/Dockerfile
booking-service/Dockerfile
place-service/Dockerfile
partner-service/Dockerfile
api-gateway/Dockerfile
```

Tous utilisent la même stratégie :

1. une étape Maven pour télécharger les dépendances et construire le JAR ;
2. une étape d'exécution basée sur Java 17 JRE Alpine ;
3. un utilisateur non-root nommé `spring` ;
4. un port exposé adapté au service ;
5. un démarrage avec `java -jar app.jar`.

Le `Dockerfile` de `place-service` expose deux ports : `9090` pour gRPC et `8083` pour HTTP/Actuator.

## 6. Fichiers techniques liés à la configuration

Ces fichiers ne sont pas des fichiers de paramètres classiques, mais ils définissent les contrats utilisés par les services :

| Fichier | Contenu |
|---|---|
| `place-service/src/main/proto/place-service.proto` | Contrat des échanges gRPC du service des places |
| `partner-service/src/main/resources/wsdl/partner.wsdl` | Contrat du service SOAP partenaire |
| `partner-service/src/main/resources/xsd/partner.xsd` | Structure XML des messages SOAP |
| `auth-service/src/main/resources/api/openapi.yml` | Documentation et contrat de l'API d'authentification |
| `booking-service/src/main/resources/api/openapi.yml` | Documentation et contrat de l'API de réservation |

## 7. Ordre de configuration recommandé

1. Copier `.env.example` vers `.env`.
2. Vérifier les noms, utilisateurs et mots de passe PostgreSQL.
3. Remplacer `JWT_SECRET` par une valeur secrète et suffisamment longue.
4. Garder `PARTNER_MODE=SIMULATED` pour un environnement local.
5. Vérifier les ports qui ne sont pas déjà utilisés.
6. Démarrer l'ensemble avec :

```powershell
docker compose up --build
```

Les données PostgreSQL restent dans les volumes Docker nommés. Pour arrêter les conteneurs sans supprimer les données :

```powershell
docker compose down
```

## 8. Points importants de sécurité

- Ne jamais commiter `.env`.
- Ne pas utiliser les mots de passe par défaut en production.
- Changer `JWT_SECRET` avant tout déploiement.
- Ne pas activer `PARTNER_MODE=REAL` sans fournir les identifiants du partenaire.
- Désactiver ou protéger les endpoints Actuator exposés publiquement.
- Conserver la réflexion gRPC désactivée dans les environnements de production.
