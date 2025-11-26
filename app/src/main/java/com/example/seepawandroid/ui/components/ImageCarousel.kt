package com.example.seepawandroid.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.seepawandroid.R

/**
 * Stateless image carousel component for displaying animal images.
 *
 * Displays a horizontal pager with images and page indicators below.
 * All state is managed externally.
 *
 * @param imageUrls List of image URLs to display.
 * @param animalName Name of the animal (for content description).
 * @param modifier Optional modifier.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    animalName: String,
    modifier: Modifier = Modifier
) {
    if (imageUrls.isEmpty()) {
        // No images - show placeholder
        AsyncImage(
            model = null,
            placeholder = painterResource(R.drawable.no_image_found),
            error = painterResource(R.drawable.no_image_found),
            contentDescription = "No image available for $animalName",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .testTag("animalImage_placeholder")
        )
    } else {
        val pagerState = rememberPagerState(pageCount = { imageUrls.size })

        Column(modifier = modifier) {
            // Image pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .testTag("animalImageCarousel")
            ) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    placeholder = painterResource(R.drawable.no_image_found),
                    error = painterResource(R.drawable.no_image_found),
                    contentDescription = "Image ${page + 1} of $animalName",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("animalImage_$page")
                )
            }

            // Page indicators
            if (imageUrls.size > 1) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(imageUrls.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(horizontal = 2.dp)
                                .clip(CircleShape)
                                .then(
                                    if (index == pagerState.currentPage) {
                                        Modifier
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (index == pagerState.currentPage) {
                                        Color(0xFF37474F)
                                    } else {
                                        Color.LightGray
                                    }
                                )
                            ) {}
                        }
                    }
                }
            }
        }
    }
}