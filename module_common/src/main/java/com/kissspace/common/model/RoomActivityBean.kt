package com.kissspace.common.model

data class RoomActivityBean(var url: String, var schema: String,val state:String?=null,val os:String?=null,val grade:String? = null,val activeType:String?=null)