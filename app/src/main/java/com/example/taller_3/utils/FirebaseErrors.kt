package com.example.taller_3.utils

import com.example.taller_3.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException

/** Traduce las excepciones de Firebase a un mensaje de @string para el usuario. */
object FirebaseErrors {

    fun toMessage(e: Throwable): Int = when (e) {
        is FirebaseAuthWeakPasswordException -> R.string.error_short_password
        is FirebaseAuthUserCollisionException -> R.string.error_email_in_use
        is FirebaseAuthInvalidUserException -> R.string.error_wrong_credentials
        is FirebaseAuthInvalidCredentialsException -> R.string.error_wrong_credentials
        is FirebaseNetworkException -> R.string.error_network
        is FirebaseAuthException ->
            if (e.errorCode == "ERROR_OPERATION_NOT_ALLOWED") R.string.error_auth_disabled
            else R.string.error_generic
        is FirebaseFirestoreException -> when (e.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> R.string.error_permission_denied
            FirebaseFirestoreException.Code.NOT_FOUND -> R.string.error_db_missing
            FirebaseFirestoreException.Code.UNAVAILABLE -> R.string.error_network
            else -> R.string.error_generic
        }
        else -> R.string.error_generic
    }
}
