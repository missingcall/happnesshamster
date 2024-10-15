package com.kissspace.common.model
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName



@kotlinx.serialization.Serializable
data class NewBieResponse(
    val pages: Int,
    val records: List<NewBieBean>,
    val total: Int
)
@Serializable
@Parcelize
data class NewBieBean(
    var accid :String = "",
    @SerialName("birthday")
    var birthday: String = "",
    @SerialName("certificationStatus")
    var certificationStatus: String = "",
    @SerialName("displayId")
    var displayId: String = "",
    @SerialName("editDate")
    var editDate: String = "",
    @SerialName("hideWaterMessage")
    var hideWaterMessage: String = "",
    @SerialName("isDestroy")
    var isDestroy: String = "",
    @SerialName("isFrozen")
    var isFrozen: String = "",
    @SerialName("isLimitedConsumption")
    var isLimitedConsumption: String = "",
    @SerialName("machineCode")
    var machineCode: String = "",
    @SerialName("mobile")
    var mobile: String = "",
    @SerialName("nickname")
    var nickname: String = "",
    @SerialName("personalSignature")
    var personalSignature: String = "",
    @SerialName("privilege")
    var privilege: String = "",
    @SerialName("profilePath")
    var profilePath: String = "",
    @SerialName("registerDate")
    var registerDate: String = "",
    @SerialName("sex")
    var sex: String = "",
    @SerialName("userId")
    var userId: String = "",
    @SerialName("userRights")
    var userRights: String = ""
) : Parcelable
