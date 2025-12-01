package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.models.schedule.UnavailableSlot
import com.example.seepawandroid.ui.screens.schedule.CELL_HEIGHT
import com.example.seepawandroid.ui.screens.schedule.SlotType
import com.example.seepawandroid.ui.screens.schedule.TimeSlotCell
import com.example.seepawandroid.ui.theme.AvailableSlotColor
import com.example.seepawandroid.ui.theme.EmptySlotColor
import com.example.seepawandroid.ui.theme.OwnReservationColor
import com.example.seepawandroid.ui.theme.ReservedSlotColor
import com.example.seepawandroid.ui.theme.UnavailableSlotColor


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