package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.models.schedule.DaySchedule
import com.example.seepawandroid.ui.screens.schedule.toTimeSlotCells
import java.time.DayOfWeek

/**
 * A composable that displays a column for a single day in the schedule.
 *
 * @param dailySchedule The schedule for the day.
 * @param verticalScrollState The scroll state for the column.
 * @param onSelectSlotCell A callback that is invoked when a slot is selected.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun DayColumn(
    dailySchedule: DaySchedule,
    verticalScrollState: ScrollState,
    onSelectSlotCell: (AvailableSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNameRes = when (dailySchedule.date.dayOfWeek) {
        DayOfWeek.MONDAY -> R.string.day_monday_short
        DayOfWeek.TUESDAY -> R.string.day_tuesday_short
        DayOfWeek.WEDNESDAY -> R.string.day_wednesday_short
        DayOfWeek.THURSDAY -> R.string.day_thursday_short
        DayOfWeek.FRIDAY -> R.string.day_friday_short
        DayOfWeek.SATURDAY -> R.string.day_saturday_short
        DayOfWeek.SUNDAY -> R.string.day_sunday_short
    }

    val cells = remember(dailySchedule) {
        dailySchedule.toTimeSlotCells()
    }

    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(dayNameRes),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = dailySchedule.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.verticalScroll(verticalScrollState)
        ) {
            cells.forEach { cell ->
                TimeSlotCellBlock(
                    cell = cell,
                    date = dailySchedule.date,
                    onClick = {
                        cell.slot?.let { slot ->
                            if (slot is AvailableSlot) {
                                onSelectSlotCell(slot)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}