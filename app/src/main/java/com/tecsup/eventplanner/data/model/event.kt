package com.tecsup.eventplanner.data.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val userId: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "date" to date,
            "description" to description,
            "userId" to userId,
            "createdAt" to createdAt
        )
    }
}