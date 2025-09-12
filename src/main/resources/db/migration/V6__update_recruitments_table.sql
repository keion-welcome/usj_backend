-- 募集テーブルにリアルタイム機能用のフィールドを追加
ALTER TABLE recruitments 
ADD COLUMN max_participants INT NOT NULL DEFAULT 4,
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
ADD COLUMN expires_at TIMESTAMP NULL;

-- 参加者テーブルに参加日時を追加
ALTER TABLE recruitment_participants 
ADD COLUMN joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;