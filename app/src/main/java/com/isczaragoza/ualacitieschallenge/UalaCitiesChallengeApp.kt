package com.isczaragoza.ualacitieschallenge

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.isczaragoza.citydetail.CityDetailScreen
import com.isczaragoza.citydetail.CityDetailViewModel
import com.isczaragoza.ualacitieschallenge.citylist.CityListScreen
import com.isczaragoza.ualacitieschallenge.citylist.CityListViewModel
import com.isczaragoza.ualacitieschallenge.citylocationmap.CityLocationMapScreen
import com.isczaragoza.ualacitieschallenge.citylocationmap.CityLocationMapViewModel
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.infrastructure.R
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.GreenA400
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.Grey800
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.YellowA400
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun UalaCitiesChallengeApp(isConnected: Boolean) {
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    var lastStateConnection by rememberSaveable { mutableStateOf(true) }
    val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val navController = rememberNavController()
    println("Is Connected: $isConnected")
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor =
                        if (data.visuals.message == stringResource(R.string.connection_recovered)) {
                            GreenA400
                        } else {
                            YellowA400
                        },
                    contentColor = Grey800,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { innerPadding ->
        /**
         * Aquí pueden comentar la linea correspondiente para probar cualquiera de las 2 implementaciones.
         * */
        ListDetailPaneApproach(modifier = Modifier.padding(innerPadding))
        /*Column(modifier = Modifier.padding(innerPadding)) {
            ManualTwoPaneApproach(navController, isLandScape)
        }*/
    }
    val message =
        if (isConnected.not()) {
            stringResource(R.string.connection_lost)
        } else {
            stringResource(
                R.string.connection_recovered
            )
        }
    LaunchedEffect(isConnected) {
        if (isConnected.not()) {
            lastStateConnection = false
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Indefinite
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
            if (lastStateConnection && isConnected.not()) {
                lastStateConnection = false
                return@LaunchedEffect
            }
            if (lastStateConnection && isConnected) {
                return@LaunchedEffect
            }
            if (lastStateConnection.not() && isConnected) {
                lastStateConnection = true
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                return@LaunchedEffect
            }
        }
    }
}

/**
 * Implementé 2 enfoques diferentes para crear la vista dividida en 2 cuando se encuentra en Landscape
 * la aplicación, porque hay una forma Nativa de hacerlo con "NavigableListDetailPaneScaffold" porque
 * se menciona en el documento de la prueba técnica que no se puede usar librerias en beta, pero
 * aunque la librería de Compose ya es estable desde hace mucho el atributo Composable está marcado
 * como experimental entonces para probar mis pantallas implementé el ListDetailPane una vez funcional
 * todas las pantallas realicé la implementación nativa con NavHost (ManualTwoPaneApproach).
 * Ambas son funcionales, si gustan probar, pueden realizar el cambio de llamada de función en el Scaffold.
 * */
@Composable
fun ManualTwoPaneApproach(
    navController: NavHostController,
    isLandScape: Boolean
) {
    val startDestination = if (isLandScape) "cityList" else "cityListMap"
    NavHost(navController = navController, "cityList") {
        composable("cityList") {
            CityListResponsive(navController, isLandScape)
        }
        composable("map/{cityId}") { backStackEntry ->
            val content = backStackEntry.arguments?.getString("cityId")?.toLongOrNull() ?: -1L
            LocationMapResponsive(navController, isLandScape, content)
        }
        composable("extra/{cityId}") { backStackEntry ->
            val content = backStackEntry.arguments?.getString("cityId")?.toLongOrNull() ?: -1L
            val viewModel: CityDetailViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val onBackClick: () -> Unit = {
                navController.popBackStack("map/{cityId}", false)
            }
            CityDetailScreen(content, state, true, viewModel::setCityId, onBackClick)
        }
    }
}

@Composable
fun CityListResponsive(
    navController: NavHostController,
    isLandScape: Boolean
) {
    if (isLandScape.not()) {
        val viewModel: CityListViewModel = hiltViewModel()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val isFavoriteFiltered by viewModel.isFavoriteFiltered.collectAsStateWithLifecycle()
        val syncCitiesState by viewModel.syncCitiesState.collectAsStateWithLifecycle()
        val cityListPaging = viewModel.cityListPaging.collectAsLazyPagingItems()
        CityListScreen(
            viewModel::startCitySyncOneTimeWorker,
            searchQuery,
            isFavoriteFiltered,
            syncCitiesState,
            cityListPaging,
            viewModel::updateSearchQuery,
            { city ->
                val cityId = city.id
                navController.navigate("map/$cityId") {
                    launchSingleTop = true
                }
            },
            viewModel::toggleFavorite,
            viewModel::filterByFavorites
        )
        return
    }
    Row {
        val viewModelCityList: CityListViewModel = hiltViewModel()
        val searchQuery by viewModelCityList.searchQuery.collectAsStateWithLifecycle()
        val isFavoriteFiltered by viewModelCityList.isFavoriteFiltered.collectAsStateWithLifecycle()
        val syncCitiesState by viewModelCityList.syncCitiesState.collectAsStateWithLifecycle()
        val cityListPaging = viewModelCityList.cityListPaging.collectAsLazyPagingItems()
        var cityId: Long by rememberSaveable { mutableLongStateOf(-1) }
        Box(Modifier.weight(1f)) {
            CityListScreen(
                viewModelCityList::startCitySyncOneTimeWorker,
                searchQuery,
                isFavoriteFiltered,
                syncCitiesState,
                cityListPaging,
                viewModelCityList::updateSearchQuery,
                { city ->
                    cityId = city.id
                },
                viewModelCityList::toggleFavorite,
                viewModelCityList::filterByFavorites
            )
        }

        val viewModelLocationMap: CityLocationMapViewModel = hiltViewModel()
        val state by viewModelLocationMap.state.collectAsStateWithLifecycle()
        val onDetailCityClick: (City) -> Unit = { city ->
            val cityId = city.id
            navController.navigate("map/$cityId") {
                launchSingleTop = true
                restoreState = true
            }
        }
        val onBackClick: () -> Unit = {
            navController.popBackStack()
        }
        Box(Modifier.weight(1f)) {
            CityLocationMapScreen(
                cityId,
                state,
                true,
                viewModelLocationMap::setCityId,
                onDetailCityClick,
                onBackClick
            )
        }
    }
}

@Composable
fun LocationMapResponsive(
    navController: NavHostController,
    isLandScape: Boolean,
    content: Long
) {
    if (isLandScape.not()) {
        val viewModel: CityLocationMapViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val onDetailCityClick: (City) -> Unit = { city ->
            val cityId = city.id
            navController.navigate("extra/$cityId") {
                launchSingleTop = true
                restoreState = true
            }
        }
        val onBackClick: () -> Unit = {
            navController.popBackStack()
        }
        CityLocationMapScreen(
            content,
            state,
            true,
            viewModel::setCityId,
            onDetailCityClick,
            onBackClick
        )
        return
    }
    Row {
        val viewModelLocationMap: CityLocationMapViewModel = hiltViewModel()
        val stateLocationMap by viewModelLocationMap.state.collectAsStateWithLifecycle()
        var cityIdToCityDetailScreen: Long by rememberSaveable { mutableLongStateOf(-1) }
        val onDetailCityClick: (City) -> Unit = { city ->
            cityIdToCityDetailScreen = city.id
        }
        val onBackClick: () -> Unit = {
            navController.popBackStack()
        }
        Box(Modifier.weight(1f)) {
            CityLocationMapScreen(
                content,
                stateLocationMap,
                true,
                viewModelLocationMap::setCityId,
                onDetailCityClick,
                onBackClick
            )
        }
        val viewModelDetail: CityDetailViewModel = hiltViewModel()
        val stateDetail by viewModelDetail.state.collectAsStateWithLifecycle()
        Box(Modifier.weight(1f)) {
            CityDetailScreen(
                content,
                stateDetail,
                false,
                viewModelDetail::setCityId,
                onBackClick
            )
        }
    }
}

/**
 * Aquí se anota como experimental...
 * */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ListDetailPaneApproach(modifier: Modifier = Modifier) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val navigator =
        rememberListDetailPaneScaffoldNavigator<Any>(
            scaffoldDirective =
                calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(
                    currentWindowAdaptiveInfo()
                )
        )
    NavigableListDetailPaneScaffold(
        modifier = modifier,
        navigator = navigator,
        listPane = {
            val viewModel: CityListViewModel = viewModel()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val isFavoriteFiltered by viewModel.isFavoriteFiltered.collectAsStateWithLifecycle()
            val syncCitiesState by viewModel.syncCitiesState.collectAsStateWithLifecycle()
            val cityListPaging = viewModel.cityListPaging.collectAsLazyPagingItems()
            CityListScreen(
                viewModel::startCitySyncOneTimeWorker,
                searchQuery,
                isFavoriteFiltered,
                syncCitiesState,
                cityListPaging,
                viewModel::updateSearchQuery,
                { city ->
                    coroutineScope.launch {
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail,
                            contentKey = city.id
                        )
                    }
                },
                viewModel::toggleFavorite,
                viewModel::filterByFavorites
            )
        },
        detailPane = {
            val content =
                navigator.currentDestination
                    ?.contentKey
                    ?.toString()
                    ?.toLongOrNull() ?: -1L
            val viewModel: CityLocationMapViewModel = viewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val onDetailCityClick: (City) -> Unit = { city ->
                coroutineScope.launch {
                    navigator.navigateTo(
                        pane = ListDetailPaneScaffoldRole.Extra,
                        contentKey = city.id
                    )
                }
            }
            val onBackClick: () -> Unit = {
                coroutineScope.launch {
                    navigator.navigateBack()
                }
            }
            CityLocationMapScreen(
                content,
                state,
                true,
                viewModel::setCityId,
                onDetailCityClick,
                onBackClick
            )
        },
        extraPane = {
            val content =
                navigator.currentDestination
                    ?.contentKey
                    ?.toString()
                    ?.toLongOrNull() ?: -1L
            val viewModel: CityDetailViewModel = viewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val onBackClick: () -> Unit = {
                coroutineScope.launch {
                    navigator.navigateBack()
                }
            }
            CityDetailScreen(content, state, true, viewModel::setCityId, onBackClick)
        }
    )
}
