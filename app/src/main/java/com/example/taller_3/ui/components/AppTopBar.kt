package com.example.taller_3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.taller_3.R

/**
 * Barra superior de la pantalla principal con las 2 opciones del menú siempre visibles:
 * - Mi perfil: tu foto de perfil (toca para abrir el perfil)
 * - Cerrar sesión: icono de salida
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    photoUrl: String?,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            // Opción 1: Mi perfil (con la foto del usuario)
            IconButton(onClick = onProfile) {
                AsyncImage(
                    model = photoUrl?.ifBlank { null },
                    contentDescription = stringResource(R.string.menu_profile),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_default_avatar),
                    error = painterResource(R.drawable.ic_default_avatar),
                    fallback = painterResource(R.drawable.ic_default_avatar),
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
            // Opción 2: Cerrar sesión
            IconButton(onClick = onLogout) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = stringResource(R.string.menu_logout)
                )
            }
        }
    )
}

/** Barra superior con flecha de regreso (Perfil, Registro). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
        }
    )
}
