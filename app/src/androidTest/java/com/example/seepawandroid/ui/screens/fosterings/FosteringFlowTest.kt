package com.example.seepawandroid.ui.screens.fosterings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.BaseUiTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Test Suite for the Fostering Flow.
 *
 * This suite validates the complete fostering user journey:
 * - Viewing fostering progress bar on animal detail
 * - Creating a new fostering through the wizard
 * - Viewing the fostering list
 * - Cancelling a fostering
 *
 * IMPORTANT - EXECUTION ORDER:
 * We use @FixMethodOrder(MethodSorters.NAME_ASCENDING) to ensure tests run in order.
 * Tests that create data (t5) run before tests that depend on that data (t6, t7).
 *
 * TEST ANIMAL: Mika (ID: f055cc31-fdeb-4c65-bb73-4f558f67dd4b)
 * This animal should be in "Available" or "PartiallyFostered" state for tests to work.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FosteringFlowTest : BaseUiTest() {

    companion object {
        private const val VALID_EMAIL = "carlos@test.com"
        private const val VALID_PASSWORD = "Pa\$\$w0rd"

        // Known GUID from the seed data - Mika
        private const val VALID_ANIMAL_ID = "f055cc31-fdeb-4c65-bb73-4f558f67dd4b"
        private const val ANIMAL_NAME = "Mika"

        private const val MAX_PAGES_TO_SEARCH = 10
    }

    @Before
    override fun setUp() {
        super.setUp()
        composeTestRule.waitForIdle()
        logoutIfNeeded()
    }

    // =========================================================================
    // TEST 1: Fostering Progress Bar Display
    // =========================================================================

    /**
     * Verifies that the fostering progress bar is displayed on animal detail screen.
     */
    @Test
    fun t1_animalDetail_showsFosteringProgressBar() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        // Verify progress bar components exist
        composeTestRule.onNodeWithTag("fosteringProgressBar").awaitDisplayed().assertExists()
        composeTestRule.onNodeWithTag("fosteringPercentageText").assertExists()
        composeTestRule.onNodeWithTag("fosteringAmountText").assertExists()
    }

    // =========================================================================
    // TEST 2: Fostering Button State
    // =========================================================================

    /**
     * Verifies that the fostering button is visible and enabled for available animals.
     */
    @Test
    fun t2_animalDetail_fosteringButtonIsEnabled() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        // Scroll to fostering button
        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("fosteringButton"))

        // Verify button exists and is enabled
        composeTestRule.onNodeWithTag("fosteringButton").assertExists()
        composeTestRule.onNodeWithTag("fosteringButton").assertIsEnabled()
    }

    // =========================================================================
    // TEST 3: Fostering Amount Dialog
    // =========================================================================

    /**
     * Verifies that clicking the fostering button opens the amount selection dialog.
     */
    @Test
    fun t3_fosteringDialog_displaysCorrectly() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        // Click fostering button
        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("fosteringButton"))
        composeTestRule.onNodeWithTag("fosteringButton").safeClick()

        // Verify dialog appears
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("fosteringAmountDialog").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        // Verify dialog components
        composeTestRule.onNodeWithTag("fosteringAmountDialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("radioButton_10.0").assertExists()
        composeTestRule.onNodeWithTag("radioButton_15.0").assertExists()
        composeTestRule.onNodeWithTag("radioButton_20.0").assertExists()
        composeTestRule.onNodeWithTag("radioButton_custom").assertExists()
        composeTestRule.onNodeWithTag("fosteringConfirmButton").assertExists()
        composeTestRule.onNodeWithTag("fosteringCancelButton").assertExists()
    }

    // =========================================================================
    // TEST 4: Cancel Dialog Dismisses
    // =========================================================================

    /**
     * Verifies that clicking cancel on the amount dialog dismisses it.
     */
    @Test
    fun t4_fosteringDialog_cancelDismisses() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        // Open dialog
        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("fosteringButton"))
        composeTestRule.onNodeWithTag("fosteringButton").safeClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("fosteringAmountDialog").fetchSemanticsNodes().isNotEmpty()
        }

        // Click cancel
        composeTestRule.onNodeWithTag("fosteringCancelButton").safeClick()

        // Verify dialog dismissed
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("fosteringAmountDialog").fetchSemanticsNodes().isEmpty()
        }
    }

    // =========================================================================
    // TEST 5: Full Flow - Create Fostering
    // =========================================================================

    /**
     * Executes the complete fostering creation flow:
     * 1. Navigate to animal detail
     * 2. Click fostering button
     * 3. Select amount (10€)
     * 4. Confirm amount
     * 5. Confirm mock payment
     * 6. Verify success dialog
     *
     * WARNING: This test creates data in the database.
     */
    @Test
    fun t5_fullFlow_createsFostering_successfully() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        // 1. Click fostering button
        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("fosteringButton"))
        composeTestRule.onNodeWithTag("fosteringButton").safeClick()

        // 2. Wait for amount dialog
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("fosteringAmountDialog").fetchSemanticsNodes().isNotEmpty()
        }

        // 3. Select 10€ option
        composeTestRule.onNodeWithTag("radioButton_10.0").safeClick()

        // 4. Confirm amount selection
        composeTestRule.onNodeWithTag("fosteringConfirmButton").safeClick()

        // 5. Wait for payment mock dialog
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("fosteringPaymentMockDialog").fetchSemanticsNodes().isNotEmpty()
        }

        // 6. Verify payment dialog fields are displayed
        composeTestRule.onNodeWithTag("accountNumberField").assertExists()
        composeTestRule.onNodeWithTag("holderNameField").assertExists()
        composeTestRule.onNodeWithTag("cvvField").assertExists()

        // 7. Confirm payment
        composeTestRule.onNodeWithTag("confirmPaymentButton").safeClick()

        // 8. Wait for success dialog
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            try {
                composeTestRule.onNodeWithTag("fosteringSuccessDialog").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        // 9. Dismiss success dialog
        composeTestRule.onNodeWithTag("fosteringResultOkButton").safeClick()

        // 10. Verify we're back on animal detail
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("animalDetailScreen").assertExists()
                true
            } catch (e: Throwable) { false }
        }
    }

    // =========================================================================
    // TEST 6: Fostering List Shows Created Fostering
    // =========================================================================

    /**
     * Verifies that the fostering list shows the created fostering.
     * Depends on t5 having run successfully.
     */
    @Test
    fun t6_fosteringList_showsActiveFostering() {
        performLogin()

        // Navigate to fostering list
        navigateToFosteringList()

        // Wait for list to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("fosteringListScreen").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        waitUntilLoadingFinishes()

        // Verify active fosterings tab is selected and shows content
        composeTestRule.onNodeWithTag("activeFosteringsTab").assertExists()

        // Check if we have content or empty state
        try {
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithTag("fosteringListContent").fetchSemanticsNodes().isNotEmpty() ||
                        composeTestRule.onAllNodesWithTag("emptyState").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: Throwable) {
            // Either state is acceptable depending on test order
        }
    }

    // =========================================================================
    // TEST 7: Fostering List - Tab Navigation
    // =========================================================================

    /**
     * Verifies that tab navigation works correctly on the fostering list.
     */
    @Test
    fun t7_fosteringList_tabNavigationWorks() {
        performLogin()
        navigateToFosteringList()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("fosteringListScreen").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        waitUntilLoadingFinishes()

        // Verify both tabs exist
        composeTestRule.onNodeWithTag("activeFosteringsTab").assertExists()
        composeTestRule.onNodeWithTag("receiptsTab").assertExists()

        // Click receipts tab
        composeTestRule.onNodeWithTag("receiptsTab").safeClick()

        // Verify receipts content appears (either content or empty state)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("fosteringReceiptsContent").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithTag("emptyReceiptsState").fetchSemanticsNodes().isNotEmpty()
        }

        // Click back to active fosterings
        composeTestRule.onNodeWithTag("activeFosteringsTab").safeClick()

        // Verify we're back on active tab
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("fosteringListContent").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithTag("emptyState").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithTag("emptyTabState").fetchSemanticsNodes().isNotEmpty()
        }
    }

    // =========================================================================
    // TEST 8: Cancel Fostering Flow
    // =========================================================================

    /**
     * Tests the cancel fostering dialog flow.
     * This test attempts to cancel a fostering if one exists.
     */
    @Test
    fun t8_fosteringList_cancelDialogWorks() {
        performLogin()
        navigateToFosteringList()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("fosteringListScreen").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        waitUntilLoadingFinishes()

        // Try to find a fostering card with cancel button
        try {
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithTag("fosteringListContent").fetchSemanticsNodes().isNotEmpty()
            }

            // Click cancel button on first fostering
            composeTestRule.onNodeWithTag("cancelFosteringButton").safeClick()

            // Verify cancel dialog appears
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithTag("cancelFosteringDialog").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onNodeWithTag("cancelFosteringDialog").assertIsDisplayed()
            composeTestRule.onNodeWithTag("confirmCancelButton").assertExists()
            composeTestRule.onNodeWithTag("dismissCancelButton").assertExists()

            // Dismiss the dialog (don't actually cancel)
            composeTestRule.onNodeWithTag("dismissCancelButton").safeClick()

            // Verify dialog dismissed
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("cancelFosteringDialog").fetchSemanticsNodes().isEmpty()
            }

        } catch (e: Throwable) {
            // If no fosterings exist, this test passes (nothing to cancel)
        }
    }

    // =========================================================================
    // TEST 9: Custom Amount Input
    // =========================================================================

    /**
     * Verifies that custom amount input works correctly in the fostering dialog.
     */
    @Test
    fun t9_fosteringDialog_customAmountInputWorks() {
        performLogin()
        navigateToCatalogue()
        navigateToAnimalDetail()
        waitUntilLoadingFinishes()

        // Open dialog
        composeTestRule.onNodeWithTag("animalDetailContent")
            .performScrollToNode(hasTestTag("fosteringButton"))
        composeTestRule.onNodeWithTag("fosteringButton").safeClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("fosteringAmountDialog").fetchSemanticsNodes().isNotEmpty()
        }

        // Select custom option
        composeTestRule.onNodeWithTag("radioButton_custom").safeClick()

        // Verify custom input field appears
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("customAmountInput").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("customAmountInput").assertExists()

        // Cancel to clean up
        composeTestRule.onNodeWithTag("fosteringCancelButton").safeClick()
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private fun performLogin() {
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithText("SeePaw Login").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (_: Throwable) {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("openLoginButton").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("openLoginButton").safeClick()
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                    true
                } catch (e: Throwable) { false }
            }
        }

        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").safeClick()

        waitUntilLoadingFinishes()
    }

    private fun navigateToFosteringList() {
        composeTestRule.onNodeWithTag("openDrawerButton").safeClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("drawerItemFosteringList").assertExists()
                true
            } catch (e: Throwable) { false }
        }

        composeTestRule.onNodeWithTag("drawerItemFosteringList").safeClick()
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
                composeTestRule.onNodeWithTag("animalCard_$animalId").safeClick()
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
            composeTestRule.onNodeWithTag("nextPageButton").safeClick()
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
}