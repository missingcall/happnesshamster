package com.kissspace.common.model


import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InfoListModel(
    @SerialName("countId")
    val countId: String = "",
    @SerialName("current")
    val current: Int = 0,
    @SerialName("hitCount")
    val hitCount: Boolean = false,
    @SerialName("maxLimit")
    val maxLimit: Int = 0,
    @SerialName("optimizeCountSql")
    val optimizeCountSql: Boolean = false,
    @SerialName("orders")
    val orders: List<Order> = listOf(),
    @SerialName("pages")
    val pages: Int = 0,
    @SerialName("records")
    val records: List<Record> = listOf(),
    @SerialName("searchCount")
    val searchCount: Boolean = false,
    @SerialName("size")
    val size: Int = 0,
    @SerialName("total")
    val total: Int = 0
) {
    @Serializable
    data class Order(
        @SerialName("asc")
        val asc: Boolean = false,
        @SerialName("column")
        val column: String = ""
    )
    @Parcelize
    @Serializable
    data class Record(
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
        @SerialName("unlockMethod")
        val unlockMethod: String = "",
        @SerialName("unlockStatus")
        val unlockStatus: Boolean = false,
        @SerialName("wearStatus")
        val wearStatus: Boolean = false,
        @SerialName("checked")
        var checked: Boolean = false

    ): Parcelable, BaseObservable()
}