package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * A modal dialog for confirming an activity.
 *
 * @param slot The slot for the activity.
 * @param animalName The name of the animal.
 * @param onConfirm A callback that is invoked when the confirmation button is clicked.
 * @param onCancel A callback that is invoked when the dialog is dismissed.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun ConfirmActivityModal(
    slot: AvailableSlot,
    animalName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        .withLocale(Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val formattedDate = slot.start.toLocalDate().format(dateFormatter)
    val startTime = slot.start.toLocalTime().format(timeFormatter)
    val endTime = slot.end.toLocalTime().format(timeFormatter)

    AlertDialog(
        onDismissRequest = onCancel,
        modifier = modifier.testTag("confirmActivityModal"),
        title = {
            Text(
                text = stringResource(R.string.modal_confirm_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.modal_confirm_message, animalName),
                    style = MaterialTheme.typography.bodyLarge
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DateTimeInfoRow(
                            icon = Icons.Default.CalendarToday,
                            label = stringResource(R.string.modal_date_label),
                            value = formattedDate
                        )
                        DateTimeInfoRow(
                            icon = Icons.Default.Schedule,
                            label = stringResource(R.string.modal_time_label),
                            value = "$startTime - $endTime"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("confirmModalButton"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.modal_confirm_button))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.testTag("cancelModalButton")
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * A modal dialog for displaying an error.
 *
 * @param message The error message to be displayed.
 * @param onConfirm A callback that is invoked when the confirmation button is clicked.
 * @param onDismiss A callback that is invoked when the dialog is dismissed.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun ErrorModal(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("errorModal"),
        icon = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = stringResource(R.string.modal_error_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("errorModalMessage")
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("errorModalRetryButton"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.modal_error_retry))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("errorModalCancelButton")
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * A modal dialog for displaying a success message after confirming an activity.
 *
 * @param animalName The name of the animal.
 * @param date The formatted date of the activity.
 * @param time The formatted time range of the activity.
 * @param onDismiss A callback that is invoked when the dialog is dismissed.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun SuccessModal(
    animalName: String,
    date: String,
    time: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("successModal"),
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.modal_success_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.modal_success_message, animalName),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("successModalMessage")
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DateTimeInfoRow(
                            icon = Icons.Default.CalendarToday,
                            label = stringResource(R.string.modal_date_label),
                            value = date
                        )
                        DateTimeInfoRow(
                            icon = Icons.Default.Schedule,
                            label = stringResource(R.string.modal_time_label),
                            value = time
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("successModalButton"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@Composable
private fun DateTimeInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}