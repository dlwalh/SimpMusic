package com.maxrave.simpmusic.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.domain.data.model.home.HomeItem
import com.maxrave.domain.utils.toSongEntity
import com.maxrave.domain.utils.toTrack
import com.maxrave.simpmusic.Platform
import com.maxrave.simpmusic.expect.ui.HorizontalScrollBar
import com.maxrave.simpmusic.extension.getScreenSizeInfo
import com.maxrave.simpmusic.extension.mouseDragScroll
import com.maxrave.simpmusic.getPlatform
import com.maxrave.simpmusic.viewModel.HomeViewModel

// How wide a row may grow in landscape, so a wide window shows several columns instead of a few
// stretched ones. Same value Home's Quick picks uses.
private val LandscapeRowMaxWidth = 400.dp

// Up to four rows per column, like YouTube Music's song shelves.
private const val MAX_ROWS = 4

/**
 * A shelf of songs as compact rows — a small cover, the title and the artist — in columns of up to
 * four, scrolling sideways. This replaces the large hero carousel: it is the same layout Quick picks
 * uses, and it is what YouTube Music itself shows for a shelf of songs.
 * Tapping a row starts the song's radio; a long press opens the song menu.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeDiscoverSection(
    data: HomeItem,
    navController: NavController,
    homeViewModel: HomeViewModel,
) {
    val songs = remember(data.contents) { data.contents.filterNotNull() }
    if (songs.isEmpty()) return
    var sheetTrack by remember { mutableStateOf<Track?>(null) }
    sheetTrack?.let { track ->
        NowPlayingBottomSheet(
            onDismiss = { sheetTrack = null },
            song = track.toSongEntity(),
            navController = navController,
        )
    }

    val gridState = rememberLazyGridState()
    val flingBehavior =
        rememberSnapFlingBehavior(
            SnapLayoutInfoProvider(lazyGridState = gridState, snapPosition = SnapPosition.Start),
        )
    val isPortrait = getScreenSizeInfo().let { it.wDP < it.hDP }
    val density = LocalDensity.current
    var widthDp by remember { mutableStateOf(0.dp) }
    val rows = minOf(MAX_ROWS, songs.size)
    // QuickPicksItem is 44dp of cover plus padding: a 56dp cell on Android, 64dp elsewhere.
    val rowHeight = if (getPlatform() == Platform.Android) 56.dp else 64.dp

    Column(
        Modifier.onGloballyPositioned { coordinates ->
            with(density) { widthDp = coordinates.size.width.toDp() }
        },
    ) {
        HomeSectionHeader(data = data, navController = navController, onPlayAll = { homeViewModel.playAll(data) })
        LazyHorizontalGrid(
            rows = GridCells.Fixed(rows),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(rowHeight * rows)
                    .mouseDragScroll(gridState, flingBehavior),
            state = gridState,
            flingBehavior = flingBehavior,
        ) {
            items(songs) { song ->
                QuickPicksItem(
                    onClick = { song.playRadio(homeViewModel) },
                    onLongClick = { sheetTrack = song.toTrack() },
                    data = song,
                    widthDp = if (isPortrait) widthDp else minOf(widthDp, LandscapeRowMaxWidth + 30.dp),
                )
            }
        }
        if (getPlatform() == Platform.Desktop) {
            HorizontalScrollBar(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                scrollState = gridState,
                flingBehavior = flingBehavior,
            )
        }
    }
}
