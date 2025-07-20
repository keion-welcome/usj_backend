# JWT認証の変更 - emailからuserIdベースへの移行

## 📋 概要

現在のemailベースJWT認証から、userId（UUID）ベースJWT認証への変更について説明します。

## 🔄 変更の背景

### 現在の問題点

1. **セキュリティリスク**
   - メールアドレスがJWTトークンに露出
   - 個人情報の漏洩リスク
   - メールアドレス変更時のトークン無効化

2. **プライバシー問題**
   - JWTトークンから個人情報が推測可能
   - ユーザー識別子として個人情報を使用

3. **パフォーマンス問題**
   - メールアドレスでの検索は非効率
   - インデックスサイズが大きい

## 🎯 変更内容

### 1. JWTトークンの変更

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

### 2. APIレスポンスの変更

**変更前:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

**変更後:(変更なし)**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

## 🔧 実装変更

### 1. JwtUtilの変更

```kotlin
@Component
class JwtUtil {
    
    // 既存の設定...
    
    /**
     * JWTトークンを生成する
     * @param userId ユーザーの外部ID（UUID）
     * @return 生成されたJWTトークン
     */
    fun generateToken(userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTimeMs)

        return Jwts.builder()
            .setSubject(userId)  // UUIDを使用
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, algorithm)
            .compact()
    }

    /**
     * トークンの有効性を検証する
     * @param token JWTトークン
     * @param userIdFromDb DBから取得したユーザーの外部ID
     * @return 有効な場合はtrue
     */
    fun validateToken(token: String, userIdFromDb: String): Boolean {
        return try {
            val userIdFromToken = getSubject(token)
            userIdFromToken == userIdFromDb && !isTokenExpired(token)
        } catch (ex: Exception) {
            false
        }
    }

    // 既存メソッド...
}
```

### 2. UserDetailsServiceの変更

```kotlin
@Service
class UserDetailsServiceImpl(
    private val securityUserRepository: SecurityUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(userId: String): UserDetails {
        // userIdパラメータは実際にはUUID
        val user = securityUserRepository.findByUserId(userId)
            ?: throw UsernameNotFoundException("User not found with userId: $userId")

        return User.builder()
            .username(user.userId)  // UUIDをusernameとして使用
            .password(user.password)
            .authorities("USER")
            .build()
    }
}
```

### 3. SecurityUserRepositoryの変更

```kotlin
interface SecurityUserRepository {
    fun findByUserId(userId: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
}

@Service
class SecurityUserRepositoryImpl(
    private val jpaUserRepository: JpaUserRepository
) : SecurityUserRepository {
    
    override fun findByUserId(userId: String): UserEntity? {
        return jpaUserRepository.findByUserId(userId)
    }
    
    override fun findByEmail(email: String): UserEntity? {
        return jpaUserRepository.findByEmail(email)
    }
}
```

### 4. JpaUserRepositoryの変更

```kotlin
@Repository
interface JpaUserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findByUserId(userId: String): UserEntity?  // 追加
}
```

### 5. ユースケースの変更

```kotlin
@Service
class LoginUseCase(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    
    fun execute(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid credentials")
        
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        
        val token = jwtUtil.generateToken(user.userId!!)  // UUIDを使用
        
        return AuthResponse(
            token = token,
            message = "Login successful"
        )
    }
}
```

## 🔄 フロントエンド側の変更

### 1. 認証処理

```javascript
// 変更前
const response = await login(email, password);
localStorage.setItem('token', response.token);
localStorage.setItem('userId', response.userId);

// 変更後
const response = await login(email, password);
localStorage.setItem('token', response.token);
// userIdはJWTトークンから取得
const userId = jwtDecode(response.token).sub;
localStorage.setItem('userId', userId);
```

### 2. JWTデコードユーティリティ

```javascript
// jwt-decodeライブラリを使用
import jwtDecode from 'jwt-decode';

export function extractUserIdFromToken(token) {
    try {
        const decoded = jwtDecode(token);
        return decoded.sub;  // userId（UUID）
    } catch (error) {
        console.error('Failed to decode JWT token:', error);
        return null;
    }
}

export function isTokenValid(token) {
    try {
        const decoded = jwtDecode(token);
        const currentTime = Date.now() / 1000;
        return decoded.exp > currentTime;
    } catch (error) {
        return false;
    }
}
```

### 3. API呼び出し

```javascript
// 認証ヘッダーの設定
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}

// API呼び出し例
async function getProfile() {
    const response = await fetch('/api/profile', {
        headers: getAuthHeaders()
    });
    return response.json();
}
```

## 🔐 セキュリティ改善

### 1. 個人情報保護

- **JWTトークン**: 個人情報（email）を含まない
- **ユーザー識別**: 予測不可能なUUID使用
- **情報漏洩**: 最小限の情報露出

### 2. トークン管理

- **有効期限**: 適切な期限設定
- **署名**: 強固な署名アルゴリズム
- **検証**: 厳密なトークン検証

### 3. アクセス制御

- **認証**: JWTトークンによる認証
- **認可**: 適切な権限チェック
- **監査**: アクセスログの記録

## 📊 変更前後の比較

| 項目 | 変更前（email） | 変更後（userId） |
|------|-----------------|------------------|
| **JWT Subject** | email | UUID |
| **セキュリティ** | 中 | 高 |
| **パフォーマンス** | 中 | 高 |
| **プライバシー** | 中 | 高 |
| **実装複雑度** | 低 | 中 |
| **フロントエンド対応** | 不要 | 必要 |

## 🚨 注意事項

### 1. フロントエンド対応

- JWTトークンのデコード機能が必要
- 既存のuserId取得ロジックの変更
- エラーハンドリングの更新

### 2. テスト

- JWT認証のテスト更新
- フロントエンド側のテスト更新
- 統合テストの実行

### 3. 移行

- 段階的な移行を推奨
- 既存トークンの無効化
- ユーザーへの事前通知

## 🔄 移行手順

### Phase 1: バックエンド変更
1. `JwtUtil`の`generateToken`メソッド変更
2. `UserDetailsService`の更新
3. リポジトリの`findByUserId`メソッド追加
4. ユースケースの更新

### Phase 2: フロントエンド対応
1. JWTデコード機能の実装
2. 認証処理の更新
3. API呼び出しの確認
4. エラーハンドリングの更新

### Phase 3: テスト・検証
1. 単体テストの更新
2. 統合テストの実行
3. セキュリティテストの実行
4. フロントエンド側のテスト

## 📝 実装チェックリスト

### バックエンド
- [ ] `JwtUtil.generateToken`メソッドの引数を`userId`に変更
- [ ] `UserDetailsService.loadUserByUsername`の更新
- [ ] `SecurityUserRepository`に`findByUserId`メソッド追加
- [ ] `JpaUserRepository`に`findByUserId`メソッド追加
- [ ] ユースケースでのJWT生成ロジック更新
- [ ] テストケースの更新

### フロントエンド
- [ ] JWTデコードライブラリの導入
- [ ] `extractUserIdFromToken`関数の実装
- [ ] 認証処理でのuserId取得ロジック変更
- [ ] API呼び出しでの認証ヘッダー確認
- [ ] エラーハンドリングの更新
- [ ] テストの更新

## 🎯 期待される効果

1. **セキュリティ向上**: 個人情報の保護
2. **パフォーマンス改善**: 効率的な認証処理
3. **プライバシー保護**: 情報漏洩の防止
4. **スケーラビリティ**: 将来の拡張性確保

---

**作成日**: 2024-01-01
**更新日**: 2024-01-01
**作成者**: 開発チーム 