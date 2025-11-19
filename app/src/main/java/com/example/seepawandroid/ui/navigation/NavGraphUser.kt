package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.ScreenPlaceholder

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
    //windowSize: WindowWidthSizeClass,
    isLoggedIn: Boolean = true
) {
    // adicionar aqui variáveis de ViewModels necessários

fun NavGraphUser(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.USER_HOME
        startDestination = "UserHome"
    ) {
        composable(NavigationRoutes.USER_HOME) {
            UserHomeScreen_DEMO(authViewModel)
        }

        //adicionar aqui rotas


    }
}