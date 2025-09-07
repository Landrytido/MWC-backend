-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : dim. 07 sep. 2025 à 22:22
-- Version du serveur : 8.2.0
-- Version de PHP : 8.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `mywebcompanion`
--

-- --------------------------------------------------------

--
-- Structure de la table `bloc_notes`
--

DROP TABLE IF EXISTS `bloc_notes`;
CREATE TABLE IF NOT EXISTS `bloc_notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext COLLATE utf8mb4_general_ci,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_bloc_notes_user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `bloc_notes`
--

INSERT INTO `bloc_notes` (`id`, `content`, `user_id`, `created_at`, `updated_at`) VALUES
(1, '# Bloc-notes rapide Admin\n\n## À faire aujourd\'hui\n- [ ] Réviser le code de l\'API\n- [ ] Planifier la prochaine sprint\n- [ ] Répondre aux emails\n\n## Idées en vrac\n- Ajouter un système de tags colorés\n- Implémenter la recherche vocale\n- Mode collaboratif pour les notes\n\n## Citations inspirantes\n> \"Le code est de la poésie en mouvement\" - Anonyme\n\n---\n\n**Note**: Se rappeler de faire une sauvegarde avant le déploiement !', 1, '2024-01-15 07:00:00', '2024-01-18 13:30:00'),
(2, '# Notes de John\n\n## Apprentissage du jour\nAujourd\'hui j\'ai appris:\n- Les React Hooks personnalisés\n- L\'optimisation des performances\n- Les patterns de state management\n\n## Questions à poser\n1. Comment gérer les états complexes ?\n2. Quand utiliser useCallback vs useMemo ?\n3. Architecture recommandée pour les gros projets ?\n\n## Liens utiles\n- Documentation React\n- Tutoriels avancés\n- Exemples de code\n\n---\n\n**Objectif semaine**: Maîtriser les hooks avancés', 2, '2024-01-16 07:30:00', '2024-01-18 15:45:00'),
(3, '# Carnet de Marie\n\n## Planning repas semaine\n**Lundi**: Salade de quinoa\n**Mardi**: Ratatouille maison\n**Mercredi**: Sushi fait maison\n**Jeudi**: Reste + salade\n**Vendredi**: Restaurant (réserver !)\n**Samedi**: Barbecue si beau temps\n**Dimanche**: Dîner famille\n\n## Liste courses\n- Quinoa bio\n- Légumes pour ratatouille\n- Saumon pour sushi\n- Riz japonais\n- Algues nori\n\n## Voyage Japon - Checklist\n- [x] Passeport valide\n- [x] Visa (pas nécessaire)\n- [ ] Assurance voyage\n- [ ] Réservation hôtels\n- [ ] JR Pass\n- [ ] Téléphone portable international\n\n**Budget actuel**: 2,450€ / 3,000€', 3, '2024-01-17 13:00:00', '2024-01-18 17:20:00');

-- --------------------------------------------------------

--
-- Structure de la table `comments`
--

DROP TABLE IF EXISTS `comments`;
CREATE TABLE IF NOT EXISTS `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `note_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK9iabamoajs0wme1dmrp7tfqv6` (`note_id`),
  KEY `FK8omq0tc18jd43bu5tjh6jvraq` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `comments`
--

INSERT INTO `comments` (`id`, `content`, `note_id`, `user_id`, `created_at`) VALUES
(1, 'Excellente idée ! Il faudrait aussi penser à l\'accessibilité.', 2, 2, '2024-01-15 10:30:00'),
(2, 'J\'ai testé cette configuration, ça marche parfaitement.', 1, 3, '2024-01-15 11:00:00'),
(3, 'N\'oublie pas d\'ajouter du basilic frais à la fin !', 7, 1, '2024-01-17 15:30:00'),
(4, 'Super planning ! Pense à réserver les restaurants populaires à l\'avance.', 8, 2, '2024-01-17 17:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `events`
--

DROP TABLE IF EXISTS `events`;
CREATE TABLE IF NOT EXISTS `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mode` enum('PRESENTIEL','DISTANCIEL') COLLATE utf8mb4_general_ci DEFAULT 'PRESENTIEL',
  `meeting_link` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `type` enum('EVENT','TASK_BASED') COLLATE utf8mb4_general_ci DEFAULT 'EVENT',
  `task_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_events_user_id` (`user_id`),
  KEY `idx_events_start_date` (`start_date`),
  KEY `FKc8skqgaifjtrpsaolcolcy3c1` (`task_id`)
) ENGINE=MyISAM AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `events`
--

INSERT INTO `events` (`id`, `title`, `description`, `start_date`, `end_date`, `location`, `mode`, `meeting_link`, `type`, `task_id`, `user_id`, `created_at`, `updated_at`) VALUES
(1, 'Réunion équipe développement', 'Point hebdomadaire sur l\'avancement du projet', '2024-08-22 09:00:00', '2024-08-22 10:30:00', 'Salle de réunion A', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2024-08-15 13:00:00', '2024-08-15 13:00:00'),
(2, 'Formation React', 'Session de formation sur les dernières fonctionnalités React', '2024-08-25 14:00:00', '2024-08-25 17:00:00', NULL, 'DISTANCIEL', 'https://meet.google.com/abc-defg-hij', 'EVENT', NULL, 1, '2024-08-16 08:00:00', '2024-08-16 08:00:00'),
(3, 'Sprint Planning Septembre', 'Planification du sprint septembre avec toute l\'équipe', '2025-09-02 09:00:00', '2025-09-02 11:00:00', 'Salle de réunion B', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2025-08-30 08:00:00', '2025-08-30 08:00:00'),
(4, 'Workshop Sécurité', 'Formation sur les bonnes pratiques de sécurité web', '2025-09-10 14:00:00', '2025-09-10 17:00:00', NULL, 'DISTANCIEL', 'https://teams.microsoft.com/security-workshop', 'TASK_BASED', 3, 1, '2025-09-05 07:00:00', '2025-09-05 07:00:00'),
(5, 'Code Review Session', 'Session de révision du code frontend avec l\'équipe', '2025-09-15 15:00:00', '2025-09-15 17:00:00', 'Open Space', 'PRESENTIEL', NULL, 'TASK_BASED', 4, 1, '2025-09-12 09:00:00', '2025-09-12 09:00:00'),
(6, 'Conférence TypeScript', 'Participation à la conférence TypeScript avancé', '2025-09-08 09:00:00', '2025-09-08 18:00:00', 'Centre de conférences', 'PRESENTIEL', NULL, 'TASK_BASED', 7, 2, '2025-09-01 12:00:00', '2025-09-01 12:00:00'),
(7, 'Cours photographie', 'Premier cours de photographie de paysage', '2025-09-21 10:00:00', '2025-09-21 16:00:00', 'Studio Photo Nature', 'PRESENTIEL', NULL, 'TASK_BASED', 11, 3, '2025-09-15 16:00:00', '2025-09-15 16:00:00'),
(8, 'Réunion client stratégique', 'Présentation de la roadmap produit aux principaux clients', '2025-09-05 14:00:00', '2025-09-05 16:30:00', 'Salle de conférence', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2025-09-01 08:00:00', '2025-09-01 08:00:00'),
(9, 'Stand-up quotidien', 'Point quotidien de l\'équipe de développement', '2025-09-09 09:00:00', '2025-09-09 09:30:00', NULL, 'DISTANCIEL', 'https://meet.google.com/daily-standup', 'EVENT', NULL, 2, '2025-09-08 15:00:00', '2025-09-08 15:00:00'),
(10, 'Déjeuner équipe', 'Déjeuner d\'équipe pour célébrer la fin du sprint', '2025-09-20 12:00:00', '2025-09-20 14:00:00', 'Restaurant Le Moderne', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2025-09-18 14:00:00', '2025-09-18 14:00:00'),
(11, 'Séance sport', 'Session de sport en équipe - volleyball', '2025-09-25 18:00:00', '2025-09-25 20:00:00', 'Gymnase municipal', 'PRESENTIEL', NULL, 'EVENT', NULL, 2, '2025-09-22 13:00:00', '2025-09-22 13:00:00'),
(12, 'Cours de cuisine italien', 'Atelier pasta et risotto', '2025-09-28 14:00:00', '2025-09-28 17:00:00', 'École de cuisine Milano', 'PRESENTIEL', NULL, 'EVENT', NULL, 3, '2025-09-20 17:00:00', '2025-09-20 17:00:00'),
(13, 'Migration BDD - Phase 1', 'Première phase de migration de la base de données', '2025-10-05 08:00:00', '2025-10-05 12:00:00', 'Salle serveurs', 'PRESENTIEL', NULL, 'TASK_BASED', 12, 1, '2025-10-01 07:00:00', '2025-10-01 07:00:00'),
(14, 'Audit Sécurité', 'Session d\'audit de sécurité avec l\'équipe externe', '2025-10-15 09:00:00', '2025-10-15 18:00:00', NULL, 'DISTANCIEL', 'https://zoom.us/security-audit', 'TASK_BASED', 13, 1, '2025-10-08 08:00:00', '2025-10-08 08:00:00'),
(15, 'Présentation Direction', 'Présentation des nouvelles features au comité de direction', '2025-10-22 10:00:00', '2025-10-22 12:00:00', 'Salle de conférence', 'PRESENTIEL', NULL, 'TASK_BASED', 14, 1, '2025-10-18 12:00:00', '2025-10-18 12:00:00'),
(16, 'Workshop React 19', 'Atelier découverte des nouveautés React 19', '2025-10-25 09:00:00', '2025-10-25 17:00:00', 'Campus Tech', 'PRESENTIEL', NULL, 'TASK_BASED', 18, 2, '2025-10-20 13:00:00', '2025-10-20 13:00:00'),
(17, 'Consultation médecin', 'Rendez-vous médical pour bilan annuel', '2025-10-12 10:00:00', '2025-10-12 11:00:00', 'Cabinet Dr. Moreau', 'PRESENTIEL', NULL, 'TASK_BASED', 21, 3, '2025-10-08 14:00:00', '2025-10-08 14:00:00'),
(18, 'Soirée Halloween', 'Organisation de la soirée Halloween entre amis', '2025-10-31 19:00:00', '2025-10-31 23:59:00', 'Maison de Marie', 'PRESENTIEL', NULL, 'TASK_BASED', 20, 3, '2025-10-25 18:00:00', '2025-10-25 18:00:00'),
(19, 'Réunion mensuelle équipe', 'Bilan mensuel et planification novembre', '2025-10-01 10:00:00', '2025-10-01 12:00:00', 'Salle de réunion C', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2025-09-28 12:00:00', '2025-09-28 12:00:00'),
(20, 'Formation Git avancé', 'Workshop sur les bonnes pratiques Git et GitFlow', '2025-10-07 09:00:00', '2025-10-07 12:00:00', NULL, 'DISTANCIEL', 'https://teams.microsoft.com/git-workshop', 'EVENT', NULL, 2, '2025-10-03 09:00:00', '2025-10-03 09:00:00'),
(21, 'Webinar UX Design', 'Conférence en ligne sur les tendances UX 2025', '2025-10-10 14:00:00', '2025-10-10 16:00:00', NULL, 'DISTANCIEL', 'https://ux-trends-2025.com/webinar', 'EVENT', NULL, 1, '2025-10-05 14:00:00', '2025-10-05 14:00:00'),
(22, 'Rendez-vous dentiste', 'Contrôle dentaire semestriel', '2025-10-18 16:00:00', '2025-10-18 17:00:00', 'Cabinet dentaire Centre', 'PRESENTIEL', NULL, 'EVENT', NULL, 2, '2025-10-10 12:00:00', '2025-10-10 12:00:00'),
(23, 'Visite musée exposition photo', 'Exposition \"Paysages du monde\" au musée d\'art moderne', '2025-10-20 15:00:00', '2025-10-20 17:30:00', 'Musée d\'Art Moderne', 'PRESENTIEL', NULL, 'EVENT', NULL, 3, '2025-10-15 16:00:00', '2025-10-15 16:00:00'),
(24, 'Conférence DevOps', 'Participation à DevOps Days Paris 2025', '2025-10-28 09:00:00', '2025-10-28 18:00:00', 'Palais des Congrès', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2025-10-20 08:00:00', '2025-10-20 08:00:00'),
(25, 'Soirée cinéma', 'Sortie cinéma en groupe - nouveau film Marvel', '2025-10-26 20:00:00', '2025-10-26 23:00:00', 'Cinéma Gaumont', 'PRESENTIEL', NULL, 'EVENT', NULL, 2, '2025-10-22 17:00:00', '2025-10-22 17:00:00'),
(26, 'Kick-off Version 2.0', 'Lancement officiel du développement version 2.0', '2025-11-08 09:00:00', '2025-11-08 12:00:00', 'Auditorium', 'PRESENTIEL', NULL, 'TASK_BASED', 22, 1, '2025-11-05 09:00:00', '2025-11-05 09:00:00'),
(27, 'Formation DevOps Team', 'Session de formation DevOps pour toute l\'équipe', '2025-11-15 09:00:00', '2025-11-15 18:00:00', 'Salle de formation', 'PRESENTIEL', NULL, 'TASK_BASED', 23, 1, '2025-11-10 10:00:00', '2025-11-10 10:00:00'),
(28, 'Bilan Q4 - Réunion', 'Réunion de présentation du bilan du dernier trimestre', '2025-11-28 14:00:00', '2025-11-28 17:00:00', 'Salle de conférence', 'PRESENTIEL', NULL, 'TASK_BASED', 24, 1, '2025-11-25 08:00:00', '2025-11-25 08:00:00'),
(29, 'Conférence React Paris', 'Participation et présentation à React Paris 2025', '2025-11-25 09:00:00', '2025-11-25 19:00:00', 'Palais des Congrès Paris', 'PRESENTIEL', NULL, 'TASK_BASED', 28, 2, '2025-11-20 15:00:00', '2025-11-20 15:00:00'),
(30, 'Cours cuisine thaï', 'Premier cours de cuisine thaïlandaise', '2025-11-19 18:30:00', '2025-11-19 21:30:00', 'École de cuisine Asie', 'PRESENTIEL', NULL, 'TASK_BASED', 30, 3, '2025-11-15 16:00:00', '2025-11-15 16:00:00'),
(31, 'Réunion famille Noël', 'Organisation des fêtes de fin d\'année en famille', '2025-11-30 14:00:00', '2025-11-30 17:00:00', 'Maison des parents', 'PRESENTIEL', NULL, 'TASK_BASED', 29, 3, '2025-11-25 18:00:00', '2025-11-25 18:00:00'),
(32, 'Hackathon interne', 'Hackathon 48h sur l\'innovation produit', '2025-11-02 09:00:00', '2025-11-04 18:00:00', 'Open Space Innovation', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2025-10-28 14:00:00', '2025-10-28 14:00:00'),
(33, 'Meetup JavaScript', 'Meetup mensuel de la communauté JS locale', '2025-11-05 19:00:00', '2025-11-05 22:00:00', 'Espace Coworking Tech', 'PRESENTIEL', NULL, 'EVENT', NULL, 2, '2025-11-01 15:00:00', '2025-11-01 15:00:00'),
(34, 'Formation Kubernetes', 'Workshop pratique sur le déploiement avec Kubernetes', '2025-11-12 09:00:00', '2025-11-12 17:00:00', NULL, 'DISTANCIEL', 'https://k8s-workshop.tech', 'EVENT', NULL, 1, '2025-11-05 13:00:00', '2025-11-05 13:00:00'),
(35, 'Marché de Noël', 'Visite du marché de Noël avec les amis', '2025-11-22 16:00:00', '2025-11-22 20:00:00', 'Place du marché', 'PRESENTIEL', NULL, 'EVENT', NULL, 3, '2025-11-18 19:00:00', '2025-11-18 19:00:00'),
(36, 'Conférence IA & Développement', 'Conférence sur l\'impact de l\'IA dans le développement', '2025-11-14 10:00:00', '2025-11-14 17:00:00', 'Centre de conférences Tech', 'PRESENTIEL', NULL, 'EVENT', NULL, 2, '2025-11-08 11:00:00', '2025-11-08 11:00:00'),
(37, 'Atelier photo portrait', 'Workshop portrait et éclairage studio', '2025-11-16 13:00:00', '2025-11-16 18:00:00', 'Studio Photo Pro', 'PRESENTIEL', NULL, 'EVENT', NULL, 3, '2025-11-12 16:00:00', '2025-11-12 16:00:00'),
(38, 'Rétrospective sprint', 'Rétrospective du sprint et amélioration continue', '2025-11-21 15:00:00', '2025-11-21 17:00:00', 'Salle agile', 'PRESENTIEL', NULL, 'EVENT', NULL, 1, '2025-11-18 10:00:00', '2025-11-18 10:00:00'),
(39, 'Sortie bowling équipe', 'Activité team building au bowling', '2025-11-29 19:00:00', '2025-11-29 22:00:00', 'Bowling Strike', 'PRESENTIEL', NULL, 'EVENT', NULL, 2, '2025-11-25 15:00:00', '2025-11-25 15:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `labels`
--

DROP TABLE IF EXISTS `labels`;
CREATE TABLE IF NOT EXISTS `labels` (
  `id` varchar(191) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_labels_user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `labels`
--

INSERT INTO `labels` (`id`, `name`, `user_id`, `created_at`, `updated_at`) VALUES
('label-1-urgent', 'Urgent', 1, '2024-01-15 09:15:00', '2024-01-15 09:15:00'),
('label-1-important', 'Important', 1, '2024-01-15 09:16:00', '2024-01-15 09:16:00'),
('label-1-dev', 'Développement', 1, '2024-01-15 09:17:00', '2024-01-15 09:17:00'),
('label-1-design', 'Design', 1, '2024-01-15 09:18:00', '2024-01-15 09:18:00'),
('label-2-tech', 'Technologie', 2, '2024-01-16 08:30:00', '2024-01-16 08:30:00'),
('label-2-perso', 'Personnel', 2, '2024-01-16 08:31:00', '2024-01-16 08:31:00'),
('label-3-cuisine', 'Cuisine', 3, '2024-01-17 14:00:00', '2024-01-17 14:00:00'),
('label-3-voyage', 'Voyage', 3, '2024-01-17 14:01:00', '2024-01-17 14:01:00');

-- --------------------------------------------------------

--
-- Structure de la table `link_groups`
--

DROP TABLE IF EXISTS `link_groups`;
CREATE TABLE IF NOT EXISTS `link_groups` (
  `id` varchar(191) COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_link_groups_user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `link_groups`
--

INSERT INTO `link_groups` (`id`, `title`, `description`, `user_id`, `created_at`, `updated_at`) VALUES
('group-dev-tools', 'Outils de développement', 'Collection d\'outils essentiels pour le développement web', 1, '2024-01-15 11:00:00', '2024-01-15 11:00:00'),
('group-learning', 'Ressources d\'apprentissage', 'Sites et plateformes pour apprendre le développement', 2, '2024-01-16 09:00:00', '2024-01-16 09:00:00'),
('group-cooking', 'Cuisine et recettes', 'Mes sites préférés pour la cuisine', 3, '2024-01-17 15:30:00', '2024-01-17 15:30:00'),
('group-travel-japan', 'Voyage Japon', 'Ressources pour préparer le voyage au Japon', 3, '2024-01-17 16:30:00', '2024-01-17 16:30:00');

-- --------------------------------------------------------

--
-- Structure de la table `notebooks`
--

DROP TABLE IF EXISTS `notebooks`;
CREATE TABLE IF NOT EXISTS `notebooks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notebooks_user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `notebooks`
--

INSERT INTO `notebooks` (`id`, `title`, `user_id`, `created_at`, `updated_at`) VALUES
(1, 'Personnel', 1, '2024-01-15 09:00:00', '2024-01-15 09:00:00'),
(2, 'Travail', 1, '2024-01-15 09:05:00', '2024-01-15 09:05:00'),
(3, 'Projets', 1, '2024-01-15 09:10:00', '2024-01-15 09:10:00'),
(4, 'Idées', 2, '2024-01-16 08:00:00', '2024-01-16 08:00:00'),
(5, 'Formation', 2, '2024-01-16 08:15:00', '2024-01-16 08:15:00'),
(6, 'Recettes', 3, '2024-01-17 13:00:00', '2024-01-17 13:00:00'),
(7, 'Voyages', 3, '2024-01-17 13:30:00', '2024-01-17 13:30:00');

-- --------------------------------------------------------

--
-- Structure de la table `notes`
--

DROP TABLE IF EXISTS `notes`;
CREATE TABLE IF NOT EXISTS `notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `content` text COLLATE utf8mb4_general_ci,
  `notebook_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notes_user_id` (`user_id`),
  KEY `idx_notes_notebook_id` (`notebook_id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `notes`
--

INSERT INTO `notes` (`id`, `title`, `content`, `notebook_id`, `user_id`, `created_at`, `updated_at`) VALUES
(1, 'Configuration du projet', 'Configuration initiale du projet MyWebCompanion:\n\n## Technologies utilisées\n- React + TypeScript\n- Tailwind CSS\n- Spring Boot\n- MySQL\n\n## Étapes suivantes\n1. Finaliser l\'API\n2. Implémenter l\'authentification\n3. Tests unitaires', 2, 1, '2024-01-15 09:30:00', '2024-01-15 09:30:00'),
(2, 'Idées d\'amélioration UX', 'Liste des améliorations UX à implémenter:\n\n- [ ] Mode sombre\n- [ ] Raccourcis clavier\n- [ ] Drag & drop pour les notes\n- [ ] Recherche en temps réel\n- [ ] Notifications push\n\n## Priorités\n1. Mode sombre (demandé par 80% des utilisateurs)\n2. Raccourcis clavier\n3. Drag & drop', 2, 1, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(3, 'Notes de réunion - 15 janvier', 'Réunion équipe produit:\n\n**Participants:** John, Marie, Admin\n\n**Points abordés:**\n- Roadmap Q1 2024\n- Nouvelles fonctionnalités\n- Retours utilisateurs\n\n**Décisions:**\n- Prioriser le système de labels\n- Améliorer les performances\n- Beta test en février', 2, 1, '2024-01-15 13:00:00', '2024-01-15 13:00:00'),
(4, 'Liste de courses', 'Courses pour la semaine:\n\n**Légumes:**\n- Tomates\n- Courgettes\n- Carottes\n- Salade\n\n**Viandes:**\n- Poulet\n- Saumon\n\n**Autres:**\n- Pain\n- Lait\n- Œufs', 1, 1, '2024-01-16 07:00:00', '2024-01-16 07:00:00'),
(5, 'Apprentissage React Hooks', 'Notes sur les React Hooks:\n\n## useState\n```javascript\nconst [count, setCount] = useState(0);\n```\n\n## useEffect\n```javascript\nuseEffect(() => {\n  // Effect logic\n}, [dependencies]);\n```\n\n## useContext\nUtile pour partager l\'état global\n\n## Custom Hooks\nPour réutiliser la logique stateful', 5, 2, '2024-01-16 09:00:00', '2024-01-16 09:00:00'),
(6, 'Idée: App de méditation', 'Nouvelle idée d\'application:\n\n**Concept:** Application de méditation guidée\n\n**Fonctionnalités:**\n- Sessions guidées (5, 10, 20 min)\n- Suivi des progrès\n- Rappels quotidiens\n- Musiques relaxantes\n\n**Marché cible:**\n- Professionnels stressés\n- Étudiants\n- Seniors\n\n**Monétisation:**\n- Freemium\n- Abonnement premium', 4, 2, '2024-01-16 14:30:00', '2024-01-16 14:30:00'),
(7, 'Recette: Ratatouille', 'Ma recette de ratatouille préférée:\n\n**Ingrédients:**\n- 2 aubergines\n- 3 courgettes\n- 4 tomates\n- 1 poivron rouge\n- 1 oignon\n- 3 gousses d\'ail\n- Herbes de Provence\n\n**Préparation:**\n1. Couper tous les légumes en dés\n2. Faire revenir l\'oignon et l\'ail\n3. Ajouter les légumes un à un\n4. Laisser mijoter 45 min\n5. Assaisonner avec les herbes', 6, 3, '2024-01-17 15:00:00', '2024-01-17 15:00:00'),
(8, 'Voyage Japon - Planning', 'Planning pour le voyage au Japon (Mars 2024):\n\n**Semaine 1: Tokyo**\n- Jour 1-2: Shibuya, Harajuku\n- Jour 3-4: Asakusa, temples\n- Jour 5-7: Akihabara, gaming\n\n**Semaine 2: Kyoto**\n- Temples traditionnels\n- Bambouseraie d\'Arashiyama\n- Quartier de Gion\n\n**Budget estimé:** 3000€\n**À réserver:** Vols, hôtels, JR Pass', 7, 3, '2024-01-17 16:00:00', '2024-01-17 16:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `note_labels`
--

DROP TABLE IF EXISTS `note_labels`;
CREATE TABLE IF NOT EXISTS `note_labels` (
  `note_id` bigint NOT NULL,
  `label_id` varchar(191) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`note_id`,`label_id`),
  KEY `FK3noj0i1oyi51khfbyth53ipjb` (`label_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `note_labels`
--

INSERT INTO `note_labels` (`note_id`, `label_id`) VALUES
(1, 'label-1-dev'),
(1, 'label-1-important'),
(2, 'label-1-design'),
(2, 'label-1-important'),
(3, 'label-1-important'),
(3, 'label-1-urgent'),
(4, 'label-1-important'),
(5, 'label-2-tech'),
(6, 'label-2-perso'),
(7, 'label-3-cuisine'),
(8, 'label-3-voyage');

-- --------------------------------------------------------

--
-- Structure de la table `saved_links`
--

DROP TABLE IF EXISTS `saved_links`;
CREATE TABLE IF NOT EXISTS `saved_links` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_saved_links_user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `saved_links`
--

INSERT INTO `saved_links` (`id`, `url`, `title`, `description`, `user_id`, `created_at`, `updated_at`) VALUES
(1, 'https://react.dev/', 'Documentation React officielle', 'La documentation complète et à jour de React', 1, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(2, 'https://tailwindcss.com/', 'Tailwind CSS', 'Framework CSS utility-first pour créer des designs rapidement', 1, '2024-01-15 10:05:00', '2024-01-15 10:05:00'),
(3, 'https://spring.io/projects/spring-boot', 'Spring Boot', 'Framework Java pour créer des applications web robustes', 1, '2024-01-15 10:10:00', '2024-01-15 10:10:00'),
(4, 'https://github.com/', 'GitHub', 'Plateforme de développement collaboratif avec Git', 2, '2024-01-16 08:00:00', '2024-01-16 08:00:00'),
(5, 'https://stackoverflow.com/', 'Stack Overflow', 'Communauté de développeurs pour résoudre les problèmes', 2, '2024-01-16 08:05:00', '2024-01-16 08:05:00'),
(6, 'https://www.marmiton.org/', 'Marmiton', 'Site de recettes de cuisine françaises', 3, '2024-01-17 14:00:00', '2024-01-17 14:00:00'),
(7, 'https://www.booking.com/', 'Booking.com', 'Réservation d\'hôtels dans le monde entier', 3, '2024-01-17 14:30:00', '2024-01-17 14:30:00'),
(8, 'https://japan-guide.com/', 'Japan Guide', 'Guide complet pour voyager au Japon', 3, '2024-01-17 15:00:00', '2024-01-17 15:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `saved_link_groups`
--

DROP TABLE IF EXISTS `saved_link_groups`;
CREATE TABLE IF NOT EXISTS `saved_link_groups` (
  `link_group_id` varchar(191) COLLATE utf8mb4_general_ci NOT NULL,
  `saved_link_id` bigint NOT NULL,
  `link_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `click_counter` int DEFAULT '0',
  PRIMARY KEY (`link_group_id`,`saved_link_id`),
  KEY `FKj50vg75glbri2j0pefhwcxils` (`saved_link_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `saved_link_groups`
--

INSERT INTO `saved_link_groups` (`link_group_id`, `saved_link_id`, `link_name`, `click_counter`) VALUES
('group-dev-tools', 1, 'React Docs', 15),
('group-dev-tools', 2, 'Tailwind CSS', 8),
('group-dev-tools', 3, 'Spring Boot', 12),
('group-learning', 4, 'GitHub', 25),
('group-learning', 5, 'Stack Overflow', 40),
('group-cooking', 6, 'Marmiton', 20),
('group-travel-japan', 7, 'Booking', 5),
('group-travel-japan', 8, 'Japan Guide', 18);

-- --------------------------------------------------------

--
-- Structure de la table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
CREATE TABLE IF NOT EXISTS `tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `due_date` datetime DEFAULT NULL,
  `priority` int DEFAULT '1',
  `completed` tinyint(1) DEFAULT '0',
  `completed_at` datetime DEFAULT NULL,
  `carried_over` tinyint(1) DEFAULT '0',
  `notification_sent` tinyint(1) DEFAULT '0',
  `order_index` int DEFAULT '0',
  `token` varchar(191) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `idx_tasks_user_id` (`user_id`),
  KEY `idx_tasks_due_date` (`due_date`)
) ENGINE=MyISAM AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `tasks`
--

INSERT INTO `tasks` (`id`, `title`, `description`, `due_date`, `priority`, `completed`, `completed_at`, `carried_over`, `notification_sent`, `order_index`, `token`, `user_id`, `created_at`, `updated_at`) VALUES
(1, 'Finaliser l\'API des notes', 'Implémenter les endpoints CRUD pour les notes avec gestion des erreurs', '2024-08-15 18:00:00', 3, 1, '2024-08-14 17:30:00', 0, 0, 1, 'task_token_001', 1, '2024-08-01 07:00:00', '2024-08-14 15:30:00'),
(2, 'Acheter des légumes', 'Aller au marché pour acheter des légumes frais pour la semaine', '2024-08-20 17:00:00', 2, 1, '2024-08-19 16:00:00', 0, 0, 2, 'task_token_002', 1, '2024-08-18 06:00:00', '2024-08-19 14:00:00'),
(3, 'Mise à jour sécurité serveur', 'Appliquer les derniers patchs de sécurité sur les serveurs de production', '2025-09-10 16:00:00', 3, 1, '2025-09-10 15:45:00', 0, 0, 3, 'task_token_003', 1, '2025-09-01 07:00:00', '2025-09-10 13:45:00'),
(4, 'Révision code frontend', 'Code review du nouveau système de notifications', '2025-09-15 18:00:00', 2, 1, '2025-09-15 17:20:00', 0, 0, 4, 'task_token_004', 1, '2025-09-12 08:00:00', '2025-09-15 15:20:00'),
(5, 'Planifier sprint octobre', 'Définir les objectifs et user stories pour le sprint d\'octobre', '2025-09-25 17:00:00', 3, 0, NULL, 0, 0, 5, 'task_token_005', 1, '2025-09-20 07:00:00', '2025-09-20 07:00:00'),
(6, 'Backup base de données', 'Effectuer la sauvegarde mensuelle et tester la restauration.ok', '2025-09-30 09:00:00', 2, 0, NULL, 0, 0, 6, 'task_token_006', 1, '2025-09-25 12:00:00', '2025-09-07 19:24:37'),
(7, 'Formation TypeScript avancé', 'Suivre le cours sur les types utilitaires et les génériques', '2025-09-08 20:00:00', 2, 1, '2025-09-08 19:45:00', 0, 0, 7, 'task_token_007', 2, '2025-09-01 08:00:00', '2025-09-08 17:45:00'),
(8, 'Optimiser performances app', 'Analyser et améliorer les temps de chargement des composants', '2025-09-18 17:00:00', 3, 0, NULL, 0, 0, 8, 'task_token_008', 2, '2025-09-10 09:00:00', '2025-09-10 09:00:00'),
(9, 'Tests unitaires modules auth', 'Écrire les tests pour les hooks d\'authentification', '2025-09-28 16:00:00', 2, 0, NULL, 0, 0, 9, 'task_token_009', 2, '2025-09-20 07:30:00', '2025-09-20 07:30:00'),
(10, 'Préparer voyage automne', 'Réserver billets et hébergements pour le voyage en Italie', '2025-09-12 19:00:00', 1, 1, '2025-09-12 18:30:00', 0, 0, 10, 'task_token_010', 3, '2025-09-05 12:00:00', '2025-09-12 16:30:00'),
(11, 'Cours de photographie', 'S\'inscrire au cours de photographie de paysage', '2025-09-20 12:00:00', 1, 0, NULL, 0, 0, 11, 'task_token_011', 3, '2025-09-15 14:00:00', '2025-09-15 14:00:00'),
(12, 'Migration base de données', 'Migrer vers la nouvelle version de MySQL avec optimisations', '2025-10-05 10:00:00', 3, 0, NULL, 0, 0, 12, 'task_token_012', 1, '2025-09-28 07:00:00', '2025-09-28 07:00:00'),
(13, 'Audit sécurité complet', 'Effectuer l\'audit de sécurité trimestriel avec tests de pénétration', '2025-10-15 17:00:00', 3, 0, NULL, 0, 0, 13, 'task_token_013', 1, '2025-10-01 06:00:00', '2025-10-01 06:00:00'),
(14, 'Présentation nouvelles features', 'Préparer la démo des nouvelles fonctionnalités pour le comité de direction', '2025-10-22 14:00:00', 3, 0, NULL, 0, 0, 14, 'task_token_014', 1, '2025-10-15 08:00:00', '2025-10-15 08:00:00'),
(15, 'Configuration CI/CD', 'Mettre en place le pipeline d\'intégration continue avec Docker', '2025-10-30 16:00:00', 2, 0, NULL, 0, 0, 15, 'task_token_015', 1, '2025-10-20 07:00:00', '2025-10-20 07:00:00'),
(16, 'Refactoring composants UI', 'Refactoriser les anciens composants pour utiliser les hooks modernes', '2025-10-10 18:00:00', 2, 0, NULL, 0, 0, 16, 'task_token_016', 2, '2025-10-01 07:00:00', '2025-10-01 07:00:00'),
(17, 'Documentation API frontend', 'Documenter toutes les fonctions et hooks personnalisés', '2025-10-18 17:00:00', 2, 0, NULL, 0, 0, 17, 'task_token_017', 2, '2025-10-08 09:00:00', '2025-10-08 09:00:00'),
(18, 'Workshop React 19', 'Participer au workshop sur les nouvelles fonctionnalités de React 19', '2025-10-25 15:00:00', 1, 0, NULL, 0, 0, 18, 'task_token_018', 2, '2025-10-15 12:00:00', '2025-10-15 12:00:00'),
(19, 'Album photo Italie', 'Trier et organiser les photos du voyage en Italie', '2025-10-08 20:00:00', 1, 0, NULL, 0, 0, 19, 'task_token_019', 3, '2025-10-01 14:00:00', '2025-10-01 14:00:00'),
(20, 'Préparer menu Halloween', 'Planifier le menu thématique pour la soirée Halloween', '2025-10-28 19:00:00', 2, 0, NULL, 0, 0, 20, 'task_token_020', 3, '2025-10-20 13:00:00', '2025-10-20 13:00:00'),
(21, 'Rendez-vous médecin', 'Prendre rendez-vous pour le bilan de santé annuel', '2025-10-12 14:00:00', 2, 0, NULL, 0, 0, 21, 'task_token_021', 3, '2025-10-05 08:00:00', '2025-10-05 08:00:00'),
(22, 'Préparation version 2.0', 'Finaliser les spécifications pour la version majeure 2.0', '2025-11-08 17:00:00', 3, 0, NULL, 0, 0, 22, 'task_token_022', 1, '2025-11-01 08:00:00', '2025-11-01 08:00:00'),
(23, 'Formation équipe DevOps', 'Organiser la formation sur les pratiques DevOps avancées', '2025-11-15 16:00:00', 2, 0, NULL, 0, 0, 23, 'task_token_023', 1, '2025-11-05 09:00:00', '2025-11-05 09:00:00'),
(24, 'Bilan trimestriel Q4', 'Préparer le rapport de performance du dernier trimestre', '2025-11-28 18:00:00', 3, 0, NULL, 0, 0, 24, 'task_token_024', 1, '2025-11-20 07:00:00', '2025-11-20 07:00:00'),
(25, 'Planification 2026', 'Définir la roadmap produit pour l\'année 2026.', '2025-11-30 17:00:00', 3, 0, NULL, 0, 0, 25, 'task_token_025', 1, '2025-11-25 08:00:00', '2025-09-07 19:08:46'),
(26, 'Migration vers React 19', 'Planifier et commencer la migration vers React 19', '2025-11-12 18:00:00', 3, 0, NULL, 0, 0, 26, 'task_token_026', 2, '2025-11-01 09:00:00', '2025-11-01 09:00:00'),
(27, 'Optimisation bundle size', 'Analyser et réduire la taille du bundle JavaScript', '2025-11-20 16:00:00', 2, 0, NULL, 0, 0, 27, 'task_token_027', 2, '2025-11-10 10:00:00', '2025-11-10 10:00:00'),
(28, 'Conférence React Paris', 'Participer à la conférence React Paris et présenter notre retour d\'expérience', '2025-11-25 19:00:00', 1, 0, NULL, 0, 0, 28, 'task_token_028', 2, '2025-11-15 13:00:00', '2025-11-15 13:00:00'),
(29, 'Préparation fêtes fin d\'année', 'Commander les cadeaux et planifier les repas de Noël', '2025-11-10 20:00:00', 2, 0, NULL, 0, 0, 29, 'task_token_029', 3, '2025-11-01 15:00:00', '2025-11-01 15:00:00'),
(30, 'Cours de cuisine asiatique', 'S\'inscrire au cours de cuisine thaïlandaise', '2025-11-18 19:00:00', 1, 0, NULL, 0, 0, 30, 'task_token_030', 3, '2025-11-12 14:00:00', '2025-11-12 14:00:00'),
(31, 'Bilan photos année', 'Créer l\'album photo récapitulatif de l\'année 2025', '2025-11-30 22:00:00', 1, 0, NULL, 0, 0, 31, 'task_token_031', 3, '2025-11-20 17:00:00', '2025-11-20 17:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(191) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `first_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email_verified` tinyint(1) DEFAULT '0',
  `enabled` tinyint(1) DEFAULT '1',
  `email_verification_token` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email_verification_expiry` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `first_name`, `last_name`, `email_verified`, `enabled`, `email_verification_token`, `email_verification_expiry`, `created_at`, `updated_at`) VALUES
(1, 'admin@mywebcompanion.com', '$2a$10$nFA2h2scN2lpV.2Gp8ErBek86k1im2BpoPzHiiz3MhBDD9tkMCg5y', 'Admin', 'Principal', 1, 1, NULL, NULL, '2025-09-07 16:51:57', '2025-09-07 17:50:59'),
(2, 'john.doe@example.com', '$2a$10$nFA2h2scN2lpV.2Gp8ErBek86k1im2BpoPzHiiz3MhBDD9tkMCg5y', 'John', 'Doe', 1, 1, NULL, NULL, '2025-09-07 16:51:57', '2025-09-07 17:50:59'),
(3, 'marie.martin@example.com', '$2a$10$nFA2h2scN2lpV.2Gp8ErBek86k1im2BpoPzHiiz3MhBDD9tkMCg5y', 'Marie', 'Martin', 1, 1, NULL, NULL, '2025-09-07 16:51:57', '2025-09-07 17:50:59'),
(4, 'test@test.com', '$2a$10$nFA2h2scN2lpV.2Gp8ErBek86k1im2BpoPzHiiz3MhBDD9tkMCg5y', 'Test', 'User', 1, 1, NULL, NULL, '2025-09-07 17:46:27', '2025-09-07 17:50:59');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
