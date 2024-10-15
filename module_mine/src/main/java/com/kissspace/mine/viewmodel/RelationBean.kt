package com.kissspace.mine.viewmodel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.kissspace.common.model.GiftBean
import com.kissspace.common.model.GiftModel
import com.kissspace.common.model.GiftTabModel
import com.kissspace.common.model.PackGiftModel
import kotlinx.parcelize.IgnoredOnParcel

@Serializable
@Parcelize
data class RelationBean(
    @SerialName("userRelationshipTypeLeftId")
    var userRelationshipTypeLeftId: String = "",
    @SerialName("userRelationshipTypeLeftName")
    var userRelationshipTypeLeftName: String = "",
    @SerialName("userRelationshipTypeRightId")
    var userRelationshipTypeRightId: String = "",
    @SerialName("userRelationshipTypeRightName")
    var userRelationshipTypeRightName: String = "",
    var giftList:List<GiftBean> = mutableListOf()
) : Parcelable,BaseObservable(){
    @IgnoredOnParcel
    var  isSelected = false
}