package com.example.taller_3.utils

import android.util.Patterns
import androidx.annotation.StringRes
import com.example.taller_3.R

/** Cada función devuelve el id del string de error, o null si el dato es válido. */
object Validators {

    @StringRes
    fun nombre(value: String): Int? =
        if (value.isBlank()) R.string.error_empty_name else null

    @StringRes
    fun email(value: String): Int? = when {
        value.isBlank() -> R.string.error_empty_fields
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> R.string.error_invalid_email
        else -> null
    }

    @StringRes
    fun password(value: String): Int? =
        if (value.length < 6) R.string.error_short_password else null

    @StringRes
    fun telefono(value: String): Int? {
        val digits = value.trim()
        return if (digits.length < 7 || digits.length > 15 || !digits.all { it.isDigit() || it == '+' })
            R.string.error_invalid_phone else null
    }

    @StringRes
    fun registro(nombre: String, email: String, password: String, telefono: String): Int? =
        nombre(nombre) ?: email(email) ?: password(password) ?: telefono(telefono)
}
