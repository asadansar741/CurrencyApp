package com.asad.currency.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.asad.currency.data.local.database.CurrencyDatabase
import com.asad.currency.domain.DatabaseRepository
import com.asad.currency.domain.model.Currency
import com.asad.currency.domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseRepositoryImpl(
    private val database: CurrencyDatabase
) : DatabaseRepository {
    override suspend fun insertCurrencyData(currency: Currency) {
        database.currencySqlQueries.upsert(
            code = currency.code,
            value_ = currency.value.toString()
        )
    }

    override fun readCurrencyData(): Flow<RequestState<List<Currency>>> {
        return database.currencySqlQueries
            .getAllCurrencies()
            .asFlow()
            .mapToList(context = Dispatchers.IO)
            .map { currencies ->
                runCatching {
                    currencies.map {
                        Currency(it.code, it.value_.toDouble())
                    }
                }.fold(
                    onSuccess = { RequestState.Success(data = it) },
                    onFailure = { RequestState.Error(message = it.message ?: "Unknown error") }
                )
            }
    }

    /*override fun readCurrencyData(): Flow<RequestState<List<Currency>>> {
        return database.currencySqlQueries
            .getAllCurrencies()
            .asFlow()
            .mapToList(context = Dispatchers.IO)
            .map { currencies ->
                val mappedCurrencies = currencies.map { currency ->
                    Currency(
                        code = currency.code,
                        value = currency.value_.toDouble()
                    )
                }
                RequestState.Success(data = mappedCurrencies)
            }
    }*/

    override suspend fun delete() {
        database.currencySqlQueries
            .delete()
    }
}