package com.kissspace.common.model.config

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class WxConfig(
    val wechat_app_id : String = "",
    val wechat_app_secret: String  = ""
) : Parcelable
