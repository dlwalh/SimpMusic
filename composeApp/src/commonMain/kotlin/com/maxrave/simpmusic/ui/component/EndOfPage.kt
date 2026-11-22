package com.maxrave.simpmusic.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Blank space at the bottom of a scrolling page, so its last row clears the mini player and the
 * bottom bar. The credit line (year, app name, version, author) that used to sit at its top is gone,
 * which is why [withoutCredit] no longer changes anything. The parameter stays so existing callers
 * keep compiling.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun EndOfPage(withoutCredit: Boolean = false) {
    Spacer(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(280.dp),
    )
}
