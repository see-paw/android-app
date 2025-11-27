package com.example.seepawandroid.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
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

    /**
     * Formats an ISO datetime string to Portuguese date format (day/month/year only).
     *
     * @param isoDateTime ISO datetime string (e.g., "2025-11-26T18:56:36.332622").
     * @return Formatted date string (e.g., "26/11/2025") or original string if parsing fails.
     */
    fun formatToPortugueseDate(isoDateTime: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDateTime)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "PT"))
            dateTime.format(formatter)
        } catch (e: Exception) {
            try {
                val instant = Instant.parse(isoDateTime)
                val formatter = DateTimeFormatter
                    .ofPattern("dd/MM/yyyy", Locale("pt", "PT"))
                    .withZone(ZoneId.of("Europe/Lisbon"))
                formatter.format(instant)
            } catch (e2: Exception) {
                isoDateTime  // Last fallback
            }
        }
    }
}