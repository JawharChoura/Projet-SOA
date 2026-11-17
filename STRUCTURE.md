# Structure du Projet - Billetterie de Transport

## ✅ Ce qui a été créé (Phase 1 - Infrastructure)

### 📁 Structure globale

```
ProjetSOA/
├── 📄 README.md                    ✅ Documentation principale
├── 📄 .gitignore                   ✅ Fichiers à ignorer par Git
├── 📄 .env.example                 ✅ Template variables d'environnement
├── 📄 docker-compose.yml           ✅ Orchestration complète
├── 📄 STRUCTURE.md                 ✅ Ce fichier
│
├── 📁 api-gateway/                 ✅ Service créé
│   ├── Dockerfile                  ✅ Multi-stage build
│   ├── pom.xml                     ✅ Spring Cloud Gateway
│   └── src/main/
│       ├── java/com/billetterie/gateway/
│       └── resources/
│
├── 📁 auth-service/                ✅ Service créé
│   ├── Dockerfile                  ✅ Multi-stage build
│   ├── pom.xml                     ✅ Spring Security + JWT
│   └── src/main/
│       ├── java/com/billetterie/auth/
│       └── resources/
│
├── 📁 booking-service/             ✅ Service créé
│   ├── Dockerfile                  ✅ Multi-stage build
│   ├── pom.xml                     ✅ REST + gRPC client + SOAP client
│   └── src/main/
│       ├── java/com/billetterie/booking/
│       └── resources/
│
├── 📁 place-service/               ✅ Service créé
│   ├── Dockerfile                  ✅ Multi-stage build
│   ├── pom.xml                     ✅ gRPC Server + Protobuf
│   └── src/main/
│       ├── java/com/billetterie/place/
│       └── resources/
│
└── 📁 partner-service/             ✅ Service créé
    ├── Dockerfile                  ✅ Multi-stage build
    ├── pom.xml                     ✅ SOAP Web Services + JAXB
    └── src/main/
        ├── java/com/billetterie/partner/
        └── resources/
```

## 🎯 Prochaines étapes

### Phase 2A : Configuration de base (MAINTENANT)
- [ ] `application.yml` pour chaque service
- [ ] Classes principales (@SpringBootApplication)
- [ ] Configuration Flyway (migrations SQL)

### Phase 2B : Auth Service (Simple)
- [ ] Entities (User, Role)
- [ ] Repositories
- [ ] Services (inscription, connexion)
- [ ] Controllers (REST)
- [ ] JWT generation

### Phase 2C : API Gateway
- [ ] Routes configuration
- [ ] JWT validation filter
- [ ] Routage vers booking-service

### Phase 3 : Contrats d'API
- [ ] XSD pour SOAP (partner-service)
- [ ] .proto pour gRPC (place-service)
- [ ] OpenAPI pour REST (booking-service)

### Phase 4 : Implémentation services métier
- [ ] Partner Service (SOAP)
- [ ] Place Service (gRPC)
- [ ] Booking Service (orchestrateur)

### Phase 5 : Intégration & Tests
- [ ] Tests unitaires
- [ ] Tests d'intégration
- [ ] Collection Postman
- [ ] Documentation

## 🚀 Commandes rapides

### Construire tous les services
```bash
mvn clean install -f auth-service/pom.xml
mvn clean install -f booking-service/pom.xml
mvn clean install -f place-service/pom.xml
mvn clean install -f partner-service/pom.xml
mvn clean install -f api-gateway/pom.xml
```

### Démarrer avec Docker Compose
```bash
# Copier le fichier d'environnement
cp .env.example .env

# Démarrer tous les services
docker-compose up --build

# Arrêter
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

### Vérifier les services
```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Auth Service
curl http://localhost:8081/actuator/health

# Booking Service
curl http://localhost:8082/actuator/health

# Place Service (HTTP)
curl http://localhost:8083/actuator/health

# Partner Service
curl http://localhost:8084/actuator/health
```

## 📦 Technologies configurées

| Service | Port(s) | Technologies |
|---------|---------|--------------|
| **api-gateway** | 8080 | Spring Cloud Gateway, JWT validation |
| **auth-service** | 8081 | Spring Security, JWT, PostgreSQL, Flyway |
| **booking-service** | 8082 | REST, gRPC client, SOAP client, Resilience4j |
| **place-service** | 9090, 8083 | gRPC Server, Protobuf, PostgreSQL |
| **partner-service** | 8084 | SOAP, JAXB, XSD/WSDL, PostgreSQL |

## 🗄️ Bases de données

| Database | Port | Service |
|----------|------|---------|
| auth-db | 5432 | auth-service |
| booking-db | 5433 | booking-service |
| place-db | 5434 | place-service |
| partner-db | 5435 | partner-service |

## 🌐 Réseau Docker

- **Nom** : billetterie-network
- **Type** : bridge
- **Services** : Tous les services communiquent via ce réseau

## 📊 État actuel

✅ **TERMINÉ**
- Structure complète du projet
- Docker Compose avec 5 services + 4 bases
- Dockerfiles multi-stage pour chaque service
- pom.xml avec toutes les dépendances
- Configuration des ports et réseaux
- Variables d'environnement
- Documentation de base

⏳ **EN COURS**
- Configuration application.yml
- Classes principales
- Migrations Flyway

❌ **À FAIRE**
- Logique métier
- Contrats d'API
- Tests
- Collection Postman
