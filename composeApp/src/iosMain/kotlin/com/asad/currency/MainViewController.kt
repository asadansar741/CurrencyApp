package com.asad.currency

import androidx.compose.ui.window.ComposeUIViewController
import com.asad.currency.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }