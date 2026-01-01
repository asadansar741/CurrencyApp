package com.asad.currency

import android.app.Application
import com.asad.currency.di.initKoin
import org.koin.android.ext.koin.androidContext

class CurrencyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CurrencyApplication)
        }
    }
}