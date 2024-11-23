package com.kissspace.common.config

/**
 * 公用请求api
 */
object CommonApi {
    //获取用户信息
    const val API_GET_USER_INFO = "/hamster-user/user/info"

    //跟据id查询用户信息
    const val API_GET_USER_INFO_BY_ID = "/hamster-user/user/queryUserByUserId"

    //获取app全局配置
    const val API_GET_APP_CONFIG = "/hamster-user/djsConfigList/queryOneConfigByKey"

    //关注用户
    const val API_FOLLOW_USER = "/hamster-user/userAttention/attentionUser"

    //取消关注用户
    const val API_CANCEL_FOLLOW_USER = "/hamster-user/userAttention/cancelAttentionUser"

    //查询房间是否有密码
    const val API_QUERY_ROOM_PASSWORD = "/hamster-chatroom/chatRoom/getChatRoomRoomPwd"

    //查询emoji表情列表
    const val API_QUERY_EMOJI_LIST = "/hamster-user/chatEmoji/queryAllEmojiList"

    //拉黑用户
    const val API_BAN_USER = "/hamster-user/user/userBlacklist"

    //取消拉黑
    const val API_CANCNEL_BAN_USER = "/hamster-user/user/userCancelBlacklist"

    //查询用户主页信息
    const val API_QUERY_USER_PROFILE = "/hamster-user/user/personalData"

    //用户充值列表
    const val API_SelectPayChannelList = "/hamster-payment/pay/selectPayChannelList"

    //发送验证码
    const val API_SendSms = "/hamster-user/sms/sendCode"

    //根据手机号获取用户账号信息列表(如果为空自动创建)
    const val API_USER_LIST_BY_PHONE = "/hamster-user/user/queryUserListByPhone"

    //根据手机号获取用户账号信息列表(不执行创建,只查询)
    const val API_CHECK_USER_LIST_BY_PHONE = "/hamster-user/user/checkUserListByPhone"

    //创建账号
    const val API_USER_ACCOUNT_CREATE = "/hamster-user/user/accountCreate"

    //创建账号(无限制)
    const val API_USER_ACCOUNT_CREATE_NEW = "/hamster-user/user/accountCreateNew"

    //根据用户id登录
    const val API_LOGIN_BY_USERID = "/hamster-user/user/loginByUserId"

    //我的收藏
    const val API_QUERY_MY_COLLECT = "/hamster-user/userCollect/queryUserCollect"

    //校验支付订单结果
    const val API_IPayNotify = "/hamster-payment/notice/payNotify"

    //领取房间任务奖励
    const val API_RECEIVE_TASK_REWARD = "/hamster-task/taskInfo/receiveTaskReward"

    //系统消息
    const val API_SYSTEM_MESSAGE = "/hamster-user/systemMessage/pageQuery"

    //任务-发送消息
    const val API_TASK_SEND_MESSAGE = "/hamster-chatroom/message/sendMessage"

    //首页潮播banner
    const val API_HOME_BANNER_CHAOBO = "/hamster-chatroom/recommend/getTrendLiveStreaming"

    //首页派对banner
    const val API_HOME_BANNER_PARTY = "/hamster-chatroom/recommend/getParty"

    const val API_GET_RANDOM_ROOM = "/hamster-chatroom/chatRoom/getRandomRoom"

    //短信验证码校验
    const val API_VERIFICATION_CODE = "/hamster-user/sms/verificationCode"

    //杉德支付
    const val API_SAND_PAY = "/hamster-payment/pay/sandPay"

    const val QUERY_PLATFORM_RANKING = "/hamster-user/systemRank/queryPlatformRanking"


    //主页获取公告
    const val API_NOTICE = "/hamster-user/channel/notice/checkVersion"

}