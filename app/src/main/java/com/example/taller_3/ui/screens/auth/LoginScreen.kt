package com.example.taller_3.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller_3.R
import com.example.taller_3.ui.components.AppTextField
import com.example.taller_3.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(24.dp))

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

            vm.errorRes?.let {
                Spacer(Modifier.height(12.dp))
                Text(stringResource(it), color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.login(onLoginSuccess) },
                enabled = !vm.loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (vm.loading) CircularProgressIndicator(Modifier.size(24.dp))
                else Text(stringResource(R.string.btn_login))
            }
            TextButton(onClick = {
                vm.clearError()
                onGoToRegister()
            }) {
                Text(stringResource(R.string.go_to_register))
            }
        }
    }
}
