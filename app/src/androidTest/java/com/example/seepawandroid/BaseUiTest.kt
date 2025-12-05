package com.example.seepawandroid

import android.content.Context
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

/**
 * Base class for UI tests, providing common setup and helper functions.
 * It initializes Hilt, the Compose test rule, and provides utility
 * functions for waiting for nodes and performing safe clicks.
 */
open class BaseUiTest {

    val SEC_TO_MILLIS = 1000
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val screenshotRule = ScreenshotTestRule()

    lateinit var context: Context

    @Before
    open fun setUp() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Waits until the node is displayed on the screen.
     * Throws an exception if the timeout is reached.
     * @return The [SemanticsNodeInteraction] for chaining.
     */
    fun SemanticsNodeInteraction.awaitDisplayed(timeout: Long = 3): SemanticsNodeInteraction {
        composeTestRule.waitUntil(timeoutMillis = timeout * SEC_TO_MILLIS) {
            try {
                this.assertIsDisplayed()
                true // The node is displayed, condition met.
            } catch (_: AssertionError) {
                false // Not displayed yet, continue waiting.
            }
        }
        return this // Return self for chaining.
    }

    /**
     * Waits until the node is enabled.
     * Throws an exception if the timeout is reached.
     * @return The [SemanticsNodeInteraction] for chaining.
     */
    fun SemanticsNodeInteraction.awaitEnabled(timeout: Long = 3): SemanticsNodeInteraction {
        composeTestRule.waitUntil(timeoutMillis = timeout * SEC_TO_MILLIS) {
            try {
                this.assertIsEnabled()
                true // The node is enabled, condition met.
            } catch (_: AssertionError) {
                false // Not enabled yet, continue waiting.
            }
        }
        return this // Return self for chaining.
    }

    /**
     * Waits until the node is both displayed and enabled.
     * @return The [SemanticsNodeInteraction] for chaining.
     */
    fun SemanticsNodeInteraction.awaitDisplayedAndEnabled(
        timeout: Long = 3
    ): SemanticsNodeInteraction {
        composeTestRule.waitUntil(timeoutMillis = timeout * SEC_TO_MILLIS) {
            try {
                this.assertIsDisplayed()
                this.assertIsEnabled()
                true
            } catch (_: Throwable) {
                false
            }
        }
        return this
    }

    /**
     * Performs a click after ensuring the node is displayed and enabled.
     */
    fun SemanticsNodeInteraction.safeClick() {
        this.awaitDisplayedAndEnabled()
        this.performClick()
    }

    /**
     * Waits until the loading indicator disappears.
     * Indicates that an async operation has completed.
     */
    fun waitUntilLoadingFinishes(timeout: Long = 15) {
        composeTestRule.waitUntil(timeoutMillis = timeout * SEC_TO_MILLIS) {
            composeTestRule.onAllNodes(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
            ).fetchSemanticsNodes().isEmpty()
        }
    }

    open fun navigateToCatalogue() {
        composeTestRule.onNodeWithTag("openDrawerButton").safeClick()

        composeTestRule.onNodeWithTag("drawerItemCatalogue").awaitDisplayedAndEnabled().assertExists()

        composeTestRule.onNodeWithTag("drawerItemCatalogue").safeClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodes(hasTestTagStartingWith("animalCard_"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Logs out if the user is currently authenticated.
     *
     * Checks for the presence of demo screens (User or Admin)
     * and clicks the logout button if found.
     */
    fun logoutIfNeeded() {
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

    fun hasTestTagStartingWith(prefix: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag starts with '$prefix'") { node ->
            node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
        }
    }

    fun loginAsTestUser(email: String, password: String) {
        // Click to navigate to login screen
        composeTestRule.onNodeWithTag("openLoginButton").safeClick()

        composeTestRule.onNodeWithText("SeePaw Login").awaitDisplayed().assertExists()

        composeTestRule.onNodeWithTag("emailInput").assertExists()

        // Enter valid test credentials
        composeTestRule.onNodeWithTag("emailInput").performTextInput(email)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(password)

        // Wait for login button to be enabled (requires non-blank email and password)
        composeTestRule.onNodeWithTag("loginButton").awaitEnabled(1).assertIsEnabled()

        composeTestRule.onNodeWithTag("loginButton").performClick()
    }
}