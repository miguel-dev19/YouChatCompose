package cu.alexgi.youchat.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onFinish: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPhotoSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar perfil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveConfig(); onFinish() },
                modifier = Modifier.size(55.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.ArrowForward, "Siguiente")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // CARD 1: Foto + Alias
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(140.dp)) {
                        AsyncImage(
                            model = uiState.profileImageUri,
                            contentDescription = "Foto",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        FloatingActionButton(
                            onClick = { showPhotoSheet = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(45.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Filled.CameraAlt, "Cambiar foto", modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.alias,
                        onValueChange = viewModel::onAliasChange,
                        label = { Text("Alias") },
                        leadingIcon = { Icon(Icons.Outlined.Person, null) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // CARD 2: Configuración inicial
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Configuración inicial:", fontWeight = FontWeight.Medium, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    ConsumptionOption("Modo Ahorro", "Serán desactivadas todas las funciones que consuman datos de la aplicación.", uiState.consumptionLevel == 1) { viewModel.onConsumptionChange(1) }
                    ConsumptionOption("Modo Estándar", "Serán activadas sólo las funciones más importantes para el uso de la aplicación.", uiState.consumptionLevel == 2) { viewModel.onConsumptionChange(2) }
                    ConsumptionOption("Modo Completo", "Serán activadas todas las funciones que consuman datos, para una mejor experiencia y uso.", uiState.consumptionLevel == 3) { viewModel.onConsumptionChange(3) }
                }
            }

            // Descripción de la config
            Text(
                uiState.configDescription,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // CARD 3: Copia de seguridad
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Copia de seguridad:", fontWeight = FontWeight.Medium, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.backupStatus, fontSize = 14.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = viewModel::checkBackup,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("VERIFICAR") }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = viewModel::loadBackup,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.hasBackup,
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("CARGAR") }
                }
            }

            // CARD 4: Bandeja de entrada
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Bandeja de entrada:", fontWeight = FontWeight.Medium, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.isCheckingInbox) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }

                    Text(
                        uiState.inboxStatus,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = viewModel::reintentarInbox,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("REINTENTAR") }

                    if (uiState.canClearInbox) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = viewModel::vaciarInbox,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("VACIAR BANDEJA") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Sheet opciones foto
    if (showPhotoSheet) {
        ModalBottomSheet(onDismissRequest = { showPhotoSheet = false }) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Foto de perfil", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(headlineContent = { Text("Tomar foto") }, leadingContent = { Icon(Icons.Filled.CameraAlt, null) }, modifier = Modifier.clickable { showPhotoSheet = false; viewModel.takePhoto() })
                ListItem(headlineContent = { Text("Elegir de galería") }, leadingContent = { Icon(Icons.Filled.Image, null) }, modifier = Modifier.clickable { showPhotoSheet = false; viewModel.pickFromGallery() })
                if (uiState.profileImageUri != null) {
                    ListItem(headlineContent = { Text("Eliminar foto") }, leadingContent = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }, modifier = Modifier.clickable { showPhotoSheet = false; viewModel.removePhoto() })
                }
            }
        }
    }
}

@Composable
private fun ConsumptionOption(
    title: String, description: String, selected: Boolean, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
