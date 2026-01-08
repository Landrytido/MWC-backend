# 🌐 My Web Companion - Backend

Assistant personnel web offrant gestion de tâches, notes, calendrier, liens favoris et météo.

## 🚀 Démarrage rapide

### Prérequis

- Java 21
- MySQL 8.0+
- Maven (inclus via wrapper)

### Configuration

1. **Base de données MySQL**

```bash
mysql -u root -p < mywebcompanion.sql
```

2. **Variables d'environnement** (optionnel - valeurs par défaut disponibles)

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mywebcompanion
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=your-secret-key-256bits
WEATHER_API_KEY=your-weatherapi-key
```

3. **Lancer l'application**

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

4. **Accéder à l'API**

```
http://localhost:8080/api
```

### 🐳 Docker (optionnel)

```bash
docker build -t mwc-backend .
docker run -p 8080:8080 mwc-backend
```

## 📦 Fonctionnalités

### 🔐 Authentification & Profil

- Inscription / Connexion (JWT)
- Vérification email
- Refresh tokens
- Gestion profil utilisateur

### ✅ Gestion de tâches

- CRUD complet
- Filtres avancés (aujourd'hui, en retard, par priorité)
- **Système "End of Day"** - Report automatique
- Statistiques mensuelles
- Lien avec événements calendrier

### 📅 Calendrier

- Événements personnalisés
- Modes : Présentiel / Distanciel
- Vue mensuelle & journalière
- Intégration avec tâches

### 📝 Notes & Organisation

- **Notes** avec recherche
- **Carnets** (notebooks) pour organiser
- **Bloc-note rapide** (unique par user)
- **Labels** personnalisables
- **Commentaires** sur notes

### 🔗 Liens favoris

- Sauvegarde de liens
- Groupes de liens
- **Compteur de clics** par lien
- Top liens les plus cliqués

### 🌤️ Météo

- Météo actuelle & prévisions (WeatherAPI)
- Recherche par ville ou coordonnées GPS
- Jusqu'à 10 jours de prévisions

## 🛠️ Stack technique

**Backend**

- Spring Boot 3.3.12
- Java 21
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Lombok

**Déploiement**

- Docker ready
- Compatible Railway / Heroku

## 📡 API Endpoints

| Module      | Base URL           | Authentification |
| ----------- | ------------------ | ---------------- |
| Auth        | `/api/auth`        | ❌ Public        |
| Health      | `/api/health`      | ❌ Public        |
| Weather     | `/api/weather`     | ❌ Public        |
| Users       | `/api/users`       | ✅ Requise       |
| Tasks       | `/api/tasks`       | ✅ Requise       |
| Calendar    | `/api/calendar`    | ✅ Requise       |
| Notes       | `/api/notes`       | ✅ Requise       |
| Notebooks   | `/api/notebooks`   | ✅ Requise       |
| Bloc-note   | `/api/bloc-note`   | ✅ Requise       |
| Comments    | `/api/comments`    | ✅ Requise       |
| Labels      | `/api/labels`      | ✅ Requise       |
| Links       | `/api/links`       | ✅ Requise       |
| Link Groups | `/api/link-groups` | ✅ Requise       |

## 📄 License

Projet de stage - 2025
