package com.kissspace.common.model.wallet


import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CultivationPanelModel(
    @SerialName("cleanliness")
    val cleanliness: Cleanliness = Cleanliness(),
    @SerialName("satiety")
    val satiety: Satiety = Satiety()
) : Parcelable, BaseObservable(){
    @Serializable
    @Parcelize
    data class Cleanliness(
        @SerialName("consumption")
        val consumption: Int = 0,
        @SerialName("costPropCount")
        val costPropCount: Int = 0,
        @SerialName("promote")
        val promote: Int = 0,
        @SerialName("prop")
        val prop: Int = 0
    ) : Parcelable

    @Serializable
    @Parcelize
    data class Satiety(
        @SerialName("consumption")
        val consumption: Int = 0,
        @SerialName("costPropCount")
        val costPropCount: Int = 0,
        @SerialName("promote")
        val promote: Int = 0,
        @SerialName("prop")
        val prop: Int = 0
    ) : Parcelable
}