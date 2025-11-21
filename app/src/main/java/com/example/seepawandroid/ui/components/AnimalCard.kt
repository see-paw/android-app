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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import coil.compose.AsyncImage


@Composable
fun AnimalCard(
    name: String,
    age: Int,
    imageUrl: String?,
    isLoggedIn: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {

            AsyncImage(
                model = imageUrl,
                placeholder = painterResource(R.drawable.cat_image),
                error = painterResource(R.drawable.cat_image),
                contentDescription = stringResource(
                    R.string.animal_image_desc,
                    name
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$name, $age ${stringResource(R.string.animal_years_suffix)}")

                if (isLoggedIn) {//quando exister funcionalidade de favoritos isto passa a ser um botão
                    Icon(
                        imageVector = if (isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
