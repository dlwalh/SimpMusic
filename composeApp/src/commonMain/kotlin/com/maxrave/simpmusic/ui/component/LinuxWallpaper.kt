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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.maxrave.simpmusic.ui.theme.linuxBase

/**
 * The "desktop wallpaper" of the Linux-blur look: the playing song's artwork, blurred and dimmed,
 * filling the whole window. Every panel in the app is translucent, so this is what shows through
 * them, and what the blurred bars blur.
 *
 * The artwork is requested at 128px, so it is soft even where [blur] has no effect (it only blurs on
 * Android 12 and up). With nothing playing the wallpaper is a soft three-colour glow, so the frosted
 * panels still have something to show through.
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
            } else {
                Box(Modifier.fillMaxSize().glow())
            }
        }
        // Dims the wallpaper so text on the panels stays readable, a little more towards the bottom.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            linuxBase.copy(alpha = 0.58f),
                            linuxBase.copy(alpha = 0.80f),
                        ),
                    ),
                ),
        )
    }
}

// Three soft radial blobs on the base colour: blue top-left, violet bottom-right, teal top-right.
private fun Modifier.glow(): Modifier =
    drawBehind {
        fun blob(
            color: Color,
            cx: Float,
            cy: Float,
            r: Float,
        ) = drawRect(
            Brush.radialGradient(
                colors = listOf(color, Color.Transparent),
                center = Offset(size.width * cx, size.height * cy),
                radius = size.minDimension * r,
            ),
        )
        blob(Color(0x883B5BA5), 0.15f, 0.12f, 0.95f)
        blob(Color(0x778E4EC6), 0.90f, 0.85f, 1.05f)
        blob(Color(0x552A9D8F), 0.85f, 0.20f, 0.75f)
    }
