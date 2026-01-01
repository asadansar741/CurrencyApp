package com.asad.currency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.asad.currency.di.appModule
import com.asad.currency.di.databaseModule
import com.asad.currency.di.getCoreDatabaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(
                getCoreDatabaseModule(),
                databaseModule,
                appModule
            )
        }
        enableEdgeToEdge()
        setContent {
            App()
            //set status bar color and status bar font color
            val darkTheme = isSystemInDarkTheme()
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = this.window
                    window.statusBarColor = Color.Transparent.toArgb()
                    window.navigationBarColor = Color.Transparent.toArgb()
                    val wic = WindowCompat.getInsetsController(window, view)
                    wic.isAppearanceLightStatusBars = false
                    wic.isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}