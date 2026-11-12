package com.example.taller_3.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.taller_3.R

/**
 * Foto de perfil circular. Muestra (en orden): la foto recién tomada, la foto de Storage
 * o el avatar por defecto. Si se pasa onClick, aparece el botón de cámara.
 */
@Composable
fun ProfileAvatar(
    photoUrl: String?,
    modifier: Modifier = Modifier,
    localPhoto: Bitmap? = null,
    size: Dp = 120.dp,
    loading: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(modifier = modifier.size(size)) {
        val imageModifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        val description = stringResource(R.string.cd_profile_photo)
        when {
            localPhoto != null -> Image(
                bitmap = localPhoto.asImageBitmap(),
                contentDescription = description,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
            !photoUrl.isNullOrBlank() -> AsyncImage(
                model = photoUrl,
                contentDescription = description,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_default_avatar),
                error = painterResource(R.drawable.ic_default_avatar),
                modifier = imageModifier
            )
            else -> Image(
                painter = painterResource(R.drawable.ic_default_avatar),
                contentDescription = description,
                modifier = imageModifier.background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        if (onClick != null) {
            Icon(
                painter = painterResource(R.drawable.ic_camera),
                contentDescription = stringResource(R.string.cd_take_photo),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onClick)
                    .padding(8.dp)
            )
        }
    }
}
