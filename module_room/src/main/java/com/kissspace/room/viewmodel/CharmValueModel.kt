package com.kissspace.room.viewmodel

import com.kissspace.common.base.BaseViewModel
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.room.game.bean.CharmValueBean
import com.kissspace.room.http.RoomApi

class CharmValueModel  : BaseViewModel() {



    fun getCharmValueInfo(chatRoomId:String,microphoneNumber:Int,block: ((List<CharmValueBean>) -> Unit)? = null) {
        val param = mutableMapOf<String, Any?>()
        param["chatRoomId"] = chatRoomId
        param["microphoneNumber"] = microphoneNumber
//        val list = mutableListOf<CharmValueBean>()
//        for (i in 0..5) {
//            list.add(CharmValueBean(i.toString(),null))
//        }
//        block?.invoke(list)
        request<List<CharmValueBean>>(RoomApi.API_CHARM_IN_MIC, Method.POST,param, onSuccess = {
            block?.invoke(it)
        }
        ,
            onError = {
                block?.invoke(mutableListOf())
                it.printStackTrace()
            }
        )
    }
}