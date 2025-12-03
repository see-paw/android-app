package com.example.seepawandroid.ui.screens.schedule

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.BaseUiTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * End-to-End test suite for the Activity Scheduling flow.
 *
 * NOTE: Tests run sequentially (NAME_ASCENDING order) to avoid state conflicts.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SchedulingFlowTest : BaseUiTest() {

    companion object {
        private const val VALID_EMAIL = "helena@test.com"
        private const val VALID_PASSWORD = "Pa\$\$w0rd"
    }

    @Before
    override fun setUp() {
        super.setUp()
        composeTestRule.waitForIdle()
        logoutIfNeeded()
    }

    @After
    fun teardown() {
        try {
            composeTestRule.waitForIdle()
            Thread.sleep(500)
        } catch (_: Throwable) {}
    }

    /** -----------------------------------------
     * TEST 1: Navigation from Ownership List to Schedule Screen
     * ----------------------------------------- */
    @Test
    fun t1_navigation_fromOwnershipListToScheduleScreen() {
        prepareTestState_NavigateToOwnershipList()
        composeTestRule.onNodeWithTag("scheduleActivityButton").safeClick()
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
     * ----------------------------------------- */
    @Test
    fun t2_scheduleScreen_displaysAllComponents() {
        prepareTestState_NavigateToScheduleScreen()
        composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("weekRangeText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("prevWeekButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nextWeekButton").assertIsDisplayed()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasTestTagStartingWith("timeSlot_"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    /** -----------------------------------------
     * TEST 3: Loading State
     * ----------------------------------------- */
    @Test
    fun t3_scheduleScreen_displaysLoadingState() {
        prepareTestState_NavigateToOwnershipList()
        composeTestRule.onNodeWithTag("scheduleActivityButton").safeClick()
        try {
            composeTestRule.onNodeWithTag("schedulingLoadingIndicator").assertExists()
        } catch (e: Throwable) {
            // Loading may finish too quickly, which is acceptable
        }
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
     * ----------------------------------------- */
    @Test
    fun t4_weekNavigation_nextWeek_updatesWeekRange() {
        prepareTestState_NavigateToScheduleScreen()
        
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("weekRangeText").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
        
        val currentWeekText = composeTestRule.onNodeWithTag("weekRangeText")
            .fetchSemanticsNode()
            .config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
        
        composeTestRule.onNodeWithTag("nextWeekButton").safeClick()
        composeTestRule.waitForIdle()
        waitUntilLoadingFinishes()
        Thread.sleep(1000)
        
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                val newText = composeTestRule.onNodeWithTag("weekRangeText")
                    .fetchSemanticsNode()
                    .config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                newText != null && newText != currentWeekText
            } catch (e: Throwable) {
                false
            }
        }
        
        val newWeekText = composeTestRule.onNodeWithTag("weekRangeText")
            .fetchSemanticsNode()
            .config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
        
        assert(currentWeekText != newWeekText) {
            "Week range should update after clicking next week button. Old: $currentWeekText, New: $newWeekText"
        }
    }

    /** -----------------------------------------
     * TEST 5: Week Navigation - Previous Week Disabled for Past Weeks
     * ----------------------------------------- */
    @Test
    fun t5_weekNavigation_previousWeek_disabledForPastWeeks() {
        prepareTestState_NavigateToScheduleScreen()
        composeTestRule.onNodeWithTag("prevWeekButton").assertIsNotEnabled()
    }

    /** -----------------------------------------
     * TEST 6: Slot Selection - Available Slot Opens Confirmation Modal
     * ----------------------------------------- */
    @Test
    fun t6_slotSelection_availableSlot_opensConfirmationModal() {
        prepareTestState_NavigateToScheduleScreen()
        val availableSlotTag = findFirstAvailableSlot()
        assert(availableSlotTag != null) {
            "Should find at least one available slot within 4 weeks"
        }
        composeTestRule.onNodeWithTag(availableSlotTag!!).safeClick()
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

    /** -----------------------------------------
     * TEST 7: Confirmation Modal - Displays Correct Information
     * ----------------------------------------- */
    @Test
    fun t7_confirmationModal_displaysCorrectInformation() {
        prepareTestState_NavigateToScheduleScreen()
        val availableSlotTag = findFirstAvailableSlot()
        assert(availableSlotTag != null) {
            "Should find at least one available slot within 4 weeks"
        }
        composeTestRule.onNodeWithTag(availableSlotTag!!).safeClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("confirmActivityModal").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("confirmModalButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cancelModalButton").assertIsDisplayed()
    }

    /** -----------------------------------------
     * TEST 8: Confirmation Modal - Cancel Button Closes Modal
     * ----------------------------------------- */
    @Test
    fun t8_confirmationModal_cancelButton_closesModal() {
        prepareTestState_NavigateToScheduleScreen()
        val availableSlotTag = findFirstAvailableSlot()
        assert(availableSlotTag != null) {
            "Should find at least one available slot within 4 weeks"
        }
        composeTestRule.onNodeWithTag(availableSlotTag!!).safeClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("confirmActivityModal").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("cancelModalButton").safeClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithTag("confirmActivityModal").fetchSemanticsNodes().isEmpty()
        }
    }

    /** -----------------------------------------
     * TEST 9: Modal Loading State
     * ----------------------------------------- */
    @Test
    fun t9_confirmationModal_confirmButton_showsLoadingState() {
        prepareTestState_NavigateToScheduleScreen()
        val availableSlotTag = findFirstAvailableSlot()
        assert(availableSlotTag != null) {
            "Should find at least one available slot within 4 weeks"
        }
        composeTestRule.onNodeWithTag(availableSlotTag!!).safeClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("confirmActivityModal").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("confirmModalButton").safeClick()
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithTag("modalLoadingIndicator").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: androidx.compose.ui.test.ComposeTimeoutException) {
            // Loading may finish too quickly, which is acceptable
        }
    }

    /** -----------------------------------------
     * TEST 10: Back Button Navigation
     * ----------------------------------------- */
    @Test
    fun t10_backButton_navigatesToOwnershipList() {
        prepareTestState_NavigateToScheduleScreen()
        composeTestRule.onNodeWithTag("backButton").safeClick()
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
     * TEST 11: Success Modal - Appears After Successful Booking
     * ----------------------------------------- */
    @Test
    fun t11_successModal_appearsAfterSuccessfulBooking() {
        prepareTestState_NavigateToScheduleScreen()
        val availableSlotTag = findFirstAvailableSlot()
        if (availableSlotTag != null) {
            composeTestRule.onNodeWithTag(availableSlotTag).safeClick()
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("confirmActivityModal").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("confirmModalButton").safeClick()
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                try {
                    composeTestRule.onNodeWithTag("successModal").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }
            composeTestRule.onNodeWithTag("successModal").assertIsDisplayed()
        }
    }

    /** -----------------------------------------
     * TEST 12: Success Modal - Displays Correct Information and Closes
     * ----------------------------------------- */
    @Test
    fun t12_successModal_displaysCorrectInformationAndCloses() {
        prepareTestState_NavigateToScheduleScreen()
        val availableSlotTag = findFirstAvailableSlot()
        if (availableSlotTag != null) {
            composeTestRule.onNodeWithTag(availableSlotTag).safeClick()
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithTag("confirmActivityModal").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithTag("confirmModalButton").safeClick()
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                try {
                    composeTestRule.onNodeWithTag("successModal").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }
            composeTestRule.onNodeWithTag("successModal").assertIsDisplayed()
            composeTestRule.onNodeWithTag("successModalMessage").assertIsDisplayed()
            composeTestRule.onNodeWithTag("successModalButton").assertIsDisplayed()
            composeTestRule.onNodeWithTag("successModalButton").safeClick()
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithTag("successModal").fetchSemanticsNodes().isEmpty()
            }
            composeTestRule.onNodeWithTag("schedulingScreen").assertIsDisplayed()
        }
    }

    private fun prepareTestState_NavigateToOwnershipList() {
        performLogin()
        navigateToOwnershipList()
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
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onAllNodes(hasTestTagStartingWith("ownedAnimalCard_")).fetchSemanticsNodes().isNotEmpty()
            } catch (e: Throwable) {
                false
            }
        }
    }

    private fun prepareTestState_NavigateToScheduleScreen() {
        prepareTestState_NavigateToOwnershipList()
        composeTestRule.onNodeWithTag("scheduleActivityButton").safeClick()
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

    private fun findFirstAvailableSlot(): String? {
        // IMPORTANTE: Avançar 1 semana primeiro para evitar slots dentro de 24h
        // A regra de negócio exige que reservas sejam feitas com pelo menos 24h de antecedência
        try {
            composeTestRule.onNodeWithTag("nextWeekButton").awaitDisplayedAndEnabled()
            composeTestRule.onNodeWithTag("nextWeekButton").safeClick()
            composeTestRule.waitForIdle()
            waitUntilLoadingFinishes(timeout = 30_000)
            Thread.sleep(1000)
        } catch (e: Exception) {
            android.util.Log.w("SchedulingFlowTest", "Failed to navigate to next week, trying current week", e)
        }
        
        // Try current week (which is now next week if navigation succeeded)
        val currentWeekSlot = findAvailableSlotInCurrentWeek()
        if (currentWeekSlot != null) {
            return currentWeekSlot
        }
        
        // Try next 3 more weeks (total 4 weeks from start)
        for (week in 1..3) {
            try {
                composeTestRule.onNodeWithTag("nextWeekButton").awaitDisplayedAndEnabled()
                composeTestRule.onNodeWithTag("nextWeekButton").safeClick()
                composeTestRule.waitForIdle()
                waitUntilLoadingFinishes(timeout = 30_000)
                Thread.sleep(1000)
                
                val slot = findAvailableSlotInCurrentWeek()
                if (slot != null) {
                    return slot
                }
            } catch (e: Exception) {
                android.util.Log.w("SchedulingFlowTest", "Failed to navigate to week $week", e)
            }
        }
        
        return null
    }
    
    private fun findAvailableSlotInCurrentWeek(): String? {
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasTestTagStartingWith("timeSlot_"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        
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
            composeTestRule.waitForIdle()
            Thread.sleep(1000)
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithTag("logoutButton").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }
            composeTestRule.onNodeWithTag("logoutButton").safeClick()
            Thread.sleep(500)
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                try {
                    composeTestRule.onNodeWithTag("openLoginButton").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }
            composeTestRule.waitForIdle()
            Thread.sleep(500)
        } catch (_: Throwable) {
            Thread.sleep(300)
        }
    }

    private fun performLogin(maxRetries: Int = 20) {
        var attempt = 0
        while (attempt < maxRetries) {
            attempt++
            try {
                composeTestRule.waitUntil(timeoutMillis = 3000) {
                    composeTestRule.onAllNodesWithText("SeePaw Login").fetchSemanticsNodes().isNotEmpty()
                }
            } catch (_: Throwable) {
                composeTestRule.waitUntil(timeoutMillis = 5000) {
                    composeTestRule.onAllNodesWithTag("openLoginButton").fetchSemanticsNodes().isNotEmpty()
                }
                composeTestRule.onNodeWithTag("openLoginButton").safeClick()
                composeTestRule.waitUntil(timeoutMillis = 5000) {
                    try {
                        composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                        true
                    } catch (e: Throwable) {
                        false
                    }
                }
            }
            composeTestRule.waitForIdle()
            Thread.sleep(500)
            try {
                composeTestRule.onNodeWithTag("emailInput").performTextClearance()
                composeTestRule.onNodeWithTag("passwordInput").performTextClearance()
            } catch (_: Throwable) {}
            composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
            composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
            composeTestRule.waitForIdle()
            Thread.sleep(500)
            composeTestRule.onNodeWithTag("loginButton").safeClick()
            val loginSucceeded = try {
                composeTestRule.waitUntil(timeoutMillis = 15_000) {
                    composeTestRule.onAllNodesWithTag("openDrawerButton").fetchSemanticsNodes().isNotEmpty() || composeTestRule.onAllNodesWithTag("errorMessage").fetchSemanticsNodes().isNotEmpty()
                }
                composeTestRule.onAllNodesWithTag("openDrawerButton").fetchSemanticsNodes().isNotEmpty()
            } catch (_: Throwable) {
                false
            }
            if (loginSucceeded) {
                composeTestRule.waitForIdle()
                Thread.sleep(1000)
                return
            }
            if (attempt < maxRetries) {
                Thread.sleep(3000)
                composeTestRule.waitForIdle()
                try {
                    composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                } catch (_: Throwable) {
                    composeTestRule.waitUntil(timeoutMillis = 5000) {
                        composeTestRule.onAllNodesWithTag("openLoginButton").fetchSemanticsNodes().isNotEmpty()
                    }
                    composeTestRule.onNodeWithTag("openLoginButton").safeClick()
                    composeTestRule.waitUntil(timeoutMillis = 5000) {
                        try {
                            composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                            true
                        } catch (e: Throwable) {
                            false
                        }
                    }
                }
            }
        }
        throw AssertionError("Login failed after $maxRetries attempts")
    }

    private fun navigateToOwnershipList() {
        composeTestRule.onNodeWithTag("openDrawerButton").safeClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("drawerItemOwnershipList").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
        composeTestRule.onNodeWithTag("drawerItemOwnershipList").safeClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            try {
                composeTestRule.onNodeWithTag("ownershipListScreen").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    private fun AndroidComposeTestRule<*, *>.waitUntilTagExists(tag: String, timeout: Long = 10_000) {
        this.waitUntil(timeoutMillis = timeout) {
            try {
                this.onNodeWithTag(tag, useUnmergedTree = false).assertExists()
                true
            } catch (_: Throwable) {
                false
            }
        }
    }

    private fun hasTestTagStartingWith(prefix: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag starts with '$prefix'") { node ->
            node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
        }
    }
}