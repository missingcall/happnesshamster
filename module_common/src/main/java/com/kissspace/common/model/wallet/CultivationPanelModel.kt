package com.kissspace.common.model.wallet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CultivationPanelModel(
    @SerialName("cleanliness")
    val cleanliness: Cleanliness = Cleanliness(),
    @SerialName("satiety")
    val satiety: Satiety = Satiety()
) {
    @Serializable
    data class Cleanliness(
        @SerialName("consumption")
        val consumption: Int = 0,
        @SerialName("promote")
        val promote: Int = 0,
        @SerialName("prop")
        val prop: Int = 0
    )

    @Serializable
    data class Satiety(
        @SerialName("consumption")
        val consumption: Int = 0,
        @SerialName("promote")
        val promote: Int = 0,
        @SerialName("prop")
        val prop: Int = 0
    )
}