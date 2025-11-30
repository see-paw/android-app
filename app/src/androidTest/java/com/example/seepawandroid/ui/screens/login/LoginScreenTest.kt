package com.example.seepawandroid.ui.screens.login

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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
 * Instrumented tests for the Login screen.
 *
 * Tests user authentication flow including UI interactions,
 * validation, error handling, and successful login scenarios.
 *
 * Each test ensures complete isolation by logging out if a session
 * exists before execution.
 *
 * Test credentials:
 * - Valid User: carlos@test.com / Pa$$w0rd
 * - Valid Admin: alice@test.com / Pa$$w0rd
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        // Test credentials (global variables for easy testing)
        private const val VALID_EMAIL = "carlos@test.com"
        private const val VALID_PASSWORD = "Pa\$\$w0rd"
        private const val INVALID_EMAIL = "nobody@test.com"
        private const val INVALID_PASSWORD = "wrongpassword"
    }

    @Before
    fun setup() {
        hiltRule.inject()

        // Wait for initial composition
        composeTestRule.waitForIdle()

        // CRITICAL: If user is logged in, logout first
        // This ensures test isolation and independence
        logoutIfNeeded()

        // Ensure we're on the login screen
        ensureOnLoginScreen()
    }

    /**
     * Logs out if the user is currently authenticated.
     *
     * Checks for the presence of demo screens (User or Admin)
     * and clicks the logout button if found.
     */
    private fun logoutIfNeeded() {
        try {
            // Wait a bit to see if we're on a demo screen
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                try {
                    // Check if logout button exists (User or Admin demo screen)
                    composeTestRule.onNodeWithTag("logoutButton").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }

            // If we found the logout button, click it
            composeTestRule.onNodeWithTag("logoutButton").performClick()

            // Wait for logout to complete and return to login screen
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                try {
                    composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }

        } catch (e: Throwable) {
            // No logout button found, assume we're already logged out
            // This is fine, continue to next step
        }
    }

    /**
     * Ensures the app is on the login screen.
     * Waits for the "SeePaw Login" title to appear.
     */
    private fun ensureOnLoginScreen() {
        // First check if we're already on LOGIN screen
        try {
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule
                    .onAllNodesWithText("SeePaw Login")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            // Already on LOGIN, we're done
            return
        } catch (e: Throwable) {
            // Not on LOGIN, need to navigate from HOMEPAGE
        }

        // Wait for HOMEPAGE to load (using the login button as indicator)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("openLoginButton")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click the Login button to navigate HOMEPAGE → LOGIN
        composeTestRule
            .onNodeWithTag("openLoginButton")
            .performClick()

        // Wait for LOGIN screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }
    }

    /** -----------------------------------------
     *  HELPERS
     *  ----------------------------------------- */

    /**
     * Waits until the email input field becomes editable.
     * Used after error states to ensure UI is ready for retry.
     */
    private fun waitUntilInputEditable() {
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                val node = composeTestRule
                    .onNodeWithTag("emailInput")
                    .fetchSemanticsNode()

                androidx.compose.ui.semantics.SemanticsProperties.EditableText in node.config
            } catch (e: Throwable) {
                false
            }
        }
    }

    /**
     * Waits until the loading indicator disappears.
     * Indicates that an async operation has completed.
     */
    private fun waitUntilLoadingFinishes() {
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodes(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
            ).fetchSemanticsNodes().isEmpty()
        }
    }

    /** -----------------------------------------
     *  BASIC UI TESTS
     *  ----------------------------------------- */

    @Test
    fun loginScreen_displaysAllElements() {
        composeTestRule.onNodeWithText("SeePaw Login").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordInput").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun loginButton_initiallyDisabled() {
        composeTestRule.onNodeWithTag("loginButton").assertIsNotEnabled()
    }

    /** -----------------------------------------
     *  INPUT VALIDATION TESTS
     *  ----------------------------------------- */

    @Test
    fun loginButton_enabledWhenBothFieldsFilled() {
        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)

        composeTestRule.onNodeWithTag("loginButton").assertIsEnabled()
    }

    /** -----------------------------------------
     *  ERROR HANDLING TESTS
     *  ----------------------------------------- */

    @Test
    fun login_withInvalidPassword_showsError() {
        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(INVALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Wait for error message to appear using testTag
        // Increased timeout to handle variable network conditions
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("errorMessage").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        // Verify error message is displayed
        composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    }

    @Test
    fun login_withInvalidEmail_showsError() {
        composeTestRule.onNodeWithTag("emailInput").performTextInput(INVALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("errorMessage").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    }

    @Test
    fun login_withBothInvalid_showsError() {
        composeTestRule.onNodeWithTag("emailInput").performTextInput(INVALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(INVALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("errorMessage").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    }

    /** -----------------------------------------
     *  SUCCESS TEST
     *  ----------------------------------------- */

    @Test
    fun login_withValidCredentials_navigatesToUserHome() {
        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Wait until loading finishes
        waitUntilLoadingFinishes()

        // Verify navigation to User home screen
        val expectedTitle = composeTestRule.activity.getString(R.string.home_title)

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText(expectedTitle).assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()
    }

    /** -----------------------------------------
     *  RETRY TEST
     *  ----------------------------------------- */

    @Test
    fun login_canRetryAfterError() {
        // First attempt with wrong credentials
        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(INVALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Wait for error to appear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("errorMessage").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        // Wait until inputs become editable again
        waitUntilInputEditable()

        // Clear previous inputs
        composeTestRule.onNodeWithTag("emailInput").performTextClearance()
        composeTestRule.onNodeWithTag("passwordInput").performTextClearance()

        // Retry with correct credentials
        composeTestRule.onNodeWithTag("emailInput").performTextInput(VALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Wait for login process to complete
        waitUntilLoadingFinishes()

        // Verify success - should be on User home screen
        val expectedTitle = composeTestRule.activity.getString(R.string.home_title)

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText(expectedTitle).assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()
    }
}