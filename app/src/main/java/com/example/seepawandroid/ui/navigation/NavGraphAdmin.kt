package com.example.seepawandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seepawandroid.ui.viewmodels.DemoViewModel
import com.example.seepawandroid.ui.screens.admin.AdminHomeScreen_DEMO
import com.example.seepawandroid.ui.screens.login.AuthViewModel

@Composable
fun NavGraphAdmin(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.ADMIN_HOME
    ) {
        composable(NavigationRoutes.ADMIN_HOME) {
            val demoViewModel: DemoViewModel = hiltViewModel() // Para Demo funcionar
            AdminHomeScreen_DEMO(authViewModel, demoViewModel.sessionManager)
        }
    }
}