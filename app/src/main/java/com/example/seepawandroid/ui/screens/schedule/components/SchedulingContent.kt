package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.models.schedule.Schedule

/**
 * A composable that displays the main content of the scheduling screen.
 *
 * @param schedule The schedule to be displayed.
 * @param onPrevWeek A callback that is invoked when the user clicks the previous week button.
 * @param onNextWeek A callback that is invoked when the user clicks the next week button.
 * @param onSelectSlotCell A callback that is invoked when a slot is selected.
 * @param canNavigatePrevious Whether the user can navigate to the previous week.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun SchedulingContent(
    schedule: Schedule,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onSelectSlotCell: (AvailableSlot) -> Unit,
    canNavigatePrevious: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        WeeklyScheduleCalendar(
            weekStartDate = schedule.weekStartDate,
            weeklySchedule = schedule.days,
            onPrevWeek = onPrevWeek,
            onNextWeek = onNextWeek,
            onSelectSlotCell = onSelectSlotCell,
            canNavigatePrevious = canNavigatePrevious,
            modifier = Modifier.fillMaxSize()
        )
    }
}