package com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.GreenA400
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.Grey200

@Composable
fun LinearProgressIndicatorCustom(progress: Int? = null) {
    if (progress == null) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = GreenA400,
            gapSize = .5.dp,
            trackColor = Grey200
        )
        return
    }
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp),
        color = GreenA400,
        gapSize = .5.dp,
        trackColor = Grey200,
        progress = { progress.toFloat() / 100 }
    )
}
