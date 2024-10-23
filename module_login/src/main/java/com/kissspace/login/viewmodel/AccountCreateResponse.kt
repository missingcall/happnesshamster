package com.kissspace.login.viewmodel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountCreateResponse(
    @SerialName("bgPath")
    val bgPath: String = "",
    @SerialName("birthday")
    val birthday: Birthday = Birthday(),
    @SerialName("displayId")
    val displayId: String = "",
    @SerialName("info")
    val info: String = "",
    @SerialName("isDestroy")
    val isDestroy: String = "",
    @SerialName("isFrozen")
    val isFrozen: String = "",
    @SerialName("location")
    val location: String = "",
    @SerialName("loginId")
    val loginId: String = "",
    @SerialName("machineCode")
    val machineCode: String = "",
    @SerialName("mobile")
    val mobile: String = "",
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("password")
    val password: String = "",
    @SerialName("profilePath")
    val profilePath: String = "",
    @SerialName("registrationIcon")
    val registrationIcon: String = "",
    @SerialName("sex")
    val sex: String = "",
    @SerialName("token")
    val token: String = "",
    @SerialName("tokenHead")
    val tokenHead: String = "",
    @SerialName("userId")
    val userId: String = "",
    @SerialName("username")
    val username: String = ""
) {
    @Serializable
    data class Birthday(
        @SerialName("date")
        val date: Int = 0,
        @SerialName("day")
        val day: Int = 0,
        @SerialName("hours")
        val hours: Int = 0,
        @SerialName("minutes")
        val minutes: Int = 0,
        @SerialName("month")
        val month: Int = 0,
        @SerialName("nanos")
        val nanos: Int = 0,
        @SerialName("seconds")
        val seconds: Int = 0,
        @SerialName("time")
        val time: Int = 0,
        @SerialName("timezoneOffset")
        val timezoneOffset: Int = 0,
        @SerialName("year")
        val year: Int = 0
    )
}