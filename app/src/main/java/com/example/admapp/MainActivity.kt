package com.example.admapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.admapp.data.local.DogFinderDatabase
import com.example.admapp.data.remote.DogApiService
import com.example.admapp.data.repository.DogRepositoryImpl
import com.example.admapp.ui.navigation.DogFinderNavGraph
import com.example.admapp.ui.navigation.Screen
import com.example.admapp.ui.screens.detail.DetailViewModel
import com.example.admapp.ui.screens.favorites.FavoritesViewModel
import com.example.admapp.ui.screens.home.HomeViewModel
import com.example.admapp.ui.theme.DogFinderTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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