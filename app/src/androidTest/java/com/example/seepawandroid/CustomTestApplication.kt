package com.example.seepawandroid

import dagger.hilt.android.testing.CustomTestApplication

/**
 * Instructs Hilt to generate a test application class that extends [BaseTestApplication].
 *
 * The generated class will be named "CustomTestApplication_Application".
 * Use this class name in [HiltTestRunner].
 */
@CustomTestApplication(BaseTestApplication::class)
interface CustomTestApplication