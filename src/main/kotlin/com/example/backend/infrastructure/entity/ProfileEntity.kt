package com.example.backend.infrastructure.entity

import com.example.backend.domain.model.Gender
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * プロフィール情報を表すJPAエンティティ
 *
 * @property id プロフィールの一意なID
 * @property user 関連するユーザーエンティティ（1対1関係）
 * @property nickname ニックネーム
 * @property gender 性別
 * @property birthdate 生年月日
 * @property area 居住エリア
 * @property occupation 学年・職業
 * @property hasAnnualPass 年パスの有無
 */
@Entity
@Table(name = "profiles")
class ProfileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // ユーザーとの1対1リレーションシップ
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: UserEntity,

    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var gender: Gender,

    @Column(nullable = false)
    var birthdate: LocalDate,

    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    var area: String,

    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    var occupation: String,

    @Column(nullable = false)
    var hasAnnualPass: Boolean

) : BaseEntity() {
    
    // JPA用のデフォルトコンストラクタ
    constructor() : this(
        id = null,
        user = UserEntity(
            username = "",
            email = "",
            password = ""
        ),
        nickname = "",
        gender = Gender.PREFER_NOT_TO_SAY,
        birthdate = LocalDate.now(),
        area = "",
        occupation = "",
        hasAnnualPass = false
    )
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as ProfileEntity
        return id == other.id
    }
    
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
    
    override fun toString(): String {
        return "ProfileEntity(id=$id, nickname='$nickname', gender=$gender)"
    }
}
