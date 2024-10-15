package com.kissspace.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class RoomPKInfoMessage(
    var blueTeamDto: TeamPKInfo,
    var redTeamDto: TeamPKInfo,
    var countdown: Long = 0,
    var microphonePkValueDtoList: List<TeamPKValueInfo> ?= null,
    var victoriousTeam:String = "999"
)

@Serializable
@Parcelize
data class TeamPKInfo(
    var totalPkValue: Long,
    var userList: List<TeamPKUserInfo>,
): Parcelable

@Serializable
@Parcelize
data class TeamPKUserInfo(var profilePath: String, var boostValue: Double): Parcelable

@Serializable
@Parcelize
data class TeamPKUserBean(var userId:String = "",var displayId:String = "",var nickname:String = "",var profilePath: String, var boostValue: Double): Parcelable

@Serializable
data class TeamPKValueInfo(var microphonePkValue: Long, var microphonePosition: Int)