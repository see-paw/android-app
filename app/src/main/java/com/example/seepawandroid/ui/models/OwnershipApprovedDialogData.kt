package pt.ipp.estg.seepawandroid.ui.models

/**
 * Data class holding information for the ownership approved dialog.
 *
 * @property notificationId ID of the notification to mark as read when dismissed.
 * @property userName Name of the user who got approval.
 * @property animalName Name of the adopted animal.
 * @property animalImageUrl URL of the animal's principal image.
 */
data class OwnershipApprovedDialogData(
    val notificationId: String,
    val userName: String,
    val animalName: String,
    val animalImageUrl: String
)