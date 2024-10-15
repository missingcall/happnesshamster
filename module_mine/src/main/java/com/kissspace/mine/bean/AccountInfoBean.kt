package com.kissspace.mine.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.IgnoredOnParcel

@Serializable
@Parcelize
data class AccountInfoBean(
    @SerialName("accountBalance")
    var accountBalance: String = "",
    @SerialName("displayId")
    var displayId: String = "",
    @SerialName("mobile")
    var mobile: String = "",
    @SerialName("nickname")
    var nickname: String = "",
    @SerialName("profilePath")
    var profilePath: String = "",
    @SerialName("sex")
    var sex: String = "",
    @SerialName("userId")
    var userId: String = ""
) :BaseObservable(), Parcelable{

    @IgnoredOnParcel
    var check = false
}