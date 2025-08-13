package org.example.app

/**
 * Represents a single note entity in the local database.
 */
// PUBLIC_INTERFACE
data class Note(
    val id: Long?,
    val title: String,
    val content: String,
    val updatedAt: Long
)
