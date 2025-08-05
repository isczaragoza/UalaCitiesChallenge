package com.isczaragoza.ualacitieschallenge.citylocationmap

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.BaseScreen
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.GreenA400
import kotlinx.coroutines.delay
import com.isczaragoza.ualacitieschallenge.infrastructure.R
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.LinearProgressIndicatorCustom
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.primary

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityLocationMapScreen(
    content: Long,
    state: CityLocationMapUiState,
    isTopAppBarContent: Boolean = true,
    setCityId: (Long) -> Unit,
    onDetailCityClick: (City) -> Unit,
    onBackClick: () -> Unit
) {
    LaunchedEffect(content) {
        setCityId(content)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (state is CityLocationMapUiState.Success && isTopAppBarContent) {
                            Text(state.city?.name ?: stringResource(R.string.empty_string))
                        }
                    }
                },
                navigationIcon = {
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isTopAppBarContent) {
                            IconButton(onClick = { onBackClick() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )

            BaseScreen {
                if (content == -1L) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.city_map_screen_empty_state))
                    }
                    return@BaseScreen
                }

                when (state) {
                    is CityLocationMapUiState.Loading -> {
                        LinearProgressIndicatorCustom()
                    }

                    is CityLocationMapUiState.Success -> {
                        HorizontalDivider(thickness = 4.dp, color = Color.Transparent)

                        if (state.city == null) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(stringResource(R.string.city_map_screen_empty_state))
                            }
                            return@BaseScreen
                        }

                        Column(Modifier.fillMaxSize()) {
                            val location = LatLng(state.city.lat, state.city.lon)
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(location, 12f)
                            }

                            LaunchedEffect(location) {
                                val current = cameraPositionState.position.target
                                if (current != location) {
                                    cameraPositionState.animate(CameraUpdateFactory.zoomTo(6f))
                                    delay(300)
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLng(
                                            location
                                        )
                                    )
                                    delay(300)
                                    cameraPositionState.animate(CameraUpdateFactory.zoomTo(12f))
                                }
                            }

                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = false
                                ),
                                onMapClick = {}
                            ) {
                                Marker(
                                    state = MarkerState(position = location),
                                    title = stringResource(
                                        R.string.city_map_screen_pin,
                                        state.city.name
                                    ),
                                    snippet = stringResource(
                                        R.string.city_map_screen_pin_location,
                                        location.latitude.toString(),
                                        location.longitude.toString()
                                    )
                                )
                            }
                        }
                    }

                    is CityLocationMapUiState.LoadFailed -> {
                        HorizontalDivider(thickness = 4.dp, color = Color.Transparent)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Error al cargar los datos de la ciudad")
                        }
                    }
                }
            }
        }

        if (state is CityLocationMapUiState.Success && state.city != null) {
            FloatingActionButton(
                onClick = {
                    onDetailCityClick(state.city)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = primary
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun CityLocationMapScreenPreview() {
    MaterialTheme {

    }
}