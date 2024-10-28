 package com.hamster.happyness.http

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/10
 * @Description: api地址
 *
 */
object Api {

    //获取房间列表
    const val API_GET_ROOM_LIST = "/hamster-chatroom/chatRoom/queryChatRoomList"

    //检查版本
    const val API_UPGRADE = "/hamster-user/configVersion/checkVersion"

    //打招呼
    const val API_SAY_HI = "/hamster-user/userMessage/sayHello"

    //是否可以获取邀请码
    const val API_GET_BOOLEAN_IS_INVITATION = "/hamster-user/invitationCodeReward/popup"

    //提交邀请码
    const val API_SUBMIT_INVITATION_CODE= "/hamster-user/invitationCodeReward/submit"

    //搜索
    const val API_SEARCH_CONTENT = "/hamster-user/search/search/home"

    //实名认证
    const val API_IDENTITY_AUTH_VIEW = "/hamster-user/authentication/insertUserAuthentication"

    //活体认证
    const val API_FACE_RECOGNITION = "/hamster-user/authentication/faceRecognition"

    //获取热门用户
    const val API_POPULAR_USER_LIST = "/hamster-user/indexHome/popularUserList"

    //获取全服广播
    const val API_HOME_QUERY_MESSAGE = "/hamster-chatroom/largeScreenInteractionMessage/queryCurrentMessage"

    //首页游戏快捷入口
    const val API_GAME_QUICK_ENTER = "/hamster-user/game/gameList"



}