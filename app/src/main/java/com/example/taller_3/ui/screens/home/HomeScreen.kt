package com.example.taller_3.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller_3.R
import com.example.taller_3.ui.components.HomeTopBar
import com.example.taller_3.viewmodel.AuthViewModel

/**
 * Pantalla principal despues de autenticarse.
 * - Integrante 1: menu con Mi perfil / Cerrar sesion (listo).
 * - Integrante 2: mapa de Mapbox + switch de ubicacion dentro del Box (ver TODO).
 *
 * @param onBeforeLogout lo usa el modulo del mapa para detener las actualizaciones de ubicacion
 *                       antes de marcar enLinea=false y cerrar la sesion.
 */
@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onLoggedOut: () -> Unit,
    onBeforeLogout: () -> Unit = {},
    authVm: AuthViewModel = viewModel()
) {
    var confirmLogout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HomeTopBar(
                onProfile = onOpenProfile,
                onLogout = { confirmLogout = true }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // TODO (Integrante 2): MapboxMap ocupando todo el Box + LocationSwitch encima.
            Text(
                text = stringResource(R.string.home_map_placeholder),
                modifier = Modifier.align(Alignment.Center)
            )

            if (authVm.loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text(stringResource(R.string.menu_logout)) },
            text = { Text(stringResource(R.string.logout_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    onBeforeLogout()
                    authVm.logout(onLoggedOut)
                }) { Text(stringResource(R.string.menu_logout)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}
