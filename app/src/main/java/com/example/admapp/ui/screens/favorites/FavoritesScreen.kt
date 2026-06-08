package com.example.admapp.ui.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.admapp.domain.model.Breed
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onBreedClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val breedImages by viewModel.breedImages.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedBreed by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("❤️", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Mis favoritos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    // Botón de ordenamiento
                    if (!uiState.isEmpty) {
                        Box {
                            TextButton(
                                onClick = { showSortMenu = true }
                            ) {
                                Text(
                                    text = when (uiState.sortOrder) {
                                        SortOrder.RECENT -> "Recientes"
                                        SortOrder.OLDEST -> "Antiguos"
                                        SortOrder.A_Z    -> "A → Z"
                                        SortOrder.Z_A    -> "Z → A"
                                    },
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Ordenar",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                SortOrder.entries.forEach { order ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = when (order) {
                                                    SortOrder.RECENT -> "⏱ Más recientes"
                                                    SortOrder.OLDEST -> "🕰 Más antiguos"
                                                    SortOrder.A_Z    -> "🔤 A → Z"
                                                    SortOrder.Z_A    -> "🔤 Z → A"
                                                },
                                                fontWeight = if (uiState.sortOrder == order)
                                                    FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            viewModel.setSortOrder(order)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = uiState.isEmpty,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "favorites_content"
            ) { isEmpty ->
                if (isEmpty) {
                    EmptyFavorites(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Contador de resultados
                        item {
                            Text(
                                text = "${uiState.favorites.size} raza${if (uiState.favorites.size != 1) "s" else ""} guardada${if (uiState.favorites.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(uiState.favorites, key = { it.name }) { breed ->
                            // Carga lazy de imagen al aparecer la card
                            LaunchedEffect(breed.name) {
                                viewModel.loadImageForBreed(breed.name)
                            }
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { it / 2 }
                                ) + fadeIn()
                            ) {
                                FavoriteItem(
                                    breed = breed,
                                    imageUrl = breedImages[breed.name],
                                    onClick = { onBreedClick(breed.name) },
                                    onDelete = { viewModel.removeFavorite(breed.name) },
                                    onRandomImage = {
                                        viewModel.refreshImageForBreed(breed.name)
                                        selectedBreed = breed.name
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        selectedBreed?.let { breedName ->
            val imageUrl = breedImages[breedName]
            AlertDialog(
                onDismissRequest = { selectedBreed = null },
                title = {
                    Text(
                        text = breedName.capitalize(Locale.current),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Imagen de $breedName",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(280.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.refreshImageForBreed(breedName) }) {
                        Text("🔀 Otra imagen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedBreed = null }) { Text("Cerrar") }
                }
            )
        }
    }
}

@Composable
private fun FavoriteItem(
    breed: Breed,
    imageUrl: String?,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRandomImage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Foto de ${breed.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f),
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                    )
                                )
                            )
                    )
                } else {
                    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "shimmer_alpha"
                    )
                    Text(
                        text = "🐶",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            // Contenido textual
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = breed.name.capitalize(Locale.current),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (breed.subBreeds.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${breed.subBreeds.size} variedad${if (breed.subBreeds.size != 1) "es" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = breed.subBreeds.joinToString(", ") { it.capitalize(Locale.current) },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            // Botón imagen random
            IconButton(
                onClick = onRandomImage,
                modifier = Modifier.padding(end = 0.dp)
            ) {
                Icon(
                    Icons.Default.Shuffle,         // o Icons.Default.Casino
                    contentDescription = "Imagen aleatoria",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Botón eliminar
            IconButton(
                onClick = onDelete,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar favorito",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyFavorites(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "🐾", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Sin favoritos todavía",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Explorá razas y guardá tus preferidas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}