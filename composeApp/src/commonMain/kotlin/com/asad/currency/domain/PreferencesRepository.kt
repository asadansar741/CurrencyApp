package com.asad.currency.domain

interface PreferencesRepository {
    suspend fun saveLastUpdated(lastUpdated: String)
    suspend fun isDataFresh(currentTimeStamp: Long): Boolean
}