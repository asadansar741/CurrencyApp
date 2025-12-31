package com.asad.currency

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.asad.currency.presentation.screen.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
//    val colorScheme = if (!isSystemInDarkTheme()) LightColors else DarkColors
//    initializeKoin()
    MaterialTheme() {
        Navigator(HomeScreen())
    }
}