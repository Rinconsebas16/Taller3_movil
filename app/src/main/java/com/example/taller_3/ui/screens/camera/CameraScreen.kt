package com.example.taller_3.ui.screens.camera

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.taller_3.R
import com.example.taller_3.utils.PermissionUtils


/**
 * Devuelve una función que abre la cámara para tomar la foto de perfil.
 * Pide el permiso CAMERA si hace falta y entrega el Bitmap en onPhoto.
 */
@Composable
fun rememberTakePhoto(onPhoto: (Bitmap) -> Unit): () -> Unit {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) onPhoto(bitmap)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, R.string.permission_camera_rationale, Toast.LENGTH_LONG).show()
        }
    }

    return {
        if (PermissionUtils.hasCameraPermission(context)) {
            cameraLauncher.launch(null)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
