# Billetterie de Transport - Architecture SOA

## 📋 Description du projet

Application de billetterie de transport en ligne basée sur une architecture microservices avec Spring Boot 3.
Un voyageur peut rechercher un trajet (ex. Tunis - Sfax), réserver une place avec retenue de 10 minutes, puis recevoir son billet après confirmation.

## 🏗️ Architecture

### Microservices

| Service | Style | Port | Responsabilité |
|---------|-------|------|----------------|
| **api-gateway** | REST | 8080 | Point d'entrée unique, validation JWT, routage |
| **auth-service** | REST | 8081 | Inscription, connexion, émission JWT, rôles (TRAVELER, ADMIN) |
| **booking-service** | REST | 8082 | Orchestrateur : gestion voyageurs, réservations, billets |
| **place-service** | gRPC | 9090 | Gestion stock places, retenue 10 min, streaming disponibilité |
| **partner-service** | SOAP | 8084 | Interface avec compagnie partenaire (système hérité) |

### Justification des protocoles

- **REST** (booking, auth, gateway) : Exposition aux applications web, standard, largement outillé
- **gRPC** (place-service) : Échange interne à faible latence, streaming serveur pour pousser la disponibilité en temps réel
- **SOAP** (partner-service) : Interopérabilité avec système hérité (simulation GDS compagnie de transport)

### Flux de réservation

1. Client recherche un trajet via API Gateway
2. Gateway valide le JWT (auth-service) et route vers booking-service
3. Booking-service bloque les places via gRPC (place-service)
4. Réservation créée EN_ATTENTE avec expiration à 10 minutes
5. Après paiement, confirmation via SOAP (partner-service)
6. Statut CONFIRMÉE, billet émis, places confirmées
7. Job planifié expire automatiquement les réservations non confirmées

## 🚀 Démarrage rapide

### Prérequis

- Java 17+
- Docker & Docker Compose
- Maven 3.8+

### Lancement

```bash
# Cloner le projet
git clone <url-du-repo>
cd ProjetSOA

# Démarrer tous les services
docker-compose up --build

# Les services seront disponibles sur :
# - API Gateway: http://localhost:8080
# - Auth Service: http://localhost:8081
# - Booking Service: http://localhost:8082
# - Place Service: gRPC localhost:9090, HTTP localhost:8083
# - Partner Service: http://localhost:8084
```

## 📦 Stack technique

- **Java 17**
- **Spring Boot 3.3.x**
- **Spring Cloud Gateway**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **PostgreSQL** (une base par service)
- **gRPC** (place-service)
- **Spring Web Services** (SOAP)
- **Flyway** (migrations)
- **Resilience4j** (circuit breaker, retry)
- **Docker & Docker Compose**

## 🗂️ Structure du projet

```
ProjetSOA/
├── api-gateway/           # Point d'entrée, routage, JWT
├── auth-service/          # Authentification et autorisation
├── booking-service/       # Orchestrateur principal
├── place-service/         # Gestion stock (gRPC)
├── partner-service/       # Interface SOAP partenaire
├── docker-compose.yml     # Orchestration complète
├── .env.example           # Variables d'environnement
└── README.md
```

## 📡 APIs et Endpoints

### Auth Service (REST)
```
POST /api/v1/auth/register  - Inscription
POST /api/v1/auth/login     - Connexion (obtenir JWT)
```

### Booking Service (REST)
```
GET    /api/v1/trajets                      - Rechercher trajets
GET    /api/v1/trajets/{id}/places          - Places disponibles
POST   /api/v1/reservations                 - Créer réservation
GET    /api/v1/reservations/{id}            - Consulter réservation
POST   /api/v1/reservations/{id}/confirm    - Confirmer réservation
DELETE /api/v1/reservations/{id}            - Annuler réservation
GET    /api/v1/reservations/{id}/billet     - Récupérer billet
GET    /api/v1/voyageurs/me                 - Profil voyageur
```

### Place Service (gRPC)
```
GetAvailability      - Lire places disponibles
HoldPlaces          - Bloquer places (10 min)
ConfirmHold         - Confirmer retenue
ReleasePlaces       - Libérer retenue
WatchAvailability   - Flux serveur (streaming)
```

### Partner Service (SOAP)
```
SearchTrips       - Rechercher trajets partenaire
ConfirmBooking    - Confirmer réservation
CancelBooking     - Annuler réservation
```

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify

# Tests d'un service spécifique
cd booking-service
mvn test
```

## 👥 Équipe

- Membre 1 : Orchestration (booking-service, api-gateway)
- Membre 2 : Orchestration (booking-service, api-gateway)
- Membre 3 : gRPC (place-service, streaming)
- Membre 4 : gRPC (place-service, jobs planifiés)
- Membre 5 : SOAP (partner-service, contract-first)
- Membre 6 : Auth (auth-service, sécurité JWT)

## 📝 Documentation

- [Architecture détaillée](./docs/architecture.md)
- [Guide de développement](./docs/development.md)
- [Collection Postman](./docs/postman_collection.json)

## 📄 Licence

Projet académique - Architecture SOA et Services Web
Dr. Ing. Ghada Feki - 2026
