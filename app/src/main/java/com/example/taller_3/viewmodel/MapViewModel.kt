package com.example.taller_3.viewmodel

/*
* Estado del mapa: posición propia, ruta propia (List<Point>), switch on/off,
 * mapa de usuarios en línea (uid -> User) y sus rutas (uid -> List<Point>).
 * Al apagar el switch: online=false y se borra la ruta propia.
 * Si otro usuario se desconecta: se quita su marcador y su ruta.
*/
