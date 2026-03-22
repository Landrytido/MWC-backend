-- ============================================================================
-- Schéma SQL My Web Companion
-- Généré depuis les entités JPA du backend (source de vérité)
-- SGBD cible : MySQL 8+
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS my_web_companion;
CREATE DATABASE my_web_companion
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE my_web_companion;

-- --------------------------------------------------------------------------
-- Table: users
-- --------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NULL,
    last_name VARCHAR(255) NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    email_verified TINYINT(1) NOT NULL DEFAULT 0,
    email_verification_token VARCHAR(255) NULL,
    email_verification_expiry DATETIME NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE = InnoDB;

-- --------------------------------------------------------------------------
-- Table: notebooks
-- --------------------------------------------------------------------------
CREATE TABLE notebooks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_notebooks PRIMARY KEY (id),
    CONSTRAINT fk_notebooks_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE INDEX idx_notebooks_user_id ON notebooks (user_id);

-- --------------------------------------------------------------------------
-- Table: bloc_notes
-- --------------------------------------------------------------------------
CREATE TABLE bloc_notes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content LONGTEXT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_bloc_notes PRIMARY KEY (id),
    CONSTRAINT uq_bloc_notes_user_id UNIQUE (user_id),
    CONSTRAINT fk_bloc_notes_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- --------------------------------------------------------------------------
-- Table: labels
-- --------------------------------------------------------------------------
CREATE TABLE labels (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_labels PRIMARY KEY (id),
    CONSTRAINT uq_labels_name_user UNIQUE (name, user_id),
    CONSTRAINT fk_labels_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE INDEX idx_labels_user_id ON labels (user_id);

-- --------------------------------------------------------------------------
-- Table: saved_links
-- --------------------------------------------------------------------------
CREATE TABLE saved_links (
    id BIGINT NOT NULL AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_saved_links PRIMARY KEY (id),
    CONSTRAINT fk_saved_links_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE INDEX idx_saved_links_user_id ON saved_links (user_id);

-- --------------------------------------------------------------------------
-- Table: link_groups
-- --------------------------------------------------------------------------
CREATE TABLE link_groups (
    id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_link_groups PRIMARY KEY (id),
    CONSTRAINT fk_link_groups_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE INDEX idx_link_groups_user_id ON link_groups (user_id);

-- --------------------------------------------------------------------------
-- Table: tasks
-- --------------------------------------------------------------------------
CREATE TABLE tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    due_date DATETIME NULL,
    priority INT NOT NULL DEFAULT 2,
    completed TINYINT(1) NOT NULL DEFAULT 0,
    completed_at DATETIME NULL,
    carried_over TINYINT(1) NOT NULL DEFAULT 0,
    order_index INT NOT NULL DEFAULT 0,
    notification_sent TINYINT(1) NOT NULL DEFAULT 0,
    token VARCHAR(255) NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT uq_tasks_token UNIQUE (token),
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE INDEX idx_tasks_user_id ON tasks (user_id);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);

-- --------------------------------------------------------------------------
-- Table: notes
-- --------------------------------------------------------------------------
CREATE TABLE notes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NULL,
    user_id BIGINT NOT NULL,
    notebook_id BIGINT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_notes PRIMARY KEY (id),
    CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notes_notebook FOREIGN KEY (notebook_id) REFERENCES notebooks (id)
) ENGINE = InnoDB;

CREATE INDEX idx_notes_user_id ON notes (user_id);
CREATE INDEX idx_notes_notebook_id ON notes (notebook_id);

-- --------------------------------------------------------------------------
-- Table: events
-- --------------------------------------------------------------------------
CREATE TABLE events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    location VARCHAR(255) NULL,
    mode ENUM('PRESENTIEL', 'DISTANCIEL') NULL,
    meeting_link VARCHAR(255) NULL,
    type ENUM('EVENT', 'TASK_BASED') NULL,
    task_id BIGINT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT uq_events_task_id UNIQUE (task_id),
    CONSTRAINT fk_events_task FOREIGN KEY (task_id) REFERENCES tasks (id),
    CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE INDEX idx_events_user_id ON events (user_id);
CREATE INDEX idx_events_start_date ON events (start_date);

-- --------------------------------------------------------------------------
-- Table: comments
-- --------------------------------------------------------------------------
CREATE TABLE comments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    note_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_comments_note FOREIGN KEY (note_id) REFERENCES notes (id)
) ENGINE = InnoDB;

CREATE INDEX idx_comments_user_id ON comments (user_id);
CREATE INDEX idx_comments_note_id ON comments (note_id);

-- --------------------------------------------------------------------------
-- Table de jointure: note_labels (Many-to-Many)
-- --------------------------------------------------------------------------
CREATE TABLE note_labels (
    note_id BIGINT NOT NULL,
    label_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_note_labels PRIMARY KEY (note_id, label_id),
    CONSTRAINT fk_note_labels_note FOREIGN KEY (note_id) REFERENCES notes (id),
    CONSTRAINT fk_note_labels_label FOREIGN KEY (label_id) REFERENCES labels (id)
) ENGINE = InnoDB;

CREATE INDEX idx_note_labels_label_id ON note_labels (label_id);

-- --------------------------------------------------------------------------
-- Table: saved_link_groups (association avec clé composite)
-- --------------------------------------------------------------------------
CREATE TABLE saved_link_groups (
    link_group_id VARCHAR(255) NOT NULL,
    saved_link_id BIGINT NOT NULL,
    link_name VARCHAR(255) NOT NULL,
    click_counter INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_saved_link_groups PRIMARY KEY (link_group_id, saved_link_id),
    CONSTRAINT fk_saved_link_groups_link_group FOREIGN KEY (link_group_id) REFERENCES link_groups (id),
    CONSTRAINT fk_saved_link_groups_saved_link FOREIGN KEY (saved_link_id) REFERENCES saved_links (id)
) ENGINE = InnoDB;

CREATE INDEX idx_saved_link_groups_saved_link_id ON saved_link_groups (saved_link_id);

SET FOREIGN_KEY_CHECKS = 1;
