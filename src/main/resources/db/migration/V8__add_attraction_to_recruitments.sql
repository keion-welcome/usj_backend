-- 募集テーブルにアトラクションIDを追加
ALTER TABLE recruitments 
ADD COLUMN attraction_id BIGINT,
ADD FOREIGN KEY (attraction_id) REFERENCES attractions(id);