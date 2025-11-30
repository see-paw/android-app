package com.example.seepawandroid.ui.screens.activities

import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.models.activities.AvailableSlot
import com.example.seepawandroid.data.models.activities.DaySchedule
import com.example.seepawandroid.data.models.activities.ReservedSlot
import com.example.seepawandroid.data.models.activities.Slot
import com.example.seepawandroid.data.models.activities.UnavailableSlot
import java.time.LocalTime

const val SCHEDULE_START_HOUR = 9
const val SCHEDULE_END_HOUR = 20
val CELL_HEIGHT = 60.dp

data class TimeSlotCell(
    val time: LocalTime,
    val slotType: SlotType,
    val slot: Slot?
)

enum class SlotType {
    AVAILABLE,
    RESERVED,
    OWN_RESERVATION,
    UNAVAILABLE,
    EMPTY
}

fun DaySchedule.toTimeSlotCells(
    startHour: Int = SCHEDULE_START_HOUR,
    endHour: Int = SCHEDULE_END_HOUR
): List<TimeSlotCell> = (startHour until endHour).map { hour ->
    val cellTime = LocalTime.of(hour, 0)

    val matchingSlot = slots.firstOrNull { slot ->
        val slotStart = slot.start.toLocalTime()
        val slotEnd = slot.end.toLocalTime()
        cellTime >= slotStart && cellTime < slotEnd
    }

    when (matchingSlot) {
        is AvailableSlot -> TimeSlotCell(cellTime, SlotType.AVAILABLE, matchingSlot)
        is ReservedSlot -> {
            if (matchingSlot.isOwnReservation) {
                TimeSlotCell(cellTime, SlotType.OWN_RESERVATION, matchingSlot)
            } else {
                TimeSlotCell(cellTime, SlotType.RESERVED, matchingSlot)
            }
        }
        is UnavailableSlot -> TimeSlotCell(cellTime, SlotType.UNAVAILABLE, matchingSlot)
        null -> TimeSlotCell(cellTime, SlotType.EMPTY, null)
    }
}
