package com.example.backend.api.controller

import com.example.backend.api.dto.response.AttractionResponse
import com.example.backend.usecase.gateway.AttractionRepositoryPort
import org.springframework.web.bind.annotation.*

/**
 * アトラクションコントローラー
 * 
 * アトラクション情報のAPIエンドポイントを提供する
 */
@RestController
@RequestMapping("/api/attractions")
class AttractionController(
    private val attractionRepositoryPort: AttractionRepositoryPort
) {

    /**
     * すべてのアトラクションを取得する
     *
     * @return アトラクションリスト
     */
    @GetMapping
    fun getAllAttractions(): List<AttractionResponse> {
        return attractionRepositoryPort.findAll()
            .map { AttractionResponse.from(it) }
    }

    /**
     * アクティブなアトラクションのみを取得する
     *
     * @return アクティブなアトラクションリスト
     */
    @GetMapping("/active")
    fun getActiveAttractions(): List<AttractionResponse> {
        return attractionRepositoryPort.findActiveAttractions()
            .map { AttractionResponse.from(it) }
    }

    /**
     * アトラクションをIDで取得する
     *
     * @param id アトラクションID
     * @return アトラクションレスポンス
     */
    @GetMapping("/{id}")
    fun getAttractionById(@PathVariable id: Long): AttractionResponse {
        val attraction = attractionRepositoryPort.findById(id)
            ?: throw Exception("Attraction not found")
        return AttractionResponse.from(attraction)
    }

    /**
     * アトラクション名で検索する
     *
     * @param name 検索する名前
     * @return 該当するアトラクションリスト
     */
    @GetMapping("/search")
    fun searchAttractions(@RequestParam name: String): List<AttractionResponse> {
        return attractionRepositoryPort.findByNameContaining(name)
            .map { AttractionResponse.from(it) }
    }

    /**
     * アトラクションの待ち時間を更新する
     *
     * @param id アトラクションID
     * @param waitTime 新しい待ち時間
     * @return 更新されたアトラクションレスポンス
     */
    @PutMapping("/{id}/wait-time")
    fun updateWaitTime(
        @PathVariable id: Long,
        @RequestParam waitTime: Int
    ): AttractionResponse {
        val updatedAttraction = attractionRepositoryPort.updateWaitTime(id, waitTime)
            ?: throw Exception("Failed to update wait time")
        return AttractionResponse.from(updatedAttraction)
    }
}