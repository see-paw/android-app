package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.models.schedule.Schedule

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