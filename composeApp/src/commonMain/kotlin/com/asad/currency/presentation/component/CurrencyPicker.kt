package com.asad.currency.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.asad.currency.domain.model.CurrencyCode
import com.asad.currency.domain.model.CurrencyModel
import com.asad.currency.domain.model.CurrencyType
import com.asad.currency.ui.theme.primaryColor
import com.asad.currency.ui.theme.surfaceColor
import com.asad.currency.ui.theme.textColor

@Composable
fun CurrencyPickerDialog(
    currencies: List<CurrencyModel>,
    currencyType: CurrencyType,
    onPositiveClick: (CurrencyCode) -> Unit,
    onDismiss: () -> Unit
) {
    val allCurrencies = remember {
        mutableStateListOf<CurrencyModel>().apply { addAll(elements = currencies) }
    }
    var searchQuery by remember { mutableStateOf(value = "") }
    var selectedCurrencyCode by remember(key1 = currencyType) {
        mutableStateOf(value = currencyType.code)
    }

    AlertDialog(
        containerColor = surfaceColor,
        title = {
            Text(
                text = "Select a currency",
                color = textColor
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp)),
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query.uppercase()
                        if (query.isNotEmpty()) {
                            val filteredCurrencies = allCurrencies.filter {
                                it.code.contains(other = query.uppercase())
                            }
                            allCurrencies.clear()
                            allCurrencies.addAll(elements = filteredCurrencies)
                        } else {
                            allCurrencies.clear()
                            allCurrencies.addAll(elements = currencies)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search here",
                            color = textColor.copy(alpha = 0.38f),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textColor.copy(alpha = 0.1f),
                        unfocusedContainerColor = textColor.copy(alpha = 0.1f),
                        disabledContainerColor = textColor.copy(alpha = 0.1f),
                        errorContainerColor = textColor.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = textColor
                    )
                )
                Spacer(modifier = Modifier.height(height = 20.dp))
                AnimatedContent(
                    targetState = allCurrencies
                ) { availableCurrencies ->
                    if (availableCurrencies.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = availableCurrencies,
                                key = { it.id?.toHexString() ?: 0 }
                            ) { currency ->
                                CurrencyCodePickerView(
                                    code = CurrencyCode.valueOf(currency.code),
                                    isSelected = selectedCurrencyCode.name == currency.code,
                                    onSelect = { selectedCurrencyCode = it }
                                )
                            }
                        }
                    } else {
                        ErrorScreen(modifier = Modifier.height(250.dp))
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = textColor
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onPositiveClick(selectedCurrencyCode) }) {
                Text(
                    text = "Confirm",
                    color = primaryColor
                )
            }
        }
    )
}
