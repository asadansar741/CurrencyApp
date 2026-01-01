package com.asad.currency

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.asad.currency.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "CurrencyApp",
    ) {
        App()
    }
}