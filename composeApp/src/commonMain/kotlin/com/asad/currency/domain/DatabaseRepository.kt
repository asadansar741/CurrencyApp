package com.asad.currency.domain

import com.asad.currency.data.local.CurrencyEntity
import com.asad.currency.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun insertCurrencyData(currency: CurrencyEntity)
    fun readCurrencyData(): Flow<RequestState<List<CurrencyEntity>>>
    suspend fun delete()
}
