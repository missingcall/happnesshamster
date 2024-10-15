 package com.hamster.happyness.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.scopeLife
import androidx.lifecycle.scopeNetLife
import androidx.lifecycle.viewModelScope
import com.hamster.happyness.http.Api
import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.config.CommonApi
import com.kissspace.common.config.Constants.getTags
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.model.RoomListResponse
import com.kissspace.common.model.RoomTagListBean
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.ResultState
import com.kissspace.util.isNotEmpty
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class ExploreViewModel : BaseViewModel() {

    private val _tagListEvent = MutableSharedFlow<List<String>>()
    val tagListEvent = _tagListEvent.asSharedFlow()

    private val _exploreListEvent = MutableSharedFlow<List<String>>()
    val exploreListEvent = _exploreListEvent.asSharedFlow()

    val requiredTag :MutableLiveData<String> by lazy { MutableLiveData<String>() }



    private val _roomListEvent = MutableSharedFlow<ResultState<RoomListResponse>>()
    val roomListEvent = _roomListEvent.asSharedFlow()

    fun getExploreFlags() {
        scopeLife {
            _tagListEvent.emit(mutableListOf("推荐"))
        }
    }

    fun getExploreData(pageNum: Int){

        getTags {
            it?.let {
                if(it.isNotEmpty()){
                    getRoomList(it[0].roomTagId,pageNum,20)
                }
            }
        }
    }

    private val _randomRoomEvent = MutableSharedFlow<ResultState<String>>()
    val randomRoomEvent = _randomRoomEvent.asSharedFlow()

    fun getPartyRandomRoom(id:String){
        if (id.isNotEmpty()){
            val param = mutableMapOf<String, Any?>()
            param["tagId"] = id
            request(CommonApi.API_GET_RANDOM_ROOM,Method.GET,param,state = _randomRoomEvent)

        }
    }

    private fun getRoomList(roomTagId: String, pageNum: Int, pageSize: Int) {
        val param = mutableMapOf<String, Any?>()
        param["roomTagId"] = roomTagId
        param["roomTagCategory"] = "002"
        param["pageNum"] = pageNum
        param["pageSize"] = pageSize
        request(Api.API_GET_ROOM_LIST, Method.POST, param, state = _roomListEvent)
    }

}