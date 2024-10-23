package com.kissspace.mine.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MediatorLiveData
import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.model.NewMessageModel
import com.kissspace.common.model.RoomTagListBean
import com.kissspace.common.model.UserInfoBean
import com.kissspace.common.util.customToast
import com.kissspace.mine.http.MineApi
import com.kissspace.network.exception.AppException
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.ResultState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MineViewModel : BaseViewModel() {

    val userInfo = MediatorLiveData<UserInfoBean>()

    //我的钱包新消息
    val walletNewMessage = MediatorLiveData<Boolean>()

    //我的公会新消息
    val familyNewMessage = MediatorLiveData<Boolean>()

    //任务中心
    val taskNewMessage = MediatorLiveData<Boolean>()

    //意见反馈
    val feedBackNewMessage = MediatorLiveData<Boolean>()

    //活动中心
    val taskCenterMessage = MediatorLiveData<Boolean>()

    //显示首充
    val isShowFirstRecharge = MediatorLiveData<Boolean>()

/*    private val _refreshWalletEvent = MutableSharedFlow<ResultState<String>>()
    val refreshWalletEvent = _refreshWalletEvent.asSharedFlow()*/

    fun queryNewMessageStatus(block: (NewMessageModel) -> Unit) {
        request<NewMessageModel>(
            MineApi.API_QUERY_MESSAGE_STATUS,
            Method.GET,
            onSuccess = {
                block.invoke(it)
            }, onError = {
                customToast(it.message)
            }
        )
    }

    /**
     * 待领取松果
     */
    fun queryDayIncome(onSuccess: ((String?) -> Unit)?) {

        request<String?>(MineApi.API_HAMSTER_MARKET_QUERY_DAY_INCOME,
            Method.GET,
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onError = {
                customToast(it.message)
            })
    }

    /**
     * 领取松果
     */
    fun receivePinecone(onSuccess: ((String?) -> Unit)?, onError: ((AppException?) -> Unit)?) {

        request<String?>(MineApi.API_HAMSTER_MARKET_RECEIVE_PINE_CONE,
            Method.GET,
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onError = {
                customToast(it.message)
                onError?.invoke(it)
            })
    }

}