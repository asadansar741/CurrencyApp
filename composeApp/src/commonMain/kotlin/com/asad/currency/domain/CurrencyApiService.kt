package com.asad.currency.domain

import com.asad.currency.domain.model.Currency
import com.asad.currency.domain.model.RequestState

interface CurrencyApiService {
    suspend fun getLatestExchangeRates(): RequestState<List<Currency>>
}