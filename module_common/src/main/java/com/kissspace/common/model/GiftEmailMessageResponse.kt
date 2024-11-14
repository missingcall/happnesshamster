package com.kissspace.common.model

import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * @description:礼物邮箱
 * @author: yxt
 * @create: 2024-11-13 10:55
 **/

@Parcelize
@Serializable
data class GiftEmailMessageResponse(
    val endRow: Long,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val list: List<GiftEmailMessageModel>?,
    val navigateFirstPage: Long,
    val navigateLastPage: Long,
    val navigatePages: Long,
    val navigatepageNums: List<Long>?,//有多少页码
    val nextPage: Long,
    val pageNum: Long,
    val pageSize: Long,
    val pages: Long,
    val prePage: Long,
    val size: Long,
    val startRow: Long,
    val total: Long
):Parcelable

/**
 *  礼物邮件实体类
 *  @author yxt
 *  @property createTime 创建时间
 *  @property expireTime 过期时间
 *  @property gift 赠送Json
 *  @property readState   001 未读 002 已读
 *  @property receiveState 001 待领取 002 已领取 003 已失效
 *  @property recordId 记录ID
 *  @property recordType 001 仓鼠卡片 002 系统赠送金额 003 用户转赠
 *  @property remark 介绍
 *  @property title 标题
 */
@Parcelize
@Serializable
data class GiftEmailMessageModel(
    val createTime: String?,
    val expireTime: String?,
    val gift: String,
    val readState: String,
    val receiveState: String,
    val recordId: String,
    val recordType: String,
    val remark: String,
    val title:String?
) : BaseObservable(),Parcelable{
    /**
     * 判断是否已读
     */
    fun isRead():Boolean = readState == "002"

}


object GiftEmailRecordType{
    const val CARD = "001" //仓鼠卡片
    const val MONEY_SYS = "002" //系统赠送金额
    const val MONEY_USER= "003" //用户转赠
}


object GiftEmailReceiveState{
    const val WAIT = "001" //待领取
    const val RECEIVE = "002" //已领取
    const val INVALID = "003" //已失效
}
/**
 * COIN("001", "钻石"),
 *
 *     DIAMOND("002", "松果"),
 *
 *     POINTS("003", "积分 "),
 *
 *     INCOME("004","松子")
 */





//针对gift字段进行解析金额
data class GiftJsonMoney(val amount:String,val amountType:String){
    object GiftMoneyType{
        const val COIN = "001" //钻石
        const val DIAMOND = "002" //松果
        const val POINTS = "003" //积分
        const val INCOME = "004" //松子
    }
}
//针对gift字段进行解析仓鼠卡片
data class GiftJsonCard(val skinIcon:String,val skinId:String)