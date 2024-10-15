package com.kissspace.mine.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class RecordByPoint(
    @SerialName("consumeGemstone")
    var consumeGemstone: String = "0",
    @SerialName("conversionRatio")
    var conversionRatio: Int = 0,
    @SerialName("displayId")
    var displayId: String = "",
    @SerialName("freezeStatus")
    var freezeStatus: String = "",
    @SerialName("integralIncome")
    var integralIncome: String = "0",
    @SerialName("nickname")
    var nickname: String = "",
    @SerialName("userId")
    var userId: String = "",
    var createTime :String = ""
) : Parcelable
