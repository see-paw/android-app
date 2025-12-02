package com.example.seepawandroid.ui.screens.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.screens.schedule.components.ConfirmActivityModal
import com.example.seepawandroid.ui.screens.schedule.components.ErrorModal
import com.example.seepawandroid.ui.screens.schedule.components.SchedulingContent

/**
 * A composable that displays the scheduling screen.
 *
 * @param animalId The ID of the animal for which the schedule is being displayed.
 * @param onNavigateBack A callback that is invoked when the user clicks the back button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulingScreen(
    animalId: String,
    onNavigateBack: () -> Unit,
) {
    val viewModel: SchedulingViewModel = hiltViewModel()

    LaunchedEffect(animalId) {
        viewModel.loadSchedule(animalId)
    }

    val uiState by viewModel.uiState.observeAsState(ScheduleUiState.Loading)
    val modalState by viewModel.modalUiState.observeAsState(ModalUiState.Hidden)

    Scaffold(
        modifier = Modifier.testTag("schedulingScreen"),
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is ScheduleUiState.Success) {
                        Text(stringResource(R.string.scheduling_title, (uiState as ScheduleUiState.Success).schedule.animalName))
                    } else {
                        Text(stringResource(R.string.scheduling_title, ""))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("backButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ScheduleUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("schedulingLoadingIndicator")
                    )
                }

                is ScheduleUiState.Success -> {
                    SchedulingContent(
                        schedule = state.schedule,
                        onPrevWeek = {
                            viewModel.loadPrevWeek(
                                state.schedule.animalId,
                                state.schedule.weekStartDate
                            )
                        },
                        onNextWeek = {
                            viewModel.loadNextWeek(
                                state.schedule.animalId,
                                state.schedule.weekStartDate
                            )
                        },
                        onSelectSlotCell = { slot ->
                            viewModel.onSlotClick(
                                slot,
                                state.schedule.animalId,
                                state.schedule.animalName
                            )
                        },
                        canNavigatePrevious = viewModel.canNavigateToPreviousWeek(state.schedule.weekStartDate),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("schedulingContent")
                    )
                }

                is ScheduleUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadSchedule(state.animalId, state.startDate) },
                        modifier = Modifier
                            .padding(16.dp)
                            .testTag("schedulingErrorContent")
                    )
                }
            }

            when (val state = modalState) {
                is ModalUiState.Confirm -> {
                    ConfirmActivityModal(
                        slot = state.slot,
                        animalName = state.animalName,
                        onConfirm = { viewModel.confirmSlot(state.slot, state.animalId, state.animalName)},
                        onCancel = { viewModel.cancelSlot() }
                    )
                }
                is ModalUiState.Error -> {
                    ErrorModal(
                        message = state.message,
                        onConfirm = { viewModel.confirmSlot(state.slot, state.animalId, state.animalName) },
                        onDismiss = { viewModel.cancelSlot() }
                    )
                }

                ModalUiState.Hidden -> Unit
                ModalUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("modalLoadingIndicator")
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.testTag("schedulingErrorMessage")
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag("schedulingRetryButton")
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}
