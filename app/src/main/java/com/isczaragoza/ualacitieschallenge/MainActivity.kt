package com.isczaragoza.ualacitieschallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.SplashScreenCustom
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.UalaCitiesChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UalaCitiesChallengeTheme {
                LaunchedEffect(Unit) {
                    viewModel.startSplash()
                }
                val splashScreenTime by viewModel.splashScreenTime.collectAsStateWithLifecycle()
                val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
                if (splashScreenTime < 10) {
                    SplashScreenCustom()
                    return@UalaCitiesChallengeTheme
                }
                LaunchedEffect(Unit) {
                    viewModel.worker()
                }
                UalaCitiesChallengeApp(isConnected)
            }
        }
    }
}
