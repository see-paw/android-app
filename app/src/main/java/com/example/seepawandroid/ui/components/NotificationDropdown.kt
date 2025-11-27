package pt.ipp.estg.seepawandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seepawandroid.R
import com.example.seepawandroid.data.remote.dtos.notifications.ResNotificationDto
import com.example.seepawandroid.ui.viewmodels.NotificationDropdownUiState
import com.example.seepawandroid.utils.DateUtils
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import pt.ipp.estg.seepawandroid.ui.viewmodels.NotificationViewModel

/**
 * Dropdown component for displaying notifications.
 *
 * Fully stateless - all state managed by NotificationViewModel.
 * Displayed from AppTopBar notification bell icon.
 *
 * @param viewModel ViewModel managing notification state.
 * @param onDismiss Callback when dropdown should be dismissed.
 */
@Composable
fun NotificationDropdown(
    viewModel: NotificationViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.observeAsState(NotificationDropdownUiState.Loading)

    Surface(
        modifier = Modifier
            .width(360.dp)
            .heightIn(max = 500.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header
            NotificationDropdownHeader(
                unreadCount = (uiState as? NotificationDropdownUiState.Success)?.unreadCount ?: 0,
                onDismiss = onDismiss
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Content based on state
            when (val state = uiState) {
                is NotificationDropdownUiState.Loading -> {
                    LoadingContent()
                }
                is NotificationDropdownUiState.Success -> {
                    SuccessContent(
                        notifications = state.notifications,
                        currentPage = state.currentPage,
                        totalPages = state.totalPages,
                        onNotificationClick = { notificationId ->
                            viewModel.markAsRead(notificationId)
                        },
                        onNotificationDelete = { notificationId ->
                            viewModel.deleteNotification(notificationId)
                        },
                        onPreviousPage = { viewModel.previousPage() },
                        onNextPage = { viewModel.nextPage() }
                    )
                }
                is NotificationDropdownUiState.Empty,
                is NotificationDropdownUiState.Offline -> {
                    EmptyContent()
                }
                is NotificationDropdownUiState.Error -> {
                    ErrorContent(message = state.message)
                }
            }
        }
    }
}

/**
 * Header with title and unread count badge.
 */
@Composable
private fun NotificationDropdownHeader(
    unreadCount: Int,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.notifications),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (unreadCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Badge(
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close)
            )
        }
    }
}

/**
 * Loading state content.
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Success state content with notifications list and pagination.
 */
@Composable
private fun SuccessContent(
    notifications: List<ResNotificationDto>,
    currentPage: Int,
    totalPages: Int,
    onNotificationClick: (String) -> Unit,
    onNotificationDelete: (String) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Notifications list - FIXED: use fixed height instead of weight
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)  // ✅ Fixed height instead of weight(1f)
        ) {
            items(
                items = notifications,
                key = { it.id }
            ) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { onNotificationClick(notification.id) },
                    onDelete = { onNotificationDelete(notification.id) }
                )
            }
        }

        // Pagination controls (only show if more than 1 page)
        if (totalPages > 1) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            PaginationControls(
                currentPage = currentPage,
                totalPages = totalPages,
                onPreviousPage = onPreviousPage,
                onNextPage = onNextPage
            )
        }
    }
}

/**
 * Individual notification item with swipe-to-delete.
 */
@Composable
private fun NotificationItem(
    notification: ResNotificationDto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val deleteAction = SwipeAction(
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_notification),
                tint = Color.White
            )
        },
        background = MaterialTheme.colorScheme.error,
        onSwipe = onDelete
    )

    SwipeableActionsBox(
        endActions = listOf(deleteAction),
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            color = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Unread indicator
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DateUtils.formatTimestampRelative(notification.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Pagination controls with previous/next arrows.
 */
@Composable
private fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousPage,
            enabled = currentPage > 0
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Anterior"
            )
        }

        Text(
            text = "${currentPage + 1} / $totalPages",
            style = MaterialTheme.typography.bodyMedium
        )

        IconButton(
            onClick = onNextPage,
            enabled = currentPage < totalPages - 1
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Próximo"
            )
        }
    }
}

/**
 * Empty state content.
 */
@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_notifications),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error state content.
 */
@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}