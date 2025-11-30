package com.example.seepawandroid.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.navigation.AppScaffold
import com.example.seepawandroid.ui.screens.login.AuthViewModel

/**
 * Main entry point for the SeePaw application UI.
 *
 * This composable is called from MainActivity and sets up the entire app structure.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SeePawApp(authViewModel: AuthViewModel = hiltViewModel()) {

    Box(modifier = Modifier.fillMaxSize()) {

        // global wallpaper for all the app
        Image(
            painter = painterResource(id = R.drawable.seepaw_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        AppScaffold(
            onLogout = {
                authViewModel.logout()
            }
        )
    }
}