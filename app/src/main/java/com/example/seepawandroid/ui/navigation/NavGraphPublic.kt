package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.ScreenPlaceholder

/**
 * Grafo de navegação para utilizadores não autenticados (público)
 * Inclui: Home, Login, Register, Catálogo de animais (modo convidado)
 *
 * @param navController Controlador de navegação
 * @param authViewModel ViewModel de autenticação
 * @param onLoginSuccess Callback executado após login bem-sucedido
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraphPublic(
    navController: NavHostController,
    onLoginSuccess: () -> Unit
) {
    // colocar varivaés de ViewModels necessários aqui

    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        //colocar rotas aqui

        //placeholders para teste
        composable("Home") { ScreenPlaceholder("HOME PUBLIC") }
        composable("Login") { ScreenPlaceholder("LOGIN") }
        composable("Register") { ScreenPlaceholder("REGISTER") }
        composable("AnimalsCatalogueGuest") { ScreenPlaceholder("CATÁLOGO GUEST") }
        composable("AnimalPublicDetails/{id}") { ScreenPlaceholder("DETALHES PUBLIC") }
        }
    }
