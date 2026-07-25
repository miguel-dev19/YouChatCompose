package cu.alexgi.youchat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditField: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPhotoSheet by remember { mutableStateOf(false) }
    var showImageViewer by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(140.dp)) {
                AsyncImage(
                    model = uiState.profileImagePath,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { if (uiState.profileImagePath.isNotEmpty()) showImageViewer = true },
                    contentScale = ContentScale.Crop
                )
                FloatingActionButton(
                    onClick = { showPhotoSheet = true },
                    modifier = Modifier.align(Alignment.BottomEnd).size(45.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) { Icon(Icons.Filled.CameraAlt, "Cambiar foto", modifier = Modifier.size(20.dp)) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column {
                    ProfileField(Icons.Outlined.Person, "Alias", uiState.alias.ifEmpty { "Desconocido" }) { onEditField("alias") }
                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileField(Icons.Outlined.Email, "Correo", uiState.correo.ifEmpty { "Desconocido" }, showEdit = false)
                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileField(Icons.Outlined.Info, "Información", uiState.info.ifEmpty { "Añade una descripción" }) { onEditField("info") }
                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileField(Icons.Outlined.Call, "Teléfono", uiState.telefono.ifEmpty { "Desconocido" }) { onEditField("telefono") }
                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileField(Icons.Outlined.Face, "Género", uiState.genero.ifEmpty { "Desconocido" }) { onEditField("genero") }
                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileField(Icons.Outlined.CalendarToday, "Fecha de nacimiento", uiState.fechaNacimiento.ifEmpty { "Desconocido" }) { onEditField("fecha_nacimiento") }
                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileField(Icons.Outlined.LocationOn, "Provincia", uiState.provincia.ifEmpty { "Desconocido" }) { onEditField("provincia") }
                }
            }
        }
    }

    if (showPhotoSheet) {
        ModalBottomSheet(onDismissRequest = { showPhotoSheet = false }) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Foto de perfil", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(headlineContent = { Text("Tomar foto") }, leadingContent = { Icon(Icons.Filled.CameraAlt, null) }, modifier = Modifier.clickable { showPhotoSheet = false; viewModel.takePhoto() })
                ListItem(headlineContent = { Text("Elegir de galería") }, leadingContent = { Icon(Icons.Filled.Image, null) }, modifier = Modifier.clickable { showPhotoSheet = false; viewModel.pickFromGallery() })
                if (uiState.profileImagePath.isNotEmpty()) {
                    ListItem(headlineContent = { Text("Eliminar foto") }, leadingContent = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }, modifier = Modifier.clickable { showPhotoSheet = false; viewModel.removePhoto() })
                }
            }
        }
    }

    if (showImageViewer && uiState.profileImagePath.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)).clickable { showImageViewer = false }, contentAlignment = Alignment.Center) {
            AsyncImage(model = uiState.profileImagePath, contentDescription = "Foto", modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.Fit)
        }
    }
}

@Composable
fun ProfileField(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, showEdit: Boolean = true, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(32.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = if (value == "Desconocido" || value.contains("Añade")) Color.Gray else MaterialTheme.colorScheme.onSurface)
        }
        if (showEdit) Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
    }
}
