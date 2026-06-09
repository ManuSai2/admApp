package com.example.admapp

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.admapp.data.local.DogFinderDatabase
import com.example.admapp.data.remote.DogApiService
import com.example.admapp.data.repository.DogRepositoryImpl
import com.example.admapp.platform.DogSyncService
import com.example.admapp.platform.NetworkChangeReceiver
import com.example.admapp.ui.navigation.DogFinderNavGraph
import com.example.admapp.ui.navigation.Screen
import com.example.admapp.ui.screens.config.SettingsViewModel
import com.example.admapp.ui.screens.detail.DetailViewModel
import com.example.admapp.ui.screens.favorites.FavoritesViewModel
import com.example.admapp.ui.screens.home.HomeViewModel
import com.example.admapp.ui.theme.DogFinderTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val networkChangeReceiver = NetworkChangeReceiver()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startDogSyncService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startDogSyncService()
        }

        registerNetworkReceiver()
        queryFavoriteBreedsProvider()

        val api = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)

        val db = DogFinderDatabase.getInstance(applicationContext)
        val repository = DogRepositoryImpl(api, db.favoriteBreedDao())

        val homeViewModel      = HomeViewModel(repository)
        val detailViewModel    = DetailViewModel(repository)
        val settingsViewModel  = SettingsViewModel(application)
        val favoritesViewModel = FavoritesViewModel(repository, settingsViewModel)

        setContent {
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            DogFinderTheme(darkTheme = settingsState.darkMode) {
                MainScaffold(
                    homeViewModel      = homeViewModel,
                    detailViewModel    = detailViewModel,
                    favoritesViewModel = favoritesViewModel,
                    settingsViewModel  = settingsViewModel
                )
            }
        }
    }

    private fun startDogSyncService() {
        val serviceIntent = Intent(this, DogSyncService::class.java)
        startService(serviceIntent)
    }

    private fun registerNetworkReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        ContextCompat.registerReceiver(
            this,
            networkChangeReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        Log.d("MainActivity", "NetworkChangeReceiver registered")
    }

    private fun queryFavoriteBreedsProvider() {
        val uri = Uri.parse("content://com.example.admapp.favoritebreedsprovider/favorites")
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            Log.d("MainActivity", "FavoriteBreedsProvider rows: ${cursor.count}")
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                Log.d("MainActivity", "Provider result: $name")
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(networkChangeReceiver)
        super.onDestroy()
    }
}

@Composable
fun MainScaffold(
    homeViewModel: HomeViewModel,
    detailViewModel: DetailViewModel,
    favoritesViewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    val topLevelRoutes = listOf(Screen.Home.route, Screen.Favorites.route, Screen.Settings.route)
    val showBottomBar  = currentDestination in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Explorar") },
                        label = { Text("Explorar") }
                    )
                    NavigationBarItem(
                        selected = currentDestination == Screen.Favorites.route,
                        onClick = {
                            navController.navigate(Screen.Favorites.route) {
                                popUpTo(Screen.Home.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                        label = { Text("Favoritos") }
                    )
                    NavigationBarItem(
                        selected = currentDestination == Screen.Settings.route,
                        onClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(Screen.Home.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                        label = { Text("Config") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            DogFinderNavGraph(
                navController      = navController,
                homeViewModel      = homeViewModel,
                detailViewModel    = detailViewModel,
                favoritesViewModel = favoritesViewModel,
                settingsViewModel  = settingsViewModel
            )
        }
    }
}