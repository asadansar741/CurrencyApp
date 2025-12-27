package com.asad.currency.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        Column {
            HomeHeader(
                status = rateStatus,
                onRateRefresh = {
                    viewModel.sendEvent(
                        event = HomeUiEvent.RefreshRates
                    )
                }
            )
        }
    }
}