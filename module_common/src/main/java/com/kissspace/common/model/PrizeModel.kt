package com.kissspace.common.model

import android.os.Parcelable
import com.kissspace.common.model.immessage.WaterGameGiftInfoBean
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// [{
//        giftName = "\U9ed1\U6843A\U72c2\U6b22";
//        number = 3;
//        price = 208;
//        url = "https://djsmacro-oss.oss-cn-hangzhou.aliyuncs.com/admin/gift/images/NO.103536%20%203D%E9%BB%91%E6%A1%83A%E7%8B%82%E6%AC%A2-1701248999875.png";
//// 可能会加一个游戏名字的字段
//// 一个轮播的背景oss资源url字段
//}]
//charmLevel, chatRoomId, consumeLevel, nickname, userId, profilePath, privilege

@Serializable
@Parcelize
data class PrizeModel(
    var chatRoomId: String = "",
    var roomTitle: String = "",
    var charmLevel: Int = 0,
    var consumeLevel: Int = 0,
    var nickname: String = "",
    var userId: String = "",
    var profilePath: String = "",
    var headwearUrl: String = "",
    var privilege: String = "",
    var medalList: MutableList<UserMedalBean>? = null,
    var messageKindList: List<String> = mutableListOf(),
    var giftInfo: List<GameGiftInfoBean>?=null
): Parcelable

@Serializable
@Parcelize
data class PDModel(
    var chatRoomId: String = "",
    var roomTitle: String = "",
    var content:String = "",
    var messageKindList: List<String> = mutableListOf(),
): Parcelable

@Serializable
@Parcelize
data class GameGiftInfoBean(
    var svg:String= "",
    val gameName : String = "",
    val giftName: String,
    val url: String,
    val number: Int,
    val price: Double,
    val gameType: String = "003"
): Parcelable