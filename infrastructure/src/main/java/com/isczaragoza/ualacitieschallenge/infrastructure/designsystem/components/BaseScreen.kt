package com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BaseScreen(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(start = 8.dp, end = 8.dp)) {
        content()
    }
}