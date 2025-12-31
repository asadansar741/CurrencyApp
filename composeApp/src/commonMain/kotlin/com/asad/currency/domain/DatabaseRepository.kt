package com.asad.currency.domain

import com.asad.currency.domain.model.Currency
import com.asad.currency.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun insertCurrencyData(currency: Currency)
    fun readCurrencyData(): Flow<RequestState<List<Currency>>>
    suspend fun delete()
}
