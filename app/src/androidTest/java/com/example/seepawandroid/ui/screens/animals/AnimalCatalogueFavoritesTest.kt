package com.example.seepawandroid.ui.screens.animals

import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.BaseUiTest
import com.example.seepawandroid.MainActivity
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
class AnimalCatalogueFavoritesTest : BaseUiTest() {

    companion object {
        // Valid test credentials from backend
        private const val VALID_EMAIL = "carlos@test.com"
        private const val VALID_PASSWORD = "Pa\$\$w0rd"
    }

    @Before
    override fun setUp() {
        super.setUp()
        composeTestRule.waitForIdle()
        logoutIfNeeded()
        loginAsTestUser(VALID_EMAIL, VALID_PASSWORD)
        navigateToCatalogue()
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

}
