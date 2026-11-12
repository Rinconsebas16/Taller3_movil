package com.example.taller_3.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taller_3.data.firebase.AuthRepository
import com.example.taller_3.data.firebase.StorageRepository
import com.example.taller_3.data.firebase.UserRepository
import com.example.taller_3.model.User
import com.example.taller_3.utils.FirebaseErrors
import com.example.taller_3.utils.ImageUtils
import com.example.taller_3.utils.Validators
import kotlinx.coroutines.launch

/** Login, registro y cierre de sesión. */
class AuthViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val userRepo: UserRepository = UserRepository(),
    private val storageRepo: StorageRepository = StorageRepository()
) : ViewModel() {

    // Campos del formulario
    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var telefono by mutableStateOf("")
    var photo by mutableStateOf<Bitmap?>(null)

    var loading by mutableStateOf(false)
        private set

    var errorRes by mutableStateOf<Int?>(null)
        private set

    fun clearError() {
        errorRes = null
    }

    private fun showError(e: Exception) {
        errorRes = FirebaseErrors.toMessage(e)
    }

    fun login(onSuccess: () -> Unit) {
        val error = Validators.email(email) ?: Validators.password(password)
        if (error != null) {
            errorRes = error
            return
        }
        viewModelScope.launch {
            loading = true
            clearError()
            try {
                authRepo.login(email, password)
                clearForm()
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "login", e)
                showError(e)
            } finally {
                loading = false
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val error = Validators.registro(nombre, email, password, telefono)
        if (error != null) {
            errorRes = error
            return
        }
        viewModelScope.launch {
            loading = true
            clearError()
            try {
                // 1. Email y password -> Firebase Auth
                val uid = authRepo.register(email, password)
                // 2. Datos secundarios -> Firestore (enLinea=false, latitud=0.0, longitud=0.0)
                //    Si falla, se borra la cuenta de Auth para poder reintentar con el mismo correo.
                try {
                    userRepo.createUser(
                        User(
                            uid = uid,
                            nombre = nombre.trim(),
                            // Se usa el email que guardó Auth (normalizado) para que coincida con las reglas
                            email = authRepo.currentUser?.email ?: email.trim(),
                            telefono = telefono.trim()
                        )
                    )
                } catch (e: Exception) {
                    authRepo.deleteCurrentUser()
                    throw e
                }
                // 3. Foto (opcional) -> Storage, y su URL al documento del usuario
                photo?.let { bitmap ->
                    try {
                        val url = storageRepo.uploadProfilePhoto(uid, ImageUtils.compress(bitmap))
                        userRepo.updatePhotoUrl(uid, url)
                    } catch (e: Exception) {
                        // La cuenta ya existe: si falla la foto, se puede subir luego desde el perfil
                        Log.e(TAG, "upload photo", e)
                    }
                }
                clearForm()
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "register", e)
                showError(e)
            } finally {
                loading = false
            }
        }
    }

    /**
     * Cierra la sesión: primero marca enLinea=false en la base de datos y después llama a signOut().
     * Antes de llamarla, el mapa detiene el rastreo de ubicación.
     */
    fun logout(onDone: () -> Unit) {
        val uid = authRepo.currentUser?.uid
        viewModelScope.launch {
            loading = true
            try {
                if (uid != null) userRepo.setOnline(uid, false)
            } catch (e: Exception) {
                Log.e(TAG, "setOnline(false)", e)
            } finally {
                authRepo.signOut()
                clearForm()
                loading = false
                onDone()
            }
        }
    }

    private fun clearForm() {
        nombre = ""
        email = ""
        password = ""
        telefono = ""
        photo = null
        clearError()
    }

    private companion object {
        const val TAG = "AuthViewModel"
    }
}
