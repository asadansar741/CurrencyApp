package com.asad.currency.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.asad.currency.domain.CurrencyApiService
import com.asad.currency.domain.DatabaseRepository
import com.asad.currency.domain.PreferencesRepository
import com.asad.currency.domain.model.Currency
import com.asad.currency.domain.model.RateStatus
import com.asad.currency.domain.model.RequestState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
    data object Switch : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val dbRepository: DatabaseRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _sourceCurrency: MutableState<RequestState<Currency>> = mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> = mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies



    init {
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun fetchNewRates() {
        try {
            val localCache = dbRepository.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("Tag: Database is full")
                    _allCurrencies.clear()
                    _allCurrencies.addAll(elements = localCache.getSuccessData())
                    if (!preferences.isDataFresh(
                            currentTimeStamp = Clock.System.now().toEpochMilliseconds()
                        )
                    ) {
                        println("HomeViewModel: DATA NOT FRESH")
                        cacheTheData()
                    } else {
                        println("HomeViewModel: DATA IS FRESH")
                    }
                } else {
                    println("HomeViewModel: DATABASE NEEDS DATA")
                    cacheTheData()
                }
            } else if (localCache.isError()) {
                println("HomeViewModel: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
            }

            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun cacheTheData() {
        val fetchedData = api.getLatestExchangeRates()
        if (fetchedData.isSuccess()) {
            dbRepository.delete()
            fetchedData.getSuccessData().forEach {
                println("HomeViewModel: ADDING ${it.code}")
                dbRepository.insertCurrencyData(currency = it)
            }
            println("HomeViewModel: UPDATING _allCurrencies")
            _allCurrencies.clear()
            _allCurrencies.addAll(elements = fetchedData.getSuccessData())
        } else if (fetchedData.isError()) {
            println("HomeViewModel: FETCHING FAILED ${fetchedData.getErrorMessage()}")
        }
    }

    @OptIn(ExperimentalTime::class)
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