package com.asad.currency.presentation.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.asad.currency.domain.model.CurrencyType
import com.asad.currency.presentation.component.CurrencyPickerDialog
import com.asad.currency.presentation.component.HomeBody
import com.asad.currency.presentation.component.HomeHeader

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        val allCurrencies = viewModel.allCurrencies
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency
        var amount by rememberSaveable { mutableStateOf(0.0) }
        var selectedCurrencyType: CurrencyType by remember {
            mutableStateOf(CurrencyType.None)
        }
        var dialogOpened by remember { mutableStateOf(value = false) }
        if (dialogOpened && selectedCurrencyType != CurrencyType.None) {
            CurrencyPickerDialog(
                currencies = allCurrencies,
                currencyType = selectedCurrencyType,
                onConfirmClick = { currencyCode ->
                    if (selectedCurrencyType is CurrencyType.Source) {
                        viewModel.sendEvent(
                            HomeUiEvent.SaveSourceCurrencyCode(
                                code = currencyCode.name
                            )
                        )
                    } else if (selectedCurrencyType is CurrencyType.Target) {
                        viewModel.sendEvent(
                            HomeUiEvent.SaveTargetCurrencyCode(
                                code = currencyCode.name
                            )
                        )
                    }
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                },
                onDismiss = {
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                }
            )
        }

        Column {
            HomeHeader(
                status = rateStatus,
                onRateRefresh = {
                    viewModel.sendEvent(
                        event = HomeUiEvent.RefreshRates
                    )
                },
                source = sourceCurrency,
                target = targetCurrency,
                onSwitchClick = { viewModel.sendEvent(HomeUiEvent.SwitchCurrencies) },
                amount = amount,
                onAmountChange = { amount = it },
                onCurrencyTypeSelect = { currencyType ->
                    selectedCurrencyType = currencyType
                    dialogOpened = true
                }
            )
            HomeBody(
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount
            )
        }
    }
}

@Composable
fun AmountInput(
    amount: Double,
    onAmountChange: (Double) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .animateContentSize()
            .height(54.dp),
        value = "$amount",
        onValueChange = {
            onAmountChange(it.toDouble())
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            disabledContainerColor = Color.White.copy(alpha = 0.05f),
            errorContainerColor = Color.White.copy(alpha = 0.05f),
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}