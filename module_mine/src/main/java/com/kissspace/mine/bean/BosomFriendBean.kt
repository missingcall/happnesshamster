package com.kissspace.mine.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class BosomFriendBean(
    var userRelationshipTypeName: String = "",
    var userRelationshipTypeTimeLeft: String = "",
    var expirationTime: String = "",
    var userName: String = "",
    var userAvator: String = "",
    var userId:String = ""
//ApiModelProperty("亲密关系类型名称")
//ApiModelProperty("亲密关系剩余时间")
//ApiModelProperty("亲密关系有效日期")
//ApiModelProperty("用户名称")
//ApiModelProperty("用户头像")
) : Parcelable