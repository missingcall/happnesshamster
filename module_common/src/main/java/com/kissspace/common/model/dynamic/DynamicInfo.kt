package com.kissspace.common.model.dynamic

import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.kissspace.common.util.getFriendlyTimeSpanByNow
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @Author gaohangbo
 * @Date 2023/8/5 11:28.
 * @Describe
 */
@Serializable
data class DynamicInfo(
    val countId: String? = null,
    val current: Int,
    val hitCount: Boolean,
    val maxLimit: Int? = null,
    val optimizeCountSql: Boolean,
    val pages: Int,
    @SerialName("records")
    val dynamicInfoRecords: List<DynamicInfoRecord>,
    val searchCount: Boolean,
    val size: Int,
    val total: Int
)
//[charmLevel, consumeLevel, nickname, profilePath, sex]
@Serializable
@Parcelize
data class DynamicInfoRecord(
    var charmLevel: Int = 0,
    var consumeLevel: Int =0 ,
    val createTime: String="",
    val dynamicCommentsList: MutableList<DynamicCommentModel> = mutableListOf(),
    val dynamicId: String = "",
    val followRoomId: String = "",
    var followStatus: Boolean = false,
    var likeStatus: Boolean = false,
    var nickname: String = "",
    var numberOfLikes: Int = 0,
    val pictureContentAuditStatus: String="001",
    val pictureDynamicContent: List<String> = mutableListOf(),
    var profilePath: String= "",
    var sex: String = "001",
    val textContentAuditStatus: String = "001",
    val textDynamicContent: String = "",
    val userId: String,
    val voiceContentAuditStatus: String ="001",
    val speechDuration: Int = 0,
    var currentDuration: Int = 0,
    var isPlayingVoice: Boolean = false,
    val voiceDynamicContent: String = "",
    var commentAmount:Int = dynamicCommentsList.size
) : BaseObservable(), Parcelable {
    fun getFriendlyTime() = getFriendlyTimeSpanByNow(createTime)

}


@Serializable
@Parcelize
data class DynamicCommentModel(
    var commentId: String = "",
    var dynamicId: String = "",
    var firstLevelCommenterId: String = "",
    var firstLevelCommenterHeadSculpture: String,
    var firstLevelCommenterName: String,
    var secondaryCommenterId: String = "",
    var secondaryCommenterHeadSculpture: String = "",
    var secondaryCommenterName: String = "",
    var commentContent: String = ""
) : Parcelable


@Serializable
data class DynamicDetailCommentInfo(
    var userId: String,
    var displayId: String,
    var nickname:String,
    var sex:String,
    var profilePath:String,
    var consumeLevel:Int,
    var charmLevel:Int,
    var commentContent:String = "",
    val createTime: String = "",
){
    fun getFriendlyTime() = getFriendlyTimeSpanByNow(createTime)
}

@Serializable
data class DynamicDetailLikeInfo(
    var userId: String,
    var displayId: String,
    var nickname:String,
    var sex:String,
    var profilePath:String,
    var consumeLevel:Int,
    var charmLevel:Int,
    var personalSignature:String = "这个人很懒，什么都没有留下"
)