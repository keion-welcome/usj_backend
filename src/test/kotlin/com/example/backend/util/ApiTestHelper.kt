package com.example.backend.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.TestComponent
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.http.MediaType

/**
 * APIテスト用ヘルパークラス
 * 
 * Spring MockMvcを使用したAPIテストで共通的に使用する機能を提供します。
 * 認証付きリクエスト、バリデーションエラーの検証、レスポンス検証を簡素化します。
 */
@TestComponent
class ApiTestHelper(
    private val objectMapper: ObjectMapper = ObjectMapper()
) {

    /**
     * 認証付きリクエストを実行
     * 
     * @param mockMvc MockMvcインスタンス
     * @param request リクエストビルダー
     * @param token 認証トークン
     * @return リクエスト実行結果
     */
    fun performAuthenticatedRequest(
        mockMvc: MockMvc,
        request: MockHttpServletRequestBuilder,
        token: String
    ): ResultActions {
        return mockMvc.perform(
            request
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
    }

    /**
     * バリデーションエラーを検証
     * 
     * @param result テスト実行結果
     * @param field エラーが発生したフィールド名
     * @param message 期待されるエラーメッセージ
     */
    fun assertValidationError(result: ResultActions, field: String, message: String) {
        result
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.$field").value(message))
    }

    /**
     * 認証エラーを検証
     * 
     * @param result テスト実行結果
     */
    fun assertUnauthorized(result: ResultActions) {
        result.andExpect(status().isUnauthorized)
    }

    /**
     * 認可エラーを検証
     * 
     * @param result テスト実行結果
     */
    fun assertForbidden(result: ResultActions) {
        result.andExpect(status().isForbidden)
    }

    /**
     * 成功レスポンスを検証
     * 
     * @param result テスト実行結果
     * @param expectedData 期待されるレスポンスデータ
     */
    fun assertSuccessResponse(result: ResultActions, expectedData: Any? = null) {
        result.andExpect(status().isOk)
        expectedData?.let {
            val expectedJson = objectMapper.writeValueAsString(it)
            result.andExpect(content().json(expectedJson))
        }
    }

    /**
     * 作成成功レスポンスを検証
     * 
     * @param result テスト実行結果
     * @param expectedData 期待されるレスポンスデータ
     */
    fun assertCreatedResponse(result: ResultActions, expectedData: Any? = null) {
        result.andExpect(status().isCreated)
        expectedData?.let {
            val expectedJson = objectMapper.writeValueAsString(it)
            result.andExpect(content().json(expectedJson))
        }
    }

    /**
     * オブジェクトをJSON文字列に変換
     * 
     * @param obj 変換対象のオブジェクト
     * @return JSON文字列
     */
    fun toJson(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    /**
     * エラーレスポンスの構造を検証
     * 
     * @param result テスト実行結果
     * @param statusCode 期待されるHTTPステータスコード
     * @param errorMessage 期待されるエラーメッセージ
     */
    fun assertErrorResponse(result: ResultActions, statusCode: Int, errorMessage: String) {
        result
            .andExpect(status().`is`(statusCode))
            .andExpect(jsonPath("$.message").value(errorMessage))
    }
} 