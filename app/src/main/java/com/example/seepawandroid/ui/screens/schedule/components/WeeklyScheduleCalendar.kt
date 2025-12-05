package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.models.schedule.DaySchedule
import com.example.seepawandroid.ui.screens.schedule.CELL_HEIGHT
import com.example.seepawandroid.ui.screens.schedule.SCHEDULE_END_HOUR
import com.example.seepawandroid.ui.screens.schedule.SCHEDULE_START_HOUR
import java.time.LocalDate
import java.util.Locale
import kotlin.collections.forEach

/**
 * A composable that displays a weekly schedule calendar.
 *
 * @param weekStartDate The start date of the week.
 * @param weeklySchedule The schedule for the week.
 * @param onPrevWeek A callback that is invoked when the user clicks the previous week button.
 * @param onNextWeek A callback that is invoked when the user clicks the next week button.
 * @param onSelectSlotCell A callback that is invoked when a slot is selected.
 * @param canNavigatePrevious Whether the user can navigate to the previous week.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun WeeklyScheduleCalendar(
    weekStartDate: LocalDate,
    weeklySchedule: List<DaySchedule>,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onSelectSlotCell: (AvailableSlot) -> Unit,
    canNavigatePrevious: Boolean = true,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()

    Column(modifier = modifier) {
        WeekNavigationHeader(
            weekStartDate = weekStartDate,
            onPrevWeek = onPrevWeek,
            onNextWeek = onNextWeek,
            canNavigatePrevious = canNavigatePrevious,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ScheduleLegend(modifier = Modifier.padding(bottom = 12.dp))

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            TimeAxisColumn(
                verticalScrollState = verticalScrollState,
                modifier = Modifier.width(45.dp)
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklySchedule.forEach { daySchedule ->
                    DayColumn(
                        dailySchedule = daySchedule,
                        verticalScrollState = verticalScrollState,
                        onSelectSlotCell = onSelectSlotCell,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeAxisColumn(
    verticalScrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(52.dp))

        Column(
            modifier = Modifier.verticalScroll(verticalScrollState)
        ) {
            for (hour in SCHEDULE_START_HOUR until SCHEDULE_END_HOUR) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CELL_HEIGHT)
                        .padding(vertical = 1.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:00", hour),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }
    }
}