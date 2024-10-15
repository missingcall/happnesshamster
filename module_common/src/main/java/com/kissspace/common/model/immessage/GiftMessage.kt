package com.kissspace.common.model.immessage

import com.kissspace.common.model.UserMedalBean
import kotlinx.serialization.Serializable
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import kotlinx.serialization.SerialName



@Serializable
data class GiftMessage(
    var sourceUserChatRoomId: String = "",
    var giftSource: String,
    var sourceUser: GiftSourceInfo,
    var targetUsers: List<GiftTargetInfo>,
    var messageKindList: List<String> = mutableListOf(),
    var luckyBagGoldCoins:Long ? = null
)

@Serializable
data class GiftSourceInfo(
    var charmLevel: Int,
    var nickName: String,
    var userId: String,
    var profilePath: String,
    var consumeLevel: Int = 0,
    var headwearUrl: String? = null,
    var privilege: String = "001",
    var medalList: List<UserMedalBean> = mutableListOf()
)

@Serializable
data class GiftTargetInfo(
    var charmTotal: Long,
    var giftInfos: List<GiftInfo>,
    var nickName: String,
    var avatar:String,
    var userId: String,
    var mysteryBoxUrl: String = "",
    var mysteryBoxNumber: Int = 0,
    var mysteryBoxName: String = "",
    var mysteryBoxGiftTotalValue: Long = 0
)


@Parcelize
@Serializable
data class GiftInfo(
    var giftId: String,
    var price: Int,
    var total: Int,
    var svg: String = "",
    var url: String,
    var giftName: String,
    var relationTypeName:String?=null,
    var expireDate:String?=null,
    var firstBind:Boolean?=null,
    var giftType:String ? =null
): Parcelable