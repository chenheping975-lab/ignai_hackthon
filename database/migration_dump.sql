-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: ignai_hackthon
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `ignai_hackthon`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ignai_hackthon` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `ignai_hackthon`;

--
-- Table structure for table `ai_tasks`
--

DROP TABLE IF EXISTS `ai_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_type` enum('registration','project','mail') NOT NULL COMMENT 'AI任务目标类型：registration=报名, project=作品, mail=邮件',
  `target_id` bigint NOT NULL,
  `task_type` enum('tagging','summary','team_match','mail_draft') NOT NULL COMMENT 'AI任务类型：tagging=标签提取, summary=摘要生成, team_match=组队匹配, mail_draft=邮件草稿',
  `status` enum('queued','running','succeeded','failed') NOT NULL DEFAULT 'queued' COMMENT 'AI任务状态：queued=排队中, running=执行中, succeeded=已完成, failed=失败',
  `prompt_snapshot` text,
  `result_json` json DEFAULT NULL,
  `error_message` text,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_tasks_target` (`target_type`,`target_id`),
  KEY `idx_ai_tasks_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_tasks`
--

LOCK TABLES `ai_tasks` WRITE;
/*!40000 ALTER TABLE `ai_tasks` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(160) NOT NULL,
  `subtitle` varchar(240) DEFAULT NULL,
  `location` varchar(240) DEFAULT NULL,
  `description` text,
  `status` enum('draft','published','running','archived') NOT NULL DEFAULT 'draft' COMMENT '活动状态：draft=草稿, published=已发布, running=比赛中, archived=已归档',
  `registration_open` tinyint(1) NOT NULL DEFAULT '0',
  `rating_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `vote_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `registration_deadline` datetime DEFAULT NULL,
  `submission_deadline` datetime DEFAULT NULL,
  `rating_start_at` datetime DEFAULT NULL,
  `rating_end_at` datetime DEFAULT NULL,
  `official_site_url` varchar(500) DEFAULT 'https://www.ignai.cn/',
  `benchmark_site_url` varchar(500) DEFAULT 'https://aihacktour.com/products',
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_events_status` (`status`),
  KEY `fk_events_created_by` (`created_by`),
  CONSTRAINT `fk_events_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'IGNAI AI Skillathon','Ignite before AGI.','长沙','面向高校、开发者和 AI 行动者的线下黑客松。','published',1,1,1,NULL,NULL,NULL,NULL,'https://www.ignai.cn/','https://aihacktour.com/products',NULL,'2026-07-04 20:04:48','2026-07-04 20:04:48');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feishu_sync_logs`
--

DROP TABLE IF EXISTS `feishu_sync_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feishu_sync_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `target_type` enum('registration','team','project','project_file','mail_log') NOT NULL COMMENT '飞书同步目标类型：registration=报名, team=队伍, project=作品, project_file=作品附件, mail_log=邮件记录',
  `target_id` bigint NOT NULL,
  `sync_type` enum('base','drive','board','export') NOT NULL DEFAULT 'base' COMMENT '飞书同步类型：base=Base多维表格, drive=Drive云盘, board=Board看板, export=数据导出',
  `status` enum('queued','running','succeeded','failed') NOT NULL DEFAULT 'queued' COMMENT '飞书同步状态：queued=排队中, running=同步中, succeeded=已完成, failed=失败',
  `remote_token` varchar(180) DEFAULT NULL,
  `remote_url` varchar(600) DEFAULT NULL,
  `payload_json` json DEFAULT NULL,
  `error_message` text,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_feishu_event_status` (`event_id`,`status`),
  KEY `idx_feishu_target` (`target_type`,`target_id`),
  CONSTRAINT `fk_feishu_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feishu_sync_logs`
--

LOCK TABLES `feishu_sync_logs` WRITE;
/*!40000 ALTER TABLE `feishu_sync_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `feishu_sync_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `form_fields`
--

DROP TABLE IF EXISTS `form_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `form_fields` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `target_type` enum('registration','project') NOT NULL COMMENT '表单目标类型：registration=报名表单, project=作品提交表单',
  `field_key` varchar(80) NOT NULL,
  `label` varchar(160) NOT NULL,
  `field_type` enum('text','textarea','radio','checkbox','select','date','file','url') NOT NULL COMMENT '字段类型：text=单行文本, textarea=多行文本, radio=单选, checkbox=多选, select=下拉选择, date=日期, file=文件上传, url=链接',
  `required` tinyint(1) NOT NULL DEFAULT '0',
  `options_json` json DEFAULT NULL,
  `placeholder` varchar(255) DEFAULT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_form_fields_key` (`event_id`,`target_type`,`field_key`),
  KEY `idx_form_fields_event_target` (`event_id`,`target_type`),
  CONSTRAINT `fk_form_fields_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_fields`
--

LOCK TABLES `form_fields` WRITE;
/*!40000 ALTER TABLE `form_fields` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mail_logs`
--

DROP TABLE IF EXISTS `mail_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mail_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `template_id` bigint DEFAULT NULL,
  `target_type` enum('registration','team','project','custom') NOT NULL COMMENT '邮件目标类型：registration=报名相关, team=队伍相关, project=作品相关, custom=自定义',
  `target_id` bigint DEFAULT NULL,
  `recipient_email` varchar(160) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `status` enum('draft','sent','failed') NOT NULL DEFAULT 'draft' COMMENT '邮件状态：draft=草稿, sent=已发送, failed=发送失败',
  `error_message` text,
  `sent_by` bigint DEFAULT NULL,
  `sent_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_mail_logs_event_status` (`event_id`,`status`),
  KEY `fk_mail_logs_template` (`template_id`),
  KEY `fk_mail_logs_sent_by` (`sent_by`),
  CONSTRAINT `fk_mail_logs_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mail_logs_sent_by` FOREIGN KEY (`sent_by`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_mail_logs_template` FOREIGN KEY (`template_id`) REFERENCES `mail_templates` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mail_logs`
--

LOCK TABLES `mail_logs` WRITE;
/*!40000 ALTER TABLE `mail_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `mail_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mail_templates`
--

DROP TABLE IF EXISTS `mail_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mail_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `scene` enum('approved','rejected','waitlist','reminder','custom') NOT NULL COMMENT '邮件场景：approved=报名通过通知, rejected=报名拒绝通知, waitlist=候补通知, reminder=提醒通知, custom=自定义',
  `name` varchar(120) NOT NULL,
  `subject_template` varchar(255) NOT NULL,
  `body_template` text NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_mail_templates_event_scene` (`event_id`,`scene`),
  CONSTRAINT `fk_mail_templates_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mail_templates`
--

LOCK TABLES `mail_templates` WRITE;
/*!40000 ALTER TABLE `mail_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `mail_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_logs`
--

DROP TABLE IF EXISTS `operation_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actor_user_id` bigint DEFAULT NULL,
  `action` varchar(120) NOT NULL,
  `target_type` varchar(80) DEFAULT NULL,
  `target_id` bigint DEFAULT NULL,
  `detail_json` json DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_operation_logs_actor` (`actor_user_id`),
  KEY `idx_operation_logs_target` (`target_type`,`target_id`),
  CONSTRAINT `fk_operation_logs_actor` FOREIGN KEY (`actor_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_logs`
--

LOCK TABLES `operation_logs` WRITE;
/*!40000 ALTER TABLE `operation_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_files`
--

DROP TABLE IF EXISTS `project_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_files` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `uploaded_by` bigint DEFAULT NULL,
  `original_name` varchar(255) NOT NULL,
  `stored_name` varchar(255) NOT NULL,
  `file_type` enum('ppt','pdf','html','zip','mp3','image','doc','other') NOT NULL COMMENT '附件类型：ppt=PPT, pdf=PDF, html=HTML原型, zip=压缩包, mp3=音频, image=图片, doc=文档, other=其他',
  `mime_type` varchar(120) DEFAULT NULL,
  `size_bytes` bigint NOT NULL DEFAULT '0',
  `sha256` varchar(80) DEFAULT NULL,
  `file_path` varchar(600) NOT NULL,
  `feishu_file_token` varchar(160) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_project_files_project` (`project_id`),
  KEY `fk_project_files_user` (`uploaded_by`),
  CONSTRAINT `fk_project_files_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_project_files_user` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_files`
--

LOCK TABLES `project_files` WRITE;
/*!40000 ALTER TABLE `project_files` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_files` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projects`
--

DROP TABLE IF EXISTS `projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `team_id` bigint NOT NULL,
  `track_id` bigint DEFAULT NULL,
  `title` varchar(160) NOT NULL,
  `tagline` varchar(240) DEFAULT NULL,
  `description` text,
  `demo_url` varchar(500) DEFAULT NULL,
  `code_url` varchar(500) DEFAULT NULL,
  `cover_file_id` bigint DEFAULT NULL,
  `status` enum('draft','submitted','locked','published','archived') NOT NULL DEFAULT 'draft' COMMENT '作品状态：draft=草稿, submitted=已提交, locked=截止后锁定, published=已展示, archived=已归档',
  `submitted_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_projects_team_event` (`event_id`,`team_id`),
  KEY `idx_projects_event_status` (`event_id`,`status`),
  KEY `idx_projects_track` (`track_id`),
  KEY `fk_projects_team` (`team_id`),
  KEY `fk_projects_cover_file` (`cover_file_id`),
  CONSTRAINT `fk_projects_cover_file` FOREIGN KEY (`cover_file_id`) REFERENCES `project_files` (`id`),
  CONSTRAINT `fk_projects_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_projects_team` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `fk_projects_track` FOREIGN KEY (`track_id`) REFERENCES `tracks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects`
--

LOCK TABLES `projects` WRITE;
/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rating_rules`
--

DROP TABLE IF EXISTS `rating_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rating_rules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `scope` enum('public','participants','onsite_code') NOT NULL DEFAULT 'public' COMMENT '评分范围：public=公开, participants=仅参赛者, onsite_code=凭现场口令',
  `max_score` decimal(4,1) NOT NULL DEFAULT '5.0',
  `duplicate_strategy` enum('login_user','phone','onsite_code','browser_fingerprint') NOT NULL DEFAULT 'browser_fingerprint' COMMENT '去重策略：login_user=登录用户, phone=手机号, onsite_code=现场口令, browser_fingerprint=浏览器指纹',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rating_rules_event` (`event_id`),
  CONSTRAINT `fk_rating_rules_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rating_rules`
--

LOCK TABLES `rating_rules` WRITE;
/*!40000 ALTER TABLE `rating_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `rating_rules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ratings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `rater_user_id` bigint DEFAULT NULL,
  `rater_key` varchar(160) NOT NULL,
  `source_type` enum('public','participant','onsite_code') NOT NULL DEFAULT 'public' COMMENT '评分来源：public=公开访客, participant=参赛者, onsite_code=现场口令用户',
  `score` decimal(4,1) NOT NULL,
  `comment` text,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ratings_project_rater` (`project_id`,`rater_key`),
  KEY `idx_ratings_project` (`project_id`),
  KEY `fk_ratings_user` (`rater_user_id`),
  CONSTRAINT `fk_ratings_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ratings_user` FOREIGN KEY (`rater_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration_members`
--

DROP TABLE IF EXISTS `registration_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registration_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `registration_id` bigint NOT NULL,
  `name` varchar(80) NOT NULL,
  `role` varchar(80) DEFAULT NULL,
  `phone` varchar(40) DEFAULT NULL,
  `email` varchar(160) DEFAULT NULL,
  `skills_json` json DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reg_member_phone` (`registration_id`,`phone`),
  CONSTRAINT `fk_reg_members_registration` FOREIGN KEY (`registration_id`) REFERENCES `registrations` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration_members`
--

LOCK TABLES `registration_members` WRITE;
/*!40000 ALTER TABLE `registration_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `registration_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registrations`
--

DROP TABLE IF EXISTS `registrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registrations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `registration_type` enum('individual','team') NOT NULL DEFAULT 'individual' COMMENT '报名类型：individual=个人报名, team=团队报名',
  `team_name` varchar(120) DEFAULT NULL,
  `contact_name` varchar(80) NOT NULL,
  `contact_phone` varchar(40) NOT NULL,
  `contact_email` varchar(160) DEFAULT NULL,
  `track_id` bigint DEFAULT NULL,
  `status` enum('pending','approved','rejected','waitlist','cancelled') NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending=待审核, approved=已通过, rejected=已拒绝, waitlist=候补, cancelled=已取消',
  `payload_json` json DEFAULT NULL,
  `ai_tags_json` json DEFAULT NULL,
  `ai_summary` text,
  `review_note` text,
  `reviewed_by` bigint DEFAULT NULL,
  `reviewed_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reg_event_phone` (`event_id`,`contact_phone`),
  KEY `idx_reg_event_status` (`event_id`,`status`),
  KEY `idx_reg_track` (`track_id`),
  KEY `fk_reg_user` (`user_id`),
  KEY `fk_reg_reviewed_by` (`reviewed_by`),
  CONSTRAINT `fk_reg_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reg_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_reg_track` FOREIGN KEY (`track_id`) REFERENCES `tracks` (`id`),
  CONSTRAINT `fk_reg_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registrations`
--

LOCK TABLES `registrations` WRITE;
/*!40000 ALTER TABLE `registrations` DISABLE KEYS */;
INSERT INTO `registrations` VALUES (1,1,6,'individual',NULL,'ceshi111','15573432532','hhh1234@qq.com',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,'2026-07-08 11:25:04','2026-07-08 11:25:04');
/*!40000 ALTER TABLE `registrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_settings`
--

DROP TABLE IF EXISTS `system_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(120) NOT NULL,
  `setting_value` text,
  `description` varchar(255) DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_system_settings_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_settings`
--

LOCK TABLES `system_settings` WRITE;
/*!40000 ALTER TABLE `system_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_members`
--

DROP TABLE IF EXISTS `team_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `name` varchar(80) NOT NULL,
  `role` varchar(80) DEFAULT NULL,
  `phone` varchar(40) DEFAULT NULL,
  `email` varchar(160) DEFAULT NULL,
  `join_status` enum('invited','joined','left') NOT NULL DEFAULT 'joined' COMMENT '成员加入状态：invited=已邀请, joined=已加入, left=已离开',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_members_phone` (`team_id`,`phone`),
  KEY `idx_team_members_user` (`user_id`),
  CONSTRAINT `fk_team_members_team` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_members_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_members`
--

LOCK TABLES `team_members` WRITE;
/*!40000 ALTER TABLE `team_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `team_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `registration_id` bigint DEFAULT NULL,
  `team_name` varchar(120) NOT NULL,
  `leader_user_id` bigint DEFAULT NULL,
  `status` enum('forming','approved','locked','archived') NOT NULL DEFAULT 'forming' COMMENT '队伍状态：forming=组建中, approved=已通过, locked=已锁定, archived=已归档',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teams_event_name` (`event_id`,`team_name`),
  KEY `fk_teams_registration` (`registration_id`),
  KEY `fk_teams_leader` (`leader_user_id`),
  CONSTRAINT `fk_teams_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_teams_leader` FOREIGN KEY (`leader_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_teams_registration` FOREIGN KEY (`registration_id`) REFERENCES `registrations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tracks`
--

DROP TABLE IF EXISTS `tracks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tracks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `name` varchar(120) NOT NULL,
  `description` text,
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tracks_event_name` (`event_id`,`name`),
  CONSTRAINT `fk_tracks_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tracks`
--

LOCK TABLES `tracks` WRITE;
/*!40000 ALTER TABLE `tracks` DISABLE KEYS */;
/*!40000 ALTER TABLE `tracks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role` enum('admin','participant') NOT NULL DEFAULT 'participant' COMMENT '用户角色：admin=管理员, participant=参赛者',
  `name` varchar(80) NOT NULL,
  `email` varchar(160) DEFAULT NULL,
  `phone` varchar(40) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `status` enum('active','disabled') NOT NULL DEFAULT 'active' COMMENT '账户状态：active=正常, disabled=已禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','IGNAI Admin','admin@ignai.cn','18800000000','replace-with-real-hash',NULL,'active','2026-07-04 20:04:48','2026-07-04 20:04:48'),(2,'participant','test1','666@qq.com','18736290762',NULL,NULL,'active','2026-07-05 16:42:33','2026-07-05 16:42:33'),(4,'participant','test2','6661@qq.com','18736290722','$2a$10$v7TcAf9Bg4LCiuGa8Pu4OuqxLM.RRsAulp25wFGoBbFptAN2WZDJW',NULL,'active','2026-07-05 16:57:55','2026-07-05 16:57:55'),(5,'participant','测试人员1','hhh123@qq.com','15587641254','$2a$10$k/iXvaD0sEaYtdU2AWvAq.9N44tFpjdl5t0ZmkMKDm9Agwc0v2zr.',NULL,'active','2026-07-05 16:58:45','2026-07-05 16:58:45'),(6,'participant','ceshi111','hhh1234@qq.com','15573432532','$2a$10$aUdRwfC4g1Ubc411RTNWQu8TkocIQJinNd9gjvBo8Stg.NsJ9VwaS',NULL,'active','2026-07-05 18:12:32','2026-07-05 18:12:32');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `votes`
--

DROP TABLE IF EXISTS `votes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `votes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `voter_user_id` bigint DEFAULT NULL,
  `voter_key` varchar(160) NOT NULL,
  `source_type` enum('public','participant','onsite_code') NOT NULL DEFAULT 'public' COMMENT '投票来源：public=公开访客, participant=参赛者, onsite_code=现场口令用户',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_votes_project_voter` (`project_id`,`voter_key`),
  KEY `idx_votes_project` (`project_id`),
  KEY `fk_votes_user` (`voter_user_id`),
  CONSTRAINT `fk_votes_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_votes_user` FOREIGN KEY (`voter_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `votes`
--

LOCK TABLES `votes` WRITE;
/*!40000 ALTER TABLE `votes` DISABLE KEYS */;
/*!40000 ALTER TABLE `votes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'ignai_hackthon'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-08 16:44:22
