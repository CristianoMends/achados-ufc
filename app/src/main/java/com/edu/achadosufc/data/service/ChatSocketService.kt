import android.content.Context
import android.util.Log
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.MessageRequest
import com.edu.achadosufc.data.model.MessageResponse
import com.edu.achadosufc.data.model.UserResponse
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import org.json.JSONArray
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ChatSocketService(context: Context) {
    private var socket: Socket? = null
    private val TAG = "ChatSocketService"
    private val sessionManager = SessionManager(context)

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _messageInfo = MutableStateFlow<String?>(null)
    val messageInfo = _messageInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<MessageResponse>(replay = 1)
    val incomingMessages = _incomingMessages.asSharedFlow()

    private val _chatHistory = MutableSharedFlow<List<MessageResponse>>(replay = 1)
    val chatHistory = _chatHistory.asSharedFlow()

    fun connect() {
        if (socket?.isActive == true) {
            Log.d(TAG, "Tentativa de conexão, mas socket já está ativo.")
            return
        }

        try {
            val token = sessionManager.fetchAuthToken()
            if (token.isNullOrBlank()) {
                Log.e(TAG, "Token de autenticação nulo ou vazio. Conexão abortada.")
                return
            }

            val authPayload = mapOf(
                "token" to token
            )

            val options = IO.Options.builder()
                .setAuth(authPayload)
                .build()

            socket = IO.socket("http://192.168.1.109:3000", options)

            setupListeners()

            socket?.connect()
            Log.d(TAG, "Iniciando tentativa de conexão...")

        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao criar o socket: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun setupListeners() {
        socket?.on(Socket.EVENT_CONNECT) {
            Log.d(TAG, "Socket Conectado! ID: ${socket?.id()}")
            _isConnected.value = true
        }
        socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e(TAG, "Erro de Conexão: ${args.getOrNull(0)}")
            _isConnected.value = false
        }
        socket?.on(Socket.EVENT_DISCONNECT) { args ->
            Log.w(TAG, "Socket Desconectado: ${args.getOrNull(0)}")
            _isConnected.value = false
        }
        socket?.on("chatHistory") { args ->
            val dataArray = args[0] as JSONArray
            Log.d(TAG, "Histórico de chat recebido. Número de mensagens: ${dataArray.length()}")

            val messageList = mutableListOf<MessageResponse>()

            for (i in 0 until dataArray.length()) {
                val messageJson = dataArray.getJSONObject(i)
                val isRead = messageJson.optBoolean("isRead", false)
                val senderJson = messageJson.getJSONObject("sender")
                val sender = UserResponse(
                    id = senderJson.getInt("id"),
                    username = senderJson.getString("username"),
                    name = senderJson.getString("name"),
                    surname = senderJson.getString("surname"),
                    email = senderJson.getString("email"),
                    phone = senderJson.getString("phone"),
                    imageUrl = senderJson.optString("imageUrl", "")
                )
                val recipient = UserResponse(
                    id = messageJson.getJSONObject("recipient").getInt("id"),
                    username = messageJson.getJSONObject("recipient").getString("username"),
                    name = messageJson.getJSONObject("recipient").getString("name"),
                    surname = messageJson.getJSONObject("recipient").getString("surname"),
                    email = messageJson.getJSONObject("recipient").getString("email"),
                    phone = messageJson.getJSONObject("recipient").getString("phone"),
                    imageUrl = messageJson.getJSONObject("recipient").optString("imageUrl", "")
                )
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val createdAt = dateFormat.parse(messageJson.getString("createdAt"))

                val message = MessageResponse(
                    id = messageJson.getInt("id"),
                    text = messageJson.getString("text"),
                    createdAt = createdAt,
                    sender = sender,
                    recipient = recipient,
                    isRead = isRead
                )
                //_incomingMessages.tryEmit(message)
                messageList.add(message)
            }
            _chatHistory.tryEmit(messageList)
        }
        socket?.on("receivePrivateMessage") { args ->
            val data = args[0] as JSONObject
            Log.d(TAG, "MENSAGEM PRIVADA RECEBIDA! Dados: $data")

            val senderJson = data.getJSONObject("sender")
            val sender = UserResponse(
                id = senderJson.getInt("id"),
                username = senderJson.getString("username"),
                name = senderJson.getString("name"),
                surname = senderJson.getString("surname"),
                email = senderJson.getString("email"),
                phone = senderJson.getString("phone"),
                imageUrl = senderJson.optString(
                    "imageUrl",
                    ""
                )
            )
            val recipient = UserResponse(
                id = data.getJSONObject("recipient").getInt("id"),
                username = data.getJSONObject("recipient").getString("username"),
                name = data.getJSONObject("recipient").getString("name"),
                surname = data.getJSONObject("recipient").getString("surname"),
                email = data.getJSONObject("recipient").getString("email"),
                phone = data.getJSONObject("recipient").getString("phone"),
                imageUrl = data.getJSONObject("recipient").optString(
                    "imageUrl",
                    ""
                )
            )
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val createdAt = dateFormat.parse(data.getString("createdAt"))

            val message = MessageResponse(
                id = data.getInt("id"),
                text = data.getString("text"),
                createdAt = createdAt,
                sender = sender,
                recipient = recipient,
                isRead = data.optBoolean("isRead", false)
            )
            _incomingMessages.tryEmit(message)
        }
    }

    fun sendPrivateMessage(senderId: Int, recipientId: Int, text: String) {
        if (socket?.isActive != true) {
            Log.w(TAG, "Tentativa de enviar mensagem, mas o socket não está conectado.")
            return
        }
        val payload = JSONObject().apply {
            put("senderId", senderId)
            put("recipientId", recipientId)
            put("text", text)
        }
        Log.d(TAG, "Enviando mensagem privada de $senderId para o usuário $recipientId: $text")
        socket?.emit("sendPrivateMessage", payload)
    }

    suspend fun getChatHistory(otherUserId: Int):Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            var attempts = 0
            val maxAttempts = 10 // Tentar por no máximo 5 segundos (10 * 500ms)
            while (socket?.isActive != true && attempts < maxAttempts) {
                Log.w(TAG, "Socket não está ativo. Tentando obter histórico de chat... Tentativa ${attempts + 1}")
                delay(1000) // Espera 500ms antes de tentar novamente
                attempts++
            }

            if (socket?.isActive != true) {
                Log.e(TAG, "Falha ao obter histórico de chat: o socket não ficou ativo após $maxAttempts tentativas.")
                _messageInfo.value = "Erro ao obter histórico de chat. Tente novamente mais tarde."
                return@launch
            }
            val payload = JSONObject().apply {
                put("otherUserId", otherUserId)
            }
            Log.d(TAG, "Solicitando histórico de chat com o usuário $otherUserId")
            socket?.emit("getChatHistory", payload)
            Log.d(TAG, "Histórico de chat solicitado com sucesso.")
        }
        return true
    }

    fun disconnect() {
        Log.d(TAG, "Desconectando o socket...")
        socket?.disconnect()
        socket?.off()
        socket = null
        _isConnected.value = false
        Log.d(TAG, "Socket desconectado e limpo.")
    }
}
