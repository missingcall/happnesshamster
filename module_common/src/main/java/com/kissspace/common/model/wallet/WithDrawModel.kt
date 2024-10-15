package com.kissspace.common.model.wallet
import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.Parcelize

/**
 * @Author gaohangbo
 * @Date 2022/12/31 13:35.
 * @Describe
 */
@Parcelize
data class WithDrawModel (
    val withDrawType: Int,val withDrawTypeName: String, var isSelected: Boolean = false
) : Parcelable, BaseObservable()