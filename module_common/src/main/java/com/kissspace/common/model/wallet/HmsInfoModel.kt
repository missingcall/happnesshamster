package com.kissspace.common.model.wallet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HmsInfoModel(
    @SerialName("cleanliness")
    val cleanliness: Int = 0,
    @SerialName("hamsterStatus")
    var hamsterStatus: String = "",
    @SerialName("satiety")
    val satiety: Int = 0
)