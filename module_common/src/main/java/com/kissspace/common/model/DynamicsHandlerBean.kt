package com.kissspace.common.model

data class DynamicsHandlerBean(
    val type:Int, // 0 是点赞 关注 收藏   //1 文章发布  2 文章删除
    val id : String,
    val userId:String,
    val like: Boolean,
    val followStatus: Boolean,
    val collectNum: String
) {
    constructor(type: Int) : this(type, "", "", false, false ,"0")
    constructor(type: Int, dynamicId: String) : this(type, dynamicId, "", false, false,"0")

}
