package com.kissspace.common.model.wallet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RevivePanelModel(
    @SerialName("pineCone")
    val pineCone: Double = 0.0,
    @SerialName("pineNuts")
    val pineNuts: Double = 0.0,
)