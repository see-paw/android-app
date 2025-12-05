package com.example.seepawandroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R

/**
 * Stateless dialog for selecting fostering amount.
 *
 * All state is managed by the ViewModel and passed as parameters.
 * The component only renders UI and notifies changes via callbacks.
 *
 * @param animalName Name of the animal being fostered.
 * @param selectedAmount Currently selected amount (null = none, -1.0 = custom).
 * @param customAmount Custom amount input value.
 * @param showError Whether to show validation error.
 * @param onDismiss Callback when dialog is dismissed.
 * @param onAmountSelected Callback when a predefined or custom amount is selected.
 * @param onCustomAmountChanged Callback when custom amount input changes.
 * @param onConfirm Callback when confirm button is clicked.
 */
@Composable
fun FosteringAmountDialog(
    animalName: String,
    selectedAmount: Double?,
    customAmount: String,
    showError: Boolean,
    onDismiss: () -> Unit,
    onAmountSelected: (Double?) -> Unit,
    onCustomAmountChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    val predefinedAmounts = listOf(10.0, 15.0, 20.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.fostering_dialog_title, animalName))
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.fostering_dialog_description),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Predefined amounts
                predefinedAmounts.forEach { amount ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAmount == amount,
                            onClick = { onAmountSelected(amount) },
                            modifier = Modifier.testTag("radioButton_$amount")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.fostering_amount_option, amount),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom amount option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAmount == -1.0,
                        onClick = { onAmountSelected(-1.0) },
                        modifier = Modifier.testTag("radioButton_custom")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.fostering_custom_amount),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Custom amount input
                if (selectedAmount == -1.0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customAmount,
                        onValueChange = onCustomAmountChanged,
                        label = { Text(stringResource(R.string.fostering_custom_amount_label)) },
                        placeholder = { Text("25.00") },
                        isError = showError,
                        supportingText = if (showError) {
                            { Text(stringResource(R.string.fostering_invalid_amount)) }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customAmountInput")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("fosteringConfirmButton")
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("fosteringCancelButton")
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        modifier = Modifier.testTag("fosteringAmountDialog")
    )
}