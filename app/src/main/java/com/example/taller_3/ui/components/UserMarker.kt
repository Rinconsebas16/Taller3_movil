package com.example.taller_3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.taller_3.R
import com.mapbox.geojson.Point
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.MapboxMapComposable
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

/**
 * Marcador personalizado (NO el ícono default): foto de perfil circular con borde de color,
 * el nombre debajo y una punta que señala la posición exacta.
 */
@Composable
@MapboxMapComposable
fun UserMarker(
    point: Point,
    photoUrl: String,
    label: String,
    color: Color
) {
    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(point)
            annotationAnchor { anchor(ViewAnnotationAnchor.BOTTOM) }
            allowOverlap(true)
        }
    ) {
        UserMarkerContent(photoUrl = photoUrl, label = label, color = color)
    }
}

private val PinTip = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width / 2f, size.height)
    close()
}

@Composable
fun UserMarkerContent(photoUrl: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(52.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, color, CircleShape)
        ) {
            AsyncImage(
                model = photoUrl.ifBlank { null },
                contentDescription = label,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_default_avatar),
                error = painterResource(R.drawable.ic_default_avatar),
                fallback = painterResource(R.drawable.ic_default_avatar),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .size(width = 14.dp, height = 9.dp)
                .background(color, PinTip)
        )
    }
}

/** Colores para distinguir a los demás usuarios (marcador y ruta del mismo color). */
private val OtherUserColors = listOf(
    Color(0xFFE53935), Color(0xFF8E24AA), Color(0xFF43A047),
    Color(0xFFFB8C00), Color(0xFF00897B), Color(0xFF6D4C41)
)

fun colorForUser(uid: String): Color =
    OtherUserColors[(uid.hashCode() and Int.MAX_VALUE) % OtherUserColors.size]

@Composable
fun myMarkerColor(): Color = MaterialTheme.colorScheme.primary
