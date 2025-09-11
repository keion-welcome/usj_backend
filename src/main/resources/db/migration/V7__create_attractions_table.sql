-- アトラクションテーブルを作成するSQL
CREATE TABLE IF NOT EXISTS attractions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    wait_time INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 初期データの挿入
INSERT INTO attractions (name, description, wait_time, is_active) VALUES
('ハリー・ポッター・アンド・ザ・フォービドゥン・ジャーニー', 'ハリーポッターの世界を体験できるライド', 120, TRUE),
('ザ・フライング・ダイナソー', '空飛ぶ恐竜をテーマにしたジェットコースター', 90, TRUE),
('ミニオン・ハチャメチャ・ライド', 'ミニオンと一緒にハチャメチャな冒険', 60, TRUE),
('ジュラシック・パーク・ザ・ライド', '恐竜の世界を探検するボートライド', 45, TRUE),
('スパイダーマン・ザ・ライド 4K3D', 'スパイダーマンと街を駆け巡るアクション体験', 70, TRUE),
('バックドラフト', '炎の迫力を体感できるショー', 30, TRUE),
('ジョーズ', 'サメの恐怖を体験するボートライド', 40, TRUE),
('ターミネーター 2:3-D', '未来戦争の世界を体験', 35, TRUE),
('ハリウッド・ドリーム・ザ・ライド', '音楽と共に楽しむジェットコースター', 80, TRUE),
('ハリウッド・ドリーム・ザ・ライド〜バックドロップ〜', '後ろ向きで楽しむジェットコースター', 75, TRUE);