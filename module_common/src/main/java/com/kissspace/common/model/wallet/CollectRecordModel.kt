package com.kissspace.common.model.wallet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectRecordModel(
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

    @Serializable
    data class Record(
        @SerialName("coinNumber")
        val coinNumber: Int = 0,
        @SerialName("coinType")
        val coinType: String = "",
        @SerialName("createTime")
        val createTime: String = "",
        @SerialName("flowKind")
        val flowKind: String = "",
        @SerialName("message")
        val message: String = "",
        @SerialName("remainingGoldCoins")
        val remainingGoldCoins: String = "",
        @SerialName("serialNumber")
        val serialNumber: String = ""
    )
}