package com.kissspace.common.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FindPropReceiveListItem(
    @SerialName("commodityDay")
    val commodityDay: Int = 0,
    @SerialName("commodityInfoId")
    val commodityInfoId: String = "",
    @SerialName("createTime")
    val createTime: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("list")
    val list: List<Item> = listOf(),
    @SerialName("receiveContent")
    val receiveContent: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("updateTime")
    val updateTime: String = "",
    @SerialName("userBagId")
    val userBagId: String = "",
    @SerialName("userId")
    val userId: String = ""
) {
    @Serializable
    data class Item(
        @SerialName("commodityInfoId")
        val commodityInfoId: String = "",
        @SerialName("icon")
        val icon: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("number")
        val number: Int = 0,
        @SerialName("type")
        val type: String = ""
    )
}