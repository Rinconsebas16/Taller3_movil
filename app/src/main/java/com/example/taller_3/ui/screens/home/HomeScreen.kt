package com.example.taller_3.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller_3.R
import com.example.taller_3.ui.components.HomeTopBar
import com.example.taller_3.ui.components.LocationSwitch
import com.example.taller_3.utils.PermissionUtils
import com.example.taller_3.viewmodel.AuthViewModel
import com.example.taller_3.viewmodel.MapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager

@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onLoggedOut: () -> Unit,
    onBeforeLogout: () -> Unit = {},
    authVm: AuthViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    var confirmLogout by remember { mutableStateOf(false) }

    val isTrackingActive by mapViewModel.isTrackingActive.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            mapViewModel.startLocationTracking()
        } else {
            mapViewModel.stopLocationTracking()
            Toast.makeText(
                context,
                "Se requieren permisos de ubicación para activar el rastreo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        mapboxMap.loadStyle(Style.MAPBOX_STREETS)
                    }
                },
                update = { mapView ->
                    // Si hay puntos en la ruta, dibujamos o actualizamos la Polyline en Mapbox
                    if (currentRoute.isNotEmpty()) {
                        val annotationPlugin = mapView.annotations
                        val polylineManager = annotationPlugin.createPolylineAnnotationManager()
                        polylineManager.deleteAll()

                        val points = currentRoute.map { Point.fromLngLat(it.longitude, it.latitude) }
                        val polylineOptions = PolylineAnnotationOptions()
                            .withPoints(points)
                            .withLineWidth(4.0)
                            .withLineColor("#1E88E5")

                        polylineManager.create(polylineOptions)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            LocationSwitch(
                checked = isTrackingActive,
                onCheckedChange = { shouldEnable: Boolean ->
                    if (shouldEnable) {
                        if (PermissionUtils.hasLocationPermission(context)) {
                            mapViewModel.startLocationTracking()
                        } else {
                            locationPermissionLauncher.launch(PermissionUtils.locationPermissions)
                        }
                    } else {
                        mapViewModel.stopLocationTracking()
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
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
                    mapViewModel.stopLocationTracking()
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