package com.kissspace.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import com.kissspace.common.base.v2.BaseViewModel

import com.kissspace.mine.bean.AccountInfoBean
import com.kissspace.mine.bean.AccountToDK
import com.kissspace.mine.bean.AccountToMiNuo
import com.kissspace.mine.http.MineApi
import com.kissspace.network.net.requestPost
import com.kissspace.network.result.ResultState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PointViewModel: BaseViewModel() {

    private val positionModel : MutableLiveData<AccountInfoBean> by lazy { MutableLiveData<AccountInfoBean>() }
    //查询已领取奖励列表
    private val _accountList = MutableSharedFlow<ResultState<AccountToDK>>()
    val accountList = _accountList.asSharedFlow()
    fun getPointRecord(phone:String){
        val param = mutableMapOf<String, Any?>()
        param["phone"] = phone
        requestPost(MineApi.API_POINT_ACCOUNT_INFO,param,_accountList)
    }
}

