-- 添加 root 角色到 users 表
-- root = 超级管理员，admin = 普通管理员，participant = 参赛者
ALTER TABLE users MODIFY COLUMN role ENUM('root','admin','participant') NOT NULL DEFAULT 'participant' COMMENT '用户角色：root=超级管理员, admin=管理员, participant=参赛者';
