package com.example.seepawandroid.ui.screens.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.seepawandroid.R
import com.example.seepawandroid.data.models.activities.AvailableSlot
import com.example.seepawandroid.data.models.activities.DaySchedule
import com.example.seepawandroid.data.models.activities.Schedule
import com.example.seepawandroid.data.models.activities.Slot
import com.example.seepawandroid.data.models.activities.UnavailableSlot
import com.example.seepawandroid.ui.theme.AvailableSlotColor
import com.example.seepawandroid.ui.theme.EmptySlotColor
import com.example.seepawandroid.ui.theme.OwnReservationColor
import com.example.seepawandroid.ui.theme.ReservedSlotColor
import com.example.seepawandroid.ui.theme.UnavailableSlotColor
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulingScreen(
    animalId: String,
    onNavigateBack: () -> Unit,
) {
    val viewModel: SchedulingViewModel = hiltViewModel()

    LaunchedEffect(animalId) {
        viewModel.loadSchedule(animalId)
    }

    val uiState by viewModel.uiState.observeAsState(ScheduleUiState.Loading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (uiState is ScheduleUiState.Success) {
                        Text(stringResource(R.string.scheduling_title, (uiState as ScheduleUiState.Success).schedule.animalName))
                    } else {
                        Text(stringResource(R.string.scheduling_title, ""))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ScheduleUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }

                is ScheduleUiState.Success -> {
                    SchedulingContent(
                        schedule = state.schedule,
                        onPrevWeek = { viewModel.loadPrevWeek() },
                        onNextWeek = { viewModel.loadNextWeek() },
                        onSelectSlotCell = { slot -> viewModel.onSlotClick(slot) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is ScheduleUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.retry() },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SchedulingContent(
    schedule: Schedule,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onSelectSlotCell: (Slot) -> Unit,
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
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun WeeklyScheduleCalendar(
    weekStartDate: LocalDate,
    weeklySchedule: List<DaySchedule>,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onSelectSlotCell: (Slot) -> Unit,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()

    Column(modifier = modifier) {
        WeekNavigationHeader(
            weekStartDate = weekStartDate,
            onPrevWeek = onPrevWeek,
            onNextWeek = onNextWeek,
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
private fun ScheduleLegend(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Legenda:",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LegendItem(
                color = AvailableSlotColor,
                label = "Disponível",
                icon = Icons.Default.Check,
                iconTint = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
            LegendItem(
                color = OwnReservationColor,
                label = "Tua reserva",
                icon = Icons.Default.Person,
                iconTint = Color(0xFF1565C0),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LegendItem(
                color = ReservedSlotColor,
                label = "Reservado",
                icon = Icons.Default.Person,
                iconTint = Color(0xFFF57C00),
                modifier = Modifier.weight(1f)
            )
            LegendItem(
                color = UnavailableSlotColor,
                label = "Indisponível",
                icon = Icons.Default.Close,
                iconTint = Color(0xFFC62828),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(20.dp),
            color = color,
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
fun DayColumn(
    dailySchedule: DaySchedule,
    verticalScrollState: androidx.compose.foundation.ScrollState,
    onSelectSlotCell: (Slot) -> Unit,
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

@Composable
fun TimeSlotCellBlock(
    cell: TimeSlotCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, iconData) = when (cell.slotType) {
        SlotType.AVAILABLE -> AvailableSlotColor to Triple(Icons.Default.Check, Color(0xFF2E7D32), null)
        SlotType.OWN_RESERVATION -> OwnReservationColor to Triple(Icons.Default.Person, Color(0xFF1565C0), null)
        SlotType.RESERVED -> ReservedSlotColor to Triple(Icons.Default.Person, Color(0xFFF57C00), null)
        SlotType.UNAVAILABLE -> {
            val unavailable = cell.slot as? UnavailableSlot
            UnavailableSlotColor to Triple(Icons.Default.Close, Color(0xFFC62828), unavailable?.reason)
        }
        SlotType.EMPTY -> EmptySlotColor to Triple(null, Color.Transparent, null)
    }

    val (icon, iconTint, _) = iconData
    val isClickable = cell.slotType == SlotType.AVAILABLE

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(CELL_HEIGHT)
            .padding(vertical = 1.dp)
            .then(
                if (isClickable) Modifier.clickable { onClick() }
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = if (cell.slotType != SlotType.EMPTY) {
            BorderStroke(0.5.dp, Color.LightGray)
        } else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun WeekNavigationHeader(
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

@Composable
private fun TimeAxisColumn(
    verticalScrollState: androidx.compose.foundation.ScrollState,
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

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}

private fun formatWeekRange(startDate: LocalDate): String {
    val endDate = startDate.plusDays(6)
    return "${startDate.dayOfMonth}-${endDate.dayOfMonth} ${startDate.month.name.take(3)}"
}
