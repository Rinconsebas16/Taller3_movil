package com.example.taller_3.viewmodel

/*
- Estado del mapa: posición propia, ruta propia (List<LatLng>), switch on/off,
- mapa de usuarios en línea (uid -> User) y sus rutas (uid -> List<LatLng>).
- Al apagar el switch: online=false y se borra la ruta propia.
- Si otro usuario se desconecta: se quita su marcador y su ruta.
*/

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taller_3.data.firebase.UserRepository
import com.example.taller_3.data.location.LocationService
import com.example.taller_3.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

typealias UsersMap = Map<String, User>
typealias RoutesMap = Map<String, List<LatLng>>

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationService = LocationService(application)
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()
    private var locationJob: Job? = null

    // Estado del switch de rastreo (encendido / apagado)
    private val _isTrackingActive = MutableStateFlow(false)
    val isTrackingActive: StateFlow<Boolean> = _isTrackingActive.asStateFlow()

    // Lista de coordenadas de la ruta propia (Polyline)
    private val _currentRoute = MutableStateFlow<List<LatLng>>(emptyList())
    val currentRoute: StateFlow<List<LatLng>> = _currentRoute.asStateFlow()

    // Ubicación actual del usuario local
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    // Usuarios remotos activos: Mapa de UID -> User
    private val _otherUsers = MutableStateFlow<UsersMap>(emptyMap())
    val otherUsers: StateFlow<UsersMap> = _otherUsers.asStateFlow()

    // Rutas de usuarios remotos: Mapa de UID -> List<LatLng>
    private val _otherRoutes = MutableStateFlow<RoutesMap>(emptyMap())
    val otherRoutes: StateFlow<RoutesMap> = _otherRoutes.asStateFlow()

    fun startLocationTracking() {
        if (_isTrackingActive.value) return
        _isTrackingActive.value = true

        val currentUid = auth.currentUser?.uid ?: return

        locationJob = viewModelScope.launch {
            locationService.getLocationUpdates().collect { (lat, lng) ->
                val newPoint = LatLng(lat, lng)
                _currentLocation.value = newPoint
                _currentRoute.value = _currentRoute.value + newPoint

                runCatching {
                    userRepository.updateUserLocation(currentUid, lat, lng, true)
                }
            }
        }
    }

    fun stopLocationTracking() {
        _isTrackingActive.value = false
        locationJob?.cancel()
        locationJob = null

        val currentUid = auth.currentUser?.uid

        // Limpia la ruta recorrida propia
        _currentRoute.value = emptyList()
        _currentLocation.value = null

        // Marca enLinea = false en Firebase
        if (currentUid != null) {
            viewModelScope.launch {
                runCatching {
                    userRepository.updateOnlineStatus(currentUid, false)
                }
            }
        }
    }

    // Actualiza o remueve los datos de usuarios remotos (Sincronización Firestore)
    fun updateRemoteUsers(users: List<User>) {
        val currentUid = auth.currentUser?.uid
        val activeUsersMap = mutableMapOf<String, User>()
        val updatedRoutes = _otherRoutes.value.toMutableMap()

        users.filter { it.uid != currentUid && it.enLinea }.forEach { user ->
            activeUsersMap[user.uid] = user
            val newPoint = LatLng(user.latitud, user.longitud)
            val existingRoute = updatedRoutes[user.uid] ?: emptyList()
            updatedRoutes[user.uid] = existingRoute + newPoint
        }

        // Elimina las rutas de los usuarios que ya no están en línea
        val currentActiveUids = activeUsersMap.keys
        updatedRoutes.keys.retainAll(currentActiveUids)

        _otherUsers.value = activeUsersMap
        _otherRoutes.value = updatedRoutes
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
    }
}