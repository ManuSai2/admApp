package com.example.admapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.admapp.ui.screens.detail.DetailScreen
import com.example.admapp.ui.screens.detail.DetailViewModel
import com.example.admapp.ui.screens.favorites.FavoritesScreen
import com.example.admapp.ui.screens.favorites.FavoritesViewModel
import com.example.admapp.ui.screens.home.HomeScreen
import com.example.admapp.ui.screens.home.HomeViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorites : Screen("favorites")
    data object Detail : Screen("detail/{breedName}") {
        fun createRoute(breedName: String) = "detail/$breedName"
    }
}

@Composable
fun DogFinderNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    detailViewModel: DetailViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onBreedClick = { breedName ->
                    navController.navigate(Screen.Detail.createRoute(breedName))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("breedName") { type = NavType.StringType })
        ) { backStackEntry ->
            val breedName = backStackEntry.arguments?.getString("breedName") ?: return@composable
            DetailScreen(
                breedName = breedName,
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = favoritesViewModel,
                onBreedClick = { breedName ->
                    navController.navigate(Screen.Detail.createRoute(breedName))
                }
            )
        }
    }
}