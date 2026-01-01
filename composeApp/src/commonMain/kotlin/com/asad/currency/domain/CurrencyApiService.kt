package com.asad.currency.domain

import com.asad.currency.domain.model.CurrencyModel
import com.asad.currency.domain.model.RequestState

interface CurrencyApiService {
    suspend fun getLatestExchangeRates(): RequestState<List<CurrencyModel>>
}