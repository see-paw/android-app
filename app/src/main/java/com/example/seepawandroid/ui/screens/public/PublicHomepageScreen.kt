package com.example.seepawandroid.ui.screens.public

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seepawandroid.R

/**
 * Homepage shown to unauthenticated (public) users.
 *
 * Provides:
 * - Title and introduction text
 * - Button to login
 * - Button to register
 * - Link to access the animal catalogue in guest mode
 *
 * @param onLogin Navigates to the login screen
 * @param onRegister Navigates to the registration screen
 * @param onOpenCatalogue Opens the animal catalogue (guest mode)
 */
@Composable
fun PublicHomepageScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onOpenCatalogue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.public_home_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.public_home_intro),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("openLoginButton")
            ) {
                Text(stringResource(R.string.public_home_login))
            }

            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.public_home_register))
            }

            TextButton(
                onClick = onOpenCatalogue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("openCatalogueButton")
            ) {
                Text(stringResource(R.string.public_home_catalogue))
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewPublicHomepageScreen() {
    MaterialTheme {
        PublicHomepageScreen(
            onLogin = {},
            onRegister = {},
            onOpenCatalogue = {}
        )
    }
}
