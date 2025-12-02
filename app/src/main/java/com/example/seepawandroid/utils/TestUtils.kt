package com.example.seepawandroid.utils

import java.time.LocalDate

/**
 * Utility object for testing support.
 */
object TestUtils {
    /**
     * Flag indicating whether the app is running in test mode.
     */
    var isInTestMode = false

    /**
     * Provider for test dates when running in test mode.
     */
    var testDateProvider: (() -> LocalDate)? = null
}