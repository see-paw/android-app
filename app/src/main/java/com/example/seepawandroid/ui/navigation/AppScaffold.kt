package com.example.seepawandroid.ui.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import pt.ipp.estg.seepaw.ui.navigation.AppTopBar

/**
 * Scaffold principal da aplicação SeePaw
 * Gere a estrutura geral da app incluindo TopBar, Drawer e navegação
 *
 * @param isLoggedIn Estado de autenticação do utilizador
 * @param onLoginSuccess Callback executado após login bem-sucedido
 * @param onLogout Callback executado após logout
 * @param windowSize Tamanho da janela para layouts responsivos
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    isLoggedIn: Boolean,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit,
    //windowSize: WindowWidthSizeClass
) {
    // colocar variáveis de ViewModels aqui


    // Navegação
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Callback de logout que limpa a sessão e navega - preciso atualizar quando houver authviewmodel
    val onLogoutAndNavigate: () -> Unit = {
        onLogout()
    }

    val userDrawerOptions = getUserDrawerOptions()
    val selectedDrawerOption = userDrawerOptions.find { it.route == currentRoute }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isLoggedIn,
        drawerContent = {
            if (isLoggedIn) {
                DrawerUser(
                    items = userDrawerOptions,
                    selected = selectedDrawerOption,
                    onSelect = {
                        scope.launch { drawerState.close() }
                        navController.navigate(it.route) {
                            launchSingleTop = true
                        }
                    },
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    isLoggedIn = isLoggedIn,
                    currentRoute = currentRoute,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onLogoutClick = onLogoutAndNavigate,
                    onNotificationsClick = { navController.navigate("Notifications") }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when {
                    !isLoggedIn -> {
                        NavGraphPublic(
                            navController = navController,
                            onLoginSuccess = onLoginSuccess
                        )
                    }
                    else -> {
                        NavGraphUser(
                            navController = navController,
                            //windowSize = windowSize,
                            isLoggedIn = isLoggedIn
                        )
                    }
                }
            }
        }
    }
}