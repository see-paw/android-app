package com.example.seepawandroid.ui.screens.schedule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.seepawandroid.data.models.schedule.AvailableSlot

@Composable
fun ConfirmActivityModal(
    slot: AvailableSlot,
    animalName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text("Confirmar atividade com $animalName?")
        },
        text = {
            Column {
                Text("Dia: ${slot.start.toLocalDate()}")
                Text("Hora: ${slot.start.toLocalTime()} — ${slot.end.toLocalTime()}")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        },
        modifier = modifier
    )
}

@Composable
fun ErrorModal(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ocorreu um erro") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Voltar a tentar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Voltar atrás")
            }
        },
        modifier = modifier
    )
}