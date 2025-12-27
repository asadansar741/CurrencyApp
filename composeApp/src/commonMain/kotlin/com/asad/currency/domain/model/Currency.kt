package com.asad.currency.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class Currency{
//    @PrimaryKey
//    var id: ObjectId = ObjectId()

    @SerialName("code")
    var code: String = ""

    @SerialName("value")
    var value: Double = 0.0
}
