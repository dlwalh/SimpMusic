package com.maxrave.simpmusic.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.maxrave.simpmusic.ui.theme.linuxBase

/**
 * The "desktop wallpaper" of the Linux-blur look: the playing song's artwork, blurred and dimmed,
 * filling the whole window. Every panel in the app is translucent, so this is what shows through them.
 *
 * The artwork is requested at 128px, so it is soft even where [blur] has no effect (it only blurs on
 * Android 12 and up). With nothing playing the wallpaper is the plain base colour.
 */
@Composable
fun LinuxWallpaper(
    artworkUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(linuxBase)) {
        Crossfade(
            targetState = artworkUrl,
            animationSpec = tween(900),
            label = "linux_wallpaper",
        ) { url ->
            if (url != null) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalPlatformContext.current)
                            .data(url)
                            .size(128)
                            .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .blur(48.dp, BlurredEdgeTreatment.Unbounded),
                )
            }
        }
        // Dims the artwork so text on the panels stays readable, a little more towards the bottom.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            linuxBase.copy(alpha = 0.72f),
                            linuxBase.copy(alpha = 0.90f),
                        ),
                    ),
                ),
        )
    }
}
