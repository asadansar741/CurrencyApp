package com.asad.currency.data.remote.api

import com.asad.currency.domain.CurrencyApiService
import com.asad.currency.domain.model.CurrencyApiResponse
import com.asad.currency.domain.model.CurrencyItemResponse
import com.asad.currency.domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl : CurrencyApiService {
    companion object {
        const val API_KEY = "cur_live_MwUvf69i63wfOGyrd9IiJWDX3ANVnSUcbsK11MEj"
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
    }

    private val httpClient = HttpClient {
        install(plugin = ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(plugin = HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(plugin = DefaultRequest) {
            headers {
                append(name = "apikey", value = API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<CurrencyItemResponse>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            if (response.status.value == 200) {
                println("API Response: ${response.body<String>()}")
                val apiResponse = Json.decodeFromString<CurrencyApiResponse>(response.body())
                RequestState.Success(data = apiResponse.data.values.toList())
            } else {
                RequestState.Error(message = "Http Error Code${response.status}")
            }
        } catch (e: Exception) {
            RequestState.Error(message = e.message.toString())
        }
    }
}