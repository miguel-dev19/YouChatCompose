package cu.alexgi.youchat.ui.contacts

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var showFollowDialog by remember { mutableStateOf(false) }
    var showContactPreview by remember { mutableStateOf<ContactPreview?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<ContactPreview?>(null) }

    Scaffold(
        topBar = {
            // Toolbar: 56dp, elevation 4dp
            TopAppBar(
                modifier = Modifier.height(56.dp),
                title = {
                    Column {
                        Text("Contactos", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("${uiState.filteredContacts.size} Contactos", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, "Menú")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Buscar") }, onClick = { showMenu = false; viewModel.toggleSearch() }, leadingIcon = { Icon(Icons.Filled.Search, null) })
                            DropdownMenuItem(text = { Text("Actualizar lista") }, onClick = { showMenu = false; viewModel.refreshContacts() }, leadingIcon = { Icon(Icons.Filled.Refresh, null) })
                            HorizontalDivider()
                            DropdownMenuItem(text = { Text("Seguir a") }, onClick = { showMenu = false; showFollowDialog = true }, leadingIcon = { Icon(Icons.Outlined.StarBorder, null) })
                            DropdownMenuItem(text = { Text("Nueva conversación") }, onClick = { showMenu = false; showNewChatDialog = true }, leadingIcon = { Icon(Icons.Outlined.Chat, null) })
                            DropdownMenuItem(text = { Text("Nuevo contacto") }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Outlined.PersonAdd, null) })
                            DropdownMenuItem(text = { Text("Invitar amigos") }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Outlined.Share, null) })
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(if (uiState.sortByName) "Ordenar por correo" else "Ordenar por nombre") },
                                onClick = { showMenu = false; viewModel.toggleSort() },
                                leadingIcon = { Icon(Icons.Outlined.Sort, null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            // Búsqueda animada
            AnimatedVisibility(visible = uiState.isSearching, enter = slideInVertically() + fadeIn(), exit = slideOutVertically() + fadeOut()) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    trailingIcon = { IconButton(onClick = { viewModel.toggleSearch() }) { Icon(Icons.Filled.Close, "Cerrar") } },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (uiState.filteredContacts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes contactos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                    val ycContacts = uiState.filteredContacts.filter { it.usaYouchat }
                    if (ycContacts.isNotEmpty()) {
                        item {
                            Text("Contactos con YouChat", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 16.dp, vertical = 8.dp))
                        }
                        items(ycContacts, key = { it.correo }) { contact ->
                            ContactListItem(contact = contact, onLongClick = { showContactPreview = contact }, onClick = { onChatClick(contact.nombre, contact.correo) })
                        }
                    }

                    val otherContacts = uiState.filteredContacts.filter { !it.usaYouchat }
                    if (otherContacts.isNotEmpty()) {
                        item {
                            Text("Otros contactos", style = MaterialTheme.typography.labelSmall, color = Color.Gray,
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 16.dp, vertical = 8.dp))
                        }
                        items(otherContacts, key = { it.correo }) { contact ->
                            ContactListItem(contact = contact, onLongClick = { showContactPreview = contact }, onClick = { onChatClick(contact.nombre, contact.correo) })
                        }
                    }
                }
            }
        }
    }

    // Preview contacto
    showContactPreview?.let { contact ->
        ModalBottomSheet(onDismissRequest = { showContactPreview = null }) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Person, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary) }
                }
                Text(contact.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(contact.correo, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilledTonalButton(onClick = { showContactPreview = null; onChatClick(contact.nombre, contact.correo) }, modifier = Modifier.weight(1f)) { Icon(Icons.Outlined.Chat, null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("Chat") }
                    OutlinedButton(onClick = { showContactPreview = null }, modifier = Modifier.weight(1f)) { Icon(Icons.Outlined.Person, null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("Perfil") }
                }
                TextButton(onClick = { showContactPreview = null; showDeleteConfirm = contact }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("Eliminar contacto") }
            }
        }
    }

    // Confirmar eliminar
    showDeleteConfirm?.let { contact ->
        AlertDialog(onDismissRequest = { showDeleteConfirm = null }, icon = { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) }, title = { Text("Eliminar contacto") }, text = { Text("¿Deseas eliminar a ${contact.nombre}?") },
            confirmButton = { TextButton(onClick = { viewModel.deleteContact(contact.correo); showDeleteConfirm = null }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text("Cancelar") } })
    }

    // Nueva conversación
    if (showNewChatDialog) {
        var email by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showNewChatDialog = false }, icon = { Icon(Icons.Outlined.Chat, null) }, title = { Text("Nueva conversación") },
            text = { OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { if (email.isNotBlank() && email.contains("@")) { showNewChatDialog = false; onChatClick(email, email) } }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { showNewChatDialog = false }) { Text("Cancelar") } })
    }

    // Seguir a
    if (showFollowDialog) {
        var email by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showFollowDialog = false }, icon = { Icon(Icons.Outlined.StarBorder, null) }, title = { Text("Seguir a") },
            text = { Column { Text("Introduzca un correo para enviarle una solicitud", fontSize = 13.sp, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp)); OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, singleLine = true) } },
            confirmButton = { TextButton(onClick = { if (email.isNotBlank() && email.contains("@")) { showFollowDialog = false; viewModel.followUser(email) } }) { Text("Seguir") } },
            dismissButton = { TextButton(onClick = { showFollowDialog = false }) { Text("Cancelar") } })
    }
}

@Composable
fun ContactListItem(contact: ContactPreview, onLongClick: () -> Unit, onClick: () -> Unit) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar: 48dp
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = if (contact.usaYouchat) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
                Box(contentAlignment = Alignment.Center) {
                    Text(contact.nombre.take(1).uppercase(), style = MaterialTheme.typography.titleMedium, color = if (contact.usaYouchat) MaterialTheme.colorScheme.primary else Color.Gray)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(contact.nombre, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    if (contact.usaYouchat) { Spacer(modifier = Modifier.width(4.dp)); Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary) }
                }
                Text(contact.correo, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onLongClick, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Outlined.Info, "Más", modifier = Modifier.size(20.dp), tint = Color.Gray)
            }
        }
    }
    HorizontalDivider(thickness = 0.5.dp)
}
