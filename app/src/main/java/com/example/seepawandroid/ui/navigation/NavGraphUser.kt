package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.ScreenPlaceholder


/**
 * Grafo de navegação para utilizadores autenticados
 * Inclui todas as funcionalidades da app: catálogo, favoritos, pedidos de ownership,
 * agendamento de atividades, notificações, etc.
 *
 * @param navController Controlador de navegação
 * @param windowSize Tamanho da janela para layouts responsivos
 * @param isLoggedIn Estado de autenticação (deve ser sempre true neste grafo)
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraphUser(
    navController: NavHostController,
    //windowSize: WindowWidthSizeClass,
    isLoggedIn: Boolean = true
) {
    // adicionar aqui variáveis de ViewModels necessários

    NavHost(
        navController = navController,
        startDestination = "UserHome"
    ) {

        //adicionar aqui rotas

        //placeholders para teste
        composable("UserHome") { ScreenPlaceholder("HOME USER") }
        composable("AnimalsCatalogue") { ScreenPlaceholder("CATÁLOGO USER") }
        composable("Favorites") { ScreenPlaceholder("FAVORITOS") }
        composable("ScheduleActivities") { ScreenPlaceholder("MARCAR ATIVIDADE") }
        composable("ActiveActivities") { ScreenPlaceholder("ATIVIDADES ATIVAS") }
        composable("Requests") { ScreenPlaceholder("PEDIDOS DE OWNERSHIP") }

    }
}