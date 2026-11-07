package com.example.taller_3.model

/**
 * Datos del usuario guardados en Firestore (colección "users", documento = uid).
 * El email y el password los maneja Firebase Authentication;
 * el password NUNCA se guarda en la base de datos.
 * Los valores por defecto son necesarios para que Firestore pueda deserializar (toObject).
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",      // URL de la foto en Firebase Storage
    val online: Boolean = false,    // NO se pide en el registro
    val latitude: Double = 0.0,     // NO se pide en el registro
    val longitude: Double = 0.0     // NO se pide en el registro
)
