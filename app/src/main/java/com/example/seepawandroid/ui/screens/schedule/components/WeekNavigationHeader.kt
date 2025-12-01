package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.seepawandroid.R
import java.time.LocalDate

@Composable
fun WeekNavigationHeader(
    weekStartDate: LocalDate,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevWeek) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                contentDescription = stringResource(R.string.previous_week)
            )
        }

        Text(
            text = formatWeekRange(weekStartDate),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = onNextWeek) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = stringResource(R.string.next_week)
            )
        }
    }
}

private fun formatWeekRange(startDate: LocalDate): String {
    val endDate = startDate.plusDays(6)
    return "${startDate.dayOfMonth}-${endDate.dayOfMonth} ${startDate.month.name.take(3)}"
}
