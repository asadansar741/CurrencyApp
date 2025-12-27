package com.asad.currency.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.asad.currency.domain.CurrencyApiService
import com.asad.currency.domain.PreferencesRepository
import com.asad.currency.domain.model.Currency
import com.asad.currency.domain.model.RateStatus
import com.asad.currency.domain.model.RequestState
import kotlinx.coroutines.launch
import kotlin.time.Clock


sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
    data object Switch : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _sourceCurrency: MutableState<RequestState<Currency>> = mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> = mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency



    init {
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    private suspend fun fetchNewRates() {
        try {
            api.getLatestExchangeRates()
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun getRateStatus() {
        _rateStatus.value = if (preferences.isDataFresh(
                currentTimeStamp = Clock.System.now().toEpochMilliseconds()
            )
        ) RateStatus.Fresh
        else RateStatus.Stale
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> screenModelScope.launch { fetchNewRates() }
            HomeUiEvent.Switch -> {}
        }
    }

}