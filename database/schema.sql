CREATE DATABASE IF NOT EXISTS ignai_hackthon
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE ignai_hackthon;

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role ENUM('admin', 'participant') NOT NULL DEFAULT 'participant',
  name VARCHAR(80) NOT NULL,
  email VARCHAR(160),
  phone VARCHAR(40),
  password_hash VARCHAR(255),
  avatar_url VARCHAR(500),
  status ENUM('active', 'disabled') NOT NULL DEFAULT 'active',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_users_email (email),
  UNIQUE KEY uk_users_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE events (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(160) NOT NULL,
  subtitle VARCHAR(240),
  location VARCHAR(240),
  description TEXT,
  status ENUM('draft', 'published', 'running', 'archived') NOT NULL DEFAULT 'draft',
  registration_open TINYINT(1) NOT NULL DEFAULT 0,
  rating_enabled TINYINT(1) NOT NULL DEFAULT 0,
  vote_enabled TINYINT(1) NOT NULL DEFAULT 0,
  registration_deadline DATETIME,
  submission_deadline DATETIME,
  rating_start_at DATETIME,
  rating_end_at DATETIME,
  official_site_url VARCHAR(500) DEFAULT 'https://www.ignai.cn/',
  benchmark_site_url VARCHAR(500) DEFAULT 'https://aihacktour.com/products',
  created_by BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_events_status (status),
  CONSTRAINT fk_events_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tracks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  name VARCHAR(120) NOT NULL,
  description TEXT,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_tracks_event_name (event_id, name),
  CONSTRAINT fk_tracks_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE form_fields (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  target_type ENUM('registration', 'project') NOT NULL,
  field_key VARCHAR(80) NOT NULL,
  label VARCHAR(160) NOT NULL,
  field_type ENUM('text', 'textarea', 'radio', 'checkbox', 'select', 'date', 'file', 'url') NOT NULL,
  required TINYINT(1) NOT NULL DEFAULT 0,
  options_json JSON,
  placeholder VARCHAR(255),
  sort_order INT NOT NULL DEFAULT 0,
  enabled TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_form_fields_key (event_id, target_type, field_key),
  KEY idx_form_fields_event_target (event_id, target_type),
  CONSTRAINT fk_form_fields_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE registrations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  user_id BIGINT,
  registration_type ENUM('individual', 'team') NOT NULL DEFAULT 'individual',
  team_name VARCHAR(120),
  contact_name VARCHAR(80) NOT NULL,
  contact_phone VARCHAR(40) NOT NULL,
  contact_email VARCHAR(160),
  track_id BIGINT,
  status ENUM('pending', 'approved', 'rejected', 'waitlist', 'cancelled') NOT NULL DEFAULT 'pending',
  payload_json JSON,
  ai_tags_json JSON,
  ai_summary TEXT,
  review_note TEXT,
  reviewed_by BIGINT,
  reviewed_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_reg_event_phone (event_id, contact_phone),
  KEY idx_reg_event_status (event_id, status),
  KEY idx_reg_track (track_id),
  CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT fk_reg_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_reg_track FOREIGN KEY (track_id) REFERENCES tracks(id),
  CONSTRAINT fk_reg_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE registration_members (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  registration_id BIGINT NOT NULL,
  name VARCHAR(80) NOT NULL,
  role VARCHAR(80),
  phone VARCHAR(40),
  email VARCHAR(160),
  skills_json JSON,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_reg_member_phone (registration_id, phone),
  CONSTRAINT fk_reg_members_registration FOREIGN KEY (registration_id) REFERENCES registrations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE teams (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  registration_id BIGINT,
  team_name VARCHAR(120) NOT NULL,
  leader_user_id BIGINT,
  status ENUM('forming', 'approved', 'locked', 'archived') NOT NULL DEFAULT 'forming',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_teams_event_name (event_id, team_name),
  CONSTRAINT fk_teams_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT fk_teams_registration FOREIGN KEY (registration_id) REFERENCES registrations(id),
  CONSTRAINT fk_teams_leader FOREIGN KEY (leader_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE team_members (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT NOT NULL,
  user_id BIGINT,
  name VARCHAR(80) NOT NULL,
  role VARCHAR(80),
  phone VARCHAR(40),
  email VARCHAR(160),
  join_status ENUM('invited', 'joined', 'left') NOT NULL DEFAULT 'joined',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_team_members_phone (team_id, phone),
  KEY idx_team_members_user (user_id),
  CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
  CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE projects (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  track_id BIGINT,
  title VARCHAR(160) NOT NULL,
  tagline VARCHAR(240),
  description TEXT,
  demo_url VARCHAR(500),
  code_url VARCHAR(500),
  cover_file_id BIGINT,
  status ENUM('draft', 'submitted', 'locked', 'published', 'archived') NOT NULL DEFAULT 'draft',
  submitted_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_projects_team_event (event_id, team_id),
  KEY idx_projects_event_status (event_id, status),
  KEY idx_projects_track (track_id),
  CONSTRAINT fk_projects_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT fk_projects_team FOREIGN KEY (team_id) REFERENCES teams(id),
  CONSTRAINT fk_projects_track FOREIGN KEY (track_id) REFERENCES tracks(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE project_files (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  uploaded_by BIGINT,
  original_name VARCHAR(255) NOT NULL,
  stored_name VARCHAR(255) NOT NULL,
  file_type ENUM('ppt', 'pdf', 'html', 'zip', 'mp3', 'image', 'doc', 'other') NOT NULL,
  mime_type VARCHAR(120),
  size_bytes BIGINT NOT NULL DEFAULT 0,
  sha256 VARCHAR(80),
  file_path VARCHAR(600) NOT NULL,
  feishu_file_token VARCHAR(160),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_project_files_project (project_id),
  CONSTRAINT fk_project_files_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_project_files_user FOREIGN KEY (uploaded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE projects
  ADD CONSTRAINT fk_projects_cover_file FOREIGN KEY (cover_file_id) REFERENCES project_files(id);

CREATE TABLE rating_rules (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  scope ENUM('public', 'participants', 'onsite_code') NOT NULL DEFAULT 'public',
  max_score DECIMAL(4, 1) NOT NULL DEFAULT 5.0,
  duplicate_strategy ENUM('login_user', 'phone', 'onsite_code', 'browser_fingerprint') NOT NULL DEFAULT 'browser_fingerprint',
  enabled TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_rating_rules_event (event_id),
  CONSTRAINT fk_rating_rules_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ratings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  rater_user_id BIGINT,
  rater_key VARCHAR(160) NOT NULL,
  source_type ENUM('public', 'participant', 'onsite_code') NOT NULL DEFAULT 'public',
  score DECIMAL(4, 1) NOT NULL,
  comment TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_ratings_project_rater (project_id, rater_key),
  KEY idx_ratings_project (project_id),
  CONSTRAINT fk_ratings_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_ratings_user FOREIGN KEY (rater_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE votes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  voter_user_id BIGINT,
  voter_key VARCHAR(160) NOT NULL,
  source_type ENUM('public', 'participant', 'onsite_code') NOT NULL DEFAULT 'public',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_votes_project_voter (project_id, voter_key),
  KEY idx_votes_project (project_id),
  CONSTRAINT fk_votes_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_votes_user FOREIGN KEY (voter_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE mail_templates (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  scene ENUM('approved', 'rejected', 'waitlist', 'reminder', 'custom') NOT NULL,
  name VARCHAR(120) NOT NULL,
  subject_template VARCHAR(255) NOT NULL,
  body_template TEXT NOT NULL,
  enabled TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_mail_templates_event_scene (event_id, scene),
  CONSTRAINT fk_mail_templates_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE mail_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  template_id BIGINT,
  target_type ENUM('registration', 'team', 'project', 'custom') NOT NULL,
  target_id BIGINT,
  recipient_email VARCHAR(160) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  body TEXT NOT NULL,
  status ENUM('draft', 'sent', 'failed') NOT NULL DEFAULT 'draft',
  error_message TEXT,
  sent_by BIGINT,
  sent_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_mail_logs_event_status (event_id, status),
  CONSTRAINT fk_mail_logs_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT fk_mail_logs_template FOREIGN KEY (template_id) REFERENCES mail_templates(id),
  CONSTRAINT fk_mail_logs_sent_by FOREIGN KEY (sent_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ai_tasks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  target_type ENUM('registration', 'project', 'mail') NOT NULL,
  target_id BIGINT NOT NULL,
  task_type ENUM('tagging', 'summary', 'team_match', 'mail_draft') NOT NULL,
  status ENUM('queued', 'running', 'succeeded', 'failed') NOT NULL DEFAULT 'queued',
  prompt_snapshot TEXT,
  result_json JSON,
  error_message TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_ai_tasks_target (target_type, target_id),
  KEY idx_ai_tasks_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE feishu_sync_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  target_type ENUM('registration', 'team', 'project', 'project_file', 'mail_log') NOT NULL,
  target_id BIGINT NOT NULL,
  sync_type ENUM('base', 'drive', 'board', 'export') NOT NULL DEFAULT 'base',
  status ENUM('queued', 'running', 'succeeded', 'failed') NOT NULL DEFAULT 'queued',
  remote_token VARCHAR(180),
  remote_url VARCHAR(600),
  payload_json JSON,
  error_message TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_feishu_event_status (event_id, status),
  KEY idx_feishu_target (target_type, target_id),
  CONSTRAINT fk_feishu_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE operation_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  actor_user_id BIGINT,
  action VARCHAR(120) NOT NULL,
  target_type VARCHAR(80),
  target_id BIGINT,
  detail_json JSON,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_operation_logs_actor (actor_user_id),
  KEY idx_operation_logs_target (target_type, target_id),
  CONSTRAINT fk_operation_logs_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE system_settings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setting_key VARCHAR(120) NOT NULL,
  setting_value TEXT,
  description VARCHAR(255),
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_system_settings_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (role, name, email, phone, password_hash)
VALUES ('admin', 'IGNAI Admin', 'admin@ignai.cn', '18800000000', 'replace-with-real-hash');

INSERT INTO events (
  title,
  subtitle,
  location,
  description,
  status,
  registration_open,
  rating_enabled,
  vote_enabled
) VALUES (
  'IGNAI AI Skillathon',
  'Ignite before AGI.',
  '长沙',
  '面向高校、开发者和 AI 行动者的线下黑客松。',
  'published',
  1,
  1,
  1
);
