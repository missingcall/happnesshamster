package com.kissspace.message.http

object MessageApi {
    //真爱墙列表
    const val API_LOVE_WALL_LIST = "/hamster-user/realLoveWall/queryList"

    //系统消息
    const val API_SYSTEM_MESSAGE = "/hamster-user/systemMessage/pageQuery"

    //系统消息数量
    const val API_SYSTEM_MESSAGE_COUNT = "/hamster-user/dynamics/queryNumberOfUserLikesAndCommentsMessagesByUserId"
}