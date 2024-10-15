package com.kissspace.pay.wechat

/**
 * @Author gaohangbo
 * @Date 2023/1/17 11:44.
 * @Describe
 */
data class WechatPayParam(
    val orderNo:String,
    val payResult: PayResult,
    val h5Pay:Boolean = false,
    val referer:String = ""
){
    fun getRefererDefine():String{
        return if(referer.startsWith("http")){
            referer
        }else{
            "https://www.$referer"
        }
    }
}

data class PayResult(
    val appid: String,
    val nonceStr: String,
    val packageVal: String,
    val partnerId: String,
    val prepayId: String,
    val sign: String,
    val timestamp: String,
    val h5Url:String?= null
)