package pt.ipp.estg.seepawandroid.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.seepawandroid.R

/**
 * Dialog shown when an ownership request is approved.
 *
 * Displays:
 * - "Obrigada [UserName]!"
 * - "Ansiosa por te conhecer"
 * - Animal's principal image
 * - Large pink heart decoration
 * - Close button (X)
 *
 * Fully stateless - controlled by parent.
 *
 * @param userName Name of the user who got approved.
 * @param animalName Name of the adopted animal.
 * @param animalImageUrl URL of the animal's principal image.
 * @param onDismiss Callback when dialog is dismissed.
 */
@Composable
fun OwnershipApprovedDialog(
    userName: String,
    animalName: String,
    animalImageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFFF3E0) // Light warm background
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Title: "Obrigada [UserName]!"
                    Text(
                        text = stringResource(R.string.ownership_approved_title, userName),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle: "Ansiosa por te conhecer"
                    Text(
                        text = stringResource(R.string.ownership_approved_subtitle),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF5D4037),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Animal image
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(animalImageUrl),
                            contentDescription = animalName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Large pink heart
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFFF8BBD0) // Light pink
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Close button (X) - top right corner
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color(0xFF37474F)
                    )
                }
            }
        }
    }
}