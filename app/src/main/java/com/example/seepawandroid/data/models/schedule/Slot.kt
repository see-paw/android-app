package com.example.seepawandroid.data.models.schedule

import java.time.LocalDateTime

/**
 * Base class for a time slot in a schedule.
 *
 * @property id The unique identifier of the slot.
 * @property start The start date and time of the slot.
 * @property end The end date and time of the slot.
 */
@Suppress("OutdatedDocumentation")
sealed class Slot {
    abstract val id: String
    abstract val start: LocalDateTime
    abstract val end: LocalDateTime
}
