package com.hamster.happyness.viewmodel

import androidx.databinding.ObservableField
import com.hamster.happyness.http.Api
import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.config.CommonApi
import com.kissspace.common.config.Constants
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.model.*
import com.kissspace.common.model.wallet.RevivePanelModel
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.http.MineApi
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.ResultState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class HamsterViewModel : BaseViewModel() {
    val record = ObservableField<InfoListModel.Record>()
    private val _infoListEvent = MutableSharedFlow<ResultState<InfoListModel>>()
    val infoListEvent = _infoListEvent.asSharedFlow()


    /**
     * 用户查看皮肤首页
     */
    fun requestInfoList(pageNum: Int ,onSuccess: ((InfoListModel) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        request<InfoListModel>(MineApi.API_HAMSTER_ACCESSORIES_INFO_LIST, Method.POST, param, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            customToast(it.message, true)
        })
    }

    /**
     * 当前使用皮肤
     */
    fun wearSkin(pageNum: Int, skinId: Int, onSuccess: ((Boolean) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        param["skinId"] = skinId
        request<Boolean>(MineApi.API_HAMSTER_ACCESSORIES_WEAR_SKIN, Method.POST, param, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            customToast(it.message, true)
        })
    }

    fun unLockSkin(pageNum: Int, skinId: Int, onSuccess: ((Boolean) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        param["skinId"] = skinId
        request<Boolean>(MineApi.API_HAMSTER_ACCESSORIES_UNLOCK_SKIN, Method.POST, param, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            customToast(it.message, true)
        })
    }


}