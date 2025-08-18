-- 募集に参加しているユーザーを管理する中間テーブル
CREATE TABLE recruitment_participants (
    recruitment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (recruitment_id, user_id),
    FOREIGN KEY (recruitment_id) REFERENCES recruitments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
