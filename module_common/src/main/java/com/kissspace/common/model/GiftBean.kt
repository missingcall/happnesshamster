package com.kissspace.common.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.IgnoredOnParcel

@Serializable
@Parcelize
data class GiftBean(
    @SerialName("giftId")
    var giftId: String = "",
    @SerialName("giftName")
    var giftName: String = "",
    @SerialName("info")
    var info: String = "",
    @SerialName("price")
    var price: Int = 0,
    @SerialName("svg")
    var svg: String = "",
    @SerialName("url")
    var url: String = ""
) : Parcelable,BaseObservable(){

    @IgnoredOnParcel
    var select = false
}