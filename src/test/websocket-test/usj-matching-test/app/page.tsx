"use client"

import { useState, useEffect, useRef } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Users, Clock, MapPin, Wifi, WifiOff, Plus, Minus, Send } from "lucide-react"
import { Client, IMessage } from "@stomp/stompjs"

// WebSocket接続の状態を管理する型定義
interface WebSocketState {
  connected: boolean
  reconnecting: boolean
  error: string | null
}

// 募集データの型定義（バックエンドのRecruitmentResponseに合わせる）
interface Recruitment {
  id: number
  title: string
  description: string
  userId: number
  maxParticipants: number
  currentParticipants: number
  status: "ACTIVE" | "COMPLETED" | "CANCELLED"
  expiresAt: string | null
  attraction: AttractionInfo | null
  participants: ParticipantInfo[]
  isFull: boolean
  createdAt: string | null
  updatedAt: string | null
}

// アトラクション情報の型定義
interface AttractionInfo {
  id: number
  name: string
  description: string
  waitTime: number | null
  isActive: boolean
  createdAt: string | null
  updatedAt: string | null
}

// 参加者情報の型定義
interface ParticipantInfo {
  userId: number
  joinedAt: string
}

// WebSocketレスポンスの型定義（バックエンドに合わせる）
interface WebSocketResponse {
  type: "RECRUITMENT_UPDATED" | "PARTICIPANT_JOINED" | "PARTICIPANT_LEFT" | "RECRUITMENT_COMPLETED" | "RECRUITMENT_CANCELLED" | "ERROR"
  recruitmentId: number
  data?: any
  timestamp: string
}

// 募集作成リクエストの型定義
interface CreateRecruitmentRequest {
  title: string
  description: string
  attractionId?: number
  maxParticipants: number
  expiresAt?: string
}

export default function USJMatchingTest() {
  // WebSocket関連の状態管理（STOMPクライアント使用）
  const [wsState, setWsState] = useState<WebSocketState>({
    connected: false,
    reconnecting: false,
    error: null,
  })
  const stompClientRef = useRef<Client | null>(null)
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | null>(null)

  // アプリケーション状態の管理
  const [recruitments, setRecruitments] = useState<Recruitment[]>([])
  const [currentUserId, setCurrentUserId] = useState<number>(0) // テスト用のユーザーID
  const [selectedRecruitment, setSelectedRecruitment] = useState<number | null>(null)
  const [authToken] = useState<string>("test-jwt-token") // テスト用のJWTトークン
  const [isClient, setIsClient] = useState(false) // クライアントサイド判定用

  // 新規募集作成フォームの状態
  const [newRecruitment, setNewRecruitment] = useState({
    title: "",
    description: "",
    attractionId: undefined as number | undefined,
    maxParticipants: 4,
    expiresAt: "",
  })

  // 利用可能なアトラクション（テスト用）
  const availableAttractions = [
    { id: 1, name: "ハリー・ポッター・アンド・ザ・フォービドゥン・ジャーニー" },
    { id: 2, name: "ザ・フライング・ダイナソー" },
    { id: 3, name: "ミニオン・ハチャメチャ・ライド" },
    { id: 4, name: "ジュラシック・パーク・ザ・ライド" },
    { id: 5, name: "スパイダーマン・ザ・ライド" },
  ]

  // STOMP WebSocket接続を確立する関数
  const connectWebSocket = () => {
    try {
      const wsUrl = process.env.NODE_ENV === "production" 
        ? "wss://your-backend-server.com/ws" 
        : "ws://localhost:8080/ws"

      console.log("[USJ-Test] STOMP WebSocket接続を試行中:", wsUrl)

      // STOMPクライアントを作成
      const client = new Client({
        brokerURL: wsUrl,
        connectHeaders: {
          Authorization: `Bearer ${authToken}`,
        },
        debug: (str) => {
          console.log("[STOMP Debug]:", str)
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      })

      client.onConnect = (frame) => {
        console.log("[USJ-Test] STOMP接続が確立されました", frame)
        setWsState({ connected: true, reconnecting: false, error: null })

        // 全体の募集更新を購読
        client.subscribe('/topic/recruitments', (message: IMessage) => {
          try {
            const response: WebSocketResponse = JSON.parse(message.body)
            console.log("[USJ-Test] 募集更新メッセージを受信:", response)
            handleWebSocketMessage(response)
          } catch (error) {
            console.error("[USJ-Test] メッセージ解析エラー:", error)
          }
        })

        // エラーメッセージを購読
        client.subscribe(`/user/queue/errors`, (message: IMessage) => {
          try {
            const response: WebSocketResponse = JSON.parse(message.body)
            console.error("[USJ-Test] エラーメッセージを受信:", response)
            if (response.data && response.data.message) {
              setWsState(prev => ({ ...prev, error: response.data.message }))
            }
          } catch (error) {
            console.error("[USJ-Test] エラーメッセージ解析エラー:", error)
          }
        })

        // 初期データを取得（REST APIで実装されている場合）
        fetchInitialRecruitments()
      }

      client.onStompError = (frame) => {
        console.error("[USJ-Test] STOMPエラー:", frame)
        setWsState(prev => ({
          ...prev,
          error: `STOMP error: ${frame.headers['message']}`,
          connected: false,
        }))
      }

      client.onWebSocketClose = (event) => {
        console.log("[USJ-Test] WebSocket接続が閉じられました", event)
        setWsState(prev => ({ ...prev, connected: false }))
      }

      client.onDisconnect = () => {
        console.log("[USJ-Test] STOMP切断")
        setWsState(prev => ({ ...prev, connected: false }))
      }

      stompClientRef.current = client
      client.activate()

    } catch (error) {
      console.error("[USJ-Test] WebSocket接続の初期化エラー:", error)
      setWsState(prev => ({
        ...prev,
        error: "WebSocket接続の初期化に失敗しました",
        connected: false,
      }))
    }
  }

  // 初期募集データを取得する関数（REST API経由）
  const fetchInitialRecruitments = async () => {
    try {
      // モックデータで代替（実際の実装では REST API を呼び出し）
      const mockRecruitments: Recruitment[] = [
        {
          id: 1,
          title: "ハリポタエリアを一緒に楽しもう！",
          description: "一緒にハリー・ポッターの世界を楽しみましょう！",
          userId: 100,
          maxParticipants: 4,
          currentParticipants: 2,
          status: "ACTIVE",
          expiresAt: null,
          attraction: {
            id: 1,
            name: "ハリー・ポッター・アンド・ザ・フォービドゥン・ジャーニー",
            description: "魔法の世界へ",
            waitTime: 60,
            isActive: true,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          },
          participants: [
            { userId: 100, joinedAt: new Date().toISOString() },
            { userId: 101, joinedAt: new Date().toISOString() }
          ],
          isFull: false,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        },
        {
          id: 2,
          title: "絶叫系好き集まれ！",
          description: "ザ・フライング・ダイナソーで叫びましょう！",
          userId: 200,
          maxParticipants: 6,
          currentParticipants: 4,
          status: "ACTIVE",
          expiresAt: null,
          attraction: {
            id: 2,
            name: "ザ・フライング・ダイナソー",
            description: "空を飛ぶ体験",
            waitTime: 90,
            isActive: true,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          },
          participants: [
            { userId: 200, joinedAt: new Date().toISOString() },
            { userId: 201, joinedAt: new Date().toISOString() },
            { userId: 202, joinedAt: new Date().toISOString() },
            { userId: 203, joinedAt: new Date().toISOString() }
          ],
          isFull: false,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        }
      ]
      
      setRecruitments(mockRecruitments)
      console.log("[USJ-Test] 初期募集データを設定しました:", mockRecruitments)
    } catch (error) {
      console.error("[USJ-Test] 初期データ取得エラー:", error)
    }
  }

  // STOMPメッセージを送信する関数
  const sendStompMessage = (destination: string, body: any) => {
    if (stompClientRef.current && stompClientRef.current.connected) {
      stompClientRef.current.publish({
        destination,
        body: JSON.stringify(body)
      })
      console.log("[USJ-Test] STOMPメッセージを送信:", destination, body)
    } else {
      console.warn("[USJ-Test] STOMP接続が確立されていません")
      setWsState(prev => ({ ...prev, error: "WebSocket接続が確立されていません" }))
    }
  }

  // WebSocketメッセージを処理する関数
  const handleWebSocketMessage = (response: WebSocketResponse) => {
    switch (response.type) {
      case "PARTICIPANT_JOINED":
        // 参加者が追加された場合の処理
        setRecruitments(prev =>
          prev.map(recruitment =>
            recruitment.id === response.recruitmentId
              ? {
                  ...recruitment,
                  currentParticipants: response.data?.currentParticipants || recruitment.currentParticipants,
                  isFull: response.data?.isFull || false,
                  participants: [...recruitment.participants, { 
                    userId: response.data?.userId || 0, 
                    joinedAt: new Date().toISOString() 
                  }]
                }
              : recruitment
          )
        )
        break

      case "PARTICIPANT_LEFT":
        // 参加者が退出した場合の処理
        setRecruitments(prev =>
          prev.map(recruitment =>
            recruitment.id === response.recruitmentId
              ? {
                  ...recruitment,
                  currentParticipants: response.data?.currentParticipants || recruitment.currentParticipants,
                  isFull: response.data?.isFull || false,
                  participants: recruitment.participants.filter(p => p.userId !== response.data?.userId)
                }
              : recruitment
          )
        )
        break

      case "RECRUITMENT_UPDATED":
        // 募集情報が更新された場合の処理
        if (response.data) {
          setRecruitments(prev => {
            const existingIndex = prev.findIndex(r => r.id === response.recruitmentId)
            if (existingIndex >= 0) {
              // 既存の募集を更新
              const updated = [...prev]
              updated[existingIndex] = response.data
              return updated
            } else {
              // 新しい募集を追加
              return [...prev, response.data]
            }
          })
        }
        break

      case "RECRUITMENT_COMPLETED":
      case "RECRUITMENT_CANCELLED":
        // 募集が完了/キャンセルされた場合の処理
        setRecruitments(prev =>
          prev.map(recruitment =>
            recruitment.id === response.recruitmentId 
              ? { 
                  ...recruitment, 
                  status: response.type === "RECRUITMENT_COMPLETED" ? "COMPLETED" : "CANCELLED" 
                }
              : recruitment
          )
        )
        break

      case "ERROR":
        // エラーが発生した場合の処理
        console.error("[USJ-Test] WebSocketエラー:", response.data)
        setWsState(prev => ({
          ...prev,
          error: response.data?.message || "不明なエラーが発生しました"
        }))
        break
    }
  }

  // クライアントサイド判定とユーザーID初期化
  useEffect(() => {
    setIsClient(true)
    setCurrentUserId(Math.floor(Math.random() * 1000) + 1)
  }, [])

  // WebSocket接続を確立（クライアントサイドでのみ実行）
  useEffect(() => {
    if (isClient) {
      connectWebSocket()
    }

    // クリーンアップ関数
    return () => {
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current)
      }
      if (stompClientRef.current) {
        stompClientRef.current.deactivate()
      }
    }
  }, [isClient])

  // 募集に参加する関数
  const joinRecruitment = (recruitmentId: number) => {
    const recruitment = recruitments.find(r => r.id === recruitmentId)
    if (recruitment && recruitment.currentParticipants < recruitment.maxParticipants && !recruitment.isFull) {
      const joinRequest = { recruitmentId }
      sendStompMessage("/app/recruitment/join", joinRequest)
      setSelectedRecruitment(recruitmentId)
    } else {
      setWsState(prev => ({ ...prev, error: "この募集には参加できません" }))
    }
  }

  // 募集から退出する関数
  const leaveRecruitment = (recruitmentId: number) => {
    const leaveRequest = { recruitmentId }
    sendStompMessage("/app/recruitment/leave", leaveRequest)
    if (selectedRecruitment === recruitmentId) {
      setSelectedRecruitment(null)
    }
  }

  // 新しい募集を作成する関数（REST API経由）
  const createRecruitment = async () => {
    if (!isClient) {
      setWsState(prev => ({ ...prev, error: "クライアントの初期化が完了していません" }))
      return
    }

    if (!newRecruitment.title.trim()) {
      setWsState(prev => ({ ...prev, error: "募集タイトルを入力してください" }))
      return
    }

    if (currentUserId === 0) {
      setWsState(prev => ({ ...prev, error: "ユーザーIDが初期化されていません" }))
      return
    }

    try {
      // エラーメッセージをクリア
      setWsState(prev => ({ ...prev, error: null }))
      // 実際の実装では REST API を呼び出し
      const createRequest: CreateRecruitmentRequest = {
        title: newRecruitment.title,
        description: newRecruitment.description,
        attractionId: newRecruitment.attractionId,
        maxParticipants: newRecruitment.maxParticipants,
        expiresAt: newRecruitment.expiresAt || undefined,
      }

      console.log("[USJ-Test] 募集作成リクエスト:", createRequest)
      console.log("[USJ-Test] 現在のユーザーID:", currentUserId)
      console.log("[USJ-Test] クライアント状態:", isClient)

      // モック実装：新しい募集をローカルで作成
      const newRecruitmentData: Recruitment = {
        id: Date.now(), // モックID
        title: createRequest.title,
        description: createRequest.description,
        userId: currentUserId,
        maxParticipants: createRequest.maxParticipants,
        currentParticipants: 1,
        status: "ACTIVE",
        expiresAt: createRequest.expiresAt || null,
        attraction: createRequest.attractionId 
          ? availableAttractions.find(a => a.id === createRequest.attractionId) 
            ? {
                id: createRequest.attractionId,
                name: availableAttractions.find(a => a.id === createRequest.attractionId)!.name,
                description: "アトラクションの説明",
                waitTime: Math.floor(Math.random() * 120) + 30,
                isActive: true,
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString()
              }
            : null
          : null,
        participants: [{ userId: currentUserId, joinedAt: new Date().toISOString() }],
        isFull: false,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }

      // ローカル状態を更新（実際の実装では WebSocket 経由で更新される）
      setRecruitments(prev => [...prev, newRecruitmentData])

      // フォームをリセット
      setNewRecruitment({
        title: "",
        description: "",
        attractionId: undefined,
        maxParticipants: 4,
        expiresAt: "",
      })

      console.log("[USJ-Test] 募集を作成しました:", newRecruitmentData)
    } catch (error) {
      console.error("[USJ-Test] 募集作成エラー:", error)
      setWsState(prev => ({ ...prev, error: "募集の作成に失敗しました" }))
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 p-4">
      <div className="max-w-6xl mx-auto space-y-6">
        {/* ヘッダー */}
        <div className="text-center space-y-2">
          <h1 className="text-4xl font-bold text-gray-900">USJマッチングアプリ</h1>
          <p className="text-lg text-gray-600">WebSocket動作テストページ</p>
        </div>

        {/* WebSocket接続状態表示 */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              {wsState.connected ? (
                <Wifi className="h-5 w-5 text-green-500" />
              ) : (
                <WifiOff className="h-5 w-5 text-red-500" />
              )}
              WebSocket接続状態
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-4">
              <Badge variant={wsState.connected ? "default" : "destructive"}>
                {wsState.connected ? "接続中" : "切断中"}
              </Badge>
              {wsState.reconnecting && <Badge variant="secondary">再接続中...</Badge>}
              {wsState.error && <span className="text-sm text-red-600">{wsState.error}</span>}
              {isClient && <span className="text-sm text-gray-600">ユーザーID: {currentUserId}</span>}
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* 募集作成フォーム */}
          <Card>
            <CardHeader>
              <CardTitle>新しい募集を作成</CardTitle>
              <CardDescription>アトラクションと時間を指定して募集を作成できます</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="title">募集タイトル</Label>
                <Input
                  id="title"
                  placeholder="例: ハリポタエリアを一緒に楽しもう！"
                  value={newRecruitment.title}
                  onChange={(e) => setNewRecruitment(prev => ({ ...prev, title: e.target.value }))}
                />
              </div>

              <div>
                <Label htmlFor="attraction">アトラクション（任意）</Label>
                <Select
                  value={newRecruitment.attractionId?.toString() || "none"}
                  onValueChange={(value) => setNewRecruitment(prev => ({ 
                    ...prev, 
                    attractionId: value !== "none" ? parseInt(value) : undefined 
                  }))}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="アトラクションを選択（任意）" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="none">選択しない</SelectItem>
                    {availableAttractions.map(attraction => (
                      <SelectItem key={attraction.id} value={attraction.id.toString()}>
                        {attraction.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div>
                <Label htmlFor="expiresAt">募集締切（任意）</Label>
                <Input
                  id="expiresAt"
                  type="datetime-local"
                  value={newRecruitment.expiresAt}
                  onChange={(e) => setNewRecruitment(prev => ({ ...prev, expiresAt: e.target.value }))}
                />
              </div>

              <div>
                <Label htmlFor="maxParticipants">最大参加人数</Label>
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() =>
                      setNewRecruitment((prev) => ({
                        ...prev,
                        maxParticipants: Math.max(2, prev.maxParticipants - 1),
                      }))
                    }
                  >
                    <Minus className="h-4 w-4" />
                  </Button>
                  <span className="w-12 text-center">{newRecruitment.maxParticipants}</span>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() =>
                      setNewRecruitment((prev) => ({
                        ...prev,
                        maxParticipants: Math.min(10, prev.maxParticipants + 1),
                      }))
                    }
                  >
                    <Plus className="h-4 w-4" />
                  </Button>
                </div>
              </div>

              <div>
                <Label htmlFor="description">説明</Label>
                <Textarea
                  id="description"
                  placeholder="募集の詳細を入力してください"
                  value={newRecruitment.description}
                  onChange={(e) => setNewRecruitment(prev => ({ ...prev, description: e.target.value }))}
                />
              </div>

              <Button 
                onClick={() => {
                  console.log("[USJ-Test] 募集作成ボタンがクリックされました")
                  createRecruitment()
                }}
                className="w-full" 
                disabled={!wsState.connected || !isClient}
              >
                募集を作成
              </Button>
            </CardContent>
          </Card>

          {/* 募集一覧 */}
          <Card>
            <CardHeader>
              <CardTitle>現在の募集一覧</CardTitle>
              <CardDescription>リアルタイムで参加状況が更新されます</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recruitments.length === 0 ? (
                  <p className="text-center text-gray-500 py-8">現在募集はありません</p>
                ) : (
                  recruitments.map((recruitment) => (
                    <Card key={recruitment.id} className="border-2">
                      <CardContent className="p-4">
                        <div className="space-y-3">
                          <div className="flex items-start justify-between">
                            <div>
                              <h3 className="font-semibold text-lg">
                                {recruitment.title}
                              </h3>
                              {recruitment.attraction && (
                                <p className="text-sm text-gray-600 flex items-center gap-2">
                                  <MapPin className="h-4 w-4" />
                                  {recruitment.attraction.name}
                                </p>
                              )}
                              {recruitment.expiresAt && (
                                <p className="text-sm text-gray-600 flex items-center gap-2">
                                  <Clock className="h-4 w-4" />
                                  締切: {new Date(recruitment.expiresAt).toLocaleString()}
                                </p>
                              )}
                            </div>
                            <Badge variant={recruitment.status === "ACTIVE" ? "default" : "secondary"}>
                              {recruitment.status === "ACTIVE" ? "募集中" : 
                               recruitment.status === "COMPLETED" ? "完了" : "キャンセル"}
                            </Badge>
                          </div>

                          {recruitment.description && (
                            <p className="text-sm text-gray-700">{recruitment.description}</p>
                          )}

                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <Users className="h-4 w-4" />
                              <span className="text-sm">
                                {recruitment.currentParticipants}/{recruitment.maxParticipants}人
                              </span>
                              <div className="flex gap-1">
                                {Array.from({ length: recruitment.maxParticipants }).map((_, i) => (
                                  <div
                                    key={i}
                                    className={`w-3 h-3 rounded-full ${
                                      i < recruitment.currentParticipants ? "bg-blue-500" : "bg-gray-200"
                                    }`}
                                  />
                                ))}
                              </div>
                            </div>

                            <div className="flex gap-2">
                              {recruitment.participants.some(p => p.userId === currentUserId) ? (
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={() => leaveRecruitment(recruitment.id)}
                                  disabled={!wsState.connected || !isClient}
                                >
                                  退出
                                </Button>
                              ) : (
                                <Button
                                  size="sm"
                                  onClick={() => joinRecruitment(recruitment.id)}
                                  disabled={
                                    !wsState.connected ||
                                    !isClient ||
                                    recruitment.isFull ||
                                    recruitment.status !== "ACTIVE" ||
                                    recruitment.userId === currentUserId
                                  }
                                >
                                  参加
                                </Button>
                              )}
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => sendStompMessage("/app/recruitment/status", recruitment.id)}
                                disabled={!wsState.connected}
                                title="募集状態を更新"
                              >
                                <Send className="h-4 w-4" />
                              </Button>
                            </div>
                          </div>

                          {recruitment.participants.length > 0 && (
                            <div className="text-xs text-gray-500">
                              参加者: {recruitment.participants.map(p => `ユーザー${p.userId}`).join(", ")}
                            </div>
                          )}
                          
                          <div className="text-xs text-gray-400">
                            作成者: ユーザー{recruitment.userId} | 
                            作成日時: {recruitment.createdAt ? new Date(recruitment.createdAt).toLocaleString() : "不明"}
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))
                )}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* テスト用の説明 */}
        <Card>
          <CardHeader>
            <CardTitle>WebSocketテスト機能について</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            <p className="text-sm text-gray-600">
              • Spring BootのSTOMP WebSocketエンドポイント（/ws）に接続を試行しています
            </p>
            <p className="text-sm text-gray-600">
              • 実際のバックエンドが起動していない場合、接続エラーが表示されます
            </p>
            <p className="text-sm text-gray-600">
              • バックエンド（localhost:8080）が起動している場合、リアルタイムで募集の参加・退出が同期されます
            </p>
            <p className="text-sm text-gray-600">
              • 募集作成は現在REST API経由で実装されており、WebSocket経由での通知が行われます
            </p>
            <p className="text-sm text-gray-600">
              • 複数のブラウザタブで開いて、リアルタイム同期を確認してください
            </p>
            
            <div className="mt-4 p-4 bg-blue-50 rounded-lg">
              <h4 className="font-semibold text-blue-800 mb-2">テスト手順:</h4>
              <ol className="text-sm text-blue-700 space-y-1 list-decimal list-inside">
                <li>Spring Bootバックエンドを起動（localhost:8080）</li>
                <li>このページを複数のタブで開く</li>
                <li>一方のタブで募集を作成</li>
                <li>もう一方のタブで参加・退出を試す</li>
                <li>リアルタイムで参加者数が更新されることを確認</li>
              </ol>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
