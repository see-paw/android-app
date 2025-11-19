package pt.ipp.estg.seepaw.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.seepawandroid.R


/**
 * Barra superior da aplicação SeePaw
 * VERSÃO SIMPLES - Sem dependências de recursos (strings.xml ou drawables)
 *
 * @param isLoggedIn Indica se o utilizador está autenticado
 * @param currentRoute Rota atual da navegação
 * @param onMenuClick Callback para abrir o menu drawer
 * @param onLogoutClick Callback para fazer logout
 * @param onNotificationsClick Callback para ver notificações
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isAuthenticated: Boolean,
    currentRoute: String?,
    onMenuClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // Menu só aparece quando o utilizador está autenticado
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

                // Botão de logout
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
            containerColor = Color.White
        )
    )
}