package com.example.seepawandroid.ui.screens.animals

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Combined test suite for both:
 * - Public visitor flow
 * - Authenticated user flow (login → drawer → catalogue)
 * - Catalogue UI behaviour (filters, sorting, grid, empty search)
 *
 * Works with backend real data.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AnimalCatalogueFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        composeRule.waitForIdle()
    }

    // ---------------------------------------------------------
    // 1. PUBLIC FLOW: Homepage → Catalogue
    // ---------------------------------------------------------

    @Test
    fun visitor_canOpenCatalogueFromHomepage() {

        composeRule.onNodeWithTag("openCatalogueButton")
            .assertExists("Missing testTag('openCatalogueButton')")
            .performClick()

        composeRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeRule.onNodeWithTag("catalogueScreen").assertExists()
                true
            } catch (_: Throwable) { false }
        }

        composeRule.onNodeWithTag("searchInput").assertIsDisplayed()
        composeRule.onNodeWithTag("filterButton").assertIsDisplayed()
        composeRule.onNodeWithTag("sortButton").assertIsDisplayed()
    }

    // ---------------------------------------------------------
    // 2. CATALOGUE UI TESTS
    // ---------------------------------------------------------


    @Test
    fun catalogue_displaysGridWhenLoaded() {
        visitorNavigateToCatalogue()

        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithTag("animalCard")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule.onNodeWithTag("animalGrid").assertIsDisplayed()
    }

    @Test
    fun catalogue_opensFilterSheet() {
        visitorNavigateToCatalogue()

        composeRule.onNodeWithTag("filterButton").performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodes(isDialog()).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun catalogue_opensSortMenu() {
        visitorNavigateToCatalogue()

        composeRule.onNodeWithTag("sortButton").performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeRule.onNodeWithText("Most Recent").isDisplayed()
                true
            } catch (_: Throwable) { false }
        }
    }

    @Test
    fun catalogue_displaysPaginationBar() {
        visitorNavigateToCatalogue()

        composeRule.onNodeWithTag("paginationBar").assertExists()
    }

    @Test
    fun catalogue_displaysEmptyState_whenSearchReturnsNothing() {
        visitorNavigateToCatalogue()

        composeRule.onNodeWithTag("searchInput")
            .performTextInput("asduiqweuiqweui123123__NO_RESULTS__")

        composeRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeRule.onNodeWithTag("emptyState").assertExists()
                true
            } catch (_: Throwable) { false }
        }

        composeRule.onNodeWithTag("emptyState").assertIsDisplayed()
    }

    // ---------------------------------------------------------
    // 3. AUTHENTICATED FLOW: Login → Drawer → Catalogue
    // ---------------------------------------------------------

    @Test
    fun authenticatedUser_canLogin_thenOpenCatalogueInDrawer() {

        // Open login screen
        composeRule.onNodeWithTag("openLoginButton")
            .assertExists("Missing testTag('openLoginButton')")
            .performClick()

        // Insert credentials
        composeRule.onNodeWithTag("emailInput")
            .performTextInput("carlos@test.com")

        composeRule.onNodeWithTag("passwordInput")
            .performTextInput("Pa\$\$w0rd")

        // Press login
        composeRule.onNodeWithTag("loginButton")
            .assertIsEnabled()
            .performClick()

        // Wait for user home
        composeRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeRule.onNodeWithText("Bem-vindo").assertExists()
                true
            } catch (_: Throwable) { false }
        }

        // Open drawer
        composeRule.onNodeWithTag("openDrawerButton")
            .assertExists("Missing testTag('openDrawerButton')")
            .performClick()

        // Select catalogue
        composeRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeRule.onNodeWithTag("drawerItemCatalogue").assertExists()
                true
            } catch (_: Throwable) { false }
        }

        composeRule.onNodeWithTag("drawerItemCatalogue")
            .performClick()

        // Wait for catalogue as authenticated user
        composeRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeRule.onNodeWithTag("catalogueScreen").assertExists()
                true
            } catch (_: Throwable) { false }
        }

        // Basic UI checks
        composeRule.onNodeWithTag("searchInput").assertIsDisplayed()
        composeRule.onNodeWithTag("filterButton").assertIsDisplayed()
        composeRule.onNodeWithTag("paginationBar").assertExists()
    }

    // ---------------------------------------------------------
    // Shared helper
    // ---------------------------------------------------------

    private fun visitorNavigateToCatalogue() {
        composeRule.onNodeWithTag("openCatalogueButton")
            .assertExists()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeRule.onNodeWithTag("catalogueScreen").assertExists()
                true
            } catch (_: Throwable) { false }
        }
    }
}
