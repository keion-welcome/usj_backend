# USJ Backend Application

USJアトラクション募集マッチングアプリのバックエンドAPIサーバーです。

## 📋 概要

このアプリケーションは、USJのアトラクションを一緒に楽しむ仲間を募集・参加できるWebアプリケーションのバックエンドAPIを提供します。

### 主な機能

- 🔐 **ユーザー認証機能**
  - ユーザー登録/ログイン/ログアウト
  - JWT認証
  - プロフィール管理

- 🎢 **アトラクション機能**
  - アトラクション一覧取得
  - 待ち時間更新
  - アトラクション検索

- 👥 **募集機能**
  - 募集作成・更新・削除
  - 募集参加・退出
  - リアルタイム通知（WebSocket）

- 💬 **リアルタイム通信**
  - WebSocketによるリアルタイム更新
  - 参加者の入退出通知
  - 募集状態の即座な反映

## 🛠 技術スタック

- **言語**: Kotlin 1.9.25
- **フレームワーク**: Spring Boot 3.5.3
- **データベース**: PostgreSQL 15
- **認証**: JWT (JSON Web Token)
- **リアルタイム通信**: WebSocket (STOMP)
- **API文書**: OpenAPI 3 (Swagger)
- **ビルドツール**: Gradle 8.x
- **コンテナ**: Docker Compose

### 主な依存関係

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter WebSocket
- Spring Boot Starter Validation
- JWT (jjwt) 0.11.5
- PostgreSQL Driver
- SpringDoc OpenAPI 2.5.0

## 🏗 アーキテクチャ

クリーンアーキテクチャを採用したレイヤー構造：

```
src/main/kotlin/com/example/backend/
├── api/                    # プレゼンテーション層
│   ├── controller/         # RESTコントローラー
│   ├── dto/               # データ転送オブジェクト
│   └── mapper/            # DTOマッパー
├── domain/                # ドメイン層
│   └── model/             # ドメインモデル
├── usecase/               # ユースケース層
│   ├── gateway/           # ポートインターフェース
│   ├── impl/              # ユースケース実装
│   └── usecase/           # ビジネスロジック
├── infrastructure/        # インフラストラクチャ層
│   ├── entity/            # JPAエンティティ
│   ├── repository/        # リポジトリ実装
│   └── security/          # セキュリティ関連
└── config/                # 設定クラス
```

## 🚀 セットアップ

### 前提条件

- Java 21
- Docker & Docker Compose
- Git

### 1. リポジトリのクローン

```bash
git clone <repository-url>
cd usj_backend
```

### 2. データベースの起動

```bash
docker-compose up -d
```

### 3. アプリケーションの起動

```bash
./gradlew bootRun
```

### 4. 動作確認

- アプリケーション: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API文書: http://localhost:8080/v3/api-docs

## 📊 データベース

### 接続情報

- **ホスト**: localhost:15432
- **データベース**: usj_db
- **ユーザー**: usj_user
- **パスワード**: secretpass

### 主要テーブル

- `users` - ユーザー情報
- `profiles` - ユーザープロフィール
- `attractions` - アトラクション情報
- `recruitments` - 募集情報
- `recruitment_participants` - 募集参加者
- `invalidated_tokens` - 無効化されたJWTトークン

## 🔌 API エンドポイント

### 認証関連
- `POST /api/auth/register` - ユーザー登録
- `POST /api/auth/login` - ログイン
- `POST /api/auth/logout` - ログアウト

### ユーザー関連
- `GET /api/users/me` - 現在のユーザー情報取得
- `POST /api/profile` - プロフィール作成

### アトラクション関連
- `GET /api/attractions` - アトラクション一覧取得
- `GET /api/attractions/active` - アクティブなアトラクション取得
- `GET /api/attractions/{id}` - アトラクション詳細取得
- `GET /api/attractions/search` - アトラクション検索
- `PUT /api/attractions/{id}/wait-time` - 待ち時間更新

### 募集関連
- `POST /api/recruitments` - 募集作成
- `GET /api/recruitments` - 募集一覧取得
- `GET /api/recruitments/active` - アクティブな募集取得
- `GET /api/recruitments/{id}` - 募集詳細取得
- `GET /api/recruitments/my` - 自分の募集取得
- `GET /api/recruitments/participating` - 参加中の募集取得
- `POST /api/recruitments/join` - 募集参加
- `POST /api/recruitments/leave` - 募集退出
- `PUT /api/recruitments/{id}` - 募集更新
- `POST /api/recruitments/{id}/cancel` - 募集キャンセル
- `POST /api/recruitments/{id}/complete` - 募集完了
- `DELETE /api/recruitments/{id}` - 募集削除

## 🔄 WebSocket エンドポイント

### 接続
- **エンドポイント**: `/ws`
- **プロトコル**: STOMP over SockJS

### 購読トピック
- `/topic/recruitment/{recruitmentId}` - 特定募集の更新
- `/topic/recruitments` - 全体の募集更新
- `/user/queue/errors` - 個人エラー通知

### 送信先
- `/app/recruitment/join` - 募集参加
- `/app/recruitment/leave` - 募集退出
- `/app/recruitment/status` - 募集状態取得

## 🧪 テスト

### テスト実行

```bash
# 全テスト実行
./gradlew test

# 特定のテスト実行
./gradlew test --tests "BackendApplicationTests"
```

### WebSocketテスト

`src/test/websocket-test/` ディレクトリにWebSocket機能のテスト用フロントエンドが含まれています。

## 📝 開発ドキュメント

`docs/` ディレクトリに以下のドキュメントが含まれています：

- `architecture/` - アーキテクチャ関連文書
  - `api-changes.md` - API変更履歴
  - `jwt-authentication-changes.md` - JWT認証の変更点
  - `user-id-hybrid-design.md` - ユーザーID設計
- `test-users.md` - テスト用ユーザー情報

## 🔧 設定

### 環境別設定

- `application.yml` - 基本設定
- `application-dev.yml` - 開発環境設定

### 環境変数

必要に応じて以下の環境変数を設定：

```bash
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:15432/usj_db
SPRING_DATASOURCE_USERNAME=usj_user
SPRING_DATASOURCE_PASSWORD=secretpass
```

## 🚀 デプロイ

### プロダクション設定

1. `application-prod.yml` の作成
2. 本番用データベース設定
3. JWTシークレットキーの変更
4. CORS設定の調整

### Docker デプロイ

```bash
# アプリケーションのビルド
./gradlew bootJar

# Dockerイメージの作成
docker build -t usj-backend .

# コンテナの起動
docker run -p 8080:8080 usj-backend
```

## 🤝 貢献

1. フォークを作成
2. フィーチャーブランチを作成 (`git checkout -b feature/amazing-feature`)
3. 変更をコミット (`git commit -m 'Add some amazing feature'`)
4. ブランチにプッシュ (`git push origin feature/amazing-feature`)
5. プルリクエストを作成

## 📄 ライセンス

このプロジェクトは[ライセンス名]の下でライセンスされています。詳細は`LICENSE`ファイルを参照してください。

## 📞 サポート

問題や質問がある場合は、以下の方法でお気軽にお問い合わせください：

- Issue を作成
- [連絡先メールアドレス]
- [その他の連絡方法]

---

⭐ このプロジェクトが役に立った場合は、ぜひスターを付けてください！