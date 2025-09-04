package com.example.backend.shared.annotation

import org.hibernate.annotations.IdGeneratorType
import kotlin.annotation.AnnotationTarget.*
import kotlin.annotation.Retention
import kotlin.annotation.AnnotationRetention

/**
 * UUID7を自動生成するためのカスタムアノテーション
 * 
 * エンティティのフィールドにこのアノテーションを付けることで、
 * JPA保存時に自動的にUUID7が生成されます。
 * 
 * 使用例:
 * ```kotlin
 * @Entity
 * class MyEntity {
 *     @Id
 *     @GeneratedUuid7
 *     var id: String? = null
 * }
 * ```
 * 
 * 特徴:
 * - 時系列順にソート可能
 * - データベースインデックス効率が良い
 * - 分散システムで安全
 * - RFC 9562準拠
 */
@IdGeneratorType(Uuid7Generator::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(FIELD, PROPERTY, FUNCTION)
annotation class GeneratedUuid7 