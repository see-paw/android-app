package com.example.seepawandroid.system_tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seepawandroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches_showsInitialCounter() {
        // Verifica que o contador começa em 0
        composeTestRule
            .onNodeWithTag("counterText")
            .assertIsDisplayed()
            .assertTextContains("Contador: 0")
    }

    @Test
    fun clickIncrementButton_increasesCounter() {
        // Clica no botão de incrementar
        composeTestRule
            .onNodeWithTag("incrementButton")
            .performClick()

        // Verifica que o contador aumentou para 1
        composeTestRule
            .onNodeWithTag("counterText")
            .assertTextContains("Contador: 1")
    }

    @Test
    fun clickIncrementThreeTimes_showsThree() {
        // Clica 3 vezes
        repeat(3) {
            composeTestRule
                .onNodeWithTag("incrementButton")
                .performClick()
        }

        // Verifica que mostra 3
        composeTestRule
            .onNodeWithTag("counterText")
            .assertTextContains("Contador: 3")
    }

    @Test
    fun clickReset_resetsCounter() {
        // Incrementa algumas vezes
        repeat(5) {
            composeTestRule
                .onNodeWithTag("incrementButton")
                .performClick()
        }

        // Clica em reset
        composeTestRule
            .onNodeWithTag("resetButton")
            .performClick()

        // Verifica que voltou a 0
        composeTestRule
            .onNodeWithTag("counterText")
            .assertTextContains("Contador: 0")
    }
}