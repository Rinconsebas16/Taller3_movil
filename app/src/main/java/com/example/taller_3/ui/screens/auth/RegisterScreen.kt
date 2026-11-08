package com.example.taller_3.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller_3.R
import com.example.taller_3.ui.components.AppTextField
import com.example.taller_3.ui.components.BackTopBar
import com.example.taller_3.ui.components.ProfileAvatar
import com.example.taller_3.ui.screens.camera.rememberTakePhoto
import com.example.taller_3.viewmodel.AuthViewModel

/** Registro: nombre, email, password, telefono y foto. NO pide enLinea, latitud ni longitud. */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val takePhoto = rememberTakePhoto { vm.photo = it }
    val goBack = {
        vm.clearError()
        onBack()
    }

    Scaffold(
        topBar = { BackTopBar(stringResource(R.string.register_title), goBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(
                photoUrl = null,
                localPhoto = vm.photo,
                onClick = takePhoto
            )
            Text(
                text = stringResource(R.string.hint_take_photo),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(16.dp))

            AppTextField(
                value = vm.nombre,
                onValueChange = { vm.nombre = it },
                label = stringResource(R.string.label_name),
                iconRes = R.drawable.ic_person
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = vm.email,
                onValueChange = { vm.email = it },
                label = stringResource(R.string.label_email),
                iconRes = R.drawable.ic_email,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = vm.password,
                onValueChange = { vm.password = it },
                label = stringResource(R.string.label_password),
                iconRes = R.drawable.ic_lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = vm.telefono,
                onValueChange = { vm.telefono = it },
                label = stringResource(R.string.label_phone),
                iconRes = R.drawable.ic_phone,
                keyboardType = KeyboardType.Phone
            )

            vm.errorRes?.let {
                Spacer(Modifier.height(12.dp))
                Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                vm.errorDetail?.let { detail ->
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.register(onRegisterSuccess) },
                enabled = !vm.loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (vm.loading) CircularProgressIndicator(Modifier.size(24.dp))
                else Text(stringResource(R.string.btn_register))
            }
            TextButton(onClick = goBack) {
                Text(stringResource(R.string.go_to_login))
            }
        }
    }
}
