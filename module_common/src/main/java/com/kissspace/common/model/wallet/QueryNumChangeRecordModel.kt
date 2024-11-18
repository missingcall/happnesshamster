package com.kissspace.common.model.wallet


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryNumChangeRecordModel(
    @SerialName("total")
    val total: Int = 0,
    @SerialName("list")
    val list: List<Record> = listOf(),
    @SerialName("pageNum")
    val pageNum: Int = 0,
    @SerialName("pageSize")
    val pageSize: Int = 0,
    @SerialName("size")
    val size: Int = 0,
    @SerialName("startRow")
    val startRow: Int = 0,
    @SerialName("endRow")
    val endRow: Int = 0,
    @SerialName("pages")
    val pages: Int = 0,
    @SerialName("prePage")
    val prePage: Int = 0,
    @SerialName("nextPage")
    val nextPage: Int = 0,
    @SerialName("isFirstPage")
    val isFirstPage: Boolean = false,
    @SerialName("isLastPage")
    val isLastPage: Boolean = false,
    @SerialName("hasPreviousPage")
    val hasPreviousPage: Boolean = false,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean = false,
    @SerialName("navigatePages")
    val navigatePages: Int = 0,
    @SerialName("navigatepageNums")
    val navigatepageNums: List<Int> = listOf(),
    @SerialName("navigateFirstPage")
    val navigateFirstPage: Int = 0,
    @SerialName("navigateLastPage")
    val navigateLastPage: Int = 0
) {
    @Serializable
    data class Record(
        @SerialName("title")
        val title: String = "",
        @SerialName("amount")
        val amount: String = "",
        @SerialName("remark")
        val remark: String = "",
        @SerialName("createTime")
        val createTime: Long = 0,
        @SerialName("nowAmount")
        val nowAmount: Double = 0.0
    )
}