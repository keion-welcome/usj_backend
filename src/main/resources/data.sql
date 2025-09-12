-- 開発環境用の初期データ
-- アプリケーション起動時に自動実行される

-- テストユーザーの挿入 (UUIDを明示的に指定)
INSERT INTO users (id, email, password, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'test1@example.com', '$2a$10$TGiQfIiMCx2OcJi6nM23jupcWlDSGCAvW6vOFwpCwZJU/9jjkTF5u', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'test2@example.com', '$2a$10$TGiQfIiMCx2OcJi6nM23jupcWlDSGCAvW6vOFwpCwZJU/9jjkTF5u', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'admin@example.com', '$2a$10$TGiQfIiMCx2OcJi6nM23jupcWlDSGCAvW6vOFwpCwZJU/9jjkTF5u', NOW(), NOW());

-- テストプロフィールの挿入 (UUIDを使用)
INSERT INTO profiles (user_id, nickname, gender, birthdate, area, occupation, has_annual_pass, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'テストユーザー1', 'MALE', '1990-01-15', '東京都', '会社員', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'テストユーザー2', 'FEMALE', '1995-05-20', '大阪府', '学生', false, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', '管理者', 'PREFER_NOT_TO_SAY', '1985-12-10', '神奈川県', 'システム管理者', true, NOW(), NOW()); 