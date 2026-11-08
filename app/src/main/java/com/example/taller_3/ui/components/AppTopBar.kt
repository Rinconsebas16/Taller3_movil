package com.example.taller_3.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.taller_3.R

/** Barra superior de la pantalla principal con el menu de 2 opciones: Mi perfil y Cerrar sesion. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(onClick = { menuOpen = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vert),
                    contentDescription = stringResource(R.string.cd_menu)
                )
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.menu_profile)) },
                    leadingIcon = { Icon(painterResource(R.drawable.ic_person), contentDescription = null) },
                    onClick = {
                        menuOpen = false
                        onProfile()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.menu_logout)) },
                    leadingIcon = { Icon(painterResource(R.drawable.ic_logout), contentDescription = null) },
                    onClick = {
                        menuOpen = false
                        onLogout()
                    }
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
