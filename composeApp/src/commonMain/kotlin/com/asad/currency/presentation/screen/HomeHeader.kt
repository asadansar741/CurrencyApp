package com.asad.currency.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asad.currency.domain.model.Currency
import com.asad.currency.domain.model.CurrencyCode
import com.asad.currency.domain.model.DisplayResult
import com.asad.currency.domain.model.RateStatus
import com.asad.currency.domain.model.RequestState
import com.asad.currency.ui.theme.headerColor
import com.asad.currency.ui.theme.staleColor
import com.asad.currency.util.displayCurrentDateTime
import currencyapp.composeapp.generated.resources.Res
import currencyapp.composeapp.generated.resources.exchange_illustration
import currencyapp.composeapp.generated.resources.refresh_ic
import currencyapp.composeapp.generated.resources.switch_ic
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeHeader(
    status: RateStatus,
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    amount: Double,
    onAmountChange: (Double) -> Unit,
    onSwitchClick: () -> Unit,
    onRateRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(all = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(height = 24.dp))
        RatesStatus(
            status = status,
            onRateRefresh = onRateRefresh
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        CurrencyInputs(
            source = source,
            target = target,
            onSwitchClick = onSwitchClick
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        AmountInput(
            amount = amount,
            onAmountChange = onAmountChange
        )
    }
}

@Composable
fun RatesStatus(
    status: RateStatus,
    onRateRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier.size(size = 50.dp),
            painter = painterResource(resource = Res.drawable.exchange_illustration),
            contentDescription = "Exchange Rate illustration"
        )
        Spacer(modifier = Modifier.width(width = 12.dp))
        Column {
            Text(
                text = displayCurrentDateTime(),
                color = Color.White
            )
            Text(
                text = status.title,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                color = status.color
            )
        }
    }
    if (status == RateStatus.Stale) {
        IconButton(onClick = onRateRefresh) {
            Icon(
                modifier = Modifier.size(size = 24.dp),
                painter = painterResource(resource = Res.drawable.refresh_ic),
                contentDescription = "Refresh",
                tint = staleColor
            )
        }
    }
}

@Composable
fun RowScope.CurrencyView(
    placeholder: String,
    currency: RequestState<Currency>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(weight = 1f)
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = placeholder,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(height = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(shape = RoundedCornerShape(size = 8.dp))
                .height(height = 54.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            currency.DisplayResult(
                onSuccess = { data ->
                    Icon(
                        modifier = Modifier.size(size = 24.dp),
                        painter = painterResource(
                            resource = CurrencyCode.valueOf(value = data.code).flag
                        ),
                        tint = Color.Unspecified,
                        contentDescription = "Country Flag"
                    )
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Text(
                        text = CurrencyCode.valueOf(value = data.code).name,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.White
                    )
                }
            )
        }
    }
}

@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick: () -> Unit
) {
    var animationStarted by remember { mutableStateOf(value = false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (animationStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrencyView(
            placeholder = "from",
            currency = source,
            onClick = {}
        )
        Spacer(modifier = Modifier.height(height = 14.dp))
        IconButton(
            modifier = Modifier.padding(top = 24.dp)
                .graphicsLayer {
                    rotationY = animatedRotation
                },
            onClick = {
                animationStarted = !animationStarted
                onSwitchClick()
            }
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.switch_ic),
                contentDescription = "Switch Icon",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(height = 14.dp))
        CurrencyView(
            placeholder = "from",
            currency = target,
            onClick = {}
        )
    }
}

