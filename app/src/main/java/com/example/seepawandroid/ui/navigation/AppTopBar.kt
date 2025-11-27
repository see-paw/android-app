package pt.ipp.estg.seepaw.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.viewmodels.NotificationDropdownUiState
import pt.ipp.estg.seepawandroid.ui.components.NotificationDropdown
import pt.ipp.estg.seepawandroid.ui.viewmodels.NotificationViewModel

/**
 * Main top app bar displayed in authenticated screens.
 *
 * Shows:
 * - App logo
 * - Navigation menu icon (authenticated users only)
 * - Notification bell with badge (authenticated users only)
 * - Logout button
 *
 * @param isAuthenticated Indicates whether the user is authenticated.
 * @param currentRoute Current active navigation route.
 * @param onMenuClick Opens the side drawer.
 * @param onLogoutClick Executes logout action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isAuthenticated: Boolean,
    currentRoute: String?,
    onMenuClick: () -> Unit,
    onLogoutClick: () -> Unit,
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val showMenuIcon = isAuthenticated
    val unreadCount by notificationViewModel.uiState.observeAsState()
    var showNotificationDropdown by remember { mutableStateOf(false) }

    // Extract unread count from UI state
    val count = when (val state = unreadCount) {
        is NotificationDropdownUiState.Success -> state.unreadCount
        else -> 0
    }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.seepaw_logo),
                    contentDescription = stringResource(R.string.logo_description),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
            }
        },
        navigationIcon = {
            if (showMenuIcon) {
                IconButton(onClick = onMenuClick, modifier = Modifier.testTag("openDrawerButton")) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.open_menu),
                        tint = Color(0xFF37474F)
                    )
                }
            }
        },
        actions = {
            if (isAuthenticated) {
                // Notification bell with badge
                Box {
                    BadgedBox(
                        badge = {
                            if (count > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error
                                ) {
                                    Text(
                                        text = if (count > 99) "99+" else count.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = {
                                showNotificationDropdown = !showNotificationDropdown
                                if (showNotificationDropdown) {
                                    notificationViewModel.resetToFirstPage()
                                }
                            },
                            modifier = Modifier.testTag("notificationButton")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = stringResource(R.string.notifications),
                                tint = Color(0xFF37474F)
                            )
                        }
                    }

                    // Notification dropdown
                    DropdownMenu(
                        expanded = showNotificationDropdown,
                        onDismissRequest = { showNotificationDropdown = false },
                        offset = DpOffset(x = (-150).dp, y = 0.dp)
                    ) {
                        NotificationDropdown(
                            viewModel = notificationViewModel,
                            onDismiss = { showNotificationDropdown = false }
                        )
                    }
                }

                // Logout button
                IconButton(onClick = onLogoutClick, modifier = Modifier.testTag("logoutButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = stringResource(R.string.logout),
                        tint = Color(0xFF37474F)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            navigationIconContentColor = Color(0xFF37474F),
            titleContentColor = Color.Black,
            actionIconContentColor = Color(0xFF37474F)
        )
    )
}