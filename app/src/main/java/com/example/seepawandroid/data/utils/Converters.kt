package com.example.seepawandroid.data.utils

import androidx.room.TypeConverter

/**
 * Room TypeConverters used to convert List<String> fields into a single String
 * and back. Room cannot store lists directly, so these converters ensure that
 * fields such as imageUrls in the Animal entity can be persisted and retrieved
 * correctly.
 */
class Converters {

    /**
     * Converts a List<String> into a comma-separated String for database storage.
     *
     * @param list The list of strings to convert.
     * @return A comma-separated string or an empty string if the list is null.
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    /**
     * Converts a stored comma-separated String back into a List<String>.
     *
     * @param data The string retrieved from the database.
     * @return A list of strings. Returns an empty list if the string is null or blank.
     */
    @TypeConverter
    fun toStringList(data: String?): List<String> {
        return data?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }
}