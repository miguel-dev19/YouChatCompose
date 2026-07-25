package cu.alexgi.youchat.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    nombre: String, correo: String, onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }

    LaunchedEffect(correo) { viewModel.init(correo) }
    LaunchedEffect(uiState.messages.size) { if (uiState.messages.isNotEmpty()) listState.animateScrollToItem(0) }

    Scaffold(
        topBar = {
            // Toolbar: 56dp alto, elevation 4dp
            TopAppBar(
                modifier = Modifier.height(56.dp),
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        // Avatar: 45dp circular
                        Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(nombre, fontWeight = FontWeight.Medium, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(uiState.statusText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    // Llamar (si tiene teléfono)
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Call, "Llamar")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, "Menú")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Buscar") }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Filled.Search, null) })
                        DropdownMenuItem(text = { Text("Galería") }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Filled.Image, null) })
                        HorizontalDivider()
                        DropdownMenuItem(text = { Text(if (uiState.isMuted) "Activar notificaciones" else "Silenciar") }, onClick = { showMenu = false; viewModel.toggleMute() }, leadingIcon = { Icon(if (uiState.isMuted) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff, null) })
                        DropdownMenuItem(text = { Text(if (uiState.isBlocked) "Desbloquear" else "Bloquear") }, onClick = { showMenu = false; viewModel.toggleBlock() }, leadingIcon = { Icon(Icons.Filled.Block, null) })
                        HorizontalDivider()
                        DropdownMenuItem(text = { Text("Vaciar chat") }, onClick = { showMenu = false; viewModel.clearChat() }, leadingIcon = { Icon(Icons.Filled.Delete, null) })
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Column {
                    // Respuesta
                    AnimatedVisibility(visible = uiState.replyingTo != null) {
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icono respuesta: 25dp
                                Icon(Icons.Filled.Reply, null, modifier = Modifier.size(25.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                // Imagen preview: 32dp alto
                                Surface(modifier = Modifier.size(width = 32.dp, height = 32.dp), shape = RoundedCornerShape(4.dp), color = Color.Gray.copy(alpha = 0.3f)) {
                                    Icon(Icons.Filled.Image, null, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(uiState.replyingTo?.sender ?: "", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                                    Text(uiState.replyingTo?.preview ?: "", fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                                // Botón cancelar: 30dp
                                IconButton(onClick = viewModel::cancelReply, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Filled.Close, "Cancelar", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    // Barra de entrada
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji: 40dp, padding 7dp
                        IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Filled.EmojiEmotions, "Emoji", modifier = Modifier.size(26.dp), tint = Color.Gray)
                        }

                        // Texto: minHeight 40dp, maxLines 4
                        OutlinedTextField(
                            value = uiState.inputText,
                            onValueChange = viewModel::onInputChange,
                            placeholder = { Text("Mensaje") },
                            modifier = Modifier.weight(1f).heightIn(min = 40.dp),
                            maxLines = 4,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
                        )

                        // Cámara: 40dp, padding 8dp
                        IconButton(onClick = { showAttachmentSheet = true }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Filled.CameraAlt, "Adjuntar", modifier = Modifier.size(24.dp), tint = Color.Gray)
                        }

                        // Adjuntar: 40dp, padding 8dp
                        IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Filled.AttachFile, "Archivo", modifier = Modifier.size(24.dp), tint = Color.Gray)
                        }

                        // Micrófono o Enviar
                        if (uiState.inputText.isEmpty()) {
                            IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Filled.Mic, "Grabar", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            IconButton(onClick = { viewModel.sendMessage() }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Filled.Send, "Enviar", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            // FAB mini: 40dp, marginEnd 5dp
            if (uiState.messages.size > 5) {
                FloatingActionButton(
                    onClick = { listState.animateScrollToItem(0) },
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, "Ir al final", modifier = Modifier.size(20.dp))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFEDF2F8))) {
            if (uiState.messages.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No hay mensajes aún", color = Color.Gray)
                    Text("¡Envía el primero!", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    state = listState, modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    reverseLayout = true
                ) {
                    // Divisor mensajes no vistos
                    if (uiState.unreadCount > 0) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.9f), shadowElevation = 1.dp) {
                                    Text(
                                        "${uiState.unreadCount} mensaje${if (uiState.unreadCount > 1) "s" else ""} no visto${if (uiState.unreadCount > 1) "s" else ""}",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    items(uiState.messages, key = { it.id }) { msg ->
                        MessageBubble(
                            message = msg,
                            onDownload = { viewModel.downloadMessage(msg.id) },
                            onImageClick = { },
                            onReply = { viewModel.setReply(msg) },
                            onResend = { viewModel.resendMessage(msg.id) }
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }

    // Sheet adjuntos
    if (showAttachmentSheet) {
        ModalBottomSheet(onDismissRequest = { showAttachmentSheet = false }) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ListItem(headlineContent = { Text("Cámara") }, leadingContent = { Icon(Icons.Filled.CameraAlt, null) }, modifier = Modifier.clickable { showAttachmentSheet = false })
                ListItem(headlineContent = { Text("Galería") }, leadingContent = { Icon(Icons.Filled.Image, null) }, modifier = Modifier.clickable { showAttachmentSheet = false })
                ListItem(headlineContent = { Text("Documento") }, leadingContent = { Icon(Icons.Filled.InsertDriveFile, null) }, modifier = Modifier.clickable { showAttachmentSheet = false })
                ListItem(headlineContent = { Text("Contacto") }, leadingContent = { Icon(Icons.Filled.Person, null) }, modifier = Modifier.clickable { showAttachmentSheet = false })
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage, onDownload: () -> Unit, onImageClick: () -> Unit,
    onReply: () -> Unit, onResend: () -> Unit
) {
    val isMine = message.isMine
    val bubbleColor = if (isMine) Color(0xFFE1FFC7) else Color.White
    var showPopup by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp).clickable { showPopup = true },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (isMine) 16.dp else 4.dp, bottomEnd = if (isMine) 4.dp else 16.dp),
            color = bubbleColor, shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (message.replyTo != null) {
                    Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), color = Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(4.dp)) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(message.replyTo.sender, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF3F51B5))
                            Text(message.replyTo.preview, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                when (message.type) {
                    ChatMessageType.TEXT, ChatMessageType.CONTACT -> Text(message.content, fontSize = 15.sp)
                    ChatMessageType.IMAGE -> {
                        if (message.downloaded && message.filePath != null && File(message.filePath).exists()) {
                            AsyncImage(model = File(message.filePath), contentDescription = "Imagen", modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(RoundedCornerShape(8.dp)).clickable { onImageClick() }, contentScale = ContentScale.Crop)
                        } else {
                            Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF607D8B)), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Image, "Imagen", modifier = Modifier.size(32.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📷 ${message.fileSize ?: ""}", fontSize = 12.sp, color = Color.White)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    FilledTonalButton(onClick = onDownload, colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White.copy(alpha = 0.9f))) {
                                        Icon(Icons.Filled.Download, null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Descargar", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                    ChatMessageType.AUDIO -> {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                            IconButton(onClick = { }, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) }
                            Box(modifier = Modifier.weight(1f).height(4.dp).padding(horizontal = 4.dp).clip(RoundedCornerShape(2.dp)).background(Color.Gray.copy(alpha = 0.3f)))
                            Text(message.content, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    ChatMessageType.STICKER -> Text("😀", fontSize = 48.sp)
                    ChatMessageType.FILE -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.InsertDriveFile, null, modifier = Modifier.size(22.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(message.content, fontSize = 13.sp)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Text(message.time, fontSize = 11.sp, color = Color.Gray)
                    if (isMine && message.status != null) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            when (message.status) { "sending" -> Icons.Filled.Schedule; "sent" -> Icons.Filled.Check; "delivered" -> Icons.Filled.DoneAll; "read" -> Icons.Filled.DoneAll; "error" -> Icons.Filled.ErrorOutline; else -> Icons.Filled.Check },
                            null, modifier = Modifier.size(14.dp),
                            tint = if (message.status == "read") Color(0xFF3F51B5) else Color.Gray
                        )
                    }
                }
            }
        }

        DropdownMenu(expanded = showPopup, onDismissRequest = { showPopup = false }) {
            if (message.status != "sending" && message.status != "error") {
                DropdownMenuItem(text = { Text("Responder") }, onClick = { showPopup = false; onReply() }, leadingIcon = { Icon(Icons.Filled.Reply, null, modifier = Modifier.size(18.dp)) })
            }
            if (message.type == ChatMessageType.TEXT) {
                DropdownMenuItem(text = { Text("Copiar") }, onClick = { showPopup = false }, leadingIcon = { Icon(Icons.Filled.ContentCopy, null, modifier = Modifier.size(18.dp)) })
            }
            if (message.isMine && message.status != "error") {
                DropdownMenuItem(text = { Text("Editar") }, onClick = { showPopup = false }, leadingIcon = { Icon(Icons.Filled.Edit, null, modifier = Modifier.size(18.dp)) })
            }
            if (message.status == "error") {
                DropdownMenuItem(text = { Text("Reintentar") }, onClick = { showPopup = false; onResend() }, leadingIcon = { Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp)) })
            }
            if (message.type == ChatMessageType.IMAGE && message.downloaded) {
                DropdownMenuItem(text = { Text("Guardar imagen") }, onClick = { showPopup = false }, leadingIcon = { Icon(Icons.Filled.SaveAlt, null, modifier = Modifier.size(18.dp)) })
            }
            DropdownMenuItem(text = { Text("Eliminar") }, onClick = { showPopup = false }, leadingIcon = { Icon(Icons.Filled.Delete, null, modifier = Modifier.size(18.dp)) })
        }
    }
}
