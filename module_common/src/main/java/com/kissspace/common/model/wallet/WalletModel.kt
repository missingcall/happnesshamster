package com.kissspace.common.model.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * @Author gaohangbo
 * @Date 2022/12/27 19:45.
 * @Describe
 */
@Parcelize
@Serializable
data class WalletModel(
    //账户余额(松子)
    val accountBalance: Double? = null,
    //	虚拟币数目(金币)
    val coin: Double? = null,
    //钻石(松果)
    var diamond: Double? = null,
    //	身份（001-普通用户，002-主播，003-公会长）
    val identity: String,
    //积分
    val integral: Double? = null,
    //是否绑定支付宝账号
    val isBindAliPay: Boolean,
    //是否绑定银行卡账号
    val isBindBankCard: Boolean,
    //勋章
    val medal: Double? = null

) : Parcelable
