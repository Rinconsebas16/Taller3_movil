package com.example.taller_3.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taller_3.R
import com.example.taller_3.data.firebase.AuthRepository
import com.example.taller_3.data.firebase.StorageRepository
import com.example.taller_3.data.firebase.UserRepository
import com.example.taller_3.model.User
import com.example.taller_3.utils.FirebaseErrors
import com.example.taller_3.utils.ImageUtils
import com.example.taller_3.utils.Validators
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

/** Ver y editar el perfil: nombre y telefono (Firestore), contrasena (Auth) y foto (Storage). */
class ProfileViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val userRepo: UserRepository = UserRepository(),
    private val storageRepo: StorageRepository = StorageRepository()
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    // Campos editables
    var nombre by mutableStateOf("")
    var telefono by mutableStateOf("")
    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var loading by mutableStateOf(false)
        private set
    var uploadingPhoto by mutableStateOf(false)
        private set

    /** Mensaje para el Snackbar (exito o error). */
    var messageRes by mutableStateOf<Int?>(null)
        private set

    private var listener: ListenerRegistration? = null

    /** Empieza a escuchar el documento del usuario. Llamar al entrar a la pantalla. */
    fun start() {
        val uid = authRepo.currentUser?.uid ?: return
        if (listener != null) return
        listener = userRepo.observeUser(uid) { updated ->
            val first = user == null
            user = updated
            // Solo se rellenan los campos la primera vez, para no pisar lo que el usuario esta escribiendo
            if (first && updated != null) {
                nombre = updated.nombre
                telefono = updated.telefono
            }
        }
    }

    /** Deja de escuchar. Llamar al salir de la pantalla. */
    fun stop() {
        listener?.remove()
        listener = null
        user = null
        currentPassword = ""
        newPassword = ""
        confirmPassword = ""
    }

    fun messageShown() {
        messageRes = null
    }

    fun saveProfile() {
        val uid = authRepo.currentUser?.uid ?: return
        val error = Validators.nombre(nombre) ?: Validators.telefono(telefono)
        if (error != null) {
            messageRes = error
            return
        }
        viewModelScope.launch {
            loading = true
            try {
                userRepo.updateProfile(uid, nombre, telefono)
                messageRes = R.string.msg_profile_updated
            } catch (e: Exception) {
                Log.e(TAG, "saveProfile", e)
                messageRes = FirebaseErrors.toMessage(e)
            } finally {
                loading = false
            }
        }
    }

    fun changePassword() {
        val error = when {
            currentPassword.isBlank() -> R.string.error_empty_fields
            else -> Validators.password(newPassword)
                ?: if (newPassword != confirmPassword) R.string.error_password_mismatch else null
        }
        if (error != null) {
            messageRes = error
            return
        }
        viewModelScope.launch {
            loading = true
            try {
                authRepo.changePassword(currentPassword, newPassword)
                currentPassword = ""
                newPassword = ""
                confirmPassword = ""
                messageRes = R.string.msg_password_updated
            } catch (e: Exception) {
                Log.e(TAG, "changePassword", e)
                messageRes = FirebaseErrors.toMessage(e)
            } finally {
                loading = false
            }
        }
    }

    fun updatePhoto(bitmap: Bitmap) {
        val uid = authRepo.currentUser?.uid ?: return
        viewModelScope.launch {
            uploadingPhoto = true
            try {
                val url = storageRepo.uploadProfilePhoto(uid, ImageUtils.compress(bitmap))
                userRepo.updatePhotoUrl(uid, url)
                messageRes = R.string.msg_photo_updated
            } catch (e: Exception) {
                Log.e(TAG, "updatePhoto", e)
                messageRes = FirebaseErrors.toMessage(e)
            } finally {
                uploadingPhoto = false
            }
        }
    }

    override fun onCleared() {
        listener?.remove()
    }

    private companion object {
        const val TAG = "ProfileViewModel"
    }
}
