package com.example.seepawandroid.ui.screens.schedule

import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.models.schedule.DaySchedule
import com.example.seepawandroid.data.models.schedule.ReservedSlot
import com.example.seepawandroid.data.models.schedule.Slot
import com.example.seepawandroid.data.models.schedule.UnavailableSlot
import java.time.LocalTime

/**
 * The start hour of the schedule.
 */
const val SCHEDULE_START_HOUR = 9
/**
 * The end hour of the schedule.
 */
const val SCHEDULE_END_HOUR = 18
/**
 * The height of each cell in the schedule.
 */
val CELL_HEIGHT = 60.dp

/**
 * Represents a time slot cell in the schedule.
 *
 * @property time The time of the cell.
 * @property slotType The type of the slot.
 * @property slot The slot itself.
 */
data class TimeSlotCell(
    val time: LocalTime,
    val slotType: SlotType,
    val slot: Slot?
)

/**
 * Represents the type of a time slot.
 */
enum class SlotType {
    /**
     * The slot is available.
     */
    AVAILABLE,
    /**
     * The slot is reserved.
     */
    RESERVED,
    /**
     * The slot is reserved by the current user.
     */
    OWN_RESERVATION,
    /**
     * The slot is unavailable.
     */
    UNAVAILABLE,
    /**
     * The slot is empty.
     */
    EMPTY
}

/**
 * Converts a [DaySchedule] to a list of [TimeSlotCell]s.
 *
 * @param startHour The start hour of the schedule.
 * @param endHour The end hour of the schedule.
 * @return A list of [TimeSlotCell]s.
 */
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
