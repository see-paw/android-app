package com.example.seepawandroid.ui.screens.register

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.MainActivity
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.utils.TestUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import javax.inject.Inject

/**
 * Instrumented tests for the Register screen.
 *
 * Tests UI display, field validation, and navigation.
 * Note: Full registration flow tests are skipped due to date picker complexity in tests.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        // Test data
        private const val VALID_NAME = "João Silva"
        private fun generateUniqueEmail() = "test.${System.currentTimeMillis()}@example.com"
        private const val VALID_PASSWORD = "Password123!"
        private const val VALID_STREET = "Rua das Flores 15"
        private const val VALID_CITY = "Porto"
        private const val VALID_POSTAL_CODE = "4000-223"

        // Invalid data
        private const val SHORT_NAME = "J"
        private const val INVALID_EMAIL = "invalid-email"
        private const val WEAK_PASSWORD = "pass"
        private const val MISMATCHED_PASSWORD = "Different123!"
        private const val INVALID_POSTAL_CODE = "12345"

        // Existing user (for duplicate test)
        private const val EXISTING_EMAIL = "carlos@test.com"
    }

    @Before
    fun setup() {
        hiltRule.inject()

        // Date Picker in testing mode
        TestUtils.isInTestMode = true
        TestUtils.testDateProvider = { LocalDate.of(1990, 1, 1) }

        composeTestRule.waitForIdle()
        logoutIfNeeded()
        navigateToRegisterScreen()
    }

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
                    composeTestRule.onNodeWithText("SeePaw Login").assertExists()
                    true
                } catch (e: Throwable) {
                    false
                }
            }
        } catch (e: Throwable) {
            // Already logged out
        }
    }

    private fun navigateToRegisterScreen() {
//        composeTestRule.waitUntil(timeoutMillis = 5000) {
//            try {
//                composeTestRule.onNodeWithText("SeePaw Login").assertExists()
//                true
//            } catch (e: Throwable) {
//                false
//            }
//        }
//        composeTestRule.onNodeWithText("Criar conta").performClick()
//        composeTestRule.waitUntil(timeoutMillis = 3000) {
//            try {
//                composeTestRule.onNodeWithText("Criar Conta").assertExists()
//                true
//            } catch (e: Throwable) {
//                false
//            }
//        }
        // Step 1: Wait for PublicHomepage to load
        // We use the "openLoginButton" testTag as indicator that homepage is ready
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("openLoginButton")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 2: Click the Login button to navigate from HOMEPAGE → LOGIN
        composeTestRule
            .onNodeWithTag("openLoginButton")
            .performClick()

        // Step 3: Wait for Login screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("SeePaw Login")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 4: Click "Criar conta" button to navigate from LOGIN → REGISTER (existing logic)
        composeTestRule
            .onNodeWithText("Criar conta")
            .performClick()

        // Step 5: Wait for Register screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("nameInput")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun scrollToNode(tag: String) {
        composeTestRule.onNodeWithTag(tag).performScrollTo()
    }

    @Test
    fun registerScreen_displaysAllElements() {
        composeTestRule.onNodeWithText("Criar Conta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preencha os seus dados").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmPasswordInput").assertIsDisplayed()

        scrollToNode("birthDateInput")
        composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()

        scrollToNode("streetInput")
        composeTestRule.onNodeWithTag("streetInput").assertIsDisplayed()

        scrollToNode("cityInput")
        composeTestRule.onNodeWithTag("cityInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("postalCodeInput").assertIsDisplayed()

        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed()
    }

    @Test
    fun registerButton_initiallyDisabled() {
        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
    }

    @Test
    fun nameField_showsErrorForShortName() {
        composeTestRule.onNodeWithTag("nameInput").performTextInput(SHORT_NAME)
        composeTestRule.onNodeWithTag("emailInput").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Nome deve ter pelo menos 2 caracteres").assertExists()
    }

    @Test
    fun emailField_showsErrorForInvalidFormat() {
        composeTestRule.onNodeWithTag("emailInput").performTextInput(INVALID_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Formato de email inválido").assertExists()
    }

    @Test
    fun passwordField_showsErrorForWeakPassword() {
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(WEAK_PASSWORD)
        composeTestRule.onNodeWithTag("confirmPasswordInput").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(
            hasText("Mínimo 8 caracteres", substring = true) or
                    hasText("Deve conter pelo menos", substring = true)
        ).assertExists()
    }

    @Test
    fun confirmPasswordField_showsErrorForMismatch() {
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("confirmPasswordInput").performTextInput(MISMATCHED_PASSWORD)
        scrollToNode("streetInput")
        composeTestRule.onNodeWithTag("streetInput").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("As passwords não coincidem").assertExists()
    }

    @Test
    fun postalCodeField_showsErrorForInvalidFormat() {
        scrollToNode("postalCodeInput")
        composeTestRule.onNodeWithTag("postalCodeInput").performTextInput(INVALID_POSTAL_CODE)

        // Lose focus by clicking on another field
        scrollToNode("streetInput")
        composeTestRule.onNodeWithTag("streetInput").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Formato inválido (use XXXX-XXX)").assertExists()
    }

    @Test
    fun backButton_navigatesToLogin() {
        composeTestRule.onNodeWithTag("backButton").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
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
     *  HELPERS
     *  ----------------------------------------- */

    /**
     * Fills all required fields with valid data for a complete registration.
     *
     * In test mode, the birth date is automatically set when clicking the calendar icon,
     * bypassing the Material3 DatePicker dialog which doesn't work reliably in instrumented tests.
     *
     * The test date is configured in setup() via TestUtils.testDateProvider.
     */
    private fun fillAllFieldsWithValidData() {
        composeTestRule.onNodeWithTag("nameInput").performTextInput(VALID_NAME)
        composeTestRule.onNodeWithTag("emailInput").performTextInput(generateUniqueEmail())
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("confirmPasswordInput").performTextInput(VALID_PASSWORD)

        // Scroll to street field and fill address fields
        composeTestRule.onNodeWithTag("streetInput").performScrollTo()
        composeTestRule.onNodeWithTag("streetInput").performTextInput(VALID_STREET)
        composeTestRule.onNodeWithTag("cityInput").performTextInput(VALID_CITY)
        composeTestRule.onNodeWithTag("postalCodeInput").performTextInput(VALID_POSTAL_CODE)

        // Set birth date via test mode (clicks icon, which triggers testDateProvider)
        composeTestRule.onNodeWithTag("birthDateIcon").performScrollTo()
        composeTestRule.onNodeWithTag("birthDateIcon").performClick()
        composeTestRule.waitForIdle()
    }

    /**
     * Waits until the loading indicator disappears.
     */
    private fun waitUntilLoadingFinishes() {
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
            ).fetchSemanticsNodes().isEmpty()
        }
    }

    /** -----------------------------------------
     *  BUTTON STATE TESTS
     *  ----------------------------------------- */

    @Test
    fun registerButton_enabledWhenAllFieldsFilled() {
        fillAllFieldsWithValidData()

        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()
    }

    /** -----------------------------------------
     *  ERROR HANDLING TESTS
     *  ----------------------------------------- */

    @Test
    fun register_withExistingEmail_showsError() {
        // Fill with existing user email
        composeTestRule.onNodeWithTag("nameInput").performTextInput(VALID_NAME)
        composeTestRule.onNodeWithTag("emailInput").performTextInput(EXISTING_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("confirmPasswordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("streetInput").performScrollTo()
        composeTestRule.onNodeWithTag("streetInput").performTextInput(VALID_STREET)
        composeTestRule.onNodeWithTag("cityInput").performTextInput(VALID_CITY)
        composeTestRule.onNodeWithTag("postalCodeInput").performTextInput(VALID_POSTAL_CODE)

        // Set birth date
        composeTestRule.onNodeWithTag("birthDateIcon").performScrollTo()
        composeTestRule.onNodeWithTag("birthDateIcon").performClick()
        composeTestRule.waitForIdle()

        // Click register
        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").performClick()

        // Wait for error
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
     *  SUCCESS TESTS
     *  ----------------------------------------- */

    @Test
    fun register_withValidData_showsSuccessDialog() {
        fillAllFieldsWithValidData()

        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").performClick()

        // Wait for loading to finish
        waitUntilLoadingFinishes()

        // Wait for success dialog
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("successDialog").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        // Verify dialog content
        composeTestRule.onNodeWithText("Conta criada com sucesso!").assertIsDisplayed()
        composeTestRule.onNode(
            hasText("Bem-vindo", substring = true) and hasText(VALID_NAME, substring = true)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag("successDialogOkButton").assertIsDisplayed()
    }

    @Test
    fun successDialog_clickOk_navigatesToLogin() {
        fillAllFieldsWithValidData()

        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").performClick()

        waitUntilLoadingFinishes()

        // Wait for success dialog
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("successDialog").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        // Click OK
        composeTestRule.onNodeWithTag("successDialogOkButton").performClick()

        // Verify navigation to login screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
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
     *  RETRY TEST
     *  ----------------------------------------- */

    @Test
    fun register_canRetryAfterError() {
        // First attempt with existing email
        composeTestRule.onNodeWithTag("nameInput").performTextInput(VALID_NAME)
        composeTestRule.onNodeWithTag("emailInput").performTextInput(EXISTING_EMAIL)
        composeTestRule.onNodeWithTag("passwordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("confirmPasswordInput").performTextInput(VALID_PASSWORD)
        composeTestRule.onNodeWithTag("streetInput").performScrollTo()
        composeTestRule.onNodeWithTag("streetInput").performTextInput(VALID_STREET)
        composeTestRule.onNodeWithTag("cityInput").performTextInput(VALID_CITY)
        composeTestRule.onNodeWithTag("postalCodeInput").performTextInput(VALID_POSTAL_CODE)

        // Set birth date
        composeTestRule.onNodeWithTag("birthDateIcon").performScrollTo()
        composeTestRule.onNodeWithTag("birthDateIcon").performClick()
        composeTestRule.waitForIdle()

        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").performClick()

        // Wait for error
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("errorMessage").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        // Scroll back to email field and change it
        composeTestRule.onNodeWithTag("emailInput").performScrollTo()
        composeTestRule.onNodeWithTag("emailInput").performTextClearance()
        composeTestRule.onNodeWithTag("emailInput").performTextInput(generateUniqueEmail())

        // Retry registration
        scrollToNode("registerButton")
        composeTestRule.onNodeWithTag("registerButton").performClick()

        waitUntilLoadingFinishes()

        // Should show success dialog
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("successDialog").assertExists()
                true
            } catch (e: Throwable) {
                false
            }
        }

        composeTestRule.onNodeWithText("Conta criada com sucesso!").assertIsDisplayed()
    }
}