package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueScreen
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel

import com.example.seepawandroid.ui.screens.login.AuthViewModel
import com.example.seepawandroid.ui.screens.user.UserHomeScreen_DEMO

/**
 * Grafo de navegação para utilizadores autenticados
 * Inclui todas as funcionalidades da app: catálogo, favoritos, pedidos de ownership,
 * agendamento de atividades, notificações, etc.
 * Navigation graph for authenticated user screens.
 *
 * @param navController Controlador de navegação
 * @param windowSize Tamanho da janela para layouts responsivos
 * @param isLoggedIn Estado de autenticação (deve ser sempre true neste grafo)
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
            UserHomeScreen_DEMO(authViewModel)
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