package com.maxrave.simpmusic.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxrave.simpmusic.expect.ui.PlatformBackdrop

// A terminal-style chip: a small, nearly square rectangle with a thin border.
private val ChipShape = RoundedCornerShape(6.dp)

/**
 * A flat chip: a quiet grey fill with no outline, and a solid white one (black text) when selected.
 * No glass, no shadow. [backdrop] stays in the signature so existing callers keep compiling, but a
 * flat chip has nothing to refract, so it is ignored.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun Chip(
    isAnimated: Boolean = false,
    isSelected: Boolean = false,
    text: String,
    backdrop: PlatformBackdrop? = null,
    // A smaller chip for dense panels such as the Desktop sidebar: 28dp tall with a 12sp label.
    compact: Boolean = false,
    onClick: () -> Unit,
) {
    ChipFrame(isAnimated = isAnimated && isSelected) {
        ChipBody(
            text = text,
            selected = isSelected,
            borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
            onClick = onClick,
            compact = compact,
        )
    }
}

/**
 * A collapsing filter. Closed it is one grey chip ([text]); tapped, it opens into its [options]
 * inside a single grey group led by [text]; once an option is picked it folds back into one group
 * reading "[text] | option", the [text] half a white capsule sitting inside it.
 *
 * Every piece is the size of a [Chip]: the group goes through the same [ChipFrame], and each part of
 * it is the same [ChipBody], so the group lines up with the plain chips beside it to the pixel.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun ExpandableChip(
    text: String,
    options: List<String>,
    selectedIndex: Int?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (Int) -> Unit,
    isAnimated: Boolean = false,
    backdrop: PlatformBackdrop? = null,
) {
    val picked = selectedIndex?.let(options::getOrNull)
    // The folded label is still on screen while it shrinks away after the pick is cleared (All), so it
    // shows the last pick rather than nothing.
    var lastPicked by remember { mutableStateOf(picked) }
    SideEffect { if (picked != null) lastPicked = picked }
    val pickedLabel = picked ?: lastPicked.orEmpty()

    // One structure for all three states, so every change between them animates instead of swapping
    // one layout for another: closed is the [text] chip alone, open adds the options after it, and
    // folded adds the pick after it.
    val folded = !expanded && picked != null
    val active = expanded || picked != null
    val colorScheme = MaterialTheme.colorScheme
    val groupFill = colorScheme.surfaceContainer
    // The head half of an active group is the selected chip: solid accent with dark text.
    val headFill by animateColorAsState(if (active) colorScheme.primary else Color.Transparent)
    val headLabel by animateColorAsState(if (active) colorScheme.onPrimary else colorScheme.onSurface)
    ChipFrame(isAnimated = isAnimated && picked != null) {
        Row(
            modifier =
                Modifier
                    .clip(ChipShape)
                    .background(groupFill, ChipShape)
                    .border(1.dp, colorScheme.outlineVariant, ChipShape),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChipBody(
                text = text,
                selected = false,
                containerColor = headFill,
                labelColor = headLabel,
                borderColor = Color.Transparent,
                onClick = { onExpandedChange(!expanded) },
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = expanded,
                enter = expandHorizontally() + fadeIn(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    options.forEachIndexed { index, option ->
                        val isPicked = index == selectedIndex
                        ChipBody(
                            text = option,
                            selected = false,
                            containerColor = if (isPicked) colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
                            labelColor = if (isPicked) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                            borderColor = Color.Transparent,
                            onClick = { onSelect(index) },
                        )
                    }
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = folded,
                enter = expandHorizontally() + fadeIn(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                ChipBody(
                    text = pickedLabel,
                    selected = false,
                    containerColor = Color.Transparent,
                    labelColor = colorScheme.onSurface,
                    borderColor = Color.Transparent,
                    onClick = { onExpandedChange(true) },
                )
            }
        }
    }
}

/**
 * The loading ring around a chip. It also pads the chip by its 1dp border width, so anything that has
 * to be the size of a chip goes through here too.
 */
@Composable
private fun ChipFrame(
    isAnimated: Boolean,
    content: @Composable () -> Unit,
) {
    InfiniteBorderAnimationView(
        isAnimated = isAnimated,
        brush = Brush.sweepGradient(listOf(Color.Gray, Color.White)),
        backgroundColor = Color.Transparent,
        contentPadding = 0.dp,
        borderWidth = 1.dp,
        shape = ChipShape,
        oneCircleDurationMillis = 2500,
        content = content,
    )
}

@Composable
private fun ChipBody(
    text: String,
    selected: Boolean,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // The translucent panel fill of an unselected chip, with its thin border.
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    compact: Boolean = false,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        ElevatedFilterChip(
            modifier = if (compact) modifier.height(28.dp) else modifier,
            shape = ChipShape,
            // No shadow: flat, and nothing shows through a translucent fill as a dark ring.
            elevation = FilterChipDefaults.elevatedFilterChipElevation(elevation = 0.dp),
            colors =
                FilterChipDefaults.elevatedFilterChipColors(
                    containerColor = containerColor,
                    // Selected is the solid accent colour with its own contrasting text.
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    labelColor = labelColor,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            onClick = onClick,
            label = {
                // Only the size changes: the chip's own label style still supplies weight and colour.
                Text(
                    text,
                    maxLines = 1,
                    style = if (compact) LocalTextStyle.current.copy(fontSize = 12.sp, lineHeight = 16.sp) else LocalTextStyle.current,
                )
            },
            border =
                FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    selectedBorderColor = Color.Transparent,
                    borderColor = borderColor,
                ),
            // No check icon. Filled-versus-grey is already the strongest cue, and a tick would widen
            // the selected chip and reflow the row under the finger that tapped it.
            selected = selected,
        )
    }
}
