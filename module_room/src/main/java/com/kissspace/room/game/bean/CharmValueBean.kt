package com.kissspace.room.game.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

//{
//    "userId": "65a74a85e4b0b12e4f2ebf41",
//    "nickname": "米诺249610",
//    "sex": "002",
//    "profilePath": "https://minuo-prod.oss-cn-hangzhou.aliyuncs.com/admin/default/Group%201739332280.png",
//    "microphoneNumber": "13000"
//}
@Serializable
@Parcelize
class CharmValueBean(val userId : String? =null,val nickname:String?= null,
                     val sex : String? =null,val profilePath:String?= null,
                     val microphoneNumber : String? =null
): Parcelable