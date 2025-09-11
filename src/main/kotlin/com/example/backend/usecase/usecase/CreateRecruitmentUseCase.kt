package com.example.backend.usecase.usecase

import com.example.backend.domain.model.Recruitment
import com.example.backend.usecase.gateway.RecruitmentRepositoryPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 募集作成ユースケース
 */
interface CreateRecruitmentUseCase {
    /**
     * 募集を作成する
     *
     * @param title 募集のタイトル
     * @param description 募集の詳細な説明
     * @param userId 募集を作成したユーザーのID
     * @param attractionId アトラクションID
     * @param maxParticipants 最大参加者数
     * @param expiresAt 募集締切日時
     * @return 作成された募集
     */
    fun create(
        title: String, 
        description: String, 
        userId: Long,
        attractionId: Long? = null,
        maxParticipants: Int = 4,
        expiresAt: LocalDateTime? = null
    ): Recruitment
}

/**
 * 募集作成ユースケース実装
 *
 * @property recruitmentRepositoryPort 募集リポジトリポート
 */
@Service
class CreateRecruitmentUseCaseImpl(
    private val recruitmentRepositoryPort: RecruitmentRepositoryPort
) : CreateRecruitmentUseCase {

    /**
     * 募集を作成する
     *
     * @param title 募集のタイトル
     * @param description 募集の詳細な説明
     * @param userId 募集を作成したユーザーのID
     * @param attractionId アトラクションID
     * @param maxParticipants 最大参加者数
     * @param expiresAt 募集締切日時
     * @return 作成された募集
     */
    override fun create(
        title: String, 
        description: String, 
        userId: Long,
        attractionId: Long?,
        maxParticipants: Int,
        expiresAt: LocalDateTime?
    ): Recruitment {
        if (maxParticipants < 2 || maxParticipants > 8) {
            throw IllegalArgumentException("参加者数は2人以上8人以下である必要があります")
        }

        val recruitment = Recruitment(
            title = title,
            description = description,
            userId = userId,
            attractionId = attractionId,
            maxParticipants = maxParticipants,
            expiresAt = expiresAt,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return recruitmentRepositoryPort.create(recruitment)
    }
}
