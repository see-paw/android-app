package com.example.seepawandroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.models.MockPaymentData

/**
 * Stateless dialog showing mock payment data for fostering.
 *
 * Displays auto-filled payment information (account number, holder name, CVV).
 * User only needs to click OK to proceed with fostering creation.
 *
 * @param animalName Name of the animal being fostered.
 * @param amount Monthly fostering amount.
 * @param paymentData Mock payment data to display.
 * @param onDismiss Callback when dialog is dismissed.
 * @param onConfirm Callback when OK button is clicked.
 */
@Composable
fun FosteringPaymentMockDialog(
    animalName: String,
    amount: Double,
    paymentData: MockPaymentData,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.fostering_payment_title))
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.fostering_payment_description, animalName, amount),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Account number (read-only)
                OutlinedTextField(
                    value = paymentData.accountNumber,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.fostering_payment_account)) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("accountNumberField")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Holder name (read-only)
                OutlinedTextField(
                    value = paymentData.holderName,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.fostering_payment_holder)) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("holderNameField")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // CVV (read-only)
                OutlinedTextField(
                    value = paymentData.cvv,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.fostering_payment_cvv)) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cvvField")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("confirmPaymentButton")
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancelPaymentButton")
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        modifier = Modifier.testTag("fosteringPaymentMockDialog")
    )
}