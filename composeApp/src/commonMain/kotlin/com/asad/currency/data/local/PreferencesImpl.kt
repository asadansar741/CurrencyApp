package com.asad.currency.data.local

import com.asad.currency.domain.PreferencesRepository
import com.asad.currency.domain.model.CurrencyCode
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalSettingsApi::class)
class PreferencesImpl(
    private val settings: Settings
) : PreferencesRepository {
    companion object {
        const val TIME_STAMP = "lastUpdated"
        const val SOURCE_CURRENCY_KEY = "sourceCurrency"
        const val TARGET_CURRENCY_KEY = "TargetCurrency"
        val DEFAULT_SOURCE_CURRENCY = CurrencyCode.USD.name
        val DEFAULT_TARGET_CURRENCY = CurrencyCode.EUR.name
    }

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()


    @OptIn(ExperimentalTime::class)
    override suspend fun saveLastUpdated(lastUpdated: String) {
        flowSettings.putLong(
            key = TIME_STAMP,
            value = Instant.Companion.parse(lastUpdated).toEpochMilliseconds()
        )
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun isDataFresh(currentTimeStamp: Long): Boolean {
        val savedTimeStamp = flowSettings.getLong(
            key = TIME_STAMP,
            defaultValue = 0L
        )
        return if (savedTimeStamp != 0L) {
            val currentInstant = Instant.Companion.fromEpochMilliseconds(currentTimeStamp)
            val savedInstant = Instant.Companion.fromEpochMilliseconds(savedTimeStamp)
            val currentDateTime = currentInstant.toLocalDateTime(TimeZone.Companion.currentSystemDefault())
            val savedTime = savedInstant.toLocalDateTime(TimeZone.Companion.currentSystemDefault())
            val daysDifference = currentDateTime.date.dayOfYear - savedTime.date.dayOfYear
            daysDifference <= 1
        } else false
    }

    override suspend fun saveSourceCurrencyCode(code: String) {
        flowSettings.putString(
            key = SOURCE_CURRENCY_KEY,
            value = code
        )
    }

    override suspend fun saveTargetCurrencyCode(code: String) {
        flowSettings.putString(
            key = TARGET_CURRENCY_KEY,
            value = code
        )
    }

    override fun readSourceCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = SOURCE_CURRENCY_KEY,
            defaultValue = DEFAULT_SOURCE_CURRENCY
        ).map { CurrencyCode.valueOf(value = it) }
    }

    override fun readTargetCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = TARGET_CURRENCY_KEY,
            defaultValue = DEFAULT_TARGET_CURRENCY
        ).map { CurrencyCode.valueOf(value = it) }
    }
}