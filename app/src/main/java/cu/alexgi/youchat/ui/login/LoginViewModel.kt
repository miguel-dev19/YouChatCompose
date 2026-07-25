import kotlinx.coroutines.flow.asStateFlow
package cu.alexgi.youchat.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.alexgi.youchat.data.local.YouChatPreferences
import cu.alexgi.youchat.data.remote.MailClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "", val password: String = "", val passwordVisible: Boolean = false,
    val isLoading: Boolean = false, val emailError: String? = null, val errorMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(private val mailClient: MailClient, private val preferences: YouChatPreferences) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    fun onEmailChange(e: String) { _uiState.update { it.copy(email = e, emailError = null) } }
    fun onPasswordChange(p: String) { _uiState.update { it.copy(password = p) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(passwordVisible = !it.passwordVisible) } }
    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val email = _uiState.value.email.trim()
            val valid = listOf("@nauta.cu","@gmail.com","@mail.com","@mail.ru","@yahoo.com","@hotmail.com").any { email.endsWith(it) }
            if (!valid) { _uiState.update { it.copy(emailError = "Correo inválido") }; return@launch }
            _uiState.update { it.copy(isLoading = true) }
            if (mailClient.verifyCredentials(email, _uiState.value.password.trim())) {
                preferences.setCredentials(email, _uiState.value.password.trim())
                _uiState.update { it.copy(isLoading = false) }; onSuccess()
            } else _uiState.update { it.copy(isLoading = false, errorMessage = "Error al verificar") }
        }
    }
}
