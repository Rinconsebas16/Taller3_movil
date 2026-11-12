package com.example.taller_3.utils

object Constants {
    // Firestore
    const val USERS_COLLECTION = "users"
    const val FIELD_NOMBRE = "nombre"
    const val FIELD_TELEFONO = "telefono"
    const val FIELD_PHOTO_URL = "photoUrl"
    const val FIELD_EN_LINEA = "enLinea"
    const val FIELD_LATITUD = "latitud"
    const val FIELD_LONGITUD = "longitud"

    // Storage
    const val PROFILE_IMAGES_FOLDER = "profile_images"
    const val PHOTO_MAX_SIZE_PX = 512
    const val PHOTO_JPEG_QUALITY = 80

    // Mapa
    const val MAX_ONLINE_USERS = 100L
    const val LOCATION_INTERVAL_MS = 5_000L
}
