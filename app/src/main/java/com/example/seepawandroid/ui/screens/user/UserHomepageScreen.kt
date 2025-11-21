package com.example.seepawandroid.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seepawandroid.R

@Composable
fun UserHomepageScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            )

            Text(
                text = stringResource(R.string.home_intro_1),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )

            Text(
                text = stringResource(R.string.home_intro_2),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.home_slogan),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewUserHomeScreen() {
    MaterialTheme {
        UserHomepageScreen()
    }
}
