package com.kissspace.mine.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpectedTransferConversionModel(
    @SerialName("configHandingFee")
    val configHandingFee: Int = 0,
    @SerialName("configSourceAmount")
    val configSourceAmount: Int = 0,
    @SerialName("configTargetAmount")
    val configTargetAmount: Int = 0,
    @SerialName("handingFee")
    val handingFee: Double = 0.0,
    @SerialName("sourceAmount")
    val sourceAmount: Double = 0.0,
    @SerialName("targetAmount")
    val targetAmount: Double = 0.0
)