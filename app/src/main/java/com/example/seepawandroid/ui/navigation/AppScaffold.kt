package com.example.seepawandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.seepawandroid.data.models.enums.UserRole
import com.example.seepawandroid.ui.screens.login.AuthViewModel

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    val isAuthenticated by authViewModel.isAuthenticated.observeAsState(false)
    val role by authViewModel.userRole.observeAsState("")

    val userRole = UserRole.fromString(role)

    when {
        !isAuthenticated -> NavGraphPublic(navController, authViewModel)
        userRole == UserRole.ADMIN_CAA -> NavGraphAdmin(navController, authViewModel)
        else -> NavGraphUser(navController, authViewModel)
    }
}