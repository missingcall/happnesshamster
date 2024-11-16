package com.kissspace.common.model


import androidx.databinding.BaseObservable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class QueryBaseInfoList : ArrayList<QueryBaseInfoList.QueryBaseInfoListItem>() {
    @Serializable
    data class QueryBaseInfoListItem(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = "",
        @SerialName("icon")
        val icon: String = "",
        @SerialName("skinIcon")
        val skinIcon: String = "",
        @SerialName("remark")
        val remark: String = "",
        @SerialName("unlockMethod")
        val unlockMethod: String = "",
        @SerialName("price")
        val price: Double = 0.0,
        @SerialName("deleted")
        val deleted: String = "",
        @SerialName("createTime")
        val createTime: String = "",
        @SerialName("unlockStatus")
        val unlockStatus: Boolean = false,
        @SerialName("wearStatus")
        val wearStatus: Boolean = false,
        @SerialName("order")
        val order: String = "",
        @SerialName("checked")
        var checked: Boolean = false
    ): BaseObservable()
}