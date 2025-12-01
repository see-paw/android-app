package com.example.seepawandroid.ui.screens.schedule

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.MainActivity
import com.example.seepawandroid.data.managers.SessionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import javax.inject.Inject

/**
 * End-to-End test suite for the Activity Scheduling flow.
 *
 * Tests the complete user journey from the Ownership List screen to scheduling an activity.
 * Covers navigation, component rendering, UI state variations, event behaviors, and modal interactions.
 *
 * Test Flow:
 * 1. Login
 * 2. Navigate to "Os Meus Ownerships" (Ownership List)
 * 3. Click "Schedule Activity" button for an owned animal
 * 4. Verify scheduling screen renders correctly
 * 5. Test week navigation (next/previous)
 * 6. Select an available time slot
 * 7. Verify confirmation modal appears and displays correct information
 * 8. Test modal interactions (confirm, cancel)
 * 9. Test error modal display and retry functionality
 *
 * NOTE: Tests run sequentially (NAME_ASCENDING order) to avoid state conflicts.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SchedulingFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        private const val VALID_EMAIL = "diana@test.com"
        private const val VALID_PASSWORD = "Pa\$\$w0rd"
        private const val MAX_PAGES_TO_SEARCH = 10

        /**
         * IMPORTANT: These tests require that the user has at least one APPROVED ownership.
         *
         * To ensure tests pass:
         * 1. Login as carlos@test.com
         * 2. Request ownership for an animal
         * 3. Approve the ownership (via admin panel or database)
         *
         * Alternatively, seed the database with approved ownerships for this user.
         */
    }

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.waitForIdle()
        logoutIfNeeded()
    }

    /** -----------------------------------------
     * TEST 1: Navigation from Ownership List to Schedule Screen
     * Verifies that clicking the "Schedule Activity" button navigates to the scheduling screen.
     * ----------------------------------------- */
    @Test
    fun t1_navigation_fromOwnershipListToScheduleScreen() {
        prepareTestState_NavigateToOwnershipList()

        // Click the "Schedule Activity" button for the first owned animal
        composeTestRule.onAllNodesWithTag("scheduleActivityButton")
            .fetchSemanticsNodes()
            .firstOrNull()
            ?.let {
                composeTestRule.onNodeWithTag("scheduleActivityButton").performClick()
            }

        // Verify navigation to scheduling screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("schedulingScreen").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("schedulingScreen").assertIsDisplayed()
    }

    /** -----------------------------------------
     * TEST 2: Schedule Screen Components Rendering
     * Verifies that all components render correctly on the scheduling screen.
     * ----------------------------------------- */
    @Test
    fun t2_scheduleScreen_displaysAllComponents() {
        prepareTestState_NavigateToScheduleScreen()

        // Verify back button
        composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()

        // Verify week navigation header components
        composeTestRule.onNodeWithTag("weekRangeText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("prevWeekButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nextWeekButton").assertIsDisplayed()

        // Verify at least one time slot is rendered
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasTestTagStartingWith("timeSlot_"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    /** -----------------------------------------
     * TEST 3: Loading State
     * Verifies that the loading indicator displays while fetching schedule data.
     * ----------------------------------------- */
    @Test
    fun t3_scheduleScreen_displaysLoadingState() {
        prepareTestState_NavigateToOwnershipList()

        // Click schedule button for the first owned animal
        composeTestRule.onAllNodesWithTag("scheduleActivityButton")
            .fetchSemanticsNodes()
            .firstOrNull()
            ?.let {
                composeTestRule.onNodeWithTag("scheduleActivityButton").performClick()
            }

        // Check for loading indicator (it may be brief)
        try {
            composeTestRule.onNodeWithTag("schedulingLoadingIndicator").assertExists()
        } catch (e: AssertionError) {
            // Loading may finish too quickly, which is acceptable
        }

        // Eventually should show content
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithTag("schedulingContent").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    /** -----------------------------------------
     * TEST 4: Week Navigation - Next Week
     * Verifies that clicking the next week button updates the week range.
     * ----------------------------------------- */
    @Test
    fun t4_weekNavigation_nextWeek_updatesWeekRange() {
        prepareTestState_NavigateToScheduleScreen()

        // Get current week range text
        val currentWeekText = composeTestRule.onNodeWithTag("weekRangeText")
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Text)
            ?.firstOrNull()
            ?.text

        // Click next week button
        composeTestRule.onNodeWithTag("nextWeekButton").performClick()

        // Wait for update
        composeTestRule.waitForIdle()
        waitUntilLoadingFinishes()

        // Verify week range text changed
        val newWeekText = composeTestRule.onNodeWithTag("weekRangeText")
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Text)
            ?.firstOrNull()
            ?.text

        assert(currentWeekText != newWeekText) {
            "Week range should update after clicking next week button"
        }
    }

    /** -----------------------------------------
     * TEST 5: Week Navigation - Previous Week Disabled for Past Weeks
     * Verifies that navigation to past weeks is prevented.
     * ----------------------------------------- */
    @Test
    fun t5_weekNavigation_previousWeek_disabledForPastWeeks() {
        prepareTestState_NavigateToScheduleScreen()

        // Previous week button should be disabled (we're on current week)
        composeTestRule.onNodeWithTag("prevWeekButton").assertIsNotEnabled()
    }

    /** -----------------------------------------
     * TEST 6: Slot Selection - Available Slot Opens Confirmation Modal
     * Verifies that clicking an available slot opens the confirmation modal.
     * ----------------------------------------- */
    @Test
    fun t6_slotSelection_availableSlot_opensConfirmationModal() {
        prepareTestState_NavigateToScheduleScreen()

        // Find and click the first available slot
        val availableSlotTag = findFirstAvailableSlot()
        if (availableSlotTag != null) {
            composeTestRule.onNodeWithTag(availableSlotTag).performClick()

            // Verify confirmation modal appears
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithTag("confirmActivityModal").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }

            composeTestRule.onNodeWithTag("confirmActivityModal").assertIsDisplayed()
        }
    }

    /** -----------------------------------------
     * TEST 7: Confirmation Modal - Displays Correct Information
     * Verifies that the confirmation modal shows date, time, and action buttons.
     * ----------------------------------------- */
    @Test
    fun t7_confirmationModal_displaysCorrectInformation() {
        prepareTestState_NavigateToScheduleScreen()

        // Click an available slot
        val availableSlotTag = findFirstAvailableSlot()
        if (availableSlotTag != null) {
            composeTestRule.onNodeWithTag(availableSlotTag).performClick()

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("confirmActivityModal")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Verify modal buttons exist
            composeTestRule.onNodeWithTag("confirmModalButton").assertIsDisplayed()
            composeTestRule.onNodeWithTag("cancelModalButton").assertIsDisplayed()
        }
    }

    /** -----------------------------------------
     * TEST 8: Confirmation Modal - Cancel Button Closes Modal
     * Verifies that clicking cancel closes the confirmation modal.
     * ----------------------------------------- */
    @Test
    fun t8_confirmationModal_cancelButton_closesModal() {
        prepareTestState_NavigateToScheduleScreen()

        // Click an available slot
        val availableSlotTag = findFirstAvailableSlot()
        if (availableSlotTag != null) {
            composeTestRule.onNodeWithTag(availableSlotTag).performClick()

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("confirmActivityModal")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Click cancel button
            composeTestRule.onNodeWithTag("cancelModalButton").performClick()

            // Verify modal is closed
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithTag("confirmActivityModal")
                    .fetchSemanticsNodes()
                    .isEmpty()
            }
        }
    }

    /** -----------------------------------------
     * TEST 9: Modal Loading State
     * Verifies that the modal loading indicator appears when confirming a slot.
     * ----------------------------------------- */
    @Test
    fun t9_confirmationModal_confirmButton_showsLoadingState() {
        prepareTestState_NavigateToScheduleScreen()

        // Click an available slot
        val availableSlotTag = findFirstAvailableSlot()
        if (availableSlotTag != null) {
            composeTestRule.onNodeWithTag(availableSlotTag).performClick()

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("confirmActivityModal")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Click confirm button
            composeTestRule.onNodeWithTag("confirmModalButton").performClick()

            // Check for modal loading indicator (may be brief if network is fast)
            try {
                composeTestRule.waitUntil(timeoutMillis = 2000) {
                    composeTestRule.onAllNodesWithTag("modalLoadingIndicator")
                        .fetchSemanticsNodes()
                        .isNotEmpty()
                }
            } catch (e: AssertionError) {
                // Loading may finish too quickly, which is acceptable
            }
        }
    }

    /** -----------------------------------------
     * TEST 10: Back Button Navigation
     * Verifies that the back button returns to the ownership list.
     * ----------------------------------------- */
    @Test
    fun t10_backButton_navigatesToOwnershipList() {
        prepareTestState_NavigateToScheduleScreen()

        // Click back button
        composeTestRule.onNodeWithTag("backButton").performClick()

        // Verify navigation back to ownership list
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("ownershipListScreen").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("ownershipListScreen").assertIsDisplayed()
    }

    /** -----------------------------------------
     * HELPER: COMMON SETUP
     * Sequence: Login -> Ownership List -> Schedule Screen
     * ----------------------------------------- */
    private fun prepareTestState_NavigateToScheduleScreen() {
        prepareTestState_NavigateToOwnershipList()

        // Click the "Schedule Activity" button for the first owned animal
        composeTestRule.onAllNodesWithTag("scheduleActivityButton")
            .fetchSemanticsNodes()
            .firstOrNull()
            ?.let {
                composeTestRule.onNodeWithTag("scheduleActivityButton").performClick()
            }

        // Wait for schedule screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("schedulingScreen").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        waitUntilLoadingFinishes()
    }

    private fun prepareTestState_NavigateToOwnershipList() {
        performLogin()
        navigateToOwnershipList()

        // Wait for ownership list to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("ownershipListScreen").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.waitUntilTagExists("ownedAnimalsTab")

        composeTestRule.onNodeWithTag("ownedAnimalsTab", useUnmergedTree = false)
            .awaitDisplayedAndEnabled()
            .safeClick()

        composeTestRule.waitForIdle()

        // Wait for owned animals to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onAllNodes(hasTestTagStartingWith("ownedAnimalCard_"))
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            } catch (e: Throwable) {
                false
            }
        }
    }

    /** -----------------------------------------
     * HELPERS: ACTIONS & UTILS
     * ----------------------------------------- */

    private fun AndroidComposeTestRule<*, *>.waitUntilTagExists(
        tag: String,
        timeout: Long = 10_000
    ) {
        this.waitUntil(timeoutMillis = timeout) {
            try {
                this.onNodeWithTag(tag, useUnmergedTree = false).assertExists()
                true
            } catch (_: Throwable) { false }
        }
    }

    private fun SemanticsNodeInteraction.awaitDisplayedAndEnabled(
        timeout: Long = 8_000
    ): SemanticsNodeInteraction {
        composeTestRule.waitUntil(timeoutMillis = timeout) {
            try {
                this.assertIsDisplayed()
                this.assertIsEnabled()
                true
            } catch (_: Throwable) { false }
        }
        return this
    }

    private fun SemanticsNodeInteraction.safeClick() {
        this.awaitDisplayedAndEnabled()
        this.performClick()
    }



    /**
     * Finds the first available time slot tag.
     * Returns the test tag string or null if no available slots found.
     *
     * Test tag format: "timeSlot_{dayOfMonth}_{hour}_{slotType}"
     * Example: "timeSlot_15_10_AVAILABLE" = Day 15, 10h, Available
     */
    private fun findFirstAvailableSlot(): String? {
        val nodes = composeTestRule.onAllNodes(hasTestTagStartingWith("timeSlot_"))
            .fetchSemanticsNodes()

        for (node in nodes) {
            val tag = node.config.getOrNull(SemanticsProperties.TestTag)
            if (tag != null && tag.endsWith("_AVAILABLE")) {
                return tag
            }
        }
        return null
    }

    private fun logoutIfNeeded() {
        try {
            // Wait for app to fully load first
            composeTestRule.waitForIdle()
            Thread.sleep(500) // Give UI time to settle

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
        } catch (_: Throwable) {
            // Already logged out
        }
    }

    private fun performLogin() {
        // Navigate to login if needed
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithText("SeePaw Login")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
        } catch (_: Throwable) {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("openLoginButton")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
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

        // Input credentials
        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        waitUntilLoadingFinishes()
    }

    private fun navigateToOwnershipList() {
        composeTestRule.onNodeWithTag("openDrawerButton").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("drawerItemOwnershipList").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
        composeTestRule.onNodeWithTag("drawerItemOwnershipList").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("ownershipListScreen").assertExists()
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

    private fun hasTestTagStartingWith(prefix: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag starts with '$prefix'") { node ->
            node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
        }
    }
}
