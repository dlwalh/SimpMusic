package com.maxrave.simpmusic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.simpmusic.expect.ui.SystemBarAppearanceEffect
import com.maxrave.simpmusic.expect.ui.platformDynamicColorScheme

/**
 * Semantic colors that sit outside the Material 3 ColorScheme.
 * Read them via [LocalAppColors] inside composables.
 */
@Immutable
data class AppColors(
    val favorite: Color,
    val lyricActive: Color,
    val shimmerBackground: Color,
    val shimmerLine: Color,
    val overlay: Color,
    val overlayHeavy: Color,
)

private val DarkAppColors =
    AppColors(
        favorite = favoriteColor,
        lyricActive = lyricActiveColor,
        shimmerBackground = shimmerBackground,
        shimmerLine = shimmerLine,
        overlay = overlay,
        overlayHeavy = blackMoreOverlay,
    )

// Overlays stay dark in both themes: they cover artwork, where content is always light.
private val LightAppColors =
    DarkAppColors.copy(
        shimmerBackground = shimmerBackgroundLight,
        shimmerLine = shimmerLineLight,
    )

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

/** The one corner every rounded artwork takes — covers, thumbnails, sleeves. */
val ArtworkShape = RoundedCornerShape(4.dp)

/** True in dark theme. Provided by [AppTheme] so platform glass (blur/tint) can adapt reliably. */
val LocalIsDarkTheme = staticCompositionLocalOf { true }

/**
 * Whether liquid-glass surfaces may actually draw glass.
 *
 * Provided by [AppTheme] from the user's setting; the default is true so previews and anything
 * composed outside [AppTheme] keep today's look. Pass false to [AppTheme] for a fully flat app.
 */
val LocalLiquidGlassEnabled = staticCompositionLocalOf { true }

/**
 * The dark scheme to use for immersive screens while the app itself is on the light theme.
 * Provided by [AppTheme], consumed by [ForceDarkContent]; null only outside of [AppTheme].
 */
val LocalForcedDarkColorScheme = staticCompositionLocalOf<ColorScheme?> { null }

/** Parses "RRGGBB" or "AARRGGBB" (optionally "#"-prefixed) into a [Color]; null if malformed. */
fun parseThemeColorHex(hex: String): Color? {
    val clean = hex.trim().removePrefix("#")
    val argb =
        when (clean.length) {
            6 -> "FF$clean"
            8 -> clean
            else -> return null
        }
    return argb.toLongOrNull(16)?.let { Color(it) }
}

/**
 * Flat neutral surfaces for the dark theme: a pure grey ramp (R=G=B, no seed tint) on a deep
 * #050505 page. Containers step up only a few shades, so depth comes from tone alone.
 * surfaceTint is pinned to the surface colour, which switches off the primary-coloured tint that
 * Material 3 would otherwise blend into elevated surfaces. primary/secondary/tertiary stay
 * seed-derived.
 */
private fun ColorScheme.withMinimalDarkSurfaces(): ColorScheme =
    copy(
        background = appBackgroundDark,
        onBackground = Color(0xFFF2F2F2),
        surface = appBackgroundDark,
        onSurface = Color(0xFFF2F2F2),
        surfaceVariant = Color(0xFF1C1C1C),
        onSurfaceVariant = Color(0xFFB0B0B0),
        surfaceTint = appBackgroundDark,
        surfaceBright = Color(0xFF232323),
        surfaceDim = appBackgroundDark,
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF0A0A0A),
        surfaceContainer = Color(0xFF101010),
        surfaceContainerHigh = Color(0xFF161616),
        surfaceContainerHighest = Color(0xFF1C1C1C),
        outline = Color(0xFF6B6B6B),
        outlineVariant = Color(0xFF2A2A2A),
        inverseSurface = Color(0xFFE6E6E6),
        inverseOnSurface = Color(0xFF1B1B1B),
    )

/**
 * Flat neutral surfaces for the light theme, on a pure neutral-grey ramp (R=G=B, no seed tint);
 * primary/secondary/tertiary stay seed-derived. The page background is #FAFAFA rather than pure
 * white, which glares on a large window. Pure white is kept for surfaceBright and
 * surfaceContainerLowest so cards and sheets still have a brighter-than-page tier.
 * surfaceTint is pinned to the page colour, so elevated surfaces are not tinted by the primary.
 */
private fun ColorScheme.withNeutralLightSurfaces(): ColorScheme =
    copy(
        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF1B1B1B),
        surface = Color(0xFFFAFAFA),
        onSurface = Color(0xFF1B1B1B),
        surfaceVariant = Color(0xFFE2E2E2),
        onSurfaceVariant = Color(0xFF474747),
        surfaceTint = Color(0xFFFAFAFA),
        surfaceBright = Color(0xFFFFFFFF),
        surfaceDim = Color(0xFFDADADA),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF7F7F7),
        surfaceContainer = Color(0xFFF1F1F1),
        surfaceContainerHigh = Color(0xFFECECEC),
        surfaceContainerHighest = Color(0xFFE6E6E6),
        outline = Color(0xFF777777),
        outlineVariant = Color(0xFFC7C7C7),
        inverseSurface = Color(0xFF303030),
        inverseOnSurface = Color(0xFFF1F1F1),
    )

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppTheme(
    themeMode: String = DataStoreManager.THEME_MODE_DARK,
    themeColorSource: String = DataStoreManager.THEME_COLOR_DEFAULT,
    customThemeColor: Color? = null,
    liquidGlassEnabled: Boolean = true,
    content:
        @Composable()
        () -> Unit,
) {
    val isDark = isDarkTheme(themeMode)
    val wallpaperScheme =
        if (themeColorSource == DataStoreManager.THEME_COLOR_WALLPAPER) {
            platformDynamicColorScheme(isDark)
        } else {
            null
        }
    val seedColor =
        if (themeColorSource == DataStoreManager.THEME_COLOR_CUSTOM) {
            customThemeColor ?: seed
        } else {
            seed
        }
    // Both themes pin their neutrals to a flat grey ramp; only the accent colours come from the
    // seed. A wallpaper scheme is used as the system provides it.
    val colorScheme =
        wallpaperScheme
            ?: rememberDynamicColorScheme(
                seedColor = seedColor,
                isDark = isDark,
                style = PaletteStyle.TonalSpot,
                modifyColorScheme = { cs ->
                    if (isDark) cs.withMinimalDarkSurfaces() else cs.withNeutralLightSurfaces()
                },
            )
    // Immersive screens stay dark even at light theme (see [ForceDarkContent]). Resolve their scheme
    // once here instead of letting every such subtree build a palette of its own.
    val forcedDarkScheme =
        if (isDark) {
            colorScheme
        } else {
            rememberDynamicColorScheme(
                seedColor = seedColor,
                isDark = true,
                style = PaletteStyle.TonalSpot,
                modifyColorScheme = { cs -> cs.withMinimalDarkSurfaces() },
            )
        }
    SystemBarAppearanceEffect(isDark)
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        content = {
            CompositionLocalProvider(
                LocalRippleConfiguration provides SoftRippleConfiguration,
                LocalContentColor provides colorScheme.onSurfaceVariant,
                LocalAppColors provides if (isDark) DarkAppColors else LightAppColors,
                LocalIsDarkTheme provides isDark,
                LocalForcedDarkColorScheme provides forcedDarkScheme,
                LocalLiquidGlassEnabled provides liquidGlassEnabled,
                content = content,
            )
        },
        typography = typo(colorScheme),
    )
}

/**
 * A very quiet ripple, applied app-wide: enough to register a touch, not enough to flash.
 *
 * The colour is left unspecified on purpose, so the ripple keeps deriving from LocalContentColor
 * and stays correct inside [ForceDarkContent] subtrees, which run a different scheme. This also
 * reaches bare `Modifier.clickable`, since MaterialTheme's LocalIndication reads this configuration.
 *
 * The (color, rippleAlpha) constructor is deprecated, but the public replacements drop rippleAlpha
 * entirely, so the warning is suppressed. Revisit once material3 opens up the full constructor.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
private val SoftRippleConfiguration =
    RippleConfiguration(
        rippleAlpha =
            RippleAlpha(
                draggedAlpha = 0.05f,
                focusedAlpha = 0.03f,
                hoveredAlpha = 0.02f,
                pressedAlpha = 0.03f,
            ),
    )

/**
 * Wraps immersive screens — artist, album, playlist, local playlist, podcast and the players — which
 * always draw over dark artwork and must therefore render dark even on the light theme.
 *
 * [LocalForceDarkText] alone is not enough: it only recolors text through [typo]. Icons, buttons and
 * every other Material default read [LocalContentColor] and [MaterialTheme.colorScheme], which at
 * light theme resolve to dark-on-light and turn grey and unreadable over the artwork. Providing the
 * dark scheme here fixes the whole subtree in one place.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ForceDarkContent(content: @Composable () -> Unit) {
    val darkScheme = LocalForcedDarkColorScheme.current ?: MaterialTheme.colorScheme
    MaterialExpressiveTheme(
        colorScheme = darkScheme,
        content = {
            CompositionLocalProvider(
                LocalForceDarkText provides true,
                LocalIsDarkTheme provides true,
                LocalContentColor provides darkScheme.onSurfaceVariant,
                LocalAppColors provides DarkAppColors,
                content = content,
            )
        },
        typography = typo(darkScheme, forceDark = true),
    )
}

/**
 * Resolves the stored theme mode to a plain boolean.
 *
 * Pulled out of [AppTheme] so chrome living *outside* it can ask the same question — the desktop
 * title bar is drawn before AppTheme is entered, so it has no MaterialTheme to read and would
 * otherwise need its own copy of this `when`.
 */
@Composable
fun isDarkTheme(themeMode: String): Boolean =
    when (themeMode) {
        DataStoreManager.THEME_MODE_LIGHT -> false
        DataStoreManager.THEME_MODE_SYSTEM -> isSystemInDarkTheme()
        else -> true
    }
