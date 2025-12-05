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
import androidx.compose.material.icons.outlined.VolunteerActivism
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R

/**
 * Represents an option in the navigation drawer.
 *
 * @property label The string resource for the option's label.
 * @property icon The icon for the option.
 * @property route The navigation route associated with the option.
 */
data class DrawerOption(
    @StringRes val label: Int,
    val icon: ImageVector,
    val route: String
)

/**
 * Returns all drawer options available for authenticated users.
 *
 * @return A list of [DrawerOption] for authenticated users.
 */
fun getUserDrawerOptions() = listOf(
    DrawerOption(R.string.catalogue, Icons.Outlined.Pets, NavigationRoutes.ANIMALS_CATALOGUE),
    DrawerOption(R.string.favorites, Icons.Outlined.FavoriteBorder, NavigationRoutes.FAVORITES),
    DrawerOption(R.string.schedule_activities, Icons.Outlined.CalendarMonth, "ScheduleActivities"),
    DrawerOption(R.string.active_activities, Icons.Outlined.CalendarMonth, "ActiveActivities"),
    DrawerOption(R.string.requests, Icons.Outlined.ListAlt, NavigationRoutes.OWNERSHIP_LIST),
    DrawerOption(R.string.my_fosterings, Icons.Outlined.VolunteerActivism, NavigationRoutes.FOSTERING_LIST),
)

/**
 * Composable that displays the navigation drawer for authenticated users.
 *
 * @param items The list of drawer options to display.
 * @param selected The currently selected drawer option.
 * @param onSelect The callback invoked when a drawer option is selected.
 * @param onCloseDrawer The callback invoked to close the drawer.
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
                if (item.route == NavigationRoutes.ANIMALS_CATALOGUE)
                    Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawerItemCatalogue")
                else if (item.route == NavigationRoutes.FAVORITES)
                    Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawerItemFavorites")
                else if (item.route == NavigationRoutes.OWNERSHIP_LIST)
                    Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawerItemOwnershipList")
                else if (item.route == NavigationRoutes.FOSTERING_LIST)
                    Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawerItemFosteringList")
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
