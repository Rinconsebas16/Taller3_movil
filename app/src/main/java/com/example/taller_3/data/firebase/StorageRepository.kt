package com.example.taller_3.data.firebase

import com.example.taller_3.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await

/** Fotos de perfil en Firebase Storage: profile_images/{uid}.jpg */
class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    /** Sube la imagen (ya comprimida) y devuelve su URL de descarga. */
    suspend fun uploadProfilePhoto(uid: String, jpeg: ByteArray): String {
        val ref = storage.reference.child("${Constants.PROFILE_IMAGES_FOLDER}/$uid.jpg")
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        ref.putBytes(jpeg, metadata).await()
        return ref.downloadUrl.await().toString()
    }
}
