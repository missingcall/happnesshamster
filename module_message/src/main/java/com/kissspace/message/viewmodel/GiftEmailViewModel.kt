package com.kissspace.message.viewmodel

import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.model.GiftEmailMessageResponse
import com.kissspace.common.util.customToast
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


    /**
     * 设置礼物邮件状态为已读
     * @param recordId 记录id
     */
    fun setGiftEmailReadStatus(recordId:String){
        val param = mutableMapOf<String, Any?>()
        param["readState"] = "002"
        param["recordId"] = recordId
        request<Boolean>(MessageApi.API_GIFT_MAIL_READ, Method.GET,param, onSuccess = {
        }, onError = {
            customToast(it.errorMsg)
        })
    }

    /**
     * 领取单个礼物邮件
     * @param recordId 记录id
     */
    fun receiveGiftEmailSingle(recordId:String,success:()->Unit){
        val param = mutableMapOf<String, Any?>()
        param["recordId"] = recordId
        request<Boolean>(MessageApi.API_GIFT_MAIL_RECEIVE_SINGLE, Method.GET,param, onSuccess = {
            customToast("领取成功")
            success.invoke()
        }, onError = {
            customToast(it.errorMsg)
        })
    }

    /**
     * 删除单个礼物邮件
     *  @param recordId 记录id
     */
    fun deleteGiftEmailSingle(recordId:String,success:()->Unit){
        val param = mutableMapOf<String, Any?>()
        param["recordId"] = recordId

        request<Boolean>(MessageApi.API_GIFT_MAIL_DELETE_SINGLE, Method.GET,param, onSuccess = {
            customToast("删除成功")
            success.invoke()
        }, onError = {
            customToast(it.errorMsg)
        })
    }

    /**
     * 删除已领取所有邮件
     */
    fun deleteAllReceivedGiftEmail(success:()->Unit){
        request<Boolean>(MessageApi.API_GIFT_MAIL_DELETE_RECEIVED, Method.GET,null, onSuccess = {
            customToast("删除成功")
            success.invoke()
        }, onError = {
            customToast(it.errorMsg)
        })
    }


    fun receiveGiftEmailAll(success:(List<String>)->Unit){
        request<List<String>>(MessageApi.API_GIFT_MAIL_RECEIVE_ALL, Method.GET,null, onSuccess = {
            customToast("一键领取成功")
            success.invoke(it)
        }, onError = {
            customToast(it.errorMsg)
        })
    }
}