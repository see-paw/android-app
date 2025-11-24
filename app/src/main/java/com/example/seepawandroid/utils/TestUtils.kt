package com.example.seepawandroid.utils

import java.time.LocalDate

object TestUtils {
    var isInTestMode = false
    var testDateProvider: (() -> LocalDate)? = null
}