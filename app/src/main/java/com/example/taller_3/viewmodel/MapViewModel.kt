package com.example.taller_3.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taller_3.data.firebase.UserRepository
import com.example.taller_3.data.location.LocationService
import com.example.taller_3.model.User
import com.example.taller_3.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.mapbox.geojson.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

typealias UsersMap = Map<String, User>
typealias RoutesMap = Map<String, List<Point>>

/**
 * Estado del mapa: posición y ruta propias, estado del switch, usuarios en línea y sus rutas.
 * - Al apagar el switch: enLinea = false y se borra la ruta propia.
 * - Si otro usuario se desconecta: se quitan su marcador y su ruta.
 */
class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationService = LocationService(application)
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()
    private var locationJob: Job? = null
    private var usersJob: Job? = null
    private var meListener: ListenerRegistration? = null

    // Estado del switch de rastreo (encendido / apagado)
    private val _isTrackingActive = MutableStateFlow(false)
    val isTrackingActive: StateFlow<Boolean> = _isTrackingActive.asStateFlow()

    // Lista de coordenadas de la ruta propia (Polyline)
    private val _currentRoute = MutableStateFlow<List<Point>>(emptyList())
    val currentRoute: StateFlow<List<Point>> = _currentRoute.asStateFlow()

    // Ubicación actual del usuario local
    private val _currentLocation = MutableStateFlow<Point?>(null)
    val currentLocation: StateFlow<Point?> = _currentLocation.asStateFlow()

    // Datos del usuario local (para su foto en el marcador)
    private val _me = MutableStateFlow<User?>(null)
    val me: StateFlow<User?> = _me.asStateFlow()

    // Usuarios remotos activos: Mapa de UID -> User
    private val _otherUsers = MutableStateFlow<UsersMap>(emptyMap())
    val otherUsers: StateFlow<UsersMap> = _otherUsers.asStateFlow()

    // Rutas de usuarios remotos: Mapa de UID -> List<Point>
    private val _otherRoutes = MutableStateFlow<RoutesMap>(emptyMap())
    val otherRoutes: StateFlow<RoutesMap> = _otherRoutes.asStateFlow()

    /** Empieza a escuchar a los demás usuarios y al propio. Se puede llamar varias veces. */
    fun startListening() {
        val uid = auth.currentUser?.uid ?: return

        if (meListener == null) {
            meListener = userRepository.observeUser(uid) { _me.value = it }
            // Si la app se cerró con el switch encendido, el usuario quedó "en línea": se corrige
            if (!_isTrackingActive.value) {
                viewModelScope.launch { runCatching { userRepository.setOnline(uid, false) } }
            }
        }

        if (usersJob == null) {
            usersJob = viewModelScope.launch {
                userRepository.getActiveUsersFlow()
                    .catch { e -> Log.e(TAG, "usuarios en línea", e) }
                    .collect { users -> updateRemoteUsers(users) }
            }
        }
    }

    fun startLocationTracking() {
        val currentUid = auth.currentUser?.uid ?: return
        if (_isTrackingActive.value) return
        _isTrackingActive.value = true

        locationJob = viewModelScope.launch {
            locationService.getLocationUpdates(Constants.LOCATION_INTERVAL_MS).collect { (lat, lng) ->
                // Mapbox recibe (longitud, latitud)
                val newPoint = Point.fromLngLat(lng, lat)
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
                    userRepository.setOnline(currentUid, false)
                }
            }
        }
    }

    /** Al cerrar sesión: apaga el rastreo y deja de escuchar Firestore. */
    fun onLogout() {
        stopLocationTracking()
        usersJob?.cancel()
        usersJob = null
        meListener?.remove()
        meListener = null
        _me.value = null
        _otherUsers.value = emptyMap()
        _otherRoutes.value = emptyMap()
    }

    // Actualiza o remueve los datos de usuarios remotos (Sincronización Firestore)
    fun updateRemoteUsers(users: List<User>) {
        val currentUid = auth.currentUser?.uid

        val activeUsersMap = users
            .filter { it.uid != currentUid && it.enLinea }
            .filter { !(it.latitud == 0.0 && it.longitud == 0.0) } // todavía sin posición
            .associateBy { it.uid }

        // Elimina las rutas de los usuarios que ya no están en línea
        val updatedRoutes = _otherRoutes.value
            .filterKeys { it in activeUsersMap.keys }
            .toMutableMap()

        activeUsersMap.values.forEach { user ->
            val newPoint = Point.fromLngLat(user.longitud, user.latitud)
            val existingRoute = updatedRoutes[user.uid] ?: emptyList()
            // Solo se agrega el punto si la posición cambió
            if (existingRoute.lastOrNull() != newPoint) {
                updatedRoutes[user.uid] = existingRoute + newPoint
            }
        }

        _otherUsers.value = activeUsersMap
        _otherRoutes.value = updatedRoutes
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
        meListener?.remove()
    }

    private companion object {
        const val TAG = "MapViewModel"
    }
}
