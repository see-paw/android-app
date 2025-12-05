package com.example.seepawandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.seepawandroid.data.remote.dtos.fosterings.ResActiveFosteringDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Internal data model for Mock Fostering Receipts.
 */
private data class MockFosteringReceipt(
    val id: String,
    val animalName: String,
    val date: LocalDate,
    val amount: Double
)

/**
 * Stateless component that generates and displays mock monthly fostering receipts.
 *
 * Generates receipts from startDate until current date (one per month).
 *
 * @param fosterings List of active fosterings.
 */
@Composable
fun FosteringMockReceipts(
    fosterings: List<ResActiveFosteringDto>
) {
    var selectedReceipt by remember { mutableStateOf<MockFosteringReceipt?>(null) }

    val receipts = remember(fosterings) {
        generateMockFosteringReceipts(fosterings)
    }

    if (receipts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("emptyReceiptsState"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sem recibos de apadrinhamento.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("fosteringReceiptsContent"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(receipts) { receipt ->
                FosteringReceiptCard(
                    receipt = receipt,
                    onClick = { selectedReceipt = receipt }
                )
            }
        }
    }

    // Detail Dialog
    if (selectedReceipt != null) {
        FosteringReceiptDetailDialog(
            receipt = selectedReceipt!!,
            onDismiss = { selectedReceipt = null }
        )
    }
}

/**
 * A summary card for a single fostering receipt.
 */
@Composable
private fun FosteringReceiptCard(
    receipt: MockFosteringReceipt,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("receiptCard_${receipt.id}"),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Receipt Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Receipt Info
                Column {
                    Text(
                        text = receipt.animalName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "PT"))
                    Text(
                        text = receipt.date.format(formatter).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Amount
            Text(
                text = String.format("%.2f €", receipt.amount),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

/**
 * Detailed Dialog showing fostering receipt information.
 */
@Composable
private fun FosteringReceiptDetailDialog(
    receipt: MockFosteringReceipt,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag("receiptDetailDialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recibo de Apadrinhamento",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("closeReceiptDialogButton")
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Metadata
                Text(
                    text = "Animal: ${receipt.animalName}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )

                val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("pt", "PT"))
                Text(
                    text = "Data: ${receipt.date.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Line Item
                Text(
                    text = "Detalhes:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Apadrinhamento Mensal",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = String.format("%.2f €", receipt.amount),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Footer / Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = String.format("%.2f €", receipt.amount),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    }
}

// ========== MOCK DATA GENERATION LOGIC ==========

/**
 * Generates mock receipts for each fostering, one per month from startDate to now.
 */
private fun generateMockFosteringReceipts(fosterings: List<ResActiveFosteringDto>): List<MockFosteringReceipt> {
    val receipts = mutableListOf<MockFosteringReceipt>()
    val today = LocalDate.now()

    fosterings.forEach { fostering ->
        // Parse startDate (ISO format: "2025-11-25T09:21:16.477838")
        val startDate = try {
            LocalDate.parse(fostering.startDate.substring(0, 10))
        } catch (e: Exception) {
            LocalDate.now()
        }

        // Generate receipts from startDate until today (one per month)
        var currentDate = startDate
        var receiptIndex = 0

        while (currentDate.isBefore(today) || currentDate.isEqual(today)) {
            receipts.add(
                MockFosteringReceipt(
                    id = "${fostering.animalName}_$receiptIndex",
                    animalName = fostering.animalName,
                    date = currentDate,
                    amount = fostering.amount
                )
            )

            currentDate = currentDate.plusMonths(1)
            receiptIndex++
        }
    }

    // Sort by date (newest first)
    return receipts.sortedByDescending { it.date }
}