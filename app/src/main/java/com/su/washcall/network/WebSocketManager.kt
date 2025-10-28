//// WebSocketManager.kt (새로운 파일)
//import android.util.Log
//import com.su.washcall.repository.LaundryRepository
//import kotlinx.coroutines.*
//import okhttp3.*
//import org.json.JSONObject
//
//object WebSocketManager {
//
//    private var webSocket: WebSocket? = null
//    private var client: OkHttpClient = OkHttpClient()
//    // 데이터를 저장하기 위해 Repository가 필요합니다.
//    private var repository: LaundryRepository? = null
//
//    // 앱 시작 시 Application 클래스 등에서 Repository를 주입해줍니다.
//    fun initialize(repo: LaundryRepository) {
//        repository = repo
//    }
//
//    fun connect() {
//        // 이미 연결되어 있다면 중복 실행 방지
//        if (webSocket != null) {
//            Log.d("WebSocket", "이미 웹소켓에 연결되어 있습니다.")
//            return
//        }
//
//        // 🚨 중요: "ws://" 또는 "wss://"로 시작하는 웹소켓 전용 주소를 사용해야 합니다.
//        // 예: "ws://your-server-address.com/status_update"
//        val request = Request.Builder()
//            .url("ws://<서버의-웹소켓-주소>/status_update")
//            .build()
//
//        // 리스너를 생성하여 웹소켓의 이벤트를 처리합니다.
//        webSocket = client.newWebSocket(request, object : WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                super.onOpen(webSocket, response)
//                Log.d("WebSocket", "✅ 웹소켓 연결 성공")
//                // 연결 성공 시, 서버에 클라이언트 정보(토큰 등)를 보낼 수 있습니다.
//                // webSocket.send("{\"access_token\": \"...\"}")
//            }
//
//            // ◀️ [핵심] 서버로부터 메시지를 수신했을 때 호출됩니다.
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                super.onMessage(webSocket, text)
//                Log.d("WebSocket", "⬅️ 메시지 수신: $text")
//
//                try {
//                    // 1. 수신한 JSON 텍스트를 파싱합니다.
//                    val json = JSONObject(text)
//                    val machineId = json.getString("machine_id").toInt() // 명세는 str이지만, DB는 Int일 가능성
//                    val newStatus = json.getString("status")
//
//                    // 2. Repository를 통해 로컬 DB를 업데이트합니다.
//                    // Coroutine을 사용하여 IO 스레드에서 DB 작업을 수행합니다.
//                    GlobalScope.launch(Dispatchers.IO) {
//                        repository?.updateMachineStatus(machineId, newStatus)
//                        Log.d("WebSocket", "DB 업데이트 요청: Machine ID $machineId -> $newStatus")
//                    }
//
//                } catch (e: Exception) {
//                    Log.e("WebSocket", "메시지 파싱 또는 DB 업데이트 실패", e)
//                }
//            }
//
//            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//                super.onClosing(webSocket, code, reason)
//                Log.d("WebSocket", "웹소켓 연결 종료 중...")
//                webSocket.close(1000, null)
//            }
//
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                super.onFailure(webSocket, t, response)
//                Log.e("WebSocket", "❌ 웹소켓 연결 실패: ${t.message}")
//                // 여기서 N초 후 재연결 로직을 구현할 수 있습니다.
//                this@WebSocketManager.webSocket = null // 연결 실패 시 webSocket 객체 정리
//            }
//        })
//    }
//
//    fun disconnect() {
//        webSocket?.close(1000, "사용자 요청에 의해 연결 종료")
//        webSocket = null
//        Log.d("WebSocket", "웹소켓 연결이 종료되었습니다.")
//    }
//}
