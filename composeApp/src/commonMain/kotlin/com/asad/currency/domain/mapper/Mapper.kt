package com.asad.currency.domain.mapper

import com.asad.currency.data.local.CurrencyEntity
import com.asad.currency.domain.model.CurrencyModel

fun CurrencyModel.toCurrencyEntity(): CurrencyEntity {
    return CurrencyEntity(
        id = id,
        code = code,
        value = value
    )
}

fun CurrencyEntity.toCurrencyModel(): CurrencyModel {
    return CurrencyModel(
        id = id,
        code = code,
        value = value
    )
}