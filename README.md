# USJ Backend Application

## 概要
USJアプリケーションのバックエンドAPI。ユニバーサル・スタジオ・ジャパンでの体験を支援するアプリケーションです。

## 最新の変更点 (2024年更新)

### リポジトリアーキテクチャの刷新
- **JPAとJDBCの完全分離**: データアクセス層をクリーンアーキテクチャに則って再設計
- **アダプターパターンの採用**: `adapter/jpa/` と `adapter/jdbc/` フォルダに分離
- **責任の明確化**: JPAは標準CRUD、JDBCは複雑クエリと明確に役割分担

### 削除された機能
- **集計計算機能**: JDBCリポジトリから COUNT, SUM 等の集計処理を除去
- **複雑なJOIN処理**: プロフィール情報を含む複合クエリを簡素化
- **バッチ処理機能**: ユーザーステータス一括更新機能を削除

### 新しいフォルダ構造
```
infrastructure/repository/
├── adapter/          # 新設: アダプターパターンによる分離
│   ├── jpa/         # 標準CRUD操作専用
│   └── jdbc/        # 基本操作のみ（集計計算は除外）
└── impl/            # 既存: 実装クラス（両方を使い分け）
```

## アーキテクチャ

### クリーンアーキテクチャ
本プロジェクトはクリーンアーキテクチャの原則に従って設計されています。

```
src/main/kotlin/com/example/backend/
├── api/                      # プレゼンテーション層
│   ├── controller/           # コントローラー
│   ├── dto/                  # データ転送オブジェクト
│   └── mapper/               # DTOマッパー
├── usecase/                  # ユースケース層
│   ├── gateway/              # ポートインターフェース
│   ├── impl/                 # ユースケース実装
│   └── usecase/              # ビジネスロジック
├── domain/                   # ドメイン層
│   └── model/                # ドメインモデル
├── infrastructure/           # インフラストラクチャ層
│   ├── entity/               # データベースエンティティ
│   ├── repository/           # リポジトリ実装
│   │   ├── adapter/          # データアクセスアダプター
│   │   │   ├── jpa/          # JPA実装
│   │   │   └── jdbc/         # JDBC実装
│   │   └── impl/             # リポジトリ実装クラス
│   └── security/             # セキュリティ設定
├── config/                   # 設定クラス
└── shared/                   # 共通ユーティリティ
```

### データアクセス層の設計

#### JPA vs JDBC の使い分け
本プロジェクトではJPAとJDBCを目的に応じて使い分けています。

**JPAリポジトリ (`adapter/jpa/`)**
- 標準的なCRUD操作
- エンティティ間のリレーション処理
- Spring Data JPAの自動実装機能を活用
- 型安全性とメンテナンス性を重視

**JDBCリポジトリ (`adapter/jdbc/`)**
- 複雑な検索条件（LIKE, ILIKE検索）
- 動的WHERE句の構築
- ページング処理（LIMIT/OFFSET）
- 基本的なCRUD操作
- ⚠️ **注意**: 集計計算（COUNT, SUM, AVG等）は含まれません

#### リポジトリ構成

```
infrastructure/repository/
├── adapter/
│   ├── jpa/                  # JPA実装（標準CRUD）
│   │   ├── JpaUserRepository.kt
│   │   ├── JpaAttractionRepository.kt
│   │   ├── JpaRecruitmentRepository.kt
│   │   └── JpaRecruitmentParticipantRepository.kt
│   └── jdbc/                 # JDBC実装（複雑クエリ）
│       ├── JdbcUserRepository.kt
│       ├── JdbcAttractionRepository.kt
│       └── JdbcRecruitmentRepository.kt
└── impl/                     # 実装クラス（JPAとJDBCを使い分け）
    ├── UserRepositoryImpl.kt
    ├── AttractionRepositoryImpl.kt
    └── RecruitmentRepositoryImpl.kt
```

## 技術スタック

### フレームワーク・ライブラリ
- **Spring Boot 3.x** - メインフレームワーク
- **Spring Security** - 認証・認可
- **Spring Data JPA** - データアクセス
- **Spring JDBC** - 生SQLアクセス
- **JWT** - トークンベース認証
- **WebSocket** - リアルタイム通信

### データベース
- **PostgreSQL** - メインデータベース
- **Flyway** - データベースマイグレーション

### 開発・テスト
- **Kotlin** - 開発言語
- **Gradle** - ビルドツール
- **JUnit 5** - テストフレームワーク
- **Docker** - コンテナ化

## 主要機能

### 認証・認可
- ユーザー登録・ログイン
- JWT トークンベース認証
- セキュアなセッション管理

### ユーザー管理
- プロフィール作成・更新
- ユーザー情報検索

### アトラクション機能
- アトラクション情報管理
- 待ち時間情報
- 検索・フィルタリング

### 募集機能
- 募集作成・管理
- 参加・脱退機能
- リアルタイム通知

## API エンドポイント

### 認証
- `POST /api/auth/register` - ユーザー登録
- `POST /api/auth/login` - ログイン
- `POST /api/auth/logout` - ログアウト

### ユーザー
- `GET /api/users/profile` - プロフィール取得
- `PUT /api/users/profile` - プロフィール更新

### アトラクション
- `GET /api/attractions` - アトラクション一覧
- `GET /api/attractions/{id}` - アトラクション詳細
- `GET /api/attractions/search` - アトラクション検索

### 募集
- `GET /api/recruitments` - 募集一覧
- `POST /api/recruitments` - 募集作成
- `PUT /api/recruitments/{id}` - 募集更新
- `DELETE /api/recruitments/{id}` - 募集削除
- `POST /api/recruitments/{id}/join` - 募集参加
- `DELETE /api/recruitments/{id}/leave` - 募集脱退

## セットアップ

### 必要な環境
- Java 17+
- Docker & Docker Compose
- PostgreSQL (Dockerで提供)

### 起動手順

1. **リポジトリのクローン**
```bash
git clone <repository-url>
cd usj_backend
```

2. **データベースの起動**
```bash
docker-compose up -d
```

3. **アプリケーションの起動**
```bash
./gradlew bootRun
```

4. **APIの確認**
```bash
curl http://localhost:8080/api/health
```

### 環境設定

#### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/usj_db
    username: usj_user
    password: usj_password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
```

## テスト

### テスト実行
```bash
# 全テスト実行
./gradlew test

# 統合テスト実行
./gradlew integrationTest

# テストレポート確認
open build/reports/tests/test/index.html
```

### テスト構成
- **Unit Tests** - ドメインロジック、ユースケース
- **Integration Tests** - リポジトリ、データベース連携
- **API Tests** - コントローラー、エンドツーエンド

### リポジトリテストの注意点
- **JPAリポジトリテスト**: Spring Data JPAの標準機能をテスト
- **JDBCリポジトリテスト**: 生SQLの動作確認、パフォーマンステスト
- **統合テスト**: 両方のリポジトリを組み合わせた実装クラスのテスト

## データベース設計

### 主要テーブル
- **users** - ユーザー情報
- **profiles** - ユーザープロフィール
- **attractions** - アトラクション情報
- **recruitments** - 募集情報
- **recruitment_participants** - 募集参加者
- **invalidated_tokens** - 無効化されたトークン

### マイグレーション
```bash
# アプリケーション起動時に自動実行されます
./gradlew bootRun

# 手動でマイグレーションを実行する場合は、Flywayプラグインを追加してください
# build.gradle.ktsにFlywayプラグインを設定後:
# ./gradlew flywayMigrate
# ./gradlew flywayInfo
```

## 開発ガイドライン

### コーディング規約
- Kotlinのコーディング規約に従う
- クリーンアーキテクチャの原則を遵守
- 単一責任の原則を重視
- 適切なテストカバレッジを維持

### リポジトリ実装のガイドライン
- **JPAリポジトリ**: Spring Data JPAの命名規約に従う（findBy*, countBy* 等）
- **JDBCリポジトリ**: 明示的なメソッド名を使用（createUser, updateUser 等）
- **実装クラス**: JPAとJDBCの使い分けを明確にコメントで記載
- **パッケージ構造**: `adapter/jpa/` と `adapter/jdbc/` に適切に配置

### ブランチ戦略
- `main` - 本番環境
- `develop` - 開発環境
- `feature/*` - 機能開発
- `hotfix/*` - 緊急修正

### コミットメッセージ
```
type(scope): description

feat(auth): add JWT token refresh functionality
fix(recruitment): resolve participant count issue
docs(readme): update API documentation
```

## トラブルシューティング

### よくある問題

#### データベース接続エラー
```bash
# PostgreSQLコンテナの状態確認
docker-compose ps

# ログ確認
docker-compose logs postgres
```

#### 認証エラー
```bash
# JWT秘密鍵の確認
grep jwt.secret application.yml
```

#### ビルドエラー
```bash
# 依存関係の更新
./gradlew clean build --refresh-dependencies
```

#### リポジトリ関連のエラー

**JPAリポジトリでエラーが発生する場合**
```bash
# エンティティマッピングの確認
./gradlew bootRun --debug | grep -i "hibernate"
```

**JDBCリポジトリでエラーが発生する場合**
```bash
# SQL文法の確認
# JdbcTemplate のログレベルを DEBUG に設定
echo "logging.level.org.springframework.jdbc=DEBUG" >> application.yml
```

**import文のエラー**
- パッケージ移動により import 文が古い場合は、以下に更新:
  - `import ...repository.jpa.Jpa*Repository` → `import ...repository.adapter.jpa.Jpa*Repository`
  - `import ...repository.jdbc.Jdbc*Repository` → `import ...repository.adapter.jdbc.Jdbc*Repository`
