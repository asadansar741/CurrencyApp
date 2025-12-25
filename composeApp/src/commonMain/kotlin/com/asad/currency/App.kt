package com.asad.currency

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.asad.currency.presentation.screen.HomeScreen
import com.asad.currency.ui.theme.DarkColors
import com.asad.currency.ui.theme.LightColors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val colorScheme = if (!isSystemInDarkTheme()) LightColors else DarkColors
    MaterialTheme(colorScheme = colorScheme) {
        Navigator(HomeScreen())
    }
}