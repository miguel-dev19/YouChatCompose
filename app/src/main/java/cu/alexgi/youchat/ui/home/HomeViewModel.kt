package cu.alexgi.youchat.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.alexgi.youchat.core.util.ConnectionState
import cu.alexgi.youchat.core.util.ConnectivityObserver
import cu.alexgi.youchat.data.local.dao.ContactDao
import cu.alexgi.youchat.data.local.dao.MessageDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatPreview(val nombre: String, val correo: String, val lastMessage: String, val lastTime: String, val unreadCount: Int, val isMine: Boolean, val isAnclado: Boolean = false, val status: String = "sent")
data class HomeUiState(val chats: List<ChatPreview> = emptyList(), val isLoading: Boolean = true, val connectionState: ConnectionState = ConnectionState.NO_CONNECTION)

@HiltViewModel
class HomeViewModel @Inject constructor(private val messageDao: MessageDao, private val contactDao: ContactDao, private val connectivityObserver: ConnectivityObserver) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    init {
        connectivityObserver.observe()
        viewModelScope.launch { connectivityObserver.connectionState.collect { _uiState.update { it.copy(connectionState = state) } } }
        viewModelScope.launch {
            messageDao.getAllChats().collect { usuarios ->
                _uiState.update { it.copy(chats = usuarios.map { u -> val c = contactDao.getContact(u.correo); ChatPreview(nombre = c?.nombrePersonal?.ifEmpty { u.correo } ?: u.correo, correo = u.correo, lastMessage = when(u.ultMsgTipo){3,4->"📷 Imagen";7,8,9,10->"🎵 Audio";19,20->"😀 Sticker";13,14->"📎 Archivo";else->u.ultMsgTexto}, lastTime = u.ultMsgOrden, unreadCount = u.cantMsg, isMine = true, isAnclado = u.anclado, status = when(u.ultMsgEstado){1->"sending";2->"error";3->"sent";4->"delivered";5->"read";else->"sent"}) }, isLoading = false) }
            }
        }
    }
}
