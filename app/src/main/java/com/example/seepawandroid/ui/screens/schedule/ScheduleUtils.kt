package com.example.seepawandroid.ui.screens.schedule

import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.models.schedule.DaySchedule
import com.example.seepawandroid.data.models.schedule.ReservedSlot
import com.example.seepawandroid.data.models.schedule.Slot
import com.example.seepawandroid.data.models.schedule.UnavailableSlot
import java.time.LocalTime

const val SCHEDULE_START_HOUR = 9
const val SCHEDULE_END_HOUR = 18
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
        is AvailableSlot -> {
            // Create a new slot for this specific hour only
            val hourSlot = AvailableSlot(
                id = matchingSlot.id,
                start = matchingSlot.start.toLocalDate().atTime(hour, 0),
                end = matchingSlot.start.toLocalDate().atTime(hour + 1, 0)
            )
            TimeSlotCell(cellTime, SlotType.AVAILABLE, hourSlot)
        }
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
