package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.ScreenPlaceholder
import com.example.seepawandroid.ui.screens.login.AuthViewModel
import com.example.seepawandroid.ui.screens.login.LoginScreen

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
    onLoginSuccess: () -> Unit
    authViewModel: AuthViewModel
) {
    // colocar varivaés de ViewModels necessários aqui

    NavHost(
        navController = navController,
        startDestination = "Home"
        startDestination = NavigationRoutes.LOGIN
    ) {
        //colocar rotas aqui

        composable(NavigationRoutes.LOGIN) {
            LoginScreen(authViewModel = authViewModel)
        }
    }

}