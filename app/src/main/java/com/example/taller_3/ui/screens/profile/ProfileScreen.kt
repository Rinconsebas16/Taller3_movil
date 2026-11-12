package com.example.taller_3.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller_3.R
import com.example.taller_3.ui.components.AppTextField
import com.example.taller_3.ui.components.BackTopBar
import com.example.taller_3.ui.components.ProfileAvatar
import com.example.taller_3.ui.screens.camera.rememberTakePhoto
import com.example.taller_3.viewmodel.ProfileViewModel

/**
 * Perfil: muestra la información guardada en Firestore y la foto de Storage.
 * Permite cambiar nombre y teléfono (Firestore), contraseña (Auth) y foto (Storage).
 *
 * @param uid usuario a mostrar; llega como parámetro de la ruta (equivalente a un extra del Intent).
 */
@Composable
fun ProfileScreen(
    uid: String,
    onBack: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val takePhoto = rememberTakePhoto { vm.updatePhoto(it) }

    DisposableEffect(uid) {
        vm.start(uid)
        onDispose { vm.stop() }
    }

    LaunchedEffect(vm.messageRes) {
        vm.messageRes?.let {
            snackbar.showSnackbar(context.getString(it))
            vm.messageShown()
        }
    }

    Scaffold(
        topBar = { BackTopBar(stringResource(R.string.profile_title), onBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val user = vm.user
            ProfileAvatar(
                photoUrl = user?.photoUrl,
                loading = vm.uploadingPhoto,
                onClick = takePhoto
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = user?.email.orEmpty(),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(
                    if (user?.enLinea == true) R.string.status_online else R.string.status_offline
                ),
                style = MaterialTheme.typography.bodySmall,
                color = if (user?.enLinea == true) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.outline
            )

            // ---- Datos (Firestore) ----
            Spacer(Modifier.height(24.dp))
            SectionTitle(stringResource(R.string.section_personal_data))
            AppTextField(
                value = vm.nombre,
                onValueChange = { vm.nombre = it },
                label = stringResource(R.string.label_name),
                iconRes = R.drawable.ic_person
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = vm.telefono,
                onValueChange = { vm.telefono = it },
                label = stringResource(R.string.label_phone),
                iconRes = R.drawable.ic_phone,
                keyboardType = KeyboardType.Phone
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = vm::saveProfile,
                enabled = !vm.loading && user != null,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.btn_save)) }

            // ---- Contraseña (Auth) ----
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            SectionTitle(stringResource(R.string.section_password))
            AppTextField(
                value = vm.currentPassword,
                onValueChange = { vm.currentPassword = it },
                label = stringResource(R.string.label_current_password),
                iconRes = R.drawable.ic_lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = vm.newPassword,
                onValueChange = { vm.newPassword = it },
                label = stringResource(R.string.label_new_password),
                iconRes = R.drawable.ic_lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = vm.confirmPassword,
                onValueChange = { vm.confirmPassword = it },
                label = stringResource(R.string.label_confirm_password),
                iconRes = R.drawable.ic_lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = vm::changePassword,
                enabled = !vm.loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.btn_change_password)) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}
