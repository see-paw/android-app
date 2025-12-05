package com.example.seepawandroid.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A composable that displays a filter chip item.
 *
 * @param label The text to be displayed on the chip.
 * @param selected Whether the chip is currently selected.
 * @param onClick The callback to be invoked when the chip is clicked.
 * @param modifier The modifier to be applied to the chip.
 */
@Composable
fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface
        )
    )
}
