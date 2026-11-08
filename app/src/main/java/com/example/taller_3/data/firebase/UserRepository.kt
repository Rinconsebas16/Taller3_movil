package com.example.taller_3.data.firebase

import com.example.taller_3.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Datos secundarios del usuario en Cloud Firestore (coleccion "users").
class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // Métodos correspondientes a (Auth, Perfil y Registro)

    suspend fun createUser(user: User) {
        usersCollection.document(user.uid).set(user).await()
    }

    suspend fun updatePhotoUrl(uid: String, photoUrl: String) {
        usersCollection.document(uid).update("photoUrl", photoUrl).await()
    }

    suspend fun setOnline(uid: String, enLinea: Boolean) {
        usersCollection.document(uid).update("enLinea", enLinea).await()
    }

    suspend fun updateProfile(uid: String, nombre: String, telefono: String) {
        val updates = mapOf(
            "nombre" to nombre,
            "telefono" to telefono
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

    //  Métodos correspondientes a (Mapa, Rastreo y Concurrencia)

    suspend fun updateUserLocation(uid: String, lat: Double, lng: Double, enLinea: Boolean) {
        val updates = mapOf(
            "latitud" to lat,
            "longitud" to lng,
            "enLinea" to enLinea
        )
        usersCollection.document(uid).update(updates).await()
    }

    suspend fun updateOnlineStatus(uid: String, enLinea: Boolean) {
        setOnline(uid, enLinea)
    }

    fun getActiveUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = usersCollection.addSnapshotListener { snapshot, error ->
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