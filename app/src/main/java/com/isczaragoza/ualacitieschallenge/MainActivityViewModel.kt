package com.isczaragoza.ualacitieschallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isczaragoza.ualacitieschallenge.domain.usecases.StartSyncCitiesWorkerUseCase
import com.isczaragoza.ualacitieschallenge.infrastructure.InternetConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainActivityViewModel
@Inject
constructor(
    private val internetConnectivityObserver: InternetConnectivityObserver,
    private val startSyncCitiesWorkerUseCase: StartSyncCitiesWorkerUseCase
) : ViewModel() {
    private val _splashScreenTime = MutableStateFlow(0)
    val splashScreenTime = _splashScreenTime.asStateFlow()
    private var lastStateConnection = true
    val isConnected =
        internetConnectivityObserver
            .isConnected
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                false
            )

    private var splashJob: Job? = null
    private var job: Job? = null

    fun worker() {
        if (job != null) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            isConnected.collect { isConnected ->
                println("isConnected:$isConnected MainActivityViewModel")
                if (lastStateConnection && isConnected.not()) {
                    lastStateConnection = false
                    return@collect
                }
                if (lastStateConnection && isConnected) {
                    lastStateConnection = true
                    return@collect
                }
                if (lastStateConnection.not() && isConnected) {
                    lastStateConnection = true
                    startSyncCitiesWorkerUseCase().launchIn(viewModelScope)
                    return@collect
                }
                if (lastStateConnection.not() && isConnected.not()) {
                    lastStateConnection = false
                    return@collect
                }
            }
        }
    }

    fun startSplash() {
        if (splashJob != null) {
            return
        }
        splashJob =
            viewModelScope.launch(Dispatchers.IO) {
                while (splashScreenTime.value < 10) {
                    delay(120)
                    _splashScreenTime.value = splashScreenTime.value + 1
                }
            }
    }
}
