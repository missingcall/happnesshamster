package com.kissspace.room.http

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/14 14:43
 * @Description: 房间接口管理
 *
 */
object RoomApi {
    //用户上麦
    const val API_UP_MIC = "/hamster-chatroom/microphone/getMicrophone"

    //用户下麦
    const val API_QUIT_MIC = "/hamster-chatroom/microphone/leaveMicrophone"

    //创建房间
    const val API_CREATE_ROOM = "/hamster-chatroom/chatRoom/createPersonChatRoom"

    //加入房间
    const val API_JOIN_ROOM = "/hamster-chatroom/chatRoom/getIntoRoom"

    //刷新房间
    const val API_REFRESH_ROOM = "/hamster-chatroom/chatRoom/refreshIntoRoom"

    //获取礼物tab列表
    const val API_GET_GIFT_TABS = "/hamster-user/giftTab/queryAll"

    //http://120.55.183.144:8201/hamster-chatroom/config/queryLuckyBagList?tagId=100
    //根据tab id查询礼物列表
    const val API_GET_GIFT_BY_ID = "/hamster-user/giftInfo/queryGiftByTabId"

    //根据tab id查询带哎礼物列表
    const val API_GET_FD_GIFT_BY_ID = "/hamster-chatroom/config/queryLuckyBagList"

    //送礼接口
    const val API_SEND_GIFT = "/hamster-user/giftInfo/giveGiftForSomebody"

    //获取麦位列表用户
    const val API_GET_ON_MIC_USERS = "/hamster-chatroom/microphone/getOnMicrophoneUser"

    //锁麦
    const val API_LOCK_MIC = "/hamster-chatroom/microphone/lockMicrophone"

    //解锁麦
    const val API_UNLOCK_MIC = "/hamster-chatroom/microphone/unlockMicrophone"

    //设置房间密码
    const val API_SET_PASSWORD = "/hamster-chatroom/chatRoom/updatePassword"

    //获取房间排麦用户列表
    const val API_GET_MIC_QUEUE_LIST = "/hamster-chatroom/microphone/queryMicrophoneWaitingQueue"

    //获取房间可修改信息
    const val API_GET_ROOM_INFO = "/hamster-chatroom/chatRoom/queryChatRoomById"

    //修改房间信息
    const val API_UPDATE_ROOM_INFO = "/hamster-chatroom/chatRoom/UpdateChatRoom"

    //抱上麦
    const val API_INVITE_MIC = "/hamster-chatroom/microphone/inviteUserToMicrophone"

    //抱下麦
    const val API_KICK_MIC = "/hamster-chatroom/microphone/kickOutUserFromMicrophone"

    //取消排麦
    const val API_CANCEL_QUEUE = "/hamster-chatroom/microphone/cancelMicrophoneWaiting"

    //获取房间在线用户列表
    const val API_GET_ROOM_ONLINE_USER = "/hamster-user/onlineUsers/queryOnlineUserList"

    //设置用户身份
    const val API_UPDATE_USER_ROLE = "/hamster-chatroom/chatRoomRole/insertChatRoomRole"

    //取消用户管理员身份
    const val API_CANCEL_MANAGER = "/hamster-chatroom/chatRoomRole/updateChatRoomRole"

    //关闭密码房
    const val API_CLOSE_PASSWORD = "/hamster-chatroom/chatRoom/closePassword"

    //设置房间背景
    const val API_SETTING_ROOM_BACKGROUND = "/hamster-chatroom/chatRoom/setBackground"

    //获取背包礼物列表
    const val API_GET_PACK_GIFT_LIST = "/hamster-user/giftBag/queryGiftBagByUserId"

    //禁麦
    const val API_BAN_MIC = "/hamster-chatroom/chatRoom/forbiddenMike"

    //取消禁麦
    const val API_CANCEL_BAB_MIC = "/hamster-chatroom/chatRoom/cancelForbiddenMike"

    //禁言
    const val API_BAN_CHAT = "/hamster-chatroom/chatRoom/chatRoomMuted"

    //获取用户资料卡信息
    const val API_GET_USER_PROFILE_INFO = "/hamster-user/user/getUserCard"

    //获取预言列表
    const val API_GET_PREDICTION_LIST = "/hamster-game/integralGuess/getIntegralGuessInRoom"

    //获取参加过的预言记录列表
    const val API_GET_PREDICTION_HISTORY = "/hamster-game/integralGuess/getIntegralGuessBetHistory"

    //创建预言
    const val API_CREATE_PREDICTION = "/hamster-game/integralGuess/createIntegralGuess"

    //中止投注
    const val API_STOP_PREDICTION = "/hamster-game/integralGuess/stopBet"

    //结算竞猜
    const val API_SETTLE_PREDICTION = "/hamster-game/integralGuess/settleGuess"

    //删除竞猜
    const val API_DELETE_PREDICTION = "/hamster-game/integralGuess/deleteGuess"

    //下注
    const val API_PREDICTION_BET = "/hamster-game/integralGuess/bet"

    //积分广场列表
    const val API_PREDICTION_SQUARE_LIST =
        "/hamster-game/integralGuess/getIntegralGuessInProgressInSquare"

    //积分竞猜排行榜
    const val API_PREDICTION_RANKING = "/hamster-game/integralGuess/getIntegralRanking"

    //查询用户是否收藏了房间
    const val API_IS_COLLECT_ROOM = "/hamster-user/userCollect/queryCollectChatRoom"

    //收藏房间
    const val API_COLLECT_ROOM = "/hamster-user/userCollect/collectChatRoom"

    //收藏歌曲
    const val API_COLLECT_MUSIC = "/hamster-user/userCollect/collectMusic"

    //收藏歌曲列表
    const val API_COLLECT_MUSIC_LIST = "/hamster-user/userCollect/queryUserCollectMusic"

    //获取房间任务列表
    const val API_GET_TASK_REWARD_LIST =
        "/hamster-task/taskInfo/selectTaskInfoListForChatroomIntegral"

    const val API_HOME_QUERY_MESSAGE= "/hamster-chatroom/largeScreenInteractionMessage/queryCurrentMessage"

    //获取所有积分
    const val API_GET_ALL_INTEGRAL =
        "/hamster-task/taskInfo/batchReceiveTaskReward"

    //房间排行榜
    const val API_ROOM_RANK_USER = "/hamster-user/systemRank/queryChatRoomRanking"

    //清除麦位魅力值
    const val API_CLEAR_CHARM_VALUE = "/hamster-chatroom/microphone/clearCharmValue"

    //开积分盲盒
    const val API_OPEN_INTEGRAL_BOX = "/hamster-user/mysteryBox/openPointsMysteryBox"

    //锁定礼物
    const val API_GIFT_LOCK = "/hamster-user/giftBag/lockGift"

    //根据id查询竞猜
    const val API_GET_PREDICTION_BY_ID = "/hamster-game/integralGuess/getIntegralGuessById"

    //魅力值开关
    const val API_SWITCH_INCOME = "/hamster-chatroom/chatRoom/onOffChatRoomCharm"

    //踢出房间
    const val API_KICK_OUT_USER = "/hamster-chatroom/chatRoom/kickOutOfTheRoom"

    //开始OBS直播
    const val API_START_OBS_LIVESTREAM = "/hamster-chatroom/chatRoom/startTheLiveBroadcast"

    //结束obs直播
    const val API_STOP_OBS_LIVESTREAM = "/hamster-chatroom/chatRoom/endthelivebroadcast"

    //设置直播横竖屏
    const val API_UPDATE_SCREEN_DIRECTION = "/hamster-chatroom/chatRoom/updateScreenStatus"

    //查询浇水奖池状态
    const val API_QUERY_WATER_GAME_POOL = "/hamster-game/giftPoolConfig/queryUsingGiftPool"

    //退出房间
    const val API_EXIT_ROOM = "/hamster-chatroom/chatRoom/exitChatRoom"

    //退出房间
    const val API_SHARE_CHAT_ROOM = "/hamster-chatroom/chatRoom/shareChatRoom"

    //获取麦位的魅力值
    const val API_CHARM_IN_MIC = "/hamster-chatroom/microphone/selectUserCharmValue"

    //房间心跳
    const val API_ROOM_HEART_BEAT = "/hamster-chatroom/chatRoom/heartbeat"

    //查询房间背景
    const val API_QUERY_ROOM_BACKGROUND = "/hamster-chatroom/chatRoom/queryRoomBackgroundList"

    //房间流水
    const val API_ROOM_INCOME = "/hamster-chatroom/chatRoom/getRoomFlowTotal"

    //房间流水-周榜
    const val API_ROOM_INCOME_WEEK = "/hamster-chatroom/chatRoom/getRoomThisWeekFlowTotal"

    //发起pk
    const val API_START_PK = "/hamster-chatroom/chatRoom/initiateRoomPKInitialization"

    //pk送礼列表
    const val API_USER_IN_PK = "/hamster-chatroom/chatRoom/roomPkContributionList"

    // 结束PK
    const val API_END_PK = "/hamster-chatroom/chatRoom/endRoomPk"

    //搜索
    const val API_SEARCH_CONTENT = "/hamster-search/search/search/home"

    // 加入房间黑名单
    const val API_BAN_USER_IN_ROOM = "/hamster-chatroom/chatRoom/setChatRoomBlackList"

    // 取消房间黑名单
    const val API_CANCEL_BAN_USER_IN_ROOM = "/hamster-chatroom/chatRoom/cancelChatRoomBlackList"

    //房间黑名单列表
    const val API_ROOM_BLACK_LIST = "/hamster-chatroom/chatRoom/chatRoomBlackList"

    //发送全服广播
    const val API_SEND_BROADCAST_MESSAGE = "/hamster-chatroom/largeScreenInteractionMessage/send"
}
