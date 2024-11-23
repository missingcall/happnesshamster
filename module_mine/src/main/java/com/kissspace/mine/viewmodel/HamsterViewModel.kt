package com.kissspace.mine.viewmodel

import androidx.databinding.ObservableField
import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.config.Constants
import com.kissspace.common.model.*
import com.kissspace.common.util.customToast
import com.kissspace.mine.http.MineApi
import com.kissspace.network.exception.AppException
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.ResultState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class HamsterViewModel : BaseViewModel() {
    val record = ObservableField<InfoListModel.Record>()
    val baseInfoListItem = ObservableField<QueryBaseInfoList.QueryBaseInfoListItem>()
    private val _infoListEvent = MutableSharedFlow<ResultState<InfoListModel>>()
    val infoListEvent = _infoListEvent.asSharedFlow()

    private val currentlyUseSkinModel = ObservableField<CurrentlyUseSkinModel>()

    /**
     * 获取首页跳转链接
     */
    fun findHamsterQuickJumpList(onSuccess: ((MutableList<FindHamsterQuickJumpListItem>) -> Unit)? , onError: ((AppException) -> Unit) = {} ) {
        request<MutableList<FindHamsterQuickJumpListItem>>(MineApi.API_CENTER_HAMSTERMARKET_FINDHAMSTERQUICKJUMPLIST, Method.GET, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            onError.invoke(it)
        })
    }

    /**
     * 用户查看皮肤首页
     */
    fun requestInfoList(pageNum: Int, onSuccess: ((InfoListModel) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        request<InfoListModel>(MineApi.API_HAMSTER_ACCESSORIES_INFO_LIST, Method.POST, param, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            customToast(it.message, true)
        })
    }

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

    //获取皮肤信息(基础仓鼠)
    fun queryBaseInfoList(onSuccess: ((MutableList<QueryBaseInfoList.QueryBaseInfoListItem>) -> Unit) = {}) {
        request<MutableList<QueryBaseInfoList.QueryBaseInfoListItem>>(MineApi.API_HAMSTER_MARKET_QUERY_BASE_INFO_LIST, Method.GET, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
        })
    }

    //当前使用皮肤
    fun currentlyUseSkin(onSuccess: ((CurrentlyUseSkinModel) -> Unit) = {}) {
        request<CurrentlyUseSkinModel>(MineApi.API_HAMSTER_ACCESSORIES_CURRENTLY_USE_SKIN, Method.GET, onSuccess = {
            currentlyUseSkinModel.set(it)
            currentlyUseSkinModel.notifyChange()
            onSuccess.invoke(it)
        }, onError = {
        })
    }

    //点击仓鼠 获取松果
    fun click(onSuccess: ((Boolean) -> Unit) = {}) {
        request<Boolean>(MineApi.API_HAMSTER_CULTIVATE_CLICK, Method.GET, onSuccess = {
            onSuccess.invoke(it)

        })
    }

    //仓鼠交流语言获取
    fun communicate(onSuccess: ((String) -> Unit) = {}) {
        request<String>(MineApi.API_HAMSTER_CULTIVATE_COMMUNICATE, Method.GET, onSuccess = {
            onSuccess.invoke(it)

        })
    }

    //仓鼠交流语言获取
    fun getInvitePeopleList(pageNum: Int, onSuccess: ((InvitePeopleListModel) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        request<InvitePeopleListModel>(MineApi.API_USER_INVITATION_CODE_REWARD_PEOPLE_LIST, Method.GET, param, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
//            customToast(it.message, true)
        })
    }


}