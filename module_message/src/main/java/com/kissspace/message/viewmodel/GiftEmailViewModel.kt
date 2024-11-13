package com.kissspace.message.viewmodel

import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.model.GiftEmailMessageResponse
import com.kissspace.message.http.MessageApi
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.ResultState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * @description:
 * @author: yxt
 * @create: 2024-11-13 13:34
 **/
class GiftEmailViewModel: BaseViewModel() {
    private val _giftemailMessageEvent = MutableSharedFlow<ResultState<GiftEmailMessageResponse>>()
    val giftemailMessageEvent = _giftemailMessageEvent.asSharedFlow()

    /**
     * 请求礼物邮件
     * @param pageNum 页码
     * @param pageSize 每页数量
     */
    fun requestGiftEmailMessage(pageNum:Int,pageSize:Int){
        val param = mutableMapOf<String, Any?>()
        param["pageNum"] = pageNum
        param["pageSize"] = pageSize
        request(MessageApi.API_GIFT_MAIL, Method.GET,param, state = _giftemailMessageEvent)
    }
}