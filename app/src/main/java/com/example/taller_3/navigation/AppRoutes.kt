package com.example.taller_3.navigation

/** Rutas (claves del back stack de NavDisplay). */
data object LoginRoute
data object RegisterRoute
data object HomeRoute
/**
 * El perfil recibe el uid del usuario como parametro de la ruta.
 * Es el equivalente en Compose a enviar un "extra" en un Intent:
 *   intent.putExtra(EXTRA_USER_ID, uid)  ->  ProfileRoute(uid = uid)
 */
data class ProfileRoute(val uid: String)
