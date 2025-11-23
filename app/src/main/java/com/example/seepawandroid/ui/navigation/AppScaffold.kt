package com.example.seepawandroid.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.seepawandroid.data.models.enums.UserRole
import com.example.seepawandroid.ui.screens.login.AuthViewModel
import pt.ipp.estg.seepaw.ui.navigation.AppTopBar

/**
 * Main scaffold of the application.
 *
 * Controls:
 * - Authentication flow (public, user, admin)
 * - Drawer navigation for authenticated users
 * - Top app bar
 * - Navigation graphs
 *
 * @param onLoginSuccess Callback triggered after successful authentication.
 * @param onLogout Callback used to execute logout actions.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Authentication state
    val isAuthenticated by authViewModel.isAuthenticated.observeAsState(false)
    val role by authViewModel.userRole.observeAsState("")
    val userRole = UserRole.fromString(role)

    // ----- PUBLIC MODE -----
    if (!isAuthenticated) {
        NavGraphPublic(
            navController = navController,
            onLoginSuccess = onLoginSuccess,
            authViewModel = authViewModel
        )
        return
    }

    // ----- ADMIN MODE -----
    if (userRole == UserRole.ADMIN_CAA) {
        NavGraphAdmin(
            navController = navController,
            authViewModel = authViewModel
        )
        return
    }

    // ----- USER MODE -----
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerOptions = getUserDrawerOptions()
    val selectedDrawerOption = drawerOptions.find { it.route == currentRoute }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DrawerUser(
                items = drawerOptions,
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
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    isAuthenticated = isAuthenticated,
                    currentRoute = currentRoute,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onLogoutClick = onLogout
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(Modifier.padding(padding)) {
                NavGraphUser(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
