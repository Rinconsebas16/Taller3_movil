package com.example.taller_3.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMapComposable
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation

/** Ruta recorrida por un usuario (PolylineAnnotation de Mapbox). Necesita al menos 2 puntos. */
@Composable
@MapboxMapComposable
fun UserPolyline(points: List<Point>, color: Color) {
    if (points.size < 2) return
    PolylineAnnotation(points = points) {
        lineColor = color
        lineWidth = 5.0
        lineOpacity = 0.85
    }
}
