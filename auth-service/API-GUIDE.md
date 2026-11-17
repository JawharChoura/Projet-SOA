# 🔐 Auth Service - API Guide

## 📖 Vue d'ensemble

L'**auth-service** expose une API REST simple pour l'authentification et l'autorisation :
- ✅ Inscription des utilisateurs
- ✅ Connexion avec émission de JWT
- ✅ Publication JWKS pour validation par autres services

---

## 🎯 Endpoints disponibles

### 1️⃣ **POST /api/v1/auth/register** - Inscription

**Description :** Créer un nouveau compte utilisateur.

**Requête :**
```json
{
  "email": "ahmed.ben.ali@example.com",
  "password": "MySecurePass123!",
  "nom": "Ben Ali",
  "prenom": "Ahmed",
  "telephone": "+21612345678"
}
```

**Validations :**
- ✅ Email : format valide, unique
- ✅ Password : minimum 8 caractères
- ✅ Nom/Prénom : 2-50 caractères
- ✅ Téléphone : optionnel, format international

**Réponse (201 Created) :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "ahmed.ben.ali@example.com",
    "nom": "Ben Ali",
    "prenom": "Ahmed",
    "roles": ["TRAVELER"]
  }
}
```

**Erreurs possibles :**
- `400` - Validation échouée (email invalide, password trop court)
- `409` - Email déjà utilisé

**Exemple cURL :**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmed.ben.ali@example.com",
    "password": "MySecurePass123!",
    "nom": "Ben Ali",
    "prenom": "Ahmed",
    "telephone": "+21612345678"
  }'
```

---

### 2️⃣ **POST /api/v1/auth/login** - Connexion

**Description :** Authentifier un utilisateur et obtenir un JWT.

**Requête :**
```json
{
  "email": "ahmed.ben.ali@example.com",
  "password": "MySecurePass123!"
}
```

**Réponse (200 OK) :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
  "type": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "ahmed.ben.ali@example.com",
    "nom": "Ben Ali",
    "prenom": "Ahmed",
    "roles": ["TRAVELER"]
  }
}
```

**Erreurs possibles :**
- `401` - Email ou mot de passe incorrect

**Exemple cURL :**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmed.ben.ali@example.com",
    "password": "MySecurePass123!"
  }'
```

---

### 3️⃣ **GET /.well-known/jwks.json** - JWKS

**Description :** Obtenir les clés publiques pour valider les JWT.

**Usage :** Utilisé automatiquement par :
- `api-gateway` (validation JWT)
- `booking-service` (validation JWT)

**Réponse (200 OK) :**
```json
{
  "keys": [
    {
      "kty": "RSA",
      "use": "sig",
      "kid": "auth-service-key-1",
      "alg": "HS256",
      "n": "...",
      "e": "AQAB"
    }
  ]
}
```

**Standard :** RFC 7517 (JSON Web Key)

**Exemple cURL :**
```bash
curl http://localhost:8081/.well-known/jwks.json
```

---

## 🔑 JWT Token expliqué

### Structure du token

Un JWT est composé de 3 parties séparées par des points :
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9    ← Header
.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI...   ← Payload (claims)
.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV...     ← Signature
```

### Payload (claims) du JWT

```json
{
  "sub": "123e4567-e89b-12d3-a456-426614174000",  // User ID
  "email": "ahmed.ben.ali@example.com",
  "roles": ["TRAVELER"],
  "iat": 1700000000,  // Issued At (timestamp)
  "exp": 1700086400,  // Expiration (timestamp + 24h)
  "iss": "http://auth-service:8081"  // Issuer
}
```

### Utilisation du token

```bash
# Inclure dans le header Authorization
curl http://localhost:8082/api/v1/reservations \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🔐 Sécurité

### Hashage des mots de passe

Les mots de passe sont hashés avec **BCrypt** (coût 12) :
```
plaintext: MySecurePass123!
→ $2a$12$KIXqVz8Vq3rM7Uq7K3xYZuO1jqVvN2xZ3Qw4Rz5Yw6Xz7Yy8Ww9Vv0
```

### Validation JWT

Les autres services valident le JWT en :
1. Récupérant les clés publiques depuis `/.well-known/jwks.json`
2. Vérifiant la signature
3. Vérifiant l'expiration
4. Extrayant les claims (user ID, roles)

**Configuration dans application.yml :**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://auth-service:8081/.well-known/jwks.json
```

---

## 📊 Flux d'authentification complet

```
┌─────────────────────────────────────────────────────────┐
│ 1. Inscription                                          │
│    POST /api/v1/auth/register                           │
│    → Crée l'utilisateur (password hashé avec BCrypt)    │
│    → Génère JWT                                         │
│    → Retourne token + user info                         │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│ 2. Connexion (sessions suivantes)                       │
│    POST /api/v1/auth/login                              │
│    → Vérifie email/password (compare hash BCrypt)       │
│    → Génère nouveau JWT                                 │
│    → Retourne token + user info                         │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│ 3. Accès aux ressources protégées                       │
│    Client → API Gateway                                 │
│    Header: Authorization: Bearer JWT_TOKEN              │
│    → API Gateway valide JWT via JWKS                    │
│    → Redirige vers booking-service avec JWT             │
│    → booking-service extrait user ID du JWT             │
└─────────────────────────────────────────────────────────┘
```

---

## 🧪 Tests avec Postman

### Collection Postman

1. **Créer une variable d'environnement**
   - `BASE_URL` = `http://localhost:8081`
   - `TOKEN` = (sera rempli automatiquement)

2. **Inscription**
   - Méthode : POST
   - URL : `{{BASE_URL}}/api/v1/auth/register`
   - Body : JSON (voir exemple ci-dessus)
   - Test script :
     ```javascript
     pm.environment.set("TOKEN", pm.response.json().token);
     ```

3. **Connexion**
   - Méthode : POST
   - URL : `{{BASE_URL}}/api/v1/auth/login`
   - Body : JSON (voir exemple ci-dessus)
   - Test script :
     ```javascript
     pm.environment.set("TOKEN", pm.response.json().token);
     ```

4. **Utiliser le token**
   - Dans les autres requêtes (booking-service)
   - Header : `Authorization: Bearer {{TOKEN}}`

---

## 🎯 Rôles utilisateurs

### Rôles disponibles

| Rôle | Description | Permissions |
|------|-------------|-------------|
| **TRAVELER** | Voyageur standard | Créer réservations, consulter billets |
| **ADMIN** | Administrateur | Toutes permissions + gestion utilisateurs |

### Attribution des rôles

- **Par défaut** : Tous les nouveaux utilisateurs ont le rôle `TRAVELER`
- **ADMIN** : Doit être attribué manuellement en base de données (pour sécurité)

---

## 🔧 Configuration

### Variables d'environnement

```env
# JWT Configuration
JWT_SECRET=your-secret-key-change-in-production
JWT_EXPIRATION_MS=86400000  # 24 heures
JWT_ISSUER=http://auth-service:8081

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auth
DB_USER=auth_user
DB_PASSWORD=auth_password
```

### Modifier la durée du token

Dans `application.yml` :
```yaml
jwt:
  expiration-ms: 3600000  # 1 heure au lieu de 24
```

---

## 📝 Notes importantes

### Sécurité en production

⚠️ **ATTENTION** : En production, vous DEVEZ :
1. Utiliser HTTPS (pas HTTP)
2. Changer `JWT_SECRET` avec une clé forte aléatoire
3. Stocker les secrets dans un vault (pas dans `.env`)
4. Activer rate limiting sur `/login` et `/register`
5. Ajouter CAPTCHA sur inscription
6. Implémenter refresh tokens (JWT courte durée + refresh long)

### Expiration du token

- **Durée** : 24 heures (86400 secondes)
- **Après expiration** : Le client doit se reconnecter
- **Future amélioration** : Implémenter refresh token

### JWKS Cache

Les services clients peuvent mettre en cache le JWKS :
- Réduit les appels à auth-service
- Durée de cache : 1 heure recommandée

---

## 🚀 Démarrage rapide

### 1. Démarrer le service

```bash
cd auth-service
mvn spring-boot:run
```

### 2. Vérifier la santé

```bash
curl http://localhost:8081/actuator/health
```

### 3. S'inscrire

```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!",
    "nom": "Test",
    "prenom": "User"
  }'
```

### 4. Se connecter

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!"
  }'
```

### 5. Utiliser le token

```bash
# Copier le token de la réponse
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# L'utiliser dans les autres services
curl http://localhost:8082/api/v1/reservations \
  -H "Authorization: Bearer $TOKEN"
```

---

**Documentation OpenAPI complète :** `src/main/resources/api/openapi.yml`

**Swagger UI (après implémentation) :** `http://localhost:8081/swagger-ui.html`
