# Estructura del Taller 3

App en Kotlin + Jetpack Compose (como en las clases), con Firebase Auth, Cloud Firestore,
Firebase Storage, Mapbox, CameraX y Fused Location.

```
app/src/main/java/com/example/taller_3/
├── MainActivity.kt              única Activity (setContent)
├── navigation/                  rutas y NavDisplay
├── model/User.kt                datos del usuario
├── data/
│   ├── firebase/                Auth, Firestore (users) y Storage
│   └── location/                requestLocationUpdates
├── viewmodel/                   Auth, Profile y Map ViewModels
├── ui/
│   ├── screens/auth/            Login y Registro
│   ├── screens/home/            Mapa + switch + menú (Perfil / Cerrar sesión)
│   ├── screens/profile/         Ver y editar perfil
│   ├── screens/camera/          Foto de perfil
│   ├── components/              TopBar, Switch, Marker y Polyline
│   └── theme/                   Colores, tema y tipografía
└── utils/                       Constantes, permisos y validaciones

app/src/main/res/
├── values/strings.xml           todos los textos (@string)
├── values/mapbox_access_token.xml
└── drawable/                    íconos y marcadores propios

firestore.rules / storage.rules  reglas para pegar en la consola de Firebase
```

Firestore: colección `users`, documento = `uid`:
`name, email, phone, photoUrl, online, latitude, longitude` (sin password).

Storage: `profile_images/{uid}.jpg`.

Pendiente: agregar en Gradle las dependencias de Compose, Navigation 3, Firebase (BoM, auth,
firestore, storage, google-services), Mapbox, Play Services Location, CameraX, Accompanist
y Coil, y en el Manifest la Activity y los permisos (INTERNET, FINE/COARSE_LOCATION, CAMERA).
