package com.isczaragoza.ualacitieschallenge.citylist

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.BaseScreen
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.CityItemCard
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.LinearProgressIndicatorCustom
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components.SearchToolbar
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.UalaCitiesChallengeTheme
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.GreenA400
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.primary


@Composable
fun CityListScreen(
    syncCities: () -> Unit,
    searchQuery: String,
    isFavoriteFiltered: Boolean,
    syncCitiesState: SyncCitiesState,
    cityListPaging: LazyPagingItems<City>,
    onSearchQueryChanged: (String) -> Unit,
    onItemClick: (City) -> Unit,
    toggleFavorite: (City) -> Unit,
    onFilterFavoriteClick: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        syncCities()
    }
    CityListScreenContent(
        searchQuery = searchQuery,
        isFavoriteFiltered = isFavoriteFiltered,
        syncCitiesState = syncCitiesState,
        cityListPaging = cityListPaging,
        onSearchQueryChanged = onSearchQueryChanged,
        onItemClick = onItemClick,
        toggleFavorite = toggleFavorite,
        onFilterFavoriteClick = onFilterFavoriteClick
    )
}

@Composable
private fun CityListScreenContent(
    searchQuery: String,
    isFavoriteFiltered: Boolean,
    syncCitiesState: SyncCitiesState,
    cityListPaging: LazyPagingItems<City>,
    onSearchQueryChanged: (String) -> Unit,
    onItemClick: (City) -> Unit,
    toggleFavorite: (City) -> Unit,
    onFilterFavoriteClick: (Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    val previousSearchQuery = rememberSaveable { mutableStateOf(searchQuery) }
    LaunchedEffect(searchQuery) {
        if (previousSearchQuery.value != searchQuery) {
            listState.animateScrollToItem(0)
            previousSearchQuery.value = searchQuery
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            SearchToolbar(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged
            )
            BaseScreen {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (syncCitiesState is SyncCitiesState.Loading) {
                        LinearProgressIndicatorCustom(syncCitiesState.progress)
                    } else {
                        HorizontalDivider(
                            thickness = 4.dp, color = Color.Transparent
                        )
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(state = listState) {
                            items(
                                cityListPaging.itemCount,
                                cityListPaging.itemKey { it.id },
                                cityListPaging.itemContentType { "City" }
                            ) { index ->
                                val city = cityListPaging[index]
                                if (city != null) {
                                    CityItemCard(city, onItemClick, {
                                        toggleFavorite(it)
                                    })
                                }
                            }
                            cityListPaging.apply {
                                when {
                                    loadState.append is LoadState.Loading -> {
                                        item {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    loadState.append is LoadState.Error -> {
                                        val e = cityListPaging.loadState.append as LoadState.Error
                                        item {
                                            Text(
                                                text = "Error cargando más datos: ${e.error.localizedMessage}",
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }

                                    loadState.refresh is LoadState.Error -> {
                                        val e = cityListPaging.loadState.refresh as LoadState.Error
                                        item {
                                            Text(
                                                text = "Error cargando datos: ${e.error.localizedMessage}",
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (syncCitiesState is SyncCitiesState.Loading) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .blur(16.dp)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .pointerInput(Unit) {
                                        detectTapGestures {}
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(16.dp)
                                )
                            }
                        }
                        cityListPaging.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .blur(16.dp)
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .pointerInput(Unit) {
                                                detectTapGestures {}
                                            },
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                onFilterFavoriteClick(isFavoriteFiltered)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = primary
        ) {
            Icon(
                imageVector = if (isFavoriteFiltered) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun CityListScreenPreview() {
    UalaCitiesChallengeTheme {
    }
}
