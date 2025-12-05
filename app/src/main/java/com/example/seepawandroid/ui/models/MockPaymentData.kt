package com.example.seepawandroid.ui.models

/**
 * Mock payment data for fostering and ownership flows.
 *
 * @property accountNumber Mock bank account number.
 * @property holderName Mock account holder name.
 * @property cvv Mock CVV code.
 */
data class MockPaymentData(
    val accountNumber: String,
    val holderName: String,
    val cvv: String
)