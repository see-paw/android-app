package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueScreen
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel

import com.example.seepawandroid.ui.screens.DemoViewModel
import com.example.seepawandroid.ui.screens.login.AuthViewModel
import com.example.seepawandroid.ui.screens.user.UserHomeScreen_DEMO

/**
 * Navigation graph for authenticated user screens.
 *
 * Contains routes accessible only after successful authentication.
 *
 * @param navController Navigation controller managing the navigation stack
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraphUser(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // adicionar aqui variáveis de ViewModels necessários

    val isAuthenticated by authViewModel.isAuthenticated.observeAsState(false)

    val animalViewModel: AnimalViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.USER_HOME,
    ) {
        //adicionar aqui rotas
        composable(NavigationRoutes.USER_HOME) {
            val demoViewModel: DemoViewModel = hiltViewModel() // Para Demo funcionar
            UserHomeScreen_DEMO(authViewModel, demoViewModel.sessionManager)
        }

        composable (route = NavigationRoutes.ANIMALS_CATALOGUE) {
            AnimalCatalogueScreen(
                viewModel = animalViewModel,
                isLoggedIn = isAuthenticated,
                onAnimalClick = { animalId ->
                    navController.navigate(route = NavigationRoutes.animalDetailPage(animalId))
                }
            )
        }
    }
}