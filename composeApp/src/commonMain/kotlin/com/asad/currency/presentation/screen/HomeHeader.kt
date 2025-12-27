package com.asad.currency.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.asad.currency.domain.model.RateStatus
import com.asad.currency.ui.theme.headerColor
import com.asad.currency.ui.theme.staleColor
import com.asad.currency.util.displayCurrentDateTime
import currencyapp.composeapp.generated.resources.Res
import currencyapp.composeapp.generated.resources.exchange_illustration
import currencyapp.composeapp.generated.resources.refresh_ic
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeHeader(
    status: RateStatus,
    onRateRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(all = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        RatesStatus(
            status = status,
            onRateRefresh = onRateRefresh
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