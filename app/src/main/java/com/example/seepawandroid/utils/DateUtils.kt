package com.example.seepawandroid.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Parses an ISO 8601 date-time string to LocalDateTime.
 *
 * @param isoString Date-time string in ISO 8601 format (e.g., "2025-11-23T15:23:37.244Z")
 * @return LocalDateTime object or null if parsing fails
 */
fun parseIsoDateTime(isoString: String): LocalDateTime? {
    return try {
        val instant = Instant.parse(isoString)
        LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    } catch (e: Exception) {
        null
    }
}