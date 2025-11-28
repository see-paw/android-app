package com.example.seepawandroid.ui.screens.animals

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.MainActivity
import com.example.seepawandroid.R
import com.example.seepawandroid.data.managers.SessionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumented tests for the Animal Detail screen.
 *
 * Tests animal detail display, authentication checks, ownership flow,
 * and navigation behavior.
 *
 * IMPORTANT: The API requires authentication to list animals.
 * Guest tests perform login first to load animals, then logout to test guest behavior.
 *
 * Test credentials:
 * - Valid User: carlos@test.com / Pa$$w0rd
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AnimalDetailScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        private const val VALID_EMAIL = "carlos@test.com"
        private const val VALID_PASSWORD = "Pa\$\$w0rd"

        // Thor animal with valid GUID from seed
        private const val VALID_ANIMAL_ID = "f055cc31-fdeb-4c65-bb73-4f558f67dd5b"

        // Maximum pages to search before giving up
        private const val MAX_PAGES_TO_SEARCH = 10
    }

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.waitForIdle()
        logoutIfNeeded()
    }

    /** -----------------------------------------
     *  CUSTOM MATCHERS
     *  ----------------------------------------- */

    private fun hasTestTagStartingWith(prefix: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag starts with '$prefix'") { node ->
            node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
        }
    }

    /** -----------------------------------------
     *  HELPER FUNCTIONS
     *  ----------------------------------------- */

    private fun logoutIfNeeded() {
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                try {
                    composeTestRule.onNodeWithTag("logoutButton").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }

            composeTestRule.onNodeWithTag("logoutButton").performClick()

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithTag("openLoginButton").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }
        } catch (e: Throwable) {
            // Already logged out
        }
    }

    private fun performLogout() {
        composeTestRule.onNodeWithTag("logoutButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("openLoginButton").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    private fun performLogin() {
        // Navigate to login if needed
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule
                    .onAllNodesWithText("SeePaw Login")
                    .fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: Throwable) {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithTag("openLoginButton")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("openLoginButton").performClick()

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }
        }

        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        waitUntilLoadingFinishes()

        val expectedTitle = composeTestRule.activity.getString(R.string.home_title)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText(expectedTitle).assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    private fun navigateToCatalogue() {
        composeTestRule.onNodeWithTag("openDrawerButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithTag("drawerItemCatalogue").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("drawerItemCatalogue").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodes(hasTestTagStartingWith("animalCard_"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Checks if the target animal is visible on the current page.
     */
    private fun isAnimalOnCurrentPage(animalId: String): Boolean {
        return try {
            composeTestRule.onNodeWithTag("animalCard_$animalId").assertExists()
            true
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * Navigates to the next page in the catalogue if available.
     * Returns true if navigation occurred, false if no next page.
     */
    private fun goToNextPage(): Boolean {
        return try {
            composeTestRule.onNodeWithTag("nextPageButton").assertExists()
            composeTestRule.onNodeWithTag("nextPageButton").assertIsEnabled()
            composeTestRule.onNodeWithTag("nextPageButton").performClick()

            // Wait for page to load
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodes(hasTestTagStartingWith("animalCard_"))
                    .fetchSemanticsNodes().isNotEmpty()
            }
            true
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * Searches through catalogue pages to find and click on a specific animal.
     * This handles the case where the animal might be on any page.
     */
    private fun findAndClickAnimal(animalId: String) {
        var pagesSearched = 0

        while (pagesSearched < MAX_PAGES_TO_SEARCH) {
            // Wait for animals to load on current page
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule
                    .onAllNodes(hasTestTagStartingWith("animalCard_"))
                    .fetchSemanticsNodes().isNotEmpty()
            }

            // Try to scroll to the animal on current page
            try {
                composeTestRule.onNodeWithTag("animalGrid")
                    .performScrollToNode(hasTestTag("animalCard_$animalId"))

                composeTestRule.onNodeWithTag("animalCard_$animalId").performClick()
                return
            } catch (e: Throwable) {
                // Animal not on this page, try next
            }

            // Try to go to next page
            if (!goToNextPage()) {
                // No more pages, animal not found
                throw AssertionError("Animal with ID $animalId not found in catalogue after searching $pagesSearched pages")
            }

            pagesSearched++
        }

        throw AssertionError("Animal with ID $animalId not found after searching $MAX_PAGES_TO_SEARCH pages")
    }

    /**
     * Navigates to a specific animal detail screen using a known valid GUID.
     * Searches through pages until the animal is found.
     */
    private fun navigateToAnimalDetail() {
        findAndClickAnimal(VALID_ANIMAL_ID)

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("animalDetailScreen").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    /**
     * Navigates to animal detail as guest using a specific animal with valid GUID.
     * Since API requires auth to list animals, we:
     * 1. Login first to load animals
     * 2. Navigate to catalogue and animal detail
     * 3. Logout
     * 4. Navigate again to the same animal detail as guest
     */
    private fun navigateToAnimalDetailThenLogout() {
        // Login to get animals
        performLogin()

        // Navigate to catalogue
        navigateToCatalogue()

        // Navigate to specific animal detail (Thor with valid GUID)
        navigateToAnimalDetail()

        waitUntilLoadingFinishes()

        // Now logout while on animal detail
        performLogout()

        // After logout, navigate back to catalogue as guest
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag("openCatalogueButton")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("openCatalogueButton").performClick()

        // Wait for catalogue and find the animal again
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodes(hasTestTagStartingWith("animalCard_"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Find and click on the same specific animal
        findAndClickAnimal(VALID_ANIMAL_ID)

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("animalDetailScreen").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    private fun waitUntilLoadingFinishes() {
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodes(
                hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)
            ).fetchSemanticsNodes().isEmpty()
        }
    }

    /** -----------------------------------------
     *  GUEST USER TESTS
     *  ----------------------------------------- */

    @Test
    fun animalDetail_guest_displaysAllElements() {
        navigateToAnimalDetailThenLogout()
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("animalDetailContent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("animalNameAge").assertIsDisplayed()

        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("ownershipButton"))

        composeTestRule.onNodeWithTag("ownershipButton").assertIsDisplayed()
    }

    @Test
    fun animalDetail_guest_ownershipButton_showsLoginDialog() {
        navigateToAnimalDetailThenLogout()
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("ownershipButton"))

        composeTestRule.onNodeWithTag("ownershipButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("loginDialog").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("loginDialog").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.login_required)
        ).assertIsDisplayed()
    }

    @Test
    fun animalDetail_guest_loginDialog_navigatesToLogin() {
        navigateToAnimalDetailThenLogout()
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("ownershipButton"))

        composeTestRule.onNodeWithTag("ownershipButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("loginDialog").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("loginDialogOkButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithText("SeePaw Login").assertIsDisplayed()
    }

    /** -----------------------------------------
     *  AUTHENTICATED USER TESTS
     *  ----------------------------------------- */

    @Test
    fun animalDetail_authenticated_displaysAnimalInfo() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("animalDetailContent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("animalNameAge").assertIsDisplayed()

        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("ownershipButton"))

        composeTestRule.onNodeWithTag("ownershipButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ownershipButton").assertIsEnabled()
    }

    @Test
    fun animalDetail_authenticated_ownershipButton_navigatesToWizard() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("ownershipButton"))

        composeTestRule.onNodeWithTag("ownershipButton").performClick()

        // Wait for either ownership wizard OR ownership exists dialog
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithTag("ownershipRequestScreen").assertExists()
                true
            } catch (e: Throwable) {
                try {
                    composeTestRule.onNodeWithTag("ownershipExistsDialog").assertExists()
                    true
                } catch (e2: Throwable) {
                    false
                }
            }
        }

        // Verify one of the expected outcomes
        val wizardExists = try {
            composeTestRule.onNodeWithTag("ownershipRequestScreen").assertExists()
            true
        } catch (e: Throwable) {
            false
        }

        val dialogExists = try {
            composeTestRule.onNodeWithTag("ownershipExistsDialog").assertExists()
            true
        } catch (e: Throwable) {
            false
        }

        assert(wizardExists || dialogExists) {
            "Expected either ownershipRequestScreen or ownershipExistsDialog to be displayed"
        }
    }

    /** -----------------------------------------
     *  IMAGE CAROUSEL TESTS
     *  ----------------------------------------- */

    @Test
    fun animalDetail_displaysImageCarousel() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("animalImageCarousel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("animalImage_0").assertExists()
    }

    /** -----------------------------------------
     *  NAVIGATION TESTS
     *  ----------------------------------------- */

    @Test
    fun animalDetail_backButton_navigatesBack() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("backButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodes(hasTestTagStartingWith("animalCard_"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("catalogueScreen").assertExists()
    }
}