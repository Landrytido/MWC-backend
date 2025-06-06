# My Web Companion - Backend Spring

Une API REST moderne pour la gestion de notes, tâches et productivité personnelle, construite avec Spring Boot 3.3.11.

## 🚀 Fonctionnalités

### ✅ **Implémenté**

- **Authentification** - Intégration Clerk JWT sécurisée
- **Gestion des notes** - CRUD avec support Markdown
- **Organisation** - Carnets (notebooks) et étiquettes (labels)
- **Collaboration** - Système de commentaires
- **Tâches simples** - Gestion des tâches avec échéances
- **Planification quotidienne** - Tâches journalières avec historique
- **Tâches dans les notes** - Système de sous-tâches hiérarchique
- **Bloc-notes rapide** - Notes temporaires et brouillons
- **Liens sauvegardés** - Gestion de bookmarks

### 🔄 **En développement**

- Partage et collaboration avancée
- Intégration calendrier
- Module météo
- Notifications push

## 🛠️ Technologies

- **Framework** : Spring Boot 3.3.11
- **Sécurité** : Spring Security + Clerk JWT
- **Base de données** : MySQL + Spring Data JPA
- **Documentation** : Swagger/OpenAPI (à venir)
- **Tests** : REST Client (.http files)

## 📋 Prérequis

- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Compte Clerk (pour l'authentification)

## 🚀 Installation

### 1. Cloner le projet

```bash
git clone https://github.com/votre-username/my-web-companion-backend.git
cd my-web-companion-backend
```

### 2. Configuration de la base de données

```sql
CREATE DATABASE mywebcompanion;
CREATE USER 'mywebcompanion'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON mywebcompanion.* TO 'mywebcompanion'@'localhost';
```

### 3. Variables d'environnement

Créez un fichier `application-local.properties` :

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/mywebcompanion
spring.datasource.username=mywebcompanion
spring.datasource.password=password

# Clerk Authentication
clerk.jwks.url=https://your-clerk-domain.clerk.accounts.dev/.well-known/jwks.json

# Profil actif
spring.profiles.active=local
```

### 4. Démarrage

```bash
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

## 📚 API Documentation

### Authentification

Toutes les routes (sauf `/api/users/sync` et `/api/health`) nécessitent un token JWT Clerk :

```
Authorization: Bearer <jwt-token>
```

### Endpoints principaux

#### 👤 Utilisateurs

```http
GET    /api/users/profile          # Profil utilisateur
POST   /api/users/sync             # Synchronisation Clerk (public)
```

#### 📝 Notes

```http
GET    /api/notes                  # Lister toutes les notes
POST   /api/notes                  # Créer une note
PUT    /api/notes/{id}             # Modifier une note
DELETE /api/notes/{id}             # Supprimer une note
PUT    /api/notes/{id}/notebook    # Déplacer vers un carnet
```

#### 📚 Carnets (Notebooks)

```http
GET    /api/notebooks              # Lister tous les carnets
POST   /api/notebooks              # Créer un carnet
PUT    /api/notebooks/{id}         # Modifier un carnet
DELETE /api/notebooks/{id}         # Supprimer un carnet
```

#### 🏷️ Étiquettes (Labels)

```http
GET    /api/labels                 # Lister toutes les étiquettes
POST   /api/labels                 # Créer une étiquette
PUT    /api/labels/{id}            # Modifier une étiquette
DELETE /api/labels/{id}            # Supprimer une étiquette
```

#### 💭 Commentaires

```http
GET    /api/comments/notes/{id}    # Commentaires d'une note
POST   /api/comments/notes/{id}    # Ajouter un commentaire
PUT    /api/comments/{id}          # Modifier un commentaire
DELETE /api/comments/{id}          # Supprimer un commentaire
```

#### ✅ Tâches

```http
GET    /api/tasks                  # Toutes les tâches
GET    /api/tasks/pending          # Tâches en attente
GET    /api/tasks/completed        # Tâches complétées
GET    /api/tasks/overdue          # Tâches en retard
POST   /api/tasks                  # Créer une tâche
PUT    /api/tasks/{id}/toggle      # Basculer l'état
```

#### 📅 Tâches quotidiennes

```http
GET    /api/daily-tasks            # Tâches du jour
POST   /api/daily-tasks            # Créer une tâche quotidienne
PUT    /api/daily-tasks/reorder    # Réorganiser l'ordre
POST   /api/daily-tasks/confirm-end-of-day    # Confirmer fin de journée
GET    /api/daily-tasks/history?date=2025-05-20   # Historique
GET    /api/daily-tasks/report/monthly?year=2025&month=5   # Rapport mensuel
```

#### 📝 Tâches de notes

```http
GET    /api/note-tasks/notes/{id}  # Tâches d'une note
POST   /api/note-tasks/notes/{id}  # Ajouter une tâche
POST   /api/note-tasks/{id}/subtasks    # Créer une sous-tâche
PUT    /api/note-tasks/{id}/toggle # Basculer l'état
```

#### 📄 Bloc-notes

```http
GET    /api/bloc-note              # Récupérer le bloc-notes
PUT    /api/bloc-note              # Modifier le contenu
DELETE /api/bloc-note              # Effacer le contenu
```

#### 🔗 Liens sauvegardés

```http
GET    /api/links                  # Lister tous les liens
POST   /api/links                  # Ajouter un lien
PUT    /api/links/{id}             # Modifier un lien
DELETE /api/links/{id}             # Supprimer un lien
```

## 🧪 Tests

Le projet inclut un fichier `api-tests.http` pour tester tous les endpoints :

```bash
# Copier le fichier de tests
cp api-tests.http.example api-tests.http

# Remplacer YOUR_JWT_TOKEN_HERE par un vrai token
# Puis utiliser votre IDE (IntelliJ/VS Code) pour exécuter les requêtes
```

## 🏗️ Architecture

```
src/main/java/com/mywebcompanion/backendspring/
├── config/          # Configuration (sécurité, JWT)
├── controller/      # Contrôleurs REST
├── dto/            # Data Transfer Objects
├── model/          # Entités JPA
├── repository/     # Repositories Spring Data
├── security/       # Services d'authentification
└── service/        # Logique métier
```

## 📊 Modèle de données

### Entités principales :

- **User** - Utilisateurs synchronisés avec Clerk
- **Note** - Notes avec support Markdown
- **Notebook** - Carnets pour organiser les notes
- **Label** - Étiquettes pour catégoriser
- **Comment** - Commentaires sur les notes
- **Task** - Tâches simples avec échéances
- **DailyTask** - Tâches quotidiennes avec gestion d'ordre
- **NoteTask** - Tâches intégrées aux notes (avec sous-tâches)
- **BlocNote** - Bloc-notes rapide (un par utilisateur)
- **SavedLink** - Liens sauvegardés

### Relations :

- User → Notes, Tasks, Labels, etc. (1:N)
- Note → Notebook (N:1), Labels (N:N), Comments (1:N)
- NoteTask → NoteTask (auto-référence pour sous-tâches)

## 🔐 Sécurité

- **JWT Authentication** via Clerk
- **CORS** configuré pour le frontend
- **Protection CSRF** désactivée (API stateless)
- **Autorisation** basée sur l'utilisateur (clerkId)
- **Validation** des données d'entrée

## 🚀 Déploiement

### Profils Spring

- `default` - Développement local
- `prod` - Production

### Variables d'environnement

```bash
# Base de données
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mywebcompanion
DB_USERNAME=username
DB_PASSWORD=password

# Clerk
CLERK_JWKS_URL=https://your-app.clerk.accounts.dev/.well-known/jwks.json

# Autres
SPRING_PROFILES_ACTIVE=prod
```

### Docker (optionnel)

```bash
# Build
docker build -t my-web-companion-backend .

# Run
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e CLERK_JWKS_URL=your-jwks-url \
  my-web-companion-backend
```

## 📈 Monitoring

L'application expose les endpoints suivants :

- `/actuator/health` - Santé de l'application
- `/actuator/info` - Informations sur l'application

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/nouvellefonctionnalite`)
3. Commit (`git commit -m 'Ajout d'une nouvelle fonctionnalité'`)
4. Push (`git push origin feature/nouvellefonctionnalite`)
5. Ouvrir une Pull Request

## 📝 Roadmap

### Version 2.0

- [ ] Partage de notes avancé
- [ ] Système de permissions
- [ ] API pour mobile
- [ ] Notifications en temps réel

### Version 2.1

- [ ] Intégration calendrier Google
- [ ] Module météo
- [ ] Chronomètre/Pomodoro
- [ ] Recherche full-text

### Version 3.0

- [ ] Mode collaboratif
- [ ] Workspace teams
- [ ] Analytics et rapports
- [ ] API publique

## 📞 Support

- **Issues** : [GitHub Issues](https://github.com/votre-username/my-web-companion-backend/issues)
- **Discussions** : [GitHub Discussions](https://github.com/votre-username/my-web-companion-backend/discussions)

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

**Construit avec ❤️ par [Votre Nom](https://github.com/votre-username)**
