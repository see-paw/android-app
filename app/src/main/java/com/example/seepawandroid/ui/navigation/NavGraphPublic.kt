package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueScreen
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel
import com.example.seepawandroid.ui.screens.login.AuthViewModel
import com.example.seepawandroid.ui.screens.login.LoginScreen
import com.example.seepawandroid.ui.screens.public.PublicHomepageScreen

/**
 * Navigation graph for public (unauthenticated) screens.
 *
 * Includes:
 * - Homepage
 * - Login
 * - Register
 * - Animal catalogue in guest mode
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraphPublic(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val isAuthenticated = false
    val animalViewModel: AnimalViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.HOMEPAGE
    ) {
        composable(NavigationRoutes.HOMEPAGE) {
            PublicHomepageScreen(
                onLogin = { navController.navigate(NavigationRoutes.LOGIN) },
                onRegister = { navController.navigate(NavigationRoutes.REGISTER) },
                onOpenCatalogue = {
                    navController.navigate(NavigationRoutes.ANIMALS_CATALOGUE_GUEST)
                }
            )
        }

        composable(NavigationRoutes.LOGIN) {
            LoginScreen(authViewModel = authViewModel)
        }

        composable(NavigationRoutes.ANIMALS_CATALOGUE_GUEST) {
            AnimalCatalogueScreen(
                viewModel = animalViewModel,
                isLoggedIn = isAuthenticated,
                onAnimalClick = { animalId ->
                    navController.navigate(
                        NavigationRoutes.animalDetailPageGuest(animalId)
                    )
                }
            )
        }
    }
}
