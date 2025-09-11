-- attractionsテーブルの削除とアトラクション名をVARCHARとして保存するための変更

-- 1. recruitmentsテーブルのattraction_idをattraction_nameに変更
ALTER TABLE recruitments 
DROP FOREIGN KEY recruitments_ibfk_2;

ALTER TABLE recruitments 
DROP COLUMN attraction_id,
ADD COLUMN attraction_name VARCHAR(255);

-- 2. profilesテーブルにfavorite_attraction列を追加
ALTER TABLE profiles 
ADD COLUMN favorite_attraction VARCHAR(255);

-- 3. attractionsテーブルを削除
DROP TABLE IF EXISTS attractions;