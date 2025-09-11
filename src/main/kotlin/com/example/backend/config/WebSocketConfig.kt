package com.example.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

/**
 * WebSocket設定クラス
 * 
 * 募集機能のリアルタイム通信のためのWebSocket設定を行う
 */
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    /**
     * メッセージブローカーを設定する
     *
     * @param registry メッセージブローカー登録
     */
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // クライアントがメッセージを購読するためのエンドポイントプレフィックス
        // /topic/recruitment/{recruitmentId} - 特定の募集の更新を購読
        // /topic/recruitments - すべての募集の更新を購読
        registry.enableSimpleBroker("/topic", "/queue")
        
        // クライアントがメッセージを送信するためのエンドポイントプレフィックス
        registry.setApplicationDestinationPrefixes("/app")
        
        // 特定ユーザーへの通知用プレフィックス
        registry.setUserDestinationPrefix("/user")
    }

    /**
     * STOMPエンドポイントを登録する
     *
     * @param registry STOMPエンドポイント登録
     */
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // WebSocket接続エンドポイント
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*") // 開発環境用設定（本番では適切なドメインを指定）
            .withSockJS() // SockJS fallbackを有効化
    }
}
