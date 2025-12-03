package com.example.seepawandroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.seepawandroid.R

/**
 * FavoriteAnimalCard Component
 *
 * Displays a card for a favorite animal in the Favorites screen.
 * Includes:
 *  - Animal image
 *  - Name + age
 *  - Remove from favorites button (filled heart)
 *
 * Test Tags (for automated UI tests):
 *  - favoriteAnimalCard
 *  - favoriteAnimalImage
 *  - favoriteAnimalName
 *  - removeFavoriteIcon
 */
@Composable
fun FavoriteAnimalCard(
    name: String,
    age: Int,
    imageUrl: String?,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("favoriteAnimalCard"),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Animal image
            AsyncImage(
                model = imageUrl,
                placeholder = painterResource(R.drawable.no_image_found),
                error = painterResource(R.drawable.no_image_found),
                contentDescription = stringResource(
                    R.string.animal_image_desc,
                    name
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .testTag("favoriteAnimalImage")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Name + age text
                Text(
                    text = "$name, $age ${stringResource(R.string.animal_years_suffix)}",
                    modifier = Modifier.testTag("favoriteAnimalName")
                )

                // Remove from favorites button (filled heart)
                IconButton(
                    onClick = onRemoveFavorite,
                    modifier = Modifier.testTag("removeFavoriteIcon")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.remove_favorite),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
