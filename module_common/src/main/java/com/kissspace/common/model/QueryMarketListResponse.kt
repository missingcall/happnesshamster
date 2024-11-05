package com.kissspace.common.model

import com.umeng.commonsdk.debug.D

@kotlinx.serialization.Serializable
class QueryMarketList : ArrayList<QueryMarketListItem>()

@kotlinx.serialization.Serializable
data class QueryMarketListItem(
    val coinPrice: Double = 0.0,
    val commodityIcon: String = "",
    val commodityInfoId: String = "",
    val commodityMark: String = "",
    val commodityName: String = "",
    val dayIncome: Int = 0,
    val description: String = "",
    val goodsStatue: String = "",
    val payType: String = "",
    val preconditionsId: String = "",
    val preconditionsName: String = "",
    val propId: String = "",
    val timeLimit: Int = 0,
    val totalIncome: Int = 0,
    val windInterest: Int = 0
)