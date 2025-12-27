package com.asad.currency.data

import com.asad.currency.domain.PreferencesRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@OptIn(ExperimentalSettingsApi::class)
class PreferencesImpl(
    private val settings: Settings
) : PreferencesRepository {
    companion object {
        const val TIME_STAMP = "lastUpdated"
    }

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()

    override suspend fun saveLastUpdated(lastUpdated: String) {
        flowSettings.putLong(
            key = TIME_STAMP,
            value = Instant.parse(lastUpdated).toEpochMilliseconds()
        )
    }

    override suspend fun isDataFresh(currentTimeStamp: Long): Boolean {
        val savedTimeStamp = flowSettings.getLong(
            key = TIME_STAMP,
            defaultValue = 0L
        )
        return if (savedTimeStamp != 0L) {
            val currentInstant = Instant.fromEpochMilliseconds(currentTimeStamp)
            val savedInstant = Instant.fromEpochMilliseconds(savedTimeStamp)
            val currentDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val savedTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val daysDifference = currentDateTime.date.dayOfYear - savedTime.date.dayOfYear
            daysDifference <= 1
        } else false
    }
}