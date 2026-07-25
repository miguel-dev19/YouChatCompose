package cu.alexgi.youchat.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.alexgi.youchat.data.local.YouChatPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val alias: String = "", val correo: String = "", val info: String = "",
    val telefono: String = "", val genero: String = "",
    val fechaNacimiento: String = "", val provincia: String = "",
    val profileImagePath: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(private val preferences: YouChatPreferences) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            preferences.preferences.collect { prefs ->
                _uiState.update { it.copy(alias = prefs.alias, correo = prefs.correo) }
            }
        }
    }
    fun takePhoto() {}
    fun pickFromGallery() {}
    fun removePhoto() { _uiState.update { it.copy(profileImagePath = "") } }
}
