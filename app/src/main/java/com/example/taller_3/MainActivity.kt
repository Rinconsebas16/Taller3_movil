package com.example.taller_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taller_3.navigation.AppNavigation
import com.example.taller_3.ui.theme.Taller3Theme

/** Única Activity: las pantallas son @Composable y se navega con NavDisplay. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Taller3Theme {
                AppNavigation()
            }
        }
    }
}
