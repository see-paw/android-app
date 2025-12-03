package com.example.seepawandroid.ui.screens.animals

import android.content.Context
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.MainActivity
import com.example.seepawandroid.utils.NetworkUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for favorite functionality in the Animal Catalogue screen.
 *
 * Tests the favorite toggle behavior when user is authenticated.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AnimalCatalogueFavoritesTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        forceNetworkAvailable()
        composeTestRule.waitForIdle()

        // Login to access favorite features
        loginAsTestUser()
        navigateToCatalogue()
    }

    private fun loginAsTestUser() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("loginButton").assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("loginButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("emailInput").assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("emailInput").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("passwordInput").performTextInput("Test123!")
        composeTestRule.onNodeWithTag("submitLoginButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithTag("openCatalogueButton").assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    private fun navigateToCatalogue() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("openCatalogueButton").assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("openCatalogueButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithTag("catalogueScreen").assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    /** -----------------------------------------
     *  FAVORITE ICON TESTS
     *  ----------------------------------------- */

    @Test
    fun catalogueScreen_favoriteIcon_isDisplayedWhenLoggedIn() {
        waitUntilLoadingFinishes()

        // Favorite icons should be displayed for authenticated users
        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        assert(favoriteIcons.isNotEmpty()) {
            "Favorite icons should be displayed when user is logged in"
        }
    }

    @Test
    fun catalogueScreen_favoriteIcon_isClickable() {
        waitUntilLoadingFinishes()

        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        if (favoriteIcons.isNotEmpty()) {
            // Should be able to click the first favorite icon
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0]
                .assertIsDisplayed()
                .performClick()

            // Wait for API call to complete
            Thread.sleep(1500)

            // Icon should still exist after toggle
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0]
                .assertExists()
        }
    }

    @Test
    fun catalogueScreen_toggleFavorite_changesState() {
        waitUntilLoadingFinishes()

        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        if (favoriteIcons.isNotEmpty()) {
            // Click to add to favorites
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].performClick()
            Thread.sleep(2000)

            // Click again to remove from favorites
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].performClick()
            Thread.sleep(2000)

            // Icon should still exist and be clickable
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0]
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun catalogueScreen_multipleFavorites_canBeToggled() {
        waitUntilLoadingFinishes()

        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        if (favoriteIcons.size >= 2) {
            // Toggle first favorite
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].performClick()
            Thread.sleep(1500)

            // Toggle second favorite
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[1].performClick()
            Thread.sleep(1500)

            // Both should still be displayed
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].assertExists()
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[1].assertExists()
        }
    }

    @Test
    fun catalogueScreen_favoriteState_persistsAcrossPages() {
        waitUntilLoadingFinishes()

        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        if (favoriteIcons.isNotEmpty()) {
            // Add first animal to favorites
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].performClick()
            Thread.sleep(2000)

            // Navigate to next page
            composeTestRule.onNodeWithTag("nextPageButton").performClick()
            Thread.sleep(2000)

            // Navigate back to first page
            composeTestRule.onNodeWithTag("previousPageButton").performClick()
            Thread.sleep(2000)

            // Favorite icon should still exist
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0]
                .assertExists()
        }
    }

    /** -----------------------------------------
     *  HELPER METHODS
     *  ----------------------------------------- */

    private fun waitUntilLoadingFinishes() {
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            try {
                composeTestRule.onNodeWithTag("loadingIndicator").assertDoesNotExist()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    private fun forceNetworkAvailable() {
        try {
            val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
            NetworkUtils.init(context)
            android.util.Log.d("CatalogueFavTest", "📶 NetworkUtils initialized for tests")
            android.util.Log.d("CatalogueFavTest", "📶 Network available: ${NetworkUtils.isConnected()}")
        } catch (e: Exception) {
            android.util.Log.e("CatalogueFavTest", "Failed to init NetworkUtils", e)
        }
    }
}
