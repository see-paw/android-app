package com.example.seepawandroid.ui.screens.ownerships

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.BaseUiTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Test Suite for the Ownership Request Flow.
 *
 * This suite is divided into granular tests to validate each UI step individually,
 * culminating in a full End-to-End test that persists data.
 *
 * IMPORTANT - EXECUTION ORDER:
 * We use @FixMethodOrder(MethodSorters.NAME_ASCENDING) to ensure that tests
 * which only "read" the UI (t1-t4) run BEFORE the test that "writes" to the DB (t5).
 *
 * If t5 runs first, the animal "Thor" will have a "Pending" request, causing
 * the "Ownership Button" to be replaced by status text, failing subsequent tests.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class OwnershipRequestFlowTest : BaseUiTest() {

    companion object {
        private const val VALID_EMAIL = "carlos@test.com"
        private const val VALID_PASSWORD = "Pa\$\$w0rd"

        // Known GUID from the seed data
        private const val VALID_ANIMAL_ID = "f055cc31-fdeb-4c65-bb73-4f558f67dd2b"
        private const val ANIMAL_NAME = "Lunica"

        private const val MAX_PAGES_TO_SEARCH = 10
    }

    @Before
    override fun setUp() {
        super.setUp()
        composeTestRule.waitForIdle()
        logoutIfNeeded()
    }

    /** -----------------------------------------
     * TEST 1: Initial Terms State
     * Verifies that the 'Accept' button starts disabled to enforce reading.
     * ----------------------------------------- */
    @Test
    fun t1_terms_acceptButton_startsDisabled() {
        prepareTestState_NavigateToWizard()

        composeTestRule.onNodeWithTag("termsContent").awaitDisplayedAndEnabled().assertExists()

        // Ensure user cannot proceed without scrolling
        composeTestRule.onNodeWithTag("acceptTermsButton").assertIsNotEnabled()
    }

    /** -----------------------------------------
     * TEST 2: Scroll Logic
     * Verifies that scrolling to the bottom of the terms enables the button.
     * ----------------------------------------- */
    @Test
    fun t2_terms_scroll_enablesButton() {
        prepareTestState_NavigateToWizard()

        // Action: Swipe up until the button becomes enabled
        swipeUpUntilEnabled("termsScrollArea", "acceptTermsButton")

        // Verification
        composeTestRule.onNodeWithTag("acceptTermsButton").assertIsEnabled()
    }

    /** -----------------------------------------
     * TEST 3: Navigation to Form
     * Verifies that accepting terms leads to the form screen
     * and checks that the Read-Only fields are displayed correctly.
     * ----------------------------------------- */
    @Test
    fun t3_form_displaysCorrectly_afterTermsAccepted() {
        prepareTestState_NavigateToWizard()

        // Pass Terms & Conditions
        swipeUpUntilEnabled("termsScrollArea", "acceptTermsButton")
        composeTestRule.onNodeWithTag("acceptTermsButton").performClick()

        // Wait for Form transition
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("formContent").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        // Verify form elements exist (we do not input text as they are Read-Only)
        //
        composeTestRule.onNodeWithTag("accountNumberInput").assertExists()
        composeTestRule.onNodeWithTag("holderNameInput").assertExists()
        composeTestRule.onNodeWithTag("cvvInput").assertExists()

        // Verify submit button is available
        composeTestRule.onNodeWithTag("submitRequestButton").assertIsEnabled()
    }

    /** -----------------------------------------
     * TEST 4: Back Button Navigation (UX)
     * Verifies that the 'Back' button on the form returns the user to the Terms screen.
     * ----------------------------------------- */
    @Test
    fun t4_form_backButton_returnsToTerms() {
        prepareTestState_NavigateToWizard()

        // Navigate to Form
        swipeUpUntilEnabled("termsScrollArea", "acceptTermsButton")
        composeTestRule.onNodeWithTag("acceptTermsButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("formContent").fetchSemanticsNodes().isNotEmpty()
        }

        // Click Back
        composeTestRule.onNodeWithTag("backButton").performClick()

        // Verify we returned to Terms
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("termsContent").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("termsContent").assertIsDisplayed()
    }

    /** -----------------------------------------
     * TEST 5: Full End-to-End Flow
     * Executes the entire process, submits the request, and verifies the "Pending" status.
     * WARNING: This test alters the database state for the animal.
     * ----------------------------------------- */
    @Test
    fun t5_fullFlow_submitsRequest_and_verifiesPendingStatus() {
        prepareTestState_NavigateToWizard()

        // 1. Accept Terms
        swipeUpUntilEnabled("termsScrollArea", "acceptTermsButton")
        composeTestRule.onNodeWithTag("acceptTermsButton").performClick()

        // 2. Submit Form
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("formContent").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("submitRequestButton").performClick()

        // 3. Validate Success Screen
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            try {
                composeTestRule.onNodeWithTag("successContent").assertExists()
                true
            } catch (e: Throwable) { false }
        }
        composeTestRule.onNodeWithTag("doneButton").performClick()

        // 4. Navigate to Ownership List
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("openDrawerButton").fetchSemanticsNodes().isNotEmpty()
        }
        navigateToOwnershipList()

        // 5. Verify "Pending" Status
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("ownershipListContent").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        // Verify Animal Name is present in list
        composeTestRule.onNodeWithText(ANIMAL_NAME).assertExists()

        // Verify Status Badge is displayed
        // Based on OwnershipListScreen.kt logic: testTag("statusBadge_${status.name}")
        composeTestRule.onNodeWithTag("statusBadge_Pending", useUnmergedTree = true).assertExists()
    }

    /** -----------------------------------------
     * HELPER: COMMON SETUP
     * Sequence: Login -> Catalogue -> Detail -> Click Ownership Button
     * ----------------------------------------- */
    private fun prepareTestState_NavigateToWizard() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        // Scroll to and click the ownership button
        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("ownershipButton"))

        composeTestRule.onNodeWithTag("ownershipButton").safeClick()

        // Wait for Wizard screen entry
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("ownershipRequestScreen").assertExists()
                true
            } catch (e: Throwable) {
                // If this fails, T5 might have run before T1-T4, causing the
                // "Already Exists" dialog to appear instead of the wizard.
                false
            }
        }
    }

    /** -----------------------------------------
     * HELPERS: ACTIONS & UTILS
     * ----------------------------------------- */

    /**
     * Repeatedly swipes up on a scrollable container until the target button becomes enabled.
     * Essential for validating "Read to Agree" behaviors.
     */
    private fun swipeUpUntilEnabled(scrollableTag: String, buttonTag: String, maxSwipes: Int = 15) {
        var swipes = 0
        while (swipes < maxSwipes) {
            try {
                composeTestRule.onNodeWithTag(buttonTag).assertIsEnabled()
                return // Button is enabled, we are done
            } catch (e: AssertionError) {
                // Button not enabled yet
            }

            composeTestRule.onNodeWithTag(scrollableTag).awaitDisplayed().performTouchInput {
                swipeUp(durationMillis = 200)
            }
            composeTestRule.waitForIdle()
            swipes++
        }
        throw AssertionError("Button '$buttonTag' did not enable after $maxSwipes swipes on '$scrollableTag'")
    }

    // -------------------------------------------------------------------------
    // HELPERS: NAVIGATION & AUTH (Matches AnimalDetailScreenTest patterns)
    // -------------------------------------------------------------------------

    private fun logoutIfNeeded() {
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                try {
                    composeTestRule.onNodeWithTag("logoutButton").assertExists()
                    true
                } catch (e: Throwable) { false }
            }
            composeTestRule.onNodeWithTag("logoutButton").performClick()
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithTag("openLoginButton").assertExists()
                    true
                } catch (e: Throwable) { false }
            }
        } catch (_: Throwable) { /* Already logged out */ }
    }

    private fun performLogin() {
        // Handle navigation to login screen if not already there
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithText("SeePaw Login").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (_: Throwable) {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("openLoginButton").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("openLoginButton").performClick()
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                    true
                } catch (e: Throwable) { false }
            }
        }

        // Input credentials
        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        waitUntilLoadingFinishes()
    }

    private fun navigateToCatalogue() {
        composeTestRule.onNodeWithTag("openDrawerButton").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithTag("drawerItemCatalogue").assertExists()
                true
            } catch (e: Throwable) { false }
        }
        composeTestRule.onNodeWithTag("drawerItemCatalogue").performClick()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasTestTagStartingWith("animalCard_")).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun navigateToOwnershipList() {
        composeTestRule.onNodeWithTag("openDrawerButton").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("drawerItemOwnershipList").assertExists()
                true
            } catch (e: Throwable) { false }
        }
        composeTestRule.onNodeWithTag("drawerItemOwnershipList").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("ownershipListScreen").assertExists()
                true
            } catch (e: Throwable) { false }
        }
    }

    private fun findAndClickAnimal(animalId: String) {
        var pagesSearched = 0
        while (pagesSearched < MAX_PAGES_TO_SEARCH) {
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodes(hasTestTagStartingWith("animalCard_")).fetchSemanticsNodes().isNotEmpty()
            }
            try {
                composeTestRule.onNodeWithTag("animalGrid")
                    .performScrollToNode(hasTestTag("animalCard_$animalId"))
                composeTestRule.onNodeWithTag("animalCard_$animalId").performClick()
                return
            } catch (_: Throwable) { /* Animal not on this page */ }

            if (!goToNextPage()) {
                throw AssertionError("Animal with ID $animalId not found after searching $pagesSearched pages.")
            }
            pagesSearched++
        }
        throw AssertionError("Animal with ID $animalId not found after searching all pages.")
    }

    private fun goToNextPage(): Boolean {
        return try {
            composeTestRule.onNodeWithTag("nextPageButton").assertExists()
            composeTestRule.onNodeWithTag("nextPageButton").assertIsEnabled()
            composeTestRule.onNodeWithTag("nextPageButton").performClick()
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodes(hasTestTagStartingWith("animalCard_")).fetchSemanticsNodes().isNotEmpty()
            }
            true
        } catch (_: Throwable) { false }
    }

    private fun navigateToAnimalDetail() {
        findAndClickAnimal(VALID_ANIMAL_ID)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("animalDetailScreen").assertExists()
                true
            } catch (e: Throwable) { false }
        }
    }

    private fun hasTestTagStartingWith(prefix: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag starts with '$prefix'") { node ->
            node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
        }
    }
}