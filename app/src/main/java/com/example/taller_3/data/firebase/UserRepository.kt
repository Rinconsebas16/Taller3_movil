package com.example.taller_3.data.firebase

import com.example.taller_3.model.User
import com.example.taller_3.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Datos secundarios del usuario en Cloud Firestore (colección "users").
class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection(Constants.USERS_COLLECTION)

    // Registro y perfil

    suspend fun createUser(user: User) {
        usersCollection.document(user.uid).set(user).await()
    }

    suspend fun updatePhotoUrl(uid: String, photoUrl: String) {
        usersCollection.document(uid).update(Constants.FIELD_PHOTO_URL, photoUrl).await()
    }

    suspend fun setOnline(uid: String, enLinea: Boolean) {
        usersCollection.document(uid).update(Constants.FIELD_EN_LINEA, enLinea).await()
    }

    suspend fun updateProfile(uid: String, nombre: String, telefono: String) {
        val updates = mapOf(
            Constants.FIELD_NOMBRE to nombre.trim(),
            Constants.FIELD_TELEFONO to telefono.trim()
        )
        usersCollection.document(uid).update(updates).await()
    }

    fun observeUser(uid: String, onUpdate: (User?) -> Unit): ListenerRegistration {
        return usersCollection.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                onUpdate(null)
                return@addSnapshotListener
            }
            val user = snapshot?.toObject(User::class.java)
            onUpdate(user)
        }
    }

    // Mapa y ubicación en tiempo real

    suspend fun updateUserLocation(uid: String, lat: Double, lng: Double, enLinea: Boolean) {
        val updates = mapOf(
            Constants.FIELD_LATITUD to lat,
            Constants.FIELD_LONGITUD to lng,
            Constants.FIELD_EN_LINEA to enLinea
        )
        usersCollection.document(uid).update(updates).await()
    }

    /**
     * Usuarios en línea en tiempo real (addSnapshotListener), máximo 100.
     * Cuando alguien se desconecta deja de aparecer en la lista y el mapa lo quita.
     */
    fun getActiveUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = usersCollection
            .whereEqualTo(Constants.FIELD_EN_LINEA, true)
            .limit(Constants.MAX_ONLINE_USERS)
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
