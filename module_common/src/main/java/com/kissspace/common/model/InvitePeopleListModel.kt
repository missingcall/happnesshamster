package com.kissspace.common.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitePeopleListModel(
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
        @SerialName("nickname")
        val nickname: String = "",
        @SerialName("profilePath")
        val profilePath: String = "",
        @SerialName("totalAmount")
        val totalAmount: Int = 0,
        @SerialName("userId")
        val userId: String = ""
    )
}