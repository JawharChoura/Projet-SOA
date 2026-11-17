# 📜 Contrats d'API - Billetterie de Transport

## ✅ Approche Contract-First

Tous les contrats ont été définis **AVANT** l'implémentation, conformément aux exigences du projet.

---

## 1️⃣ **SOAP - Partner Service**

### 📄 Fichiers créés
- ✅ `partner-service/src/main/resources/xsd/partner.xsd`
- ✅ `partner-service/src/main/resources/wsdl/partner.wsdl`

### 🔧 Namespace
```
http://billetterie.example/partner/v1
```

### 📡 Endpoint SOAP
```
http://localhost:8084/ws/partner
```

### 📋 Opérations

#### **SearchTrips**
Rechercher les trajets disponibles
- **Input:** origine, destination, date
- **Output:** Liste de trajets (id, horaires, prix, places)
- **Faults:** InvalidDateFault

#### **ConfirmBooking**
Confirmer une réservation chez le partenaire
- **Input:** trajetId, reservationId, nbPlaces, voyageur
- **Output:** partnerRef, statut, message
- **Faults:** TripFullFault, TripNotFoundFault

#### **CancelBooking**
Annuler une réservation
- **Input:** partnerRef
- **Output:** statut, penalite, message
- **Faults:** BookingNotFoundFault, AlreadyCancelledFault

### 🚀 Génération du code
```bash
cd partner-service
mvn clean generate-sources

# Les classes Java seront générées dans:
# src/main/java/com/billetterie/partner/soap/generated/
```

### 🧪 Test SOAP
```bash
# Obtenir le WSDL
curl http://localhost:8084/ws/partner?wsdl

# Exemple de requête SOAP (SearchTrips)
curl -X POST http://localhost:8084/ws/partner \
  -H "Content-Type: text/xml" \
  -d @request.xml
```

---

## 2️⃣ **gRPC - Place Service**

### 📄 Fichier créé
- ✅ `place-service/src/main/proto/place-service.proto`

### 🔧 Package
```
billetterie.place
```

### 📡 Endpoint gRPC
```
localhost:9090
```

### 📋 Opérations (RPCs)

#### **GetAvailability** (Unaire)
Obtenir les places disponibles
- **Request:** trajet_id
- **Response:** capacite_totale, places_disponibles, version

#### **HoldPlaces** (Unaire)
Bloquer des places (retenue 10 min)
- **Request:** trajet_id, reservation_id, nb_places
- **Response:** success, hold_id, expires_at_ms

#### **ConfirmHold** (Unaire)
Confirmer une retenue
- **Request:** reservation_id
- **Response:** success, places_disponibles

#### **ReleasePlaces** (Unaire)
Libérer une retenue
- **Request:** reservation_id
- **Response:** success, places_disponibles

#### **WatchAvailability** (Streaming serveur)
Flux temps réel de disponibilité
- **Request:** trajet_id
- **Response (stream):** AvailabilityUpdate (places, timestamp, event_type)

### 🔥 Codes d'erreur gRPC
- `INVALID_ARGUMENT` - Paramètres invalides
- `NOT_FOUND` - Trajet ou réservation introuvable
- `RESOURCE_EXHAUSTED` - Stock insuffisant
- `ALREADY_EXISTS` - Retenue déjà créée (idempotence)
- `FAILED_PRECONDITION` - Retenue expirée
- `DEADLINE_EXCEEDED` - Timeout

### 🚀 Génération du code
```bash
cd place-service
mvn clean compile

# Les classes Java seront générées dans:
# target/generated-sources/protobuf/
```

### 🧪 Test gRPC
```bash
# Avec grpcurl (reflection activée en dev)
grpcurl -plaintext localhost:9090 list
grpcurl -plaintext localhost:9090 billetterie.place.PlaceService/GetAvailability

# Exemple GetAvailability
grpcurl -plaintext -d '{"trajet_id": "TUN-SFX-001"}' \
  localhost:9090 billetterie.place.PlaceService/GetAvailability

# Exemple Streaming
grpcurl -plaintext -d '{"trajet_id": "TUN-SFX-001"}' \
  localhost:9090 billetterie.place.PlaceService/WatchAvailability
```

---

## 3️⃣ **REST - Auth Service**

### 📄 Fichier créé
- ✅ `auth-service/src/main/resources/api/openapi.yml`

### 🔧 Version API
```
/api/v1/auth
```

### 📡 Base URL
```
http://localhost:8081  (direct)
http://localhost:8080  (via gateway)
```

### 🔒 Sécurité
Tous les endpoints sont **publics** (pas de JWT requis) :
- `POST /api/v1/auth/register` - Inscription
- `POST /api/v1/auth/login` - Connexion
- `GET /.well-known/jwks.json` - Clés publiques JWT

### 📋 Endpoints

#### **Authentication**
- `POST /api/v1/auth/register` - Inscription (email, password, nom, prenom)
- `POST /api/v1/auth/login` - Connexion → retourne JWT token

#### **JWKS (JSON Web Key Set)**
- `GET /.well-known/jwks.json` - Clés publiques pour validation JWT

### 📊 Codes HTTP
- `200` - Connexion réussie
- `201` - Inscription réussie
- `400` - Validation échouée
- `401` - Email/password incorrect
- `409` - Email déjà utilisé

---

## 4️⃣ **REST - Booking Service**

### 📄 Fichier créé
- ✅ `booking-service/src/main/resources/api/openapi.yml`

### 🔧 Version API
```
/api/v1
```

### 📡 Base URL
```
http://localhost:8082  (direct)
http://localhost:8080  (via gateway)
```

### 🔒 Sécurité
Toutes les routes nécessitent un **JWT Bearer** sauf :
- `GET /api/v1/trajets` (public)
- `GET /api/v1/trajets/{id}/places` (public)

### 📋 Endpoints

#### **Trajets**
- `GET /api/v1/trajets` - Rechercher trajets (query: origine, destination, date)
- `GET /api/v1/trajets/{id}/places` - Places disponibles

#### **Réservations** (JWT requis)
- `POST /api/v1/reservations` - Créer réservation (retenue 10 min)
- `GET /api/v1/reservations/{id}` - Consulter réservation
- `POST /api/v1/reservations/{id}/confirm` - Confirmer après paiement
- `DELETE /api/v1/reservations/{id}` - Annuler réservation

#### **Billets** (JWT requis)
- `GET /api/v1/reservations/{id}/billet` - Récupérer billet

#### **Voyageurs** (JWT requis)
- `GET /api/v1/voyageurs/me` - Profil du voyageur connecté

### 📊 Codes HTTP
- `200` - Succès
- `201` - Ressource créée
- `204` - Succès sans contenu
- `400` - Requête invalide
- `404` - Ressource introuvable
- `409` - Conflit (places insuffisantes, déjà annulée, etc.)

### 🚀 Documentation interactive
```bash
# Swagger UI (après implémentation)
http://localhost:8082/swagger-ui.html
```

### 🧪 Test REST
```bash
# Rechercher trajets (public)
curl http://localhost:8082/api/v1/trajets?origine=Tunis&destination=Sfax&date=2026-11-15

# Créer réservation (avec JWT)
curl -X POST http://localhost:8082/api/v1/reservations \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "trajetId": "TUN-SFX-20261115-0800",
    "nbPlaces": 2
  }'

# Confirmer réservation
curl -X POST http://localhost:8082/api/v1/reservations/{id}/confirm \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "CARD",
    "paymentReference": "PAY-123456"
  }'
```

---

## 📊 Comparaison des 3 protocoles

| Aspect | SOAP (Partner) | gRPC (Place) | REST (Booking) |
|--------|----------------|--------------|----------------|
| **Format** | XML | Protobuf | JSON |
| **Contrat** | XSD + WSDL | .proto | OpenAPI |
| **Transport** | HTTP/HTTPS | HTTP/2 | HTTP/1.1 |
| **Streaming** | ❌ Non | ✅ Oui (serveur) | ❌ Non (SSE possible) |
| **Performance** | 🐌 Lent | 🚀 Très rapide | ⚡ Rapide |
| **Lisibilité** | 📖 Verbeux | 🔧 Binaire | 📝 Lisible |
| **Usage** | Legacy/Enterprise | Interne performant | Externe/Web |
| **Génération code** | JAXB | protoc | OpenAPI Generator |

---

## 🔄 Flux d'une réservation complète

```
1. Client → GET /api/v1/trajets (REST booking-service)
   └─→ booking-service → SearchTrips (SOAP partner-service)

2. Client → POST /api/v1/reservations (REST booking-service)
   └─→ booking-service → HoldPlaces (gRPC place-service)
   
3. [Paiement côté client]

4. Client → POST /api/v1/reservations/{id}/confirm (REST booking-service)
   ├─→ booking-service → ConfirmBooking (SOAP partner-service)
   └─→ booking-service → ConfirmHold (gRPC place-service)

5. Client → GET /api/v1/reservations/{id}/billet (REST booking-service)
   └─→ Retourne le billet avec QR code
```

---

## ✅ Prochaines étapes

1. **Générer le code** depuis les contrats
   ```bash
   # Partner Service (SOAP)
   cd partner-service && mvn generate-sources
   
   # Place Service (gRPC)
   cd place-service && mvn compile
   ```

2. **Créer les migrations Flyway**
   - Schémas de base de données pour chaque service

3. **Implémenter les services**
   - En utilisant les classes générées

4. **Tester les contrats**
   - Tests d'intégration pour chaque protocole
