package com.kissspace.common.model

import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/24 10:02
 * @Description: 背包礼物实体类
 *
 */
@Serializable
@Parcelize
data class PackGiftModel(
    var id: String = "",
    var giftId: String= "",
    var giftName: String= "",
    var giftIcon: String= "",
    var price: Int= 0,
    var num: Int= 0,
    var lockFlag:String= ""
) : Parcelable, BaseObservable(){

}