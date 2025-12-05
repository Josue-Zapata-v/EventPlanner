package com.tecsup.eventplanner.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tecsup.eventplanner.data.model.Event
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val eventsCollection = firestore.collection("events")

    fun getEventsFlow(): Flow<List<Event>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val subscription = eventsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    Event(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        date = doc.getString("date") ?: "",
                        description = doc.getString("description") ?: "",
                        userId = doc.getString("userId") ?: "",
                        createdAt = doc.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now()
                    )
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun createEvent(event: Event): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
            val eventWithUser = event.copy(userId = userId)
            eventsCollection.add(eventWithUser.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(eventId: String, event: Event): Result<Unit> {
        return try {
            eventsCollection.document(eventId).update(event.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventsCollection.document(eventId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}