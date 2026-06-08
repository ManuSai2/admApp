package com.example.admapp

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.admapp.data.local.DogFinderDatabase
import com.example.admapp.data.remote.DogApiService
import com.example.admapp.data.repository.DogRepositoryImpl
import com.example.admapp.platform.DogSyncService
import com.example.admapp.platform.NetworkChangeReceiver
import com.example.admapp.ui.navigation.DogFinderNavGraph
import com.example.admapp.ui.navigation.Screen
import com.example.admapp.ui.screens.detail.DetailViewModel
import com.example.admapp.ui.screens.favorites.FavoritesViewModel
import com.example.admapp.ui.screens.home.HomeViewModel
import com.example.admapp.ui.theme.DogFinderTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val networkChangeReceiver = NetworkChangeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startDogSyncService()
        registerNetworkReceiver()
        queryFavoriteBreedsProvider()

        // Manual DI (simple, sin Hilt por ahora)
        val api = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)

        val db = DogFinderDatabase.getInstance(applicationContext)
        val repository = DogRepositoryImpl(api, db.favoriteBreedDao())

        val homeViewModel = HomeViewModel(repository)
        val detailViewModel = DetailViewModel(repository)
        val favoritesViewModel = FavoritesViewModel(repository)

        setContent {
            DogFinderTheme {
                MainScaffold(
                    homeViewModel = homeViewModel,
                    detailViewModel = detailViewModel,
                    favoritesViewModel = favoritesViewModel
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
    favoritesViewModel: FavoritesViewModel
) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    // Bottom nav only on top-level screens
    val showBottomBar = currentDestination in listOf(
        Screen.Home.route,
        Screen.Favorites.route
    )

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
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            DogFinderNavGraph(
                navController = navController,
                homeViewModel = homeViewModel,
                detailViewModel = detailViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
    }
}