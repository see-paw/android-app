package com.example.seepawandroid.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R

/**
 * Animated progress bar showing the fostering support level of an animal.
 *
 * The bar animates from 0 to the target percentage when first displayed.
 * When fully fostered (100%), shows a celebratory gradient.
 *
 * @param currentSupportValue Current fostering support amount.
 * @param totalCost Total monthly cost of the animal.
 * @param modifier Optional modifier for the component.
 */
@Composable
fun FosteringProgressBar(
    currentSupportValue: Double,
    totalCost: Double,
    modifier: Modifier = Modifier
) {
    val targetProgress = if (totalCost > 0) {
        (currentSupportValue / totalCost).coerceIn(0.0, 1.0)
    } else {
        0.0
    }

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(targetProgress) {
        animatedProgress.animateTo(
            targetValue = targetProgress.toFloat(),
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    val isFullyFostered = targetProgress >= 1.0
    val percentageInt = (animatedProgress.value * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("fosteringProgressBar")
    ) {
        // Header with label and percentage
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.fostering_support_label),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (isFullyFostered) {
                    stringResource(R.string.fostering_fully_supported)
                } else {
                    stringResource(R.string.fostering_percentage, percentageInt)
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isFullyFostered) {
                    Color(0xFF4CAF50)
                } else {
                    MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.testTag("fosteringPercentageText")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .testTag("fosteringProgressBackground")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.value)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isFullyFostered) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF66BB6A),
                                    Color(0xFF4CAF50),
                                    Color(0xFF43A047)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    )
                    .testTag("fosteringProgressFill")
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Amount text
        Text(
            text = stringResource(
                R.string.fostering_amount_progress,
                currentSupportValue,
                totalCost
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("fosteringAmountText")
        )
    }
}