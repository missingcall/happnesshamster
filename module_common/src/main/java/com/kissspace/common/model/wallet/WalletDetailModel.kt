package com.kissspace.common.model.wallet

import android.graphics.Color
import android.os.Parcelable
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.TimeUtils
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * @Author gaohangbo
 * @Date 2023/2/8 22:35.
 * @Describe
 */
@Parcelize
@Serializable
data class WalletDetailModel(
    val countId: String? = null,
    val current: Int,
    val hitCount: Boolean,
    val maxLimit: Int? = null,
    val optimizeCountSql: Boolean,
    val orders: List<String>,
    val pages: Int,
    @SerialName("records")
    val walletRecords: List<WalletRecord>? = null,
    val searchCount: Boolean,
    val size: Int,
    val total: Int,
): Parcelable
@Parcelize
@Serializable
data class WalletRecord(
    val coinNumber: String? = null,
    val coinType: String? = null,
    val coinTypeStr: String? = null,
    val profitNumber: String? = null,
    val profitType: String? = null,
    val profitTypeStr: String? = null,
    val diamondType: String? = null,
    val diamondTypeStr: String? = null,
    val diamondNumber: String? = null,
    val createTime: Long,
    val extendedFields: String? = null,
    val flowKind: String,
    val serialNumber: String,
    val targetUserId: String? = null,
    val userId: String,
    val nickName: String? = null,
    val chatRoomName: String? = null,
): Parcelable

@Serializable
@Parcelize
data class TransferRecord(
    @SerialName("coinNumber")
    var coinNumber: String = "",
    @SerialName("coinType")
    var coinType: String = "",
    @SerialName("coinTypeStr")
    var coinTypeStr: String = "",
    @SerialName("createTime")
    var createTime: Long = 0,
    @SerialName("flowKind")
    var flowKind: String = "",
    @SerialName("serialNumber")
    var serialNumber: String = "",
    @SerialName("targetUserAvator")
    var targetUserAvator: String = "",
    @SerialName("targetUserId")
    var targetUserId: String = "",
    @SerialName("targetUserName")
    var targetUserName: String = "",
    @SerialName("userId")
    var userId: String = "",
    @SerialName("userName")
    var userName: String = "",
) : Parcelable{
    fun getTransferNumStr(): CharSequence{
        return SpanUtils().append("转给TA ").append(BigDecimal(coinNumber).abs().toString())
            .setForegroundColor(Color.parseColor("#FFFF8630"))
            .append("金币").create()
    }

    fun getCreateTimeStr():String{
        return TimeUtils.millis2String(createTime)
    }
}