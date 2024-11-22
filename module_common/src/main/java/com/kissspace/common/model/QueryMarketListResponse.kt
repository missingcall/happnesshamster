package com.kissspace.common.model

import com.umeng.commonsdk.debug.D
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
class QueryMarketList : ArrayList<QueryMarketListItem>()

/**
    timeLimit 结算倒计时，当到领取时间时为负数
    settleDay 领取倒计时，结算时间之前为空，结算时间后开始有值，过期后继续为空
 */
@Serializable
data class QueryMarketListItem(
    @SerialName("buyDay")
    val buyDay: Int = 0,
    @SerialName("buyMoney")
    val buyMoney: Int = 0,
    @SerialName("buyNumber")
    val buyNumber: Int = 0,
    @SerialName("coinPrice")
    val coinPrice: Double = 0.0,
    @SerialName("commodityIcon")
    val commodityIcon: String = "",
    @SerialName("commodityInfoId")
    val commodityInfoId: String = "",
    @SerialName("commodityMark")
    val commodityMark: String = "",
    @SerialName("commodityName")
    val commodityName: String = "",
    @SerialName("commodityType")
    val commodityType: String = "",
    @SerialName("dayIncome")
    val dayIncome: Int = 0,
    @SerialName("description")
    val description: String = "",
    @SerialName("goodsStatue")
    val goodsStatue: String = "",
    @SerialName("payType")
    val payType: String = "",
    @SerialName("preconditionsId")
    val preconditionsId: String = "",
    @SerialName("preconditionsName")
    val preconditionsName: String = "",
    @SerialName("propId")
    val propId: String = "",
    @SerialName("settleDay")
    val settleDay: Int = -1,
    @SerialName("timeLimit")
    val timeLimit: Int = 0,
    @SerialName("totalIncome")
    val totalIncome: Int = 0,
    @SerialName("unitPrice")
    val unitPrice: Double = 0.0,
    @SerialName("updateTime")
    val updateTime: String = "",
    @SerialName("windInterest")
    val windInterest: Double = 0.0
)