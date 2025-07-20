-- 開発環境用の初期データ
-- アプリケーション起動時に自動実行される

-- テストユーザーの挿入
INSERT INTO users (username, email, password, user_id, created_at, updated_at) VALUES
('testuser1', 'test1@example.com', '$2a$10$TGiQfIiMCx2OcJi6nM23jupcWlDSGCAvW6vOFwpCwZJU/9jjkTF5u', gen_random_uuid(), NOW(), NOW()),
('testuser2', 'test2@example.com', '$2a$10$TGiQfIiMCx2OcJi6nM23jupcWlDSGCAvW6vOFwpCwZJU/9jjkTF5u', gen_random_uuid(), NOW(), NOW()),
('admin', 'admin@example.com', '$2a$10$TGiQfIiMCx2OcJi6nM23jupcWlDSGCAvW6vOFwpCwZJU/9jjkTF5u', gen_random_uuid(), NOW(), NOW());

-- テストプロフィールの挿入
INSERT INTO profiles (user_id, nickname, gender, birthdate, area, occupation, has_annual_pass, created_at, updated_at) VALUES
(1, 'テストユーザー1', 'MALE', '1990-01-15', '東京都', '会社員', true, NOW(), NOW()),
(2, 'テストユーザー2', 'FEMALE', '1995-05-20', '大阪府', '学生', false, NOW(), NOW()),
(3, '管理者', 'PREFER_NOT_TO_SAY', '1985-12-10', '神奈川県', 'システム管理者', true, NOW(), NOW()); 