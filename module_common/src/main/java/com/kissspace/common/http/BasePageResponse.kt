package com.kissspace.common.http


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class BasePageResponse<T:Parcelable>(
    @SerialName("records")
    var list: List<T>? = null,
    var total: Int = 0
) : Parcelable