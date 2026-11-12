package com.example.taller_3.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.taller_3.R
import com.example.taller_3.data.firebase.AuthRepository
import com.example.taller_3.ui.screens.auth.LoginScreen
import com.example.taller_3.ui.screens.auth.RegisterScreen
import com.example.taller_3.ui.screens.home.HomeScreen
import com.example.taller_3.ui.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    // Si ya hay sesión iniciada se entra directo al mapa
    val backStack = remember {
        val start: Any = if (AuthRepository().currentUser != null) HomeRoute else LoginRoute
        mutableStateListOf(start)
    }

    fun resetTo(route: Any) {
        backStack.clear()
        backStack.add(route)
    }

    fun goBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { goBack() },
        entryProvider = { key ->
            when (key) {
                LoginRoute -> NavEntry(key) {
                    LoginScreen(
                        onLoginSuccess = { resetTo(HomeRoute) },
                        onGoToRegister = { backStack.add(RegisterRoute) }
                    )
                }
                RegisterRoute -> NavEntry(key) {
                    RegisterScreen(
                        onRegisterSuccess = { resetTo(HomeRoute) },
                        onBack = { goBack() }
                    )
                }
                HomeRoute -> NavEntry(key) {
                    HomeScreen(
                        onOpenProfile = {
                            // Se envia el uid al perfil (como un extra de un Intent)
                            AuthRepository().currentUser?.uid?.let { uid ->
                                backStack.add(ProfileRoute(uid = uid))
                            }
                        },
                        onLoggedOut = { resetTo(LoginRoute) }
                    )
                }
                is ProfileRoute -> NavEntry(key) {
                    ProfileScreen(
                        uid = key.uid,
                        onBack = { goBack() }
                    )
                }
                else -> NavEntry(key) { Text(stringResource(R.string.error_generic)) }
            }
        }
    )
}
