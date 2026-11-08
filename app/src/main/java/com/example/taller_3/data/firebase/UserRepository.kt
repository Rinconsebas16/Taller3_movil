package com.example.taller_3.data.firebase

import com.example.taller_3.model.User
import com.example.taller_3.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

/** Datos secundarios del usuario en Cloud Firestore (coleccion "users"). */
class UserRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = db.collection(Constants.USERS_COLLECTION)

    /** Se llama justo despues del registro. enLinea, latitud y longitud quedan en false / 0.0 / 0.0. */
    suspend fun createUser(user: User) {
        users.document(user.uid).set(
            user.copy(enLinea = false, latitud = 0.0, longitud = 0.0)
        ).await()
    }

    suspend fun getUser(uid: String): User? =
        users.document(uid).get().await().toObject(User::class.java)

    /** Escucha en tiempo real el documento del usuario. Recuerda llamar remove() al terminar. */
    fun observeUser(uid: String, onChange: (User?) -> Unit): ListenerRegistration =
        users.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            onChange(snapshot?.toObject(User::class.java))
        }

    suspend fun updateProfile(uid: String, nombre: String, telefono: String) {
        users.document(uid).update(
            mapOf(
                Constants.FIELD_NOMBRE to nombre.trim(),
                Constants.FIELD_TELEFONO to telefono.trim()
            )
        ).await()
    }

    suspend fun updatePhotoUrl(uid: String, photoUrl: String) {
        users.document(uid).update(Constants.FIELD_PHOTO_URL, photoUrl).await()
    }

    suspend fun setOnline(uid: String, enLinea: Boolean) {
        users.document(uid).update(Constants.FIELD_EN_LINEA, enLinea).await()
    }
}
