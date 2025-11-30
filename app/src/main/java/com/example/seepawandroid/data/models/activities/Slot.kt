package com.example.seepawandroid.data.models.activities

import java.time.LocalDateTime

sealed class Slot {
    abstract val id: String
    abstract val start: LocalDateTime
    abstract val end: LocalDateTime
}
