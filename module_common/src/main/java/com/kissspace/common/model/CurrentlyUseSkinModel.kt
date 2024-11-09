package com.kissspace.common.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentlyUseSkinModel(
    @SerialName("createTime")
    val createTime: String = "",
    @SerialName("deleted")
    val deleted: String = "",
    @SerialName("icon")
    val icon: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("price")
    val price: Int = 0,
    @SerialName("remark")
    val remark: String = "",
    @SerialName("skinIcon")
    val skinIcon: String = "",
    @SerialName("unlockMethod")
    val unlockMethod: String = "",
    @SerialName("unlockStatus")
    val unlockStatus: Boolean = false,
    @SerialName("wearStatus")
    val wearStatus: Boolean = false
)