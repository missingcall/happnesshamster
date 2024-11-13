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
    val endRow: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val list: List<GiftEmailMessageModel>?,
    val navigateFirstPage: Int,
    val navigateLastPage: Int,
    val navigatePages: Int,
    val navigatepageNums: String?,
    val nextPage: Int,
    val pageNum: Int,
    val pageSize: Int,
    val pages: Int,
    val prePage: Int,
    val size: Int,
    val startRow: Int,
    val total: Int
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


//针对gift字段进行解析金额
data class GiftJsonMoney(val amount:String,val amountType:String)
//针对gift字段进行解析仓鼠卡片
data class GiftJsonCard(val skinIcon:String,val skinId:String)