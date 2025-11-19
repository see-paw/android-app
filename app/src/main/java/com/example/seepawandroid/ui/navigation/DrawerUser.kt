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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R

/**
 * Represents a menu drawer option.
 *
 * @param label String resource ID for the drawer option text.
 * @param icon Material icon to display.
 * @param route Navigation route associated with the option.
 */
data class DrawerOption(
    @StringRes val label: Int,
    val icon: ImageVector,
    val route: String
)

/**
 * Returns the list of drawer options available for authenticated users.
 */
fun getUserDrawerOptions() = listOf(
    DrawerOption(R.string.catalogue, Icons.Outlined.Pets, "AnimalsCatalogue"),
    DrawerOption(R.string.favorites, Icons.Outlined.FavoriteBorder, "Favorites"),
    DrawerOption(R.string.schedule_activities, Icons.Outlined.CalendarMonth, "ScheduleActivities"),
    DrawerOption(R.string.active_activities, Icons.Outlined.CalendarMonth, "ActiveActivities"),
    DrawerOption(R.string.requests, Icons.Outlined.ListAlt, "Requests"),
)

/**
 * Drawer component for authenticated users.
 *
 * @param items List of drawer options to display.
 * @param selected Currently selected drawer option.
 * @param onSelect Callback executed when a drawer option is selected.
 * @param onCloseDrawer Callback to close the drawer.
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
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
