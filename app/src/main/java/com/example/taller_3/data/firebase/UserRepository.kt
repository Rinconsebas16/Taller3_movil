package com.example.taller_3.data.firebase

import com.example.taller_3.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Datos secundarios del usuario en Cloud Firestore (coleccion "users"). */
class UserRepository{
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /** Se llama justo despues del registro. enLinea, latitud y longitud quedan en false / 0.0 / 0.0. */
    suspend fun updateUserLocation(uid: String, lat: Double, lng: Double, enLinea: Boolean) {
        val updates = mapOf(
            "latitud" to lat,
            "longitud" to lng,
            "enLinea" to enLinea
        )
        db.collection("users").document(uid).update(updates).await()
    }

    suspend fun updateOnlineStatus(uid: String, enLinea: Boolean) {
        db.collection("users").document(uid).update("enLinea", enLinea).await()
    }

    // Escucha en tiempo real de todos los usuarios
    fun getActiveUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(User::class.java)
                } ?: emptyList()

                trySend(users)
            }
        awaitClose { listener.remove() }
    }
}