package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.theme.AvailableSlotColor
import com.example.seepawandroid.ui.theme.OwnReservationColor
import com.example.seepawandroid.ui.theme.ReservedSlotColor
import com.example.seepawandroid.ui.theme.UnavailableSlotColor

/**
 * A composable that displays a legend for the schedule.
 *
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun ScheduleLegend(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.schedule_legend_title),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LegendItem(
                color = AvailableSlotColor,
                label = stringResource(R.string.schedule_slot_available),
                icon = Icons.Default.Check,
                iconTint = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
            LegendItem(
                color = OwnReservationColor,
                label = stringResource(R.string.schedule_slot_own_reservation),
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
                label = stringResource(R.string.schedule_slot_reserved),
                icon = Icons.Default.Person,
                iconTint = Color(0xFFF57C00),
                modifier = Modifier.weight(1f)
            )
            LegendItem(
                color = UnavailableSlotColor,
                label = stringResource(R.string.schedule_slot_unavailable),
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
    icon: ImageVector,
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
