package com.example.admapp.ui.screens.config

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.Coil
import com.example.admapp.ui.screens.favorites.FavoritesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var cacheCleared by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚙️", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Configuración",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            SettingsSection(title = "Apariencia") {
                SettingsToggleItem(
                    icon = if (uiState.darkMode) Icons.Filled.DarkMode else Icons.Outlined.LightMode,
                    title = "Modo oscuro",
                    subtitle = if (uiState.darkMode) "Tema oscuro activo" else "Tema claro activo",
                    checked = uiState.darkMode,
                    onCheckedChange = viewModel::setDarkMode
                )
            }

            SettingsSection(title = "Notificaciones") {
                SettingsToggleItem(
                    icon = if (uiState.notificationsEnabled)
                        Icons.Filled.Notifications else Icons.Outlined.NotificationsOff,
                    title = "Notificaciones",
                    subtitle = if (uiState.notificationsEnabled)
                        "Recibís alertas del sync" else "Notificaciones desactivadas",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = viewModel::setNotifications
                )
            }

            SettingsSection(title = "Datos") {
                SettingsActionItem(
                    icon = Icons.Outlined.DeleteSweep,
                    title = "Limpiar caché de imágenes",
                    subtitle = "Libera espacio eliminando imágenes guardadas",
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = { showClearCacheDialog = true }
                )
            }

            SettingsSection(title = "Acerca de") {
                SettingsActionItem(
                    icon = Icons.Outlined.Info,
                    title = "Sobre la app",
                    subtitle = "DogFinder v1.0 · Hecho con ❤️",
                    onClick = { showAboutDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsActionItem(
                    icon = Icons.Outlined.Share,
                    title = "Compartir la app",
                    subtitle = "Recomendá DogFinder a tus amigos",
                    onClick = { /* Intent de compartir */ }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            icon = { Icon(Icons.Outlined.DeleteSweep, contentDescription = null) },
            title = { Text("¿Limpiar caché?") },
            text = { Text("Se eliminarán las imágenes guardadas localmente.") },
            confirmButton = {
                TextButton(onClick = {
                    coil.Coil.imageLoader(context).memoryCache?.clear()
                    viewModel.clearImageCache(context)
                    favoritesViewModel.clearImageCache()
                    cacheCleared = true
                    showClearCacheDialog = false
                }) {
                    Text("Limpiar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) { Text("Cancelar") }
            }
        )
    }


    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = { Text("🐶", style = MaterialTheme.typography.displaySmall) },
            title = { Text("DogFinder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Versión 1.0.0")
                    Text("Datos provistos por dog.ceo/dog-api",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}