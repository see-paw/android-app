package com.example.seepawandroid.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Stateless date picker field component.
 *
 * Displays a text field with a calendar icon that opens a date picker dialog.
 *
 * @param value Currently selected date, or null if no date is selected
 * @param onDateSelected Callback invoked when user selects a date
 * @param label Label text for the field
 * @param modifier Modifier for styling
 * @param enabled Whether the field is enabled
 * @param supportingText Optional supporting text shown below the field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    value: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    isTestMode: Boolean = false, // Activate test mode parameter
    testDateProvider: (() -> LocalDate)? = null  // Data for tests
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
        onValueChange = { },
        label = { Text(label) },
        modifier = modifier,
        enabled = false,
        readOnly = true,
        trailingIcon = {
            IconButton(
                onClick = {
                    if (isTestMode && testDateProvider != null) {
                        // Modo teste: chama callback diretamente
                        onDateSelected(testDateProvider())
                    } else {
                        // Modo normal: abre dialog
                        showDialog = true
                    }
                },
                enabled = enabled,
                modifier = Modifier.testTag("birthDateIcon")
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Selecionar data"
                )
            }
        },
        supportingText = supportingText?.let { { Text(it) } }
    )

    // Dialog só abre em modo normal
    if (!isTestMode && showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                ?: Instant.now().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(selectedDate)
                        }
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }
}