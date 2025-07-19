# 開発環境テストユーザー

## 概要
開発環境では、アプリケーション起動時に自動的に以下のテストユーザーが作成されます。

## テストユーザー一覧

### 1. テストユーザー1
- **ユーザー名**: `testuser1`
- **メールアドレス**: `test1@example.com`
- **パスワード**: `password123` (BCryptハッシュ化済み)
- **プロフィール**:
  - ニックネーム: `テストユーザー1`
  - 性別: `MALE` (男性)
  - 生年月日: `1990-01-15`
  - 居住エリア: `東京都`
  - 職業: `会社員`
  - 年パス: `あり`

### 2. テストユーザー2
- **ユーザー名**: `testuser2`
- **メールアドレス**: `test2@example.com`
- **パスワード**: `password123` (BCryptハッシュ化済み)
- **プロフィール**:
  - ニックネーム: `テストユーザー2`
  - 性別: `FEMALE` (女性)
  - 生年月日: `1995-05-20`
  - 居住エリア: `大阪府`
  - 職業: `学生`
  - 年パス: `なし`

### 3. 管理者ユーザー
- **ユーザー名**: `admin`
- **メールアドレス**: `admin@example.com`
- **パスワード**: `password123` (BCryptハッシュ化済み)
- **プロフィール**:
  - ニックネーム: `管理者`
  - 性別: `PREFER_NOT_TO_SAY` (回答しない)
  - 生年月日: `1985-12-10`
  - 居住エリア: `神奈川県`
  - 職業: `システム管理者`
  - 年パス: `あり`

## 使用方法

### ログイン
```bash
# テストユーザー1でログイン
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test1@example.com",
    "password": "password123"
  }'
```

### プロフィール取得
```bash
# 認証トークンを使用してプロフィールを取得
curl -X GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 設定ファイル

### 初期データ
- **ファイル**: `src/main/resources/data.sql`
- **実行タイミング**: アプリケーション起動時（devプロファイル）

### 開発環境設定
- **ファイル**: `src/main/resources/application-dev.yml`
- **DDL-auto**: `create` (テーブルを毎回再作成)
- **SQL初期化**: `always` (data.sqlを実行)
- **遅延初期化**: `true` (JPAテーブル作成後にdata.sql実行)

## 注意事項

1. **開発環境専用**: これらのテストユーザーは開発環境でのみ使用してください
2. **本番環境**: 本番環境では`application-prod.yml`が使用され、初期データは投入されません
3. **パスワード**: 実際のパスワードは`password123`ですが、データベースにはBCryptハッシュ化された値が保存されています
4. **データリセット**: 開発環境では毎回テーブルが再作成されるため、データはリセットされます

## 関連ファイル

- `src/main/resources/data.sql` - 初期データ定義
- `src/main/resources/application-dev.yml` - 開発環境設定
- `src/main/resources/application-prod.yml` - 本番環境設定 