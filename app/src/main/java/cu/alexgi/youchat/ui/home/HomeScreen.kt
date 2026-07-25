package cu.alexgi.youchat.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import cu.alexgi.youchat.core.util.ConnectionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChatClick: (String, String) -> Unit,
    onContactsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (uiState.connectionState) {
                            ConnectionState.NO_CONNECTION -> "Esperando red..."
                            ConnectionState.CONNECTING -> "Conectando..."
                            ConnectionState.UPDATING -> "Actualizando..."
                            ConnectionState.CONNECTED -> "YouChat"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onContactsClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "Nuevo chat")
            }
        }
    ) { padding ->
        if (uiState.chats.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline, null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No tienes conversaciones", style = MaterialTheme.typography.titleMedium)
                    Text("Toca + para iniciar un nuevo chat", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.chats, key = { it.correo }) { chat ->
                    ChatListItem(chat = chat, onClick = { onChatClick(chat.nombre, chat.correo) })
                    HorizontalDivider(thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatPreview, onClick: () -> Unit) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        chat.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        chat.lastTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        chat.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (chat.unreadCount > 0) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(if (chat.unreadCount > 99) "99+" else "${chat.unreadCount}")
                        }
                    }
                    if (chat.isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            when (chat.status) {
                                "sending" -> Icons.Filled.Schedule
                                "sent" -> Icons.Filled.Check
                                "delivered" -> Icons.Filled.DoneAll
                                "read" -> Icons.Filled.DoneAll
                                else -> Icons.Filled.ErrorOutline
                            },
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = if (chat.status == "read") MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
