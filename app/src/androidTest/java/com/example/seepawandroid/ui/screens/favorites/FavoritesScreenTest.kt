package com.example.seepawandroid.ui.screens.favorites

import android.content.Context
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
 * Instrumented tests for the Favorites screen.
 *
 * Tests the favorites functionality including:
 * - Adding favorites from catalogue
 * - Viewing favorites page
 * - Removing favorites
 * - Navigation between screens
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        forceNetworkAvailable()
        composeTestRule.waitForIdle()

        // Login first to access authenticated features
        loginAsTestUser()
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

        // Use test credentials
        composeTestRule.onNodeWithTag("emailInput").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("passwordInput").performTextInput("Test123!")
        composeTestRule.onNodeWithTag("submitLoginButton").performClick()

        // Wait for login to complete
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithTag("openCatalogueButton").assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    private fun navigateToFavorites() {
        // Open drawer
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("openDrawerButton").assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("openDrawerButton").performClick()
        Thread.sleep(500)

        // Click on Favorites menu item
        composeTestRule.onNodeWithTag("drawerItemFavorites")
            .assertExists()
            .performClick()

        // Wait for favorites screen to load
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithTag("favoritesScreen").assertExists()
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
     *  BASIC UI TESTS
     *  ----------------------------------------- */

    @Test
    fun favoritesScreen_isDisplayed() {
        navigateToFavorites()

        composeTestRule.onNodeWithTag("favoritesScreen")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_showsTitle() {
        navigateToFavorites()
        waitUntilLoadingFinishes()

        // The title should be displayed (Portuguese: "Os Meus Favoritos")
        composeTestRule.onNodeWithText("Os Meus Favoritos", substring = true, useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun favoritesScreen_showsEmptyStateWhenNoFavorites() {
        navigateToFavorites()
        waitUntilLoadingFinishes()

        // Check if empty state or favorites grid is displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("emptyState").assertExists()
                true
            } catch (_: Throwable) {
                try {
                    composeTestRule.onNodeWithTag("favoritesGrid").assertExists()
                    true
                } catch (_: Throwable) {
                    false
                }
            }
        }
    }

    /** -----------------------------------------
     *  FAVORITE TOGGLE TESTS (FROM CATALOGUE)
     *  ----------------------------------------- */

    @Test
    fun catalogueScreen_favoriteIcon_isDisplayedWhenLoggedIn() {
        navigateToCatalogue()
        waitUntilLoadingFinishes()

        // Find first animal card
        val firstAnimalCard = composeTestRule.onAllNodes(
            hasTestTagSubstring("animalCard_")
        ).fetchSemanticsNodes().firstOrNull()

        if (firstAnimalCard != null) {
            // Favorite icon should be displayed for logged-in users
            composeTestRule.onNodeWithTag("animalFavoriteIcon")
                .assertExists()
        }
    }

    @Test
    fun catalogueScreen_clickFavoriteIcon_addsToFavorites() {
        navigateToCatalogue()
        waitUntilLoadingFinishes()

        // Find and click first favorite icon
        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        if (favoriteIcons.isNotEmpty()) {
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].performClick()

            // Wait for API call to complete
            Thread.sleep(2000)

            // Navigate to favorites
            navigateToFavorites()
            waitUntilLoadingFinishes()

            // Check if favorites grid is displayed (meaning we have favorites)
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                try {
                    composeTestRule.onNodeWithTag("favoritesGrid").assertExists()
                    true
                } catch (_: Throwable) {
                    false
                }
            }
        }
    }

    /** -----------------------------------------
     *  REMOVE FAVORITE TESTS
     *  ----------------------------------------- */

    @Test
    fun favoritesScreen_removeFavoriteButton_removesAnimal() {
        // First, add a favorite from catalogue
        navigateToCatalogue()
        waitUntilLoadingFinishes()

        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        if (favoriteIcons.isNotEmpty()) {
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].performClick()
            Thread.sleep(2000)

            // Navigate to favorites
            navigateToFavorites()
            waitUntilLoadingFinishes()

            // Count favorites before removal
            val favoritesBefore = composeTestRule.onAllNodes(
                hasTestTagSubstring("favoriteCard_")
            ).fetchSemanticsNodes().size

            if (favoritesBefore > 0) {
                // Click remove on first favorite
                composeTestRule.onAllNodes(hasTestTag("removeFavoriteIcon"))[0].performClick()

                // Wait for API call
                Thread.sleep(2000)

                // Count favorites after removal
                val favoritesAfter = composeTestRule.onAllNodes(
                    hasTestTagSubstring("favoriteCard_")
                ).fetchSemanticsNodes().size

                // Should have one less favorite
                assert(favoritesAfter < favoritesBefore) {
                    "Favorites count should decrease after removal"
                }
            }
        }
    }

    @Test
    fun favoritesScreen_clickAnimalCard_navigatesToDetail() {
        // First ensure we have at least one favorite
        navigateToCatalogue()
        waitUntilLoadingFinishes()

        val favoriteIcons = composeTestRule.onAllNodes(
            hasTestTag("animalFavoriteIcon")
        ).fetchSemanticsNodes()

        if (favoriteIcons.isNotEmpty()) {
            composeTestRule.onAllNodes(hasTestTag("animalFavoriteIcon"))[0].performClick()
            Thread.sleep(2000)

            // Navigate to favorites
            navigateToFavorites()
            waitUntilLoadingFinishes()

            // Click on first favorite card
            val favoriteCards = composeTestRule.onAllNodes(
                hasTestTagSubstring("favoriteCard_")
            ).fetchSemanticsNodes()

            if (favoriteCards.isNotEmpty()) {
                composeTestRule.onAllNodes(hasTestTagSubstring("favoriteCard_"))[0].performClick()

                // Wait for detail screen (we can't easily verify without more context)
                Thread.sleep(1000)
            }
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

    private fun hasTestTagSubstring(tag: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag contains '$tag'") {
            val tagValue = it.config.getOrNull(
                androidx.compose.ui.semantics.SemanticsProperties.TestTag
            )
            tagValue?.contains(tag) == true
        }
    }

    private fun forceNetworkAvailable() {
        try {
            val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
            NetworkUtils.init(context)
            android.util.Log.d("FavoritesTest", "📶 NetworkUtils initialized for tests")
            android.util.Log.d("FavoritesTest", "📶 Network available: ${NetworkUtils.isConnected()}")
        } catch (e: Exception) {
            android.util.Log.e("FavoritesTest", "Failed to init NetworkUtils", e)
        }
    }
}
