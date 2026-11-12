package com.example.taller_3.model

/**
 * Documento del usuario en Firestore: colección "users", id del documento = uid.
 *
 * - El email y el password los maneja Firebase Authentication. El password NUNCA se guarda aquí.
 * - enLinea, latitud y longitud NO se piden en el registro: se inicializan en false / 0.0 / 0.0.
 * - Se usan "var" con valores por defecto para que Firestore pueda crear el objeto con toObject().
 */
data class User(
    var uid: String = "",
    var nombre: String = "",
    var email: String = "",
    var telefono: String = "",
    var photoUrl: String = "",
    var enLinea: Boolean = false,
    var latitud: Double = 0.0,
    var longitud: Double = 0.0
)
