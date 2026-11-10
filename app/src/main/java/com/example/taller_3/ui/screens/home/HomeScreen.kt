package com.example.taller_3.ui.screens.home

import android.Manifest
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller_3.R
import com.example.taller_3.ui.components.HomeTopBar
import com.example.taller_3.ui.components.LocationSwitch
import com.example.taller_3.ui.components.UserMarker
import com.example.taller_3.ui.components.UserPolyline
import com.example.taller_3.ui.components.colorForUser
import com.example.taller_3.ui.components.myMarkerColor
import com.example.taller_3.utils.PermissionUtils
import com.example.taller_3.viewmodel.AuthViewModel
import com.example.taller_3.viewmodel.MapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

/** Bogotá, para la cámara inicial antes de tener la ubicación del usuario. */
private val DEFAULT_CENTER: Point = Point.fromLngLat(-74.0641630119028, 4.627389316647156)

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
    val currentRoute by mapViewModel.currentRoute.collectAsState()
    val currentLocation by mapViewModel.currentLocation.collectAsState()
    val me by mapViewModel.me.collectAsState()
    val otherUsers by mapViewModel.otherUsers.collectAsState()
    val otherRoutes by mapViewModel.otherRoutes.collectAsState()

    // Escucha en tiempo real a los usuarios en línea (Firestore)
    LaunchedEffect(Unit) { mapViewModel.startListening() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            mapViewModel.startLocationTracking()
        } else {
            mapViewModel.stopLocationTracking()
            Toast.makeText(context, R.string.permission_location_denied, Toast.LENGTH_SHORT).show()
        }
    }

    // Cámara del mapa: empieza en Bogotá y sigue al usuario cuando hay ubicación
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(DEFAULT_CENTER)
        }
    }
    LaunchedEffect(currentLocation) {
        currentLocation?.let { point ->
            mapViewportState.easeTo(
                cameraOptions {
                    center(point)
                    zoom(16.0)
                }
            )
        }
    }

    val myColor = myMarkerColor()
    val myLabel = stringResource(R.string.marker_you)

    Scaffold(
        topBar = {
            HomeTopBar(
                photoUrl = me?.photoUrl,
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
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                scaleBar = { }
            ) {
                // Rutas de los demás usuarios en línea
                otherRoutes.forEach { (uid, points) ->
                    key(uid) { UserPolyline(points = points, color = colorForUser(uid)) }
                }
                // Mi ruta (se borra al apagar el switch porque la lista queda vacía)
                UserPolyline(points = currentRoute, color = myColor)

                // Marcadores de los demás usuarios, con su foto de perfil
                otherUsers.values.forEach { user ->
                    key(user.uid) {
                        UserMarker(
                            point = Point.fromLngLat(user.longitud, user.latitud),
                            photoUrl = user.photoUrl,
                            label = user.nombre,
                            color = colorForUser(user.uid)
                        )
                    }
                }
                // Mi marcador
                currentLocation?.let { point ->
                    UserMarker(
                        point = point,
                        photoUrl = me?.photoUrl.orEmpty(),
                        label = myLabel,
                        color = myColor
                    )
                }
            }

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
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
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
                    mapViewModel.onLogout()
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
