package com.example.seepawandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.screens.login.AuthViewModel
import com.example.seepawandroid.ui.screens.login.LoginScreen

/**
 * Navigation graph for public (unauthenticated) screens.
 *
 * Contains routes accessible without authentication.
 *
 * @param navController Navigation controller managing the navigation stack
 */
@Composable
fun NavGraphPublic(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.LOGIN
    ) {
        composable(NavigationRoutes.LOGIN) {
            LoginScreen(authViewModel = authViewModel)
        }
    }
}