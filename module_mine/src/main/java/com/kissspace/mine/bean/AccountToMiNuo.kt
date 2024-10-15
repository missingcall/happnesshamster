package com.kissspace.mine.bean
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
@Parcelize
data class AccountToMiNuo(
    @SerialName("list")
    var list: List<AccountInfoBean> = listOf(),
    @SerialName("scale")
    var scale: String = ""
) : Parcelable

@Serializable
@Parcelize
data class AccountToDK(
    @SerialName("accountBalance")
    var accountBalance: Int = 0,
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
    var userId: String = "",
    var scale: String = ""
) : Parcelable