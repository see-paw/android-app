package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.seepawandroid.ui.screens.ownerships.OwnershipRequestScreen
import com.example.seepawandroid.ui.screens.user.UserHomepageScreen

/**
 * Navigation graph for authenticated user screens.
 *
 * Contains routes accessible only after the user successfully logs in.
 *
 * @param navController Navigation controller for screen transitions.
 * @param authViewModel Authentication state ViewModel.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraphUser(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val isAuthenticated by authViewModel.isAuthenticated.observeAsState(false)
    val animalViewModel: AnimalViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.USER_HOMEPAGE,
    ) {
        composable(NavigationRoutes.USER_HOMEPAGE) {
            UserHomepageScreen()
        }

        composable(NavigationRoutes.ANIMALS_CATALOGUE) {
            AnimalCatalogueScreen(
                viewModel = animalViewModel,
                isLoggedIn = isAuthenticated,
                onAnimalClick = { animalId ->
                    navController.navigate("${NavigationRoutes.ANIMAL_DETAIL_PAGE_BASE}/$animalId")
                }
            )
        }

        // Animal Detail Screen (Authenticated mode)
        composable(
            route = NavigationRoutes.ANIMAL_DETAIL_PAGE,
            arguments = listOf(
                navArgument("animalId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""

            AnimalDetailScreen(
                animalId = animalId,
                isAuthenticated = true,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { /* Already authenticated */ },
                onNavigateToOwnership = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Ownership Request (Needs Authentication and internet to navigate)
        composable(
            route = NavigationRoutes.OWNERSHIP_REQUEST,
            arguments = listOf(
                navArgument("animalId") { type = NavType.StringType },
                navArgument("animalName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("shelterId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""
            val animalName = backStackEntry.arguments?.getString("animalName")
            val shelterId = backStackEntry.arguments?.getString("shelterId")

            OwnershipRequestScreen(
                animalId = animalId,
                animalName = animalName,
                shelterId = shelterId,
                onNavigateBack = { navController.popBackStack() },
                onRequestComplete = {
                    // Navigate back to homepage after successful request
                    navController.navigate(NavigationRoutes.USER_HOMEPAGE) {
                        popUpTo(NavigationRoutes.USER_HOMEPAGE) { inclusive = false }
                    }
                }
            )
        }
    }
}
