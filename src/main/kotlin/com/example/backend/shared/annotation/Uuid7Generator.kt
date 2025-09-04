package com.example.backend.shared.annotation

import com.example.backend.shared.util.Uuid7Utils
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator

/**
 * Hibernate用のUUID7ジェネレーター
 * 
 * @GeneratedUuid7アノテーションと組み合わせて使用され、
 * エンティティ保存時に自動的にUUID7を生成します。
 */
class Uuid7Generator : IdentifierGenerator {
    
    /**
     * UUID7を生成
     * 
     * @param session Hibernateセッション
     * @param object 対象エンティティ
     * @return 生成されたUUID7文字列
     */
    override fun generate(session: SharedSessionContractImplementor?, `object`: Any?): Any {
        return Uuid7Utils.generate()
    }
} 