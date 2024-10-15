package com.kissspace.common.model.config

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * @Author gaohangbo
 * @Date 2023/4/14 16:28.
 * @Describe
 */
@Serializable
@Parcelize
data class RoomGameConfig(
//    val game_name: String = "",
//    val game_icon: String = "",
//    val game_url: String = ""
    val id:String = "",
    val name:String = "",
    val icon:String = "",
    val url:String = "",
    val type:String = "",
    val status:Boolean = true,
    val level:Int = 0,
) : Parcelable