package com.example.seepawandroid.data.models.schedule

import java.time.LocalDateTime

sealed class Slot {
    abstract val id: String
    abstract val start: LocalDateTime
    abstract val end: LocalDateTime
}
