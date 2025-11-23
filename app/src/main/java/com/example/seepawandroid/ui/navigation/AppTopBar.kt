package pt.ipp.estg.seepaw.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R

/**
 * Main top app bar displayed in authenticated screens.
 *
 * Shows:
 * - App logo
 * - Navigation menu icon (authenticated users only)
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
    onLogoutClick: () -> Unit
) {
    val showMenuIcon = isAuthenticated

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
                IconButton(onClick = onMenuClick) {
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
                IconButton(onClick = onLogoutClick) {
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
