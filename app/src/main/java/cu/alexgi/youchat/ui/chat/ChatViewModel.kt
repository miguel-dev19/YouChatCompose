package cu.alexgi.youchat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.alexgi.youchat.data.local.dao.ChatDao
import cu.alexgi.youchat.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ChatMessageType { TEXT, IMAGE, AUDIO, STICKER, FILE, CONTACT }
data class ReplyInfo(val sender: String, val preview: String)
data class ChatMessage(
    val id: String, val content: String, val type: ChatMessageType,
    val isMine: Boolean, val time: String, val status: String? = null,
    val replyTo: ReplyInfo? = null, val downloaded: Boolean = true,
    val filePath: String? = null, val fileSize: String? = null, val blurHash: String? = null
)
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(), val inputText: String = "",
    val replyingTo: ChatMessage? = null, val statusText: String = "",
    val isLoading: Boolean = false, val unreadCount: Int = 0,
    val isMuted: Boolean = false, val isBlocked: Boolean = false,
    val isSearching: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatDao: ChatDao, private val messageRepository: MessageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private var correo: String = ""

    fun init(c: String) {
        correo = c
        viewModelScope.launch {
            chatDao.getMessages(correo).collect { entities ->
                _uiState.update { it.copy(messages = entities.map { e ->
                    ChatMessage(
                        id=e.id, content=e.mensaje,
                        type=when { e.tipoMensaje in listOf(3,4)->ChatMessageType.IMAGE; e.tipoMensaje in listOf(7,8,9,10)->ChatMessageType.AUDIO; e.tipoMensaje in listOf(19,20)->ChatMessageType.STICKER; e.tipoMensaje in listOf(13,14)->ChatMessageType.FILE; e.tipoMensaje in listOf(11,12)->ChatMessageType.CONTACT; else->ChatMessageType.TEXT },
                        isMine=e.emisor.isNotEmpty(), time=e.hora,
                        status=when(e.estado){1->"sending";2->"error";3->"sent";4->"delivered";5->"read";else->null},
                        downloaded=e.estaDescargado, filePath=if(e.estaDescargado)e.rutaDato else null,
                        fileSize=if(e.peso>0)"${e.peso/1024} KB" else null, blurHash=e.blurhash
                    )
                }.reversed()) }
            }
        }
    }

    fun onInputChange(t: String) { _uiState.update { it.copy(inputText = t) } }
    fun sendMessage() {
        val t = _uiState.value.inputText.trim()
        if (t.isEmpty() || correo.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, inputText = "") }
            messageRepository.sendTextMessage(correo, t, _uiState.value.replyingTo?.id ?: "")
            _uiState.update { it.copy(isLoading = false, replyingTo = null) }
        }
    }
    fun setReply(m: ChatMessage) { _uiState.update { it.copy(replyingTo = m) } }
    fun cancelReply() { _uiState.update { it.copy(replyingTo = null) } }
    fun downloadMessage(id: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _uiState.update { s -> s.copy(messages = s.messages.map { if(it.id==id) it.copy(downloaded=true) else it }) }
        }
    }
    fun resendMessage(id: String) {
        viewModelScope.launch {
            _uiState.value.messages.find{it.id==id}?.let { messageRepository.sendTextMessage(correo, it.content) }
        }
    }
    fun toggleMute() { _uiState.update { it.copy(isMuted = !it.isMuted) } }
    fun toggleBlock() { _uiState.update { it.copy(isBlocked = !it.isBlocked) } }
    fun toggleSearch() { _uiState.update { it.copy(isSearching = !it.isSearching) } }
    fun clearChat() {
        viewModelScope.launch { chatDao.deleteAllMessages(correo); _uiState.update { it.copy(messages = emptyList()) } }
    }
}
