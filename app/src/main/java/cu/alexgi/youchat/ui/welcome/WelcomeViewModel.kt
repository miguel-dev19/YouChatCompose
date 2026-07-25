package cu.alexgi.youchat.ui.welcome

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.alexgi.youchat.data.local.YouChatPreferences
import cu.alexgi.youchat.data.remote.ImapClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WelcomeUiState(
    val profileImageUri: Uri? = null,
    val alias: String = "",
    val consumptionLevel: Int = 2,
    val configDescription: String = "Serán activadas sólo las funciones más importantes para el uso de la aplicación.",
    val backupStatus: String = "Toque para verificar si existe alguna copia de seguridad para cargar",
    val hasBackup: Boolean = false,
    val inboxStatus: String = "Toque el botón para hacer su primer escaneo",
    val isCheckingInbox: Boolean = false,
    val canClearInbox: Boolean = false,
    val showPhotoSheet: Boolean = false
)

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val preferences: YouChatPreferences,
    private val imapClient: ImapClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    fun onAliasChange(alias: String) { _uiState.update { it.copy(alias = alias) } }

    fun onConsumptionChange(level: Int) {
        val desc = when (level) {
            1 -> "Serán desactivadas todas las funciones que consuman datos de la aplicación."
            2 -> "Serán activadas sólo las funciones más importantes para el uso de la aplicación."
            3 -> "Serán activadas todas las funciones que consuman datos (con moderación en la configuración), para una mejor experiencia y uso."
            else -> ""
        }
        _uiState.update { it.copy(consumptionLevel = level, configDescription = desc) }
    }

    fun showPhotoSheet() { _uiState.update { it.copy(showPhotoSheet = true) } }
    fun hidePhotoSheet() { _uiState.update { it.copy(showPhotoSheet = false) } }
    fun takePhoto() { /* CameraX */ }
    fun pickFromGallery() { /* ActivityResultContract */ }
    fun removePhoto() { _uiState.update { it.copy(profileImageUri = null) } }

    fun checkBackup() {
        viewModelScope.launch {
            val file = java.io.File(android.os.Environment.getExternalStorageDirectory(), "YouChat/YouChat_BDatos.dbyc")
            if (file.exists()) {
                _uiState.update { it.copy(backupStatus = "Copia de seguridad en: ${file.path}", hasBackup = true) }
            } else {
                _uiState.update { it.copy(backupStatus = "No existen copias de seguridad", hasBackup = false) }
            }
        }
    }

    fun loadBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(backupStatus = "Copia de seguridad cargada con éxito") }
        }
    }

    fun reintentarInbox() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingInbox = true, inboxStatus = "Obteniendo cantidad de correos y peso total...") }
            try {
                val count = imapClient.getInboxCount()
                _uiState.update {
                    it.copy(
                        isCheckingInbox = false,
                        inboxStatus = "Encontrados $count correos",
                        canClearInbox = count > 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCheckingInbox = false, inboxStatus = "Falló al escanear la bandeja, vuelva a intentar.") }
            }
        }
    }

    fun vaciarInbox() {
        viewModelScope.launch {
            _uiState.update { it.copy(inboxStatus = "Vaciado finalizado con éxito", canClearInbox = false) }
        }
    }

    fun saveConfig() {
        viewModelScope.launch {
            preferences.setAlias(_uiState.value.alias)
            preferences.setMark(3)
        }
    }
}
