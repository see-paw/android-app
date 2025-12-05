package com.example.seepawandroid.data.remote.dtos.images

/**
 * DTO representing metadata for an image associated with an animal.
 *
 * @property id Unique identifier of the image record.
 * @property publicId Cloud provider image ID.
 * @property isPrincipal Whether this image is the designated main image.
 * @property url Public URL of the image.
 * @property description Optional description of the image.
 */
data class ResImageDto(
    val id: String,
    val publicId: String,
    val isPrincipal: Boolean,
    val url: String,
    val description: String
)
