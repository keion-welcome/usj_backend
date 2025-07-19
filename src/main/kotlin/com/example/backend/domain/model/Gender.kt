package com.example.backend.domain.model

/**
 * 性別を表す列挙型
 * ドメインの概念として定義
 */
enum class Gender {
    MALE,           // 男性
    FEMALE,         // 女性
    OTHER,          // その他
    PREFER_NOT_TO_SAY  // 回答しない
} 