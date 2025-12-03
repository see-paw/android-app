package com.example.seepawandroid

import android.content.Context
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.seepawandroid.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

/**
 * Base class for UI tests, providing common setup and helper functions.
 * It initializes Hilt, the Compose test rule, and provides utility
 * functions for waiting for nodes and performing safe clicks.
 */
open class BaseUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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
    fun SemanticsNodeInteraction.awaitDisplayed(timeout: Long = 5_000): SemanticsNodeInteraction {
        composeTestRule.waitUntil(timeoutMillis = timeout) {
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
    fun SemanticsNodeInteraction.awaitEnabled(timeout: Long = 5_000): SemanticsNodeInteraction {
        composeTestRule.waitUntil(timeoutMillis = timeout) {
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
        timeout: Long = 8_000
    ): SemanticsNodeInteraction {
        composeTestRule.waitUntil(timeoutMillis = timeout) {
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
    fun waitUntilLoadingFinishes(timeout: Long = 20_000) {
        composeTestRule.waitUntil(timeoutMillis = timeout) {
            composeTestRule.onAllNodes(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
            ).fetchSemanticsNodes().isEmpty()
        }
    }
}