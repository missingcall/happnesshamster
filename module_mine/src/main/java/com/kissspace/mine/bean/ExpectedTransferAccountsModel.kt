package com.kissspace.mine.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpectedTransferAccountsModel(
    @SerialName("lossAmount")
    val lossAmount: Double = 0.0,
    @SerialName("lossRate")
    val lossRate: Double = 0.0,
    @SerialName("sourceAmount")
    val sourceAmount: Double = 0.0,
    @SerialName("targetAmount")
    val targetAmount: Double = 0.0
)