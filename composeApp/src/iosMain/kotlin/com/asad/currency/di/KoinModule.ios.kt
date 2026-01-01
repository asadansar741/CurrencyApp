package com.asad.currency.di

import com.asad.currency.data.local.database.SqlDriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Darwin.create() }
        single { SqlDriverFactory().getSqlDriver() }
    }