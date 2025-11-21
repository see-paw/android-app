package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueScreen
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel
import com.example.seepawandroid.ui.screens.login.AuthViewModel
import com.example.seepawandroid.ui.screens.login.LoginScreen
import com.example.seepawandroid.ui.screens.public.PublicHomepageScreen

/**
 * Grafo de navegação para utilizadores não autenticados (público)
 * Inclui: Home, Login, Register, Catálogo de animais (modo convidado)
 * Navigation graph for public (unauthenticated) screens.
 *
 * @param navController Controlador de navegação
 * @param authViewModel ViewModel de autenticação
 * @param onLoginSuccess Callback executado após login bem-sucedido
 * Contains routes accessible without authentication.
 *
 * @param navController Navigation controller managing the navigation stack
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraphPublic(
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    // colocar variáveis de ViewModels necessários aqui

    val isAuthenticated = false
    val animalViewModel: AnimalViewModel = hiltViewModel()


    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.HOMEPAGE
    ) {
        //colocar rotas aqui

        composable(NavigationRoutes.HOMEPAGE) {
            PublicHomepageScreen(
                onLogin = {
                    navController.navigate(NavigationRoutes.LOGIN)
                },
                onRegister = {
                    navController.navigate(NavigationRoutes.REGISTER)
                },
                onOpenCatalogue = {
                    navController.navigate(NavigationRoutes.ANIMALS_CATALOGUE_GUEST)
                }
            )
        }

        composable(NavigationRoutes.LOGIN) {
            LoginScreen(authViewModel = authViewModel)
        }

        composable (route = NavigationRoutes.ANIMALS_CATALOGUE_GUEST) {
            AnimalCatalogueScreen(
                viewModel = animalViewModel,
                isLoggedIn = isAuthenticated,
                onAnimalClick = { animalId ->
                    navController.navigate(route = NavigationRoutes.animalDetailPageGuest(animalId))
                }
            )
        }
    }

}