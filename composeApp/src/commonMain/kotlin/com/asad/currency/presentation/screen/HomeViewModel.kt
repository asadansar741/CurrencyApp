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
import com.asad.currency.domain.mapper.toCurrencyEntity
import com.asad.currency.domain.mapper.toCurrencyModel
import com.asad.currency.domain.model.CurrencyModel
import com.asad.currency.domain.model.RateStatus
import com.asad.currency.domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
    data object SwitchCurrencies : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val dbRepository: DatabaseRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _sourceCurrency: MutableState<RequestState<CurrencyModel>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<CurrencyModel>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<CurrencyModel>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<CurrencyModel>> = _targetCurrency

    private var _allCurrencies = mutableStateListOf<CurrencyModel>()
    val allCurrencies: List<CurrencyModel> = _allCurrencies


    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
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
                    _allCurrencies.addAll(
                        elements = localCache.getSuccessData().map { currencyEntity->
                            currencyEntity.toCurrencyModel()
                        }
                    )
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
                dbRepository.insertCurrencyData(currency = it.toCurrencyEntity())
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

    private fun readSourceCurrency() {
        screenModelScope.launch(context = Dispatchers.Main) {
            preferences.readSourceCurrencyCode().collect { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _sourceCurrency.value = RequestState.Error(message = "Currency not found")
                }
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(context = Dispatchers.Main) {
            preferences.readTargetCurrencyCode().collect { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _targetCurrency.value = RequestState.Error(message = "Currency not found")
                }
            }
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> screenModelScope.launch { fetchNewRates() }
            HomeUiEvent.SwitchCurrencies -> switchCurrency()
        }
    }

    private fun switchCurrency() {
        val source = _sourceCurrency.value
        val target = _targetCurrency.value
        _sourceCurrency.value = target
        _targetCurrency.value = source
    }
}