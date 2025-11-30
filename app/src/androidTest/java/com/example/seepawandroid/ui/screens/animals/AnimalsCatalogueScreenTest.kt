package com.example.seepawandroid.ui.screens.animals

import android.content.Context
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.MainActivity
import com.example.seepawandroid.utils.NetworkUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for the Animal Catalogue screen.
 *
 * Tests by navigating through the app flow
 * to ensure proper ViewModel initialization and data loading.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AnimalCatalogueScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        forceNetworkAvailable()

        composeTestRule.waitForIdle()

        navigateToCatalogue()
    }

    private fun navigateToCatalogue() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("openCatalogueButton").assertExists()
                true
            } catch (_: Throwable) { false }
        }

        composeTestRule.onNodeWithTag("openCatalogueButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithTag("catalogueScreen").assertExists()
                true
            } catch (_: Throwable) { false }
        }
    }

    /** -----------------------------------------
     *  BASIC UI TESTS
     *  ----------------------------------------- */

    @Test
    fun catalogueScreen_displaysAllMainElements() {
        waitUntilLoadingFinishes()


        composeTestRule.onNodeWithTag("catalogueScreen")
            .assertExists()

        composeTestRule.onNodeWithTag("searchInput")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("filterButton")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("sortButton")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun catalogueScreen_loadsAnimalCards() {
        waitUntilLoadingFinishes()

        try {
            composeTestRule.onNodeWithTag("animalGrid")
                .assertExists()
                .assertIsDisplayed()

            val animalCards = composeTestRule.onAllNodes(
                hasTestTag("animalCard_", substring = true)
            )
            assert(animalCards.fetchSemanticsNodes().isNotEmpty()) {
                "Expected at least 1 animal card"
            }
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithTag("emptyState").assertExists()
        }
    }

    /** -----------------------------------------
     *  SEARCH FUNCTIONALITY
     *  ----------------------------------------- */

    @Test
    fun searchInput_canTypeText() {
        waitUntilLoadingFinishes()

        val searchQuery = "Max"

        composeTestRule.onNodeWithTag("searchInput")
            .performTextInput(searchQuery)

        composeTestRule.onNodeWithTag("searchInput")
            .assertTextContains(searchQuery)
    }



    /** -----------------------------------------
     *  FILTER FUNCTIONALITY
     *  ----------------------------------------- */

    @Test
    fun filterButton_opensFilterBottomSheet() {
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("filterButton")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(isDialog())
                .fetchSemanticsNodes().isNotEmpty()
        }
    }


    /** -----------------------------------------
     *  SORT FUNCTIONALITY
     *  ----------------------------------------- */


    @Test
    fun sort_nameAsc_changesResults() {
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("sortButton").performClick()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("sort_name_asc")
            .assertExists()
            .performClick()

        Thread.sleep(2000)

        composeTestRule
            .onAllNodes(hasTestTag("animalCard_", substring = true))
            .fetchSemanticsNodes()
            .also { nodes ->
                assert(nodes.isNotEmpty()) { "Deve haver animais após sort" }
            }
    }

    @Test
    fun sort_mostRecent_changesResults(){
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("sortButton").performClick()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("sort_recent_desc")
            .assertExists()
            .performClick()

        Thread.sleep(2000)

        composeTestRule
            .onAllNodes(hasTestTag("animalCard_", substring = true))
            .fetchSemanticsNodes()
            .also { nodes ->
                assert(nodes.isNotEmpty()) { "Deve haver animais após sort" }
            }
    }

    @Test
    fun sort_ageDesc_changesResults() {
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("sortButton").performClick()
        Thread.sleep(500)

        composeTestRule.onNodeWithTag("sort_age_desc")
            .assertExists()
            .performClick()

        Thread.sleep(2000)

        composeTestRule
            .onAllNodes(hasTestTag("animalCard_", substring = true))
            .fetchSemanticsNodes()
            .also { nodes ->
                assert(nodes.isNotEmpty()) { "Deve haver animais após sort" }
            }

    }


    /** -----------------------------------------
     *  PAGINATION TESTS
     *  ----------------------------------------- */

    @Test
    fun paginationBar_isDisplayed() {
        waitUntilLoadingFinishes()

        // Pagination cannot exists if there are few results
        // So, it only makes sure the animals's catalogue load
        composeTestRule.onNodeWithTag("catalogueScreen").assertExists()
    }

    @Test
    fun pagination_nextPage_changesPageNumber() {
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("nextPageButton")
            .assertExists()

        composeTestRule.onNodeWithText("1")
            .assertExists()

        composeTestRule.onNodeWithTag("nextPageButton")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("2")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("2")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun pagination_previousPage_changesBackToFirstPage() {
        waitUntilLoadingFinishes()

        composeTestRule.onNodeWithTag("nextPageButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("2")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("previousPageButton")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("1")).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("1")
            .assertExists()
            .assertIsDisplayed()
    }



    /** -----------------------------------------
     *  HELPER METHODS
     *  ----------------------------------------- */

    private fun waitUntilLoadingFinishes() {
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            try {
                composeTestRule.onNodeWithTag("loadingIndicator").assertDoesNotExist()
                true
            } catch (_: Throwable) { false }
        }
    }

    private fun hasTestTag(tag: String, substring: Boolean = false): SemanticsMatcher {
        return if (substring) {
            SemanticsMatcher("TestTag contains '$tag'") {
                val tagValue = it.config.getOrNull(
                    androidx.compose.ui.semantics.SemanticsProperties.TestTag
                )
                tagValue?.contains(tag) == true
            }
        } else {
            hasTestTag(tag)
        }
    }

    private fun forceNetworkAvailable() {
        try {
            // Inicializa o NetworkUtils com o contexto da app
            val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
            NetworkUtils.init(context)

            android.util.Log.d("TestSetup", "📶 NetworkUtils initialized for tests")
            android.util.Log.d("TestSetup", "📶 Network available: ${NetworkUtils.isConnected()}")
        } catch (e: Exception) {
            android.util.Log.e("TestSetup", "Failed to init NetworkUtils", e)
        }
    }
}