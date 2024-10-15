package com.kissspace.common.bean
import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.parcelize.IgnoredOnParcel

import kotlinx.parcelize.Parcelize

import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

@Serializable
@Parcelize
data  class LocalItem(val name:String, val icon :Int = 0): Parcelable,BaseObservable(){

    @IgnoredOnParcel
    var select = false
}


