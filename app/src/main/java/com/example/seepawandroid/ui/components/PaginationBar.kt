package com.example.seepawandroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pagination bar component used to navigate through pages of data.
 *
 * Displays:
 * - A "<" button for previous page
 * - Numbered page selector buttons
 * - A ">" button for next page
 *
 * Now supports a modifier parameter, allowing external customization
 * (e.g. testTag for UI tests, padding adjustments, etc.)
 *
 * @param currentPage The currently active page.
 * @param totalPages Total number of available pages.
 * @param onPrev Called when the previous-page button is clicked.
 * @param onNext Called when the next-page button is clicked.
 * @param onSelect Called when the user selects a specific page number.
 * @param modifier External modifier for layout, test tags, etc.
 */
@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalPages <= 1) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrev,
            enabled = currentPage > 1
        ) {
            Text("<")
        }

        for (page in 1..totalPages) {
            TextButton(onClick = { onSelect(page) }) {
                Text(
                    text = page.toString(),
                    style = if (page == currentPage)
                        MaterialTheme.typography.titleMedium
                    else
                        MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(
            onClick = onNext,
            enabled = currentPage < totalPages
        ) {
            Text(">")
        }
    }
}
