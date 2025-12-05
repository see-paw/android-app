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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.seepawandroid.data.remote.dtos.animals.ResOwnedAnimalDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

/**
 * Internal data model for Mock Receipts.
 * Used only within this UI component, not part of the domain layer.
 */
private data class MockReceipt(
    val id: String,
    val animalName: String,
    val date: LocalDate,
    val items: List<MockReceiptItem>
) {
    val total: Double get() = items.sumOf { it.price }
}

/**
 * Internal data model for a single line item in a receipt.
 */
private data class MockReceiptItem(
    val description: String,
    val price: Double
)

/**
 * Stateless component that generates and displays mock monthly receipts
 * based on the user's actual owned animals.
 *
 * It generates 6 months of history for each animal in the provided list.
 *
 * @param ownedAnimals List of real animals owned by the user.
 */
@Composable
fun OwnershipMockReceipts(
    ownedAnimals: List<ResOwnedAnimalDto>
) {
    // State to track which receipt is currently selected for the detail dialog
    var selectedReceipt by remember { mutableStateOf<MockReceipt?>(null) }

    // Generate receipts only once when the animal list changes
    // This ensures data persists across recompositions but refreshes if data updates
    val receipts = remember(ownedAnimals) {
        generateMockReceipts(ownedAnimals)
    }

    if (receipts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sem histórico de despesas.", // "No expense history"
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(receipts) { receipt ->
                ReceiptCard(
                    receipt = receipt,
                    onClick = { selectedReceipt = receipt }
                )
            }
        }
    }

    // Detail Dialog (Pop-up)
    if (selectedReceipt != null) {
        ReceiptDetailDialog(
            receipt = selectedReceipt!!,
            onDismiss = { selectedReceipt = null }
        )
    }
}

/**
 * A summary card for a single receipt displayed in the list.
 */
@Composable
private fun ReceiptCard(
    receipt: MockReceipt,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Receipt Info
                Column {
                    Text(
                        text = receipt.animalName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Format date (e.g., "November 2023")
                    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "PT"))
                    Text(
                        text = receipt.date.format(formatter).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Total Price
            Text(
                text = String.format("%.2f €", receipt.total),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

/**
 * Detailed Dialog showing all items in the invoice.
 */
@Composable
private fun ReceiptDetailDialog(
    receipt: MockReceipt,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
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
                        text = "Fatura Mensal", // "Monthly Invoice"
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Metadata
                Text(
                    text = "Referente a: ${receipt.animalName}",
                    style = MaterialTheme.typography.bodyLarge
                )

                val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("pt", "PT"))
                Text(
                    text = "Data: ${receipt.date.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Line Items
                Text(
                    text = "Detalhes:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                receipt.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = String.format("%.2f €", item.price),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                        text = String.format("%.2f €", receipt.total),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

// ========== MOCK DATA GENERATION LOGIC ==========

/**
 * Generates a list of mock receipts for the provided animals.
 * Creates one receipt per month for the last 6 months.
 */
private fun generateMockReceipts(animals: List<ResOwnedAnimalDto>): List<MockReceipt> {
    val receipts = mutableListOf<MockReceipt>()
    val today = LocalDate.now()

    // Iterate through each animal the user owns
    animals.forEach { animal ->
        // Generate 6 months of history
        for (i in 0 until 6) {
            val date = today.minusMonths(i.toLong())

            // Create random but realistic items
            val items = mutableListOf<MockReceiptItem>()

            // 1. Food (Always present)
            items.add(MockReceiptItem("Ração Premium", Random.nextDouble(20.0, 50.0)))

            // 2. Vet Appointment (Random chance)
            if (Random.nextBoolean()) {
                items.add(MockReceiptItem("Consulta Veterinária", 35.00))
            }

            // 3. Medication (Random chance)
            if (Random.nextBoolean()) {
                items.add(MockReceiptItem("Desparasitante", 12.50))
            }

            // 4. Toys (Low random chance)
            if (Random.nextInt(10) > 7) {
                items.add(MockReceiptItem("Brinquedo Novo", 8.99))
            }

            receipts.add(
                MockReceipt(
                    id = "${animal.id}_$i",
                    animalName = animal.animalName,
                    date = date,
                    items = items
                )
            )
        }
    }

    // Sort by date (newest first)
    return receipts.sortedByDescending { it.date }
}