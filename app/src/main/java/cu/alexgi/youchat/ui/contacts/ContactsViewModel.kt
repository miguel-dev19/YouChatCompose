package cu.alexgi.youchat.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.alexgi.youchat.data.local.dao.ContactDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactPreview(
    val nombre: String,
    val correo: String,
    val info: String = "",
    val telefono: String = "",
    val usaYouchat: Boolean = false
)

data class ContactsUiState(
    val contacts: List<ContactPreview> = emptyList(),
    val filteredContacts: List<ContactPreview> = emptyList(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val sortByName: Boolean = true
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactDao: ContactDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            contactDao.getAllContacts().collect { contacts ->
                val previews = contacts.map { c ->
                    ContactPreview(
                        nombre = c.nombrePersonal.ifEmpty { c.alias.ifEmpty { c.correo } },
                        correo = c.correo,
                        info = c.info,
                        telefono = c.telefono,
                        usaYouchat = c.usaYouchat
                    )
                }.sortedBy {
                    if (_uiState.value.sortByName) it.nombre.lowercase() else it.correo.lowercase()
                }

                // Contactos con YouChat primero
                val sorted = previews.sortedByDescending { it.usaYouchat }

                _uiState.update {
                    it.copy(
                        contacts = sorted,
                        filteredContacts = if (it.searchQuery.isBlank()) sorted
                        else sorted.filter { c ->
                            c.nombre.contains(it.searchQuery, true) ||
                            c.correo.contains(it.searchQuery, true)
                        }
                    )
                }
            }
        }
    }

    fun toggleSearch() {
        _uiState.update {
            it.copy(isSearching = !it.isSearching, searchQuery = "")
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredContacts = if (query.isBlank()) it.contacts
                else it.contacts.filter { c ->
                    c.nombre.contains(query, true) || c.correo.contains(query, true)
                }
            )
        }
    }

    fun toggleSort() {
        _uiState.update {
            val newSort = !it.sortByName
            val sorted = it.contacts.sortedBy { c ->
                if (newSort) c.nombre.lowercase() else c.correo.lowercase()
            }.sortedByDescending { c -> c.usaYouchat }

            it.copy(
                sortByName = newSort,
                contacts = sorted,
                filteredContacts = if (it.searchQuery.isBlank()) sorted
                else sorted.filter { c ->
                    c.nombre.contains(it.searchQuery, true) || c.correo.contains(it.searchQuery, true)
                }
            )
        }
    }

    fun refreshContacts() {
        viewModelScope.launch {
            // Aquí iría la importación de contactos del teléfono (ContentResolver)
        }
    }

    fun deleteContact(correo: String) {
        viewModelScope.launch {
            contactDao.deleteContactByEmail(correo)
        }
    }

    fun followUser(correo: String) {
        viewModelScope.launch {
            // Enviar solicitud de seguir por SMTP
        }
    }
}
