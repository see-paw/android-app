package com.example.seepawandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
@Composable
fun NavGraphUser(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.USER_HOME
    ) {
        composable(NavigationRoutes.USER_HOME) {
            val demoViewModel: DemoViewModel = hiltViewModel() // Para Demo funcionar
            UserHomeScreen_DEMO(authViewModel, demoViewModel.sessionManager)
        }
    }
}