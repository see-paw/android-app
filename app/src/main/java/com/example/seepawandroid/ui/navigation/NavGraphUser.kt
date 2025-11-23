package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueScreen
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel
import com.example.seepawandroid.ui.screens.login.AuthViewModel
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
                    navController.navigate(
                        NavigationRoutes.animalDetailPage(animalId)
                    )
                }
            )
        }
    }
}
