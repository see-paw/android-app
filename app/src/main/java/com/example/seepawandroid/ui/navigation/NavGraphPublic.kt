package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueScreen
import com.example.seepawandroid.ui.screens.animals.AnimalDetailScreen
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel
import com.example.seepawandroid.ui.screens.login.AuthViewModel
import com.example.seepawandroid.ui.screens.login.LoginScreen
import com.example.seepawandroid.ui.screens.public.PublicHomepageScreen
import com.example.seepawandroid.ui.screens.register.RegisterScreen

/**
 * Navigation graph for public (unauthenticated) screens.
 *
 * Contains routes accessible without authentication.
 * Includes:
 * - Homepage
 * - Login
 * - Register
 * - Animal catalogue in guest mode
 *
 * @param navController Navigation controller managing the navigation stack
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

        composable(NavigationRoutes.ANIMALS_CATALOGUE_GUEST) {
            AnimalCatalogueScreen(
                viewModel = animalViewModel,
                isLoggedIn = isAuthenticated,
                onAnimalClick = { animalId ->
                    navController.navigate("${NavigationRoutes.ANIMAL_DETAIL_PAGE_GUEST_BASE}/$animalId")
                }
            )
        }

        // Login Screen
        composable(NavigationRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(NavigationRoutes.REGISTER) }
            )
        }

        // Register Screen
        composable(NavigationRoutes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        popUpTo(NavigationRoutes.HOMEPAGE) { inclusive = false }
                    }
                }
            )
        }

        // Animal Detail Screen (Guest mode)
        composable(
            route = NavigationRoutes.ANIMAL_DETAIL_PAGE_GUEST,
            arguments = listOf(
                navArgument("animalId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""

            AnimalDetailScreen(
                animalId = animalId,
                isAuthenticated = false,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        launchSingleTop = true
                    }
                },
                onNavigateToOwnership = { /* Guest users can't adopt, handled by auth check */ }
            )
        }
    }
}