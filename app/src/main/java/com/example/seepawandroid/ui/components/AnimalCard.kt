package com.example.seepawandroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
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
 * AnimalCard Component
 *
 * Displays a small card used inside the Animal Catalogue grid.
 * Includes:
 *  - Animal image
 *  - Name + age
 *  - Optional "favorite" icon (only when logged in)
 *
 * Test Tags (for automated UI tests):
 *  - animalCard
 *  - animalImage
 *  - animalName
 *  - animalAge
 *  - animalFavoriteIcon
 */
@Composable
fun AnimalCard(
    name: String,
    age: Int,
    imageUrl: String?,
    isLoggedIn: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("animalCard"),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {

            //Animal image
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
                    .testTag("animalImage")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                //Name + age text
                Text(
                    text = "$name, $age ${stringResource(R.string.animal_years_suffix)}",
                    modifier = Modifier.testTag("animalName")
                )


                //Favorite icon (only when logged in)
                if (isLoggedIn) {
                    Icon(
                        imageVector = if (isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("animalFavoriteIcon")
                    )
                }
            }
        }
    }
}
