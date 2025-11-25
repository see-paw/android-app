package com.example.seepawandroid.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R

/**
 * Represents a navigation drawer item for authenticated users.
 *
 * @param label String resource ID for the displayed text.
 * @param icon Icon to show in the drawer.
 * @param route Navigation route associated with this option.
 */
data class DrawerOption(
    @StringRes val label: Int,
    val icon: ImageVector,
    val route: String
)

/**
 * Returns all drawer options available for authenticated users.
 */
fun getUserDrawerOptions() = listOf(
    DrawerOption(R.string.catalogue, Icons.Outlined.Pets, "AnimalsCatalogue"),
    DrawerOption(R.string.favorites, Icons.Outlined.FavoriteBorder, "Favorites"),
    DrawerOption(R.string.schedule_activities, Icons.Outlined.CalendarMonth, "ScheduleActivities"),
    DrawerOption(R.string.active_activities, Icons.Outlined.CalendarMonth, "ActiveActivities"),
    DrawerOption(R.string.requests, Icons.Outlined.ListAlt, "Requests"),
)

/**
 * Drawer component used in authenticated user screens.
 *
 * @param items All available drawer options.
 * @param selected Currently selected item.
 * @param onSelect Triggered when the user selects a drawer option.
 * @param onCloseDrawer Called to close the navigation drawer.
 */
@Composable
fun DrawerUser(
    items: List<DrawerOption>,
    selected: DrawerOption?,
    onSelect: (DrawerOption) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCloseDrawer) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_menu)
                )
            }
        }

        HorizontalDivider()

        items.forEach { item ->
            val itemModifier =
                if (item.route == "AnimalsCatalogue")
                    Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawerItemCatalogue")
                else
                    Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)

            NavigationDrawerItem(
                label = { Text(stringResource(id = item.label)) },
                selected = item == selected,
                onClick = { onSelect(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(id = item.label)
                    )
                },
                modifier = itemModifier
            )
        }
    }
}
