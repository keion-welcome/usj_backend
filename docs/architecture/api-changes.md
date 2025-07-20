# API変更仕様書 - ハイブリッド型ユーザーID設計対応

## 📋 概要

ハイブリッド型ユーザーID設計（内部ID + 外部UUID）への移行に伴うAPI変更について説明します。

## 🔄 変更前後の比較

### 認証レスポンス（変更前）

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

### 認証レスポンス（変更後）

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

## 📝 詳細なAPI変更

### 1. 認証API

#### POST /api/auth/register

**変更前:**
```json
// リクエスト
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}

// レスポンス
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Registration successful"
}
```

**変更後:**
```json
// リクエスト（変更なし）
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}

// レスポンス
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Registration successful"
}
```

#### POST /api/auth/login

**変更前:**
```json
// リクエスト
{
  "email": "test@example.com",
  "password": "password123"
}

// レスポンス
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

**変更後:**
```json
// リクエスト（変更なし）
{
  "email": "test@example.com",
  "password": "password123"
}

// レスポンス
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

### 2. プロフィールAPI

#### GET /api/profile

**変更前:**
```json
// レスポンス
{
  "id": 1,
  "nickname": "テストユーザー",
  "gender": "MALE",
  "birthdate": "1990-01-01",
  "area": "東京都",
  "occupation": "会社員",
  "hasAnnualPass": true
}
```

**変更後:**
```json
// レスポンス
{
  "profileId": 1,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "nickname": "テストユーザー",
  "gender": "MALE",
  "birthdate": "1990-01-01",
  "area": "東京都",
  "occupation": "会社員",
  "hasAnnualPass": true
}
```

#### POST /api/profile

**変更前:**
```json
// リクエスト
{
  "nickname": "新しいニックネーム",
  "gender": "FEMALE",
  "birthdate": "1995-05-15",
  "area": "大阪府",
  "occupation": "学生",
  "hasAnnualPass": false
}

// レスポンス
{
  "id": 1,
  "nickname": "新しいニックネーム",
  "gender": "FEMALE",
  "birthdate": "1995-05-15",
  "area": "大阪府",
  "occupation": "学生",
  "hasAnnualPass": false
}
```

**変更後:**
```json
// リクエスト（変更なし）
{
  "nickname": "新しいニックネーム",
  "gender": "FEMALE",
  "birthdate": "1995-05-15",
  "area": "大阪府",
  "occupation": "学生",
  "hasAnnualPass": false
}

// レスポンス
{
  "profileId": 1,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "nickname": "新しいニックネーム",
  "gender": "FEMALE",
  "birthdate": "1995-05-15",
  "area": "大阪府",
  "occupation": "学生",
  "hasAnnualPass": false
}
```

## 🔧 DTO変更詳細

### 1. AuthResponse

```kotlin
// 変更前
data class AuthResponse(
    val token: String,
    val message: String
)

// 変更後
data class AuthResponse(
    val token: String,
    val message: String
    // userIdはJWTトークンから取得
)
```

### 2. ProfileResponse

```kotlin
// 変更前
data class ProfileResponse(
    val id: Long,
    val nickname: String,
    val gender: Gender,
    val birthdate: LocalDate,
    val area: String,
    val occupation: String,
    val hasAnnualPass: Boolean
)

// 変更後
data class ProfileResponse(
    val profileId: Long,    // AUTO_INCREMENT ID
    val userId: String,     // UUID
    val nickname: String,
    val gender: Gender,
    val birthdate: LocalDate,
    val area: String,
    val occupation: String,
    val hasAnnualPass: Boolean
)
```

### 3. UserResponse（新規追加）

```kotlin
data class UserResponse(
    val userId: String,      // UUID
    val username: String,
    val email: String
    // 内部IDは含めない
)
```

## 🔄 マッパー変更

### 1. UserMapper

```kotlin
// 変更前
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        password = this.password
    )
}

// 変更後
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        userId = this.userId,  // 追加
        username = this.username,
        email = this.email,
        password = this.password
    )
}

// 新規追加（必要に応じて）
fun UserEntity.toResponse(): UserResponse {
    return UserResponse(
        userId = this.userId ?: "",
        username = this.username,
        email = this.email
    )
}
```

### 2. ProfileMapper

```kotlin
// 変更前
fun ProfileEntity.toResponse(): ProfileResponse {
    return ProfileResponse(
        id = this.id ?: 0,
        nickname = this.nickname,
        gender = this.gender,
        birthdate = this.birthdate,
        area = this.area,
        occupation = this.occupation,
        hasAnnualPass = this.hasAnnualPass
    )
}

// 変更後
fun ProfileEntity.toResponse(): ProfileResponse {
    return ProfileResponse(
        profileId = this.id ?: 0,
        userId = this.user.userId ?: "",
        nickname = this.nickname,
        gender = this.gender,
        birthdate = this.birthdate,
        area = this.area,
        occupation = this.occupation,
        hasAnnualPass = this.hasAnnualPass
    )
}
```

## 🔐 JWTトークンの変更

### トークン内容の変更

**変更前:**
```json
{
  "sub": "user@example.com",
  "iat": 1640995200,
  "exp": 1641052800
}
```

**変更後:**
```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "iat": 1640995200,
  "exp": 1641052800
}
```

### トークン生成の変更

```kotlin
// 変更前
fun generateToken(email: String): String {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, algorithm)
        .compact()
}

// 変更後
fun generateToken(userId: String): String {
    return Jwts.builder()
        .setSubject(userId)  // UUIDを使用
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, algorithm)
        .compact()
}
```

## 📊 エラーレスポンス

### 認証エラー

```json
{
  "error": "Authentication failed",
  "message": "Invalid credentials",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 認可エラー

```json
{
  "error": "Authorization failed",
  "message": "Access denied",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### バリデーションエラー

```json
{
  "error": "Validation failed",
  "message": "Invalid input data",
  "details": [
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 🔄 移行時の互換性

### 段階的移行対応

1. **Phase 1**: 新しいフィールドを追加（既存フィールドも維持）
2. **Phase 2**: フロントエンド側の対応
3. **Phase 3**: 古いフィールドの削除

### 互換性レスポンス例

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful",
  "legacy": {
    "email": "user@example.com"  // 移行期間中のみ
  }
}
```

## 📝 フロントエンド側の変更点

### 1. 認証処理

```javascript
// 変更前
const response = await login(email, password);
localStorage.setItem('token', response.token);

// 変更後
const response = await login(email, password);
localStorage.setItem('token', response.token);
// userIdはJWTトークンから取得
const userId = jwtDecode(response.token).sub;
localStorage.setItem('userId', userId);
```

### 2. API呼び出し

```javascript
// 変更前
const profile = await getProfile();
console.log(profile.id);  // 数値ID

// 変更後
const profile = await getProfile();
console.log(profile.userId);  // UUID
console.log(profile.profileId);  // 数値ID（AUTO_INCREMENT）
```

### 3. エラーハンドリング

```javascript
// 変更前
if (error.response.status === 401) {
  // 認証エラー処理
}

// 変更後（変更なし）
if (error.response.status === 401) {
  // 認証エラー処理
  localStorage.removeItem('token');
  localStorage.removeItem('userId');  // 追加
}
```

## 🚨 注意事項

### 1. セキュリティ
- 内部IDは絶対に外部APIで露出しない
- UUIDは適切に生成・管理する
- JWTトークンの有効期限を適切に設定する

### 2. パフォーマンス
- UUIDのインデックスを適切に作成する
- クエリの最適化を行う
- キャッシュ戦略を検討する

### 3. データ整合性
- 移行時のデータ整合性を必ず確認する
- 外部キー制約を適切に設定する
- トランザクション管理を適切に行う

## 📞 サポート

API変更に関する質問や問題がございましたら、開発チームまでお問い合わせください。

---

**作成日**: 2024-01-01
**更新日**: 2024-01-01
**作成者**: 開発チーム 