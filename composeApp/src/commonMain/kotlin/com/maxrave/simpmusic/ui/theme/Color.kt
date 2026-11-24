package com.maxrave.simpmusic.ui.theme

import androidx.compose.ui.graphics.Color

// ===== Brand =====

/**
 * Brand seed color. The whole Material 3 ColorScheme is generated from this
 * color at runtime — see [AppTheme].
 */
val seed = Color(0xFF8ECAE6)

// ===== Minimal dark surfaces =====
// A flat, near-black ramp. The page sits at #050505 and each tier steps up only a few
// shades, so panels read as separated by tone alone — no shadows, tint or borders needed.

/** Deep page background used by the dark theme. */
val appBackgroundDark = Color(0xFF050505)

// ===== Linux blur =====
// Neutral near-black base colours. The wallpaper (see LinuxWallpaper) and the tint of the frosted bars.

/** The wallpaper's base colour, drawn when no artwork is available and under the dimming layer. */
val linuxBase = Color(0xFF0B0B0D)

/** The tint laid over blurred content behind a scrolling bar. */
val linuxBarTint = Color(0xFF0B0B0D)

// ===== Semantic colors (not derivable from the color scheme) =====

/** Liked/favorite state (heart buttons, favorite tiles). */
val favoriteColor = Color(0xFFFF4081)

/** Currently playing lyric line. White rather than a saturated hue: the dimmed lines carry the contrast. */
val lyricActiveColor = Color(0xFFFFFFFF)

// Shimmer stays close to the page colour so loading placeholders are barely-there, not a flash.
val shimmerBackground = Color(0x66181818)
val shimmerLine = Color(0xFF242424)

// Light-theme counterparts of the shimmer tokens.
val shimmerBackgroundLight = Color(0x7EDCD8D8)
val shimmerLineLight = Color(0xFFCFC8C8)

// Overlays cover artwork; they are tinted with the page black instead of a grey so they blend in.
val overlay = Color(0x32050505)
val blackMoreOverlay = Color(0x8F050505)

// ===== Desktop shell =====
// Flat layering for the desktop window: the window takes the page black, panels step one
// shade up so they read as raised. The light panel is not here — it comes from
// colorScheme.surfaceContainer, which already sits at the right distance.

val desktopWindowDark = Color(0xFF050505)
val desktopWindowLight = Color(0xFFFFFFFF)
val desktopPanelDark = Color(0xFF121212)

// ===== Desktop window controls =====
// The macOS traffic lights. Fixed by convention rather than by theme — users read these by
// colour, so they stay the same in light and dark.

val windowCloseButton = Color(0xFFFF605C)
val windowCloseButtonHover = Color(0xFFE54942)
val windowMinimiseButton = Color(0xFFFFBD44)
val windowMinimiseButtonHover = Color(0xFFE5A93D)
val windowMaximiseButton = Color(0xFF00CA4E)
val windowMaximiseButtonHover = Color(0xFF00B344)

// ===== Legacy — do not add new usages =====

/**
 * Old M3 primary (lavender). Kept only for the SettingScreen storage bar,
 * which stays untouched by owner's decision.
 */
@Deprecated("Legacy storage bar color only — use MaterialTheme.colorScheme.primary in new code")
val md_theme_dark_primary = Color(0xFFB2C5FF)
