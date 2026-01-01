package com.asad.currency.di

import android.content.Context
import com.asad.currency.data.local.database.SqlDriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single { SqlDriverFactory(context = get<Context>()).getSqlDriver() }
    }