package com.kissspace.common.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FindHamsterQuickJumpListItem(
    @SerialName("icon")
    val icon: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("jumpDescribe")
    val jumpDescribe: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("text")
    val text: String = ""
): Parcelable