package com.kissspace.message.http

object MessageApi {
    //真爱墙列表
    const val API_LOVE_WALL_LIST = "/hamster-user/realLoveWall/queryList"

    //系统消息
    const val API_SYSTEM_MESSAGE = "/hamster-user/systemMessage/pageQuery"

    //礼物邮件
    const val API_GIFT_MAIL = "/hamster-user/gift/email/queryList"

    //礼物邮件设置已读
    const val API_GIFT_MAIL_READ = "/hamster-user/gift/email/updateReadState"

    //礼物邮件领取单个
    const val API_GIFT_MAIL_RECEIVE_SINGLE = "/hamster-user/gift/email/receiveReward"

    //礼物邮件删除单个
    const val API_GIFT_MAIL_DELETE_SINGLE = "/hamster-user/gift/email/deleteEmail"

    //删除已领取
    const val API_GIFT_MAIL_DELETE_RECEIVED = "/hamster-user/gift/email/deleteRead"

    //一键领取所有礼物邮件
    const val API_GIFT_MAIL_RECEIVE_ALL = "/hamster-user/gift/email/receiveAllReward"


    //系统消息数量
    const val API_SYSTEM_MESSAGE_COUNT = "/hamster-user/dynamics/queryNumberOfUserLikesAndCommentsMessagesByUserId"
}