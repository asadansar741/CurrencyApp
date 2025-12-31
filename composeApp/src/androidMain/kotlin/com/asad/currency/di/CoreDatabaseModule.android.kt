package com.asad.currency.di

import android.content.Context
import com.asad.currency.data.local.database.SqlDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun getCoreDatabaseModule(): Module {
    return module {
        single { SqlDriverFactory(context = get<Context>()).getSqlDriver() }
    }
}