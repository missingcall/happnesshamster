package com.kissspace.common.model

class AitBean(val userId:String,val nickname:String){
    fun getAitName():String{
        return "@$nickname "
    }
}