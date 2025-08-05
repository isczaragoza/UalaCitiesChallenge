package com.isczaragoza.citydetail

import android.net.Uri
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.isczaragoza.ualacitieschallenge.infrastructure.R
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.LinearProgressIndicatorCustom
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.GreenA400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(
    content: Long,
    state: CityDetailUiState,
    isTopAppBarContent: Boolean = true,
    setCityId: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    LaunchedEffect(content) {
        setCityId(content)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                if (state is CityDetailUiState.Success && isTopAppBarContent) {
                    Text(state.city?.name ?: stringResource(R.string.empty_string))
                }
            },
            navigationIcon = {
                if (isTopAppBarContent) {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = null,
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White
            )
        )

        when (state) {
            is CityDetailUiState.Loading -> {
                LinearProgressIndicatorCustom()
            }

            is CityDetailUiState.Success -> {
                val city = state.city
                if (city == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.city_detail_screen_no_info))
                    }
                    return
                }

                val googleUrl = "https://www.google.com/search?q=${Uri.encode(city.name)}"
                val wikiUrl = "https://es.wikipedia.org/wiki/${Uri.encode(city.name)}"

                key(content) {
                    var fallbackToWiki by remember { mutableStateOf(false) }

                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                                webViewClient = object : WebViewClient() {
                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        if (!fallbackToWiki) {
                                            fallbackToWiki = true
                                            view?.loadUrl(wikiUrl)
                                        }
                                    }
                                }
                                loadUrl(googleUrl)
                            }
                        },
                        update = { webView ->
                            val urlToLoad = if (fallbackToWiki) wikiUrl else googleUrl
                            webView.loadUrl(urlToLoad)
                        }
                    )
                }
            }

            is CityDetailUiState.LoadFailed -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.load_error))
                }
            }
        }
    }
}
