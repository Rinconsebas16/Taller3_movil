package com.example.taller_3.data.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/** Firebase Authentication: email y password. */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /** Crea la cuenta y devuelve el uid del nuevo usuario. */
    suspend fun register(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        return result.user?.uid ?: error("No se pudo crear el usuario")
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    /**
     * Cambio seguro de contraseña: primero se re-autentica con la contraseña actual
     * (Firebase lo exige si el login no es reciente) y luego se llama a updatePassword.
     */
    suspend fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser ?: error("No hay sesion activa")
        val email = user.email ?: error("El usuario no tiene email")
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    /** Borra la cuenta recién creada (se usa si falla guardar sus datos en Firestore). */
    suspend fun deleteCurrentUser() {
        try {
            auth.currentUser?.delete()?.await()
        } catch (_: Exception) {
        }
        auth.signOut()
    }

    fun signOut() = auth.signOut()
}
