package com.kissspace.mine.http

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/29 21:13
 * @Description:
 *
 */
object MineApi {

    //分页查询公会列表
    const val API_GET_FAMILY_LIST_BY_PARAMETER =
        "/hamster-family/family/selectFamilyInfoListByParameter"

    //分页查询公会列表 (在对应的公会里面)
    const val API_GET_FAMILY_LIST = "/hamster-family/family/selectFamilyInfoList"

    //    //分页查询热门公会列表
    const val API_GET_FAMILY_HOT_LIST = "/hamster-family/family/selectHotFamilyInfoList"

    //申请公会接口
    const val API_GET_FAMILY_USER_APPLY = "/hamster-family/familyUserApply/userApply"

    //根据Id查询公会信息
    const val API_GET_FAMILY_BY_ID = "/hamster-family/family/selectFamilyInfoById"

    //意见反馈列表
    const val API_QUERY_USER_FEEDBACK = "/hamster-user/userFeedback/queryUserFeedback"

    //查询意见反馈类型列表
    const val API_QUERY_USER_FEEDBACK_TYPE = "/hamster-user/userFeedbackType/queryUserFeedbackType"

    //添加用户意见反馈
    const val API_INSERT_USER_FEEDBACK = "/hamster-user/userFeedback/insertUserFeedback"


    //用户已读信息之后修改成已读状态
    const val API_READ_STATUS_USER_FEEDBACK = "/hamster-user/userFeedback/readStatusUserFeedback"


//    const val API_InsertUserFeedback = "/hamster-user/userFeedback/insertUserFeedback"
//    const val API_InsertUserFeedback = "/hamster-user/userFeedback/insertUserFeedback"
//    /hamster-user/userFeedback/readStatusUserFeedback

    //查询公会信息
    const val API_GET_SELECT_FAMILY_INFO = "/hamster-family/family/selectFamilyInfo"

    //根据参数获取公会流水列表
    const val API_GET_SELECT_QUERY_FAMILY_FLOWLIST = "/hamster-family/flow/queryFamilyFlowList"

    //更新公会信息
    const val API_GET_UPDATE_FAMILY_INFO = "/hamster-family/family/updateFamilyInfo"

    //移出公会
    const val API_GET_FAMILY_MOVE_OUT = "/hamster-family/family/moveOut"

    //审核用户加入公会申请
    const val API_GET_FAMILY_CHECK_USER_APPLY = "/hamster-family/familyUserApply/checkUserApply"

    //查询举报类型
    const val API_QueryInformantType = "/hamster-user/informantType/queryInformantType"

    //举报用户
    const val API_REPORT_USER = "/hamster-user/informantUser/insertInformantUser"

    //举报房间
    const val API_REPORT_CHATROOM = "/hamster-chatroom/informantChatRoom/insertInformantChatRoom"


    //更新公会头像
    const val API_GET_FAMILY_UPLOAD_ICON = "/hamster-family/family/uploadFamilyIcon"

    //查询所有申请加入公会用户信息
    const val API_GET_FamilyUserApplyList =
        "/hamster-family/familyUserApply/selectFamilyUserApplyListByParameter"

    //查询所有公会成员信息
    const val API_GET_FAMILY_USER_LIST = "/hamster-family/family/selectFamilyUserList"

    //根据公会ID查询所有公会成员信息
    const val API_GET_FAMILY_USER_LIST_BY_ID =
        "/hamster-family/family/selectFamilyUserListByFamilyId"

    const val API_QUERY_FAMILY_USER_LIST =
        "/hamster-family/family/pageQueryFamilyUserList"


    const val API_BATCH_TRANSFER_DIAMOND =
        "/hamster-user/userNum/batchTransferDiamond"


    //商城列表
    const val API_STORE_GOODS_LIST = "/hamster-user/commodity/queryCommodityList"

    //购买装扮
    const val API_BUY_DRESS_UP = "/hamster-user/commodity/buyCommodity"

    //查询我的装扮
    const val API_QUERY_MY_DRESS_UP = "/hamster-user/userBag/myBag"

    //佩戴装扮
    const val API_WEAR_DRESS_UP = "/hamster-user/commodity/wearedHeadwearOrCar"

    //取消佩戴坐骑
    const val API_CANCEL_WEAR_DRESS_UP = "/hamster-user/commodity/cancelWearedHeadwearOrCar"


    //取消收藏
    const val API_CANCEL_COLLECT = "/hamster-user/userCollect/collectChatRoom"

    //我的关注
    const val API_QUERY_MY_FOLLOW = "/hamster-user/userAttention/getUserAttentionList"

    //我的访客
    const val API_QUERY_MY_VISITOR = "/hamster-user/visitor/queryUserVisitor"


    //萌新列表
    const val API_QUERY_NEWBIE = "/hamster-user/user/queryRecentlyRegisteredUserList"

    //我的粉丝
    const val API_QUERY_MY_FANS = "/hamster-user/userAttention/getUserBeAttentionList"

    //查询用户主页信息
    const val API_QUERY_USER_PROFILE = "/hamster-user/user/personalData"

    //获取礼物tab列表
    const val API_GET_GIFT_TABS = "/hamster-user/giftTab/queryAll"

    //根据tab id查询礼物列表
    const val API_GET_GIFT_BY_ID = "/hamster-user/giftInfo/queryGiftByTabId"

    //编辑个人资料
    const val API_EDIT_PROFILE = "/hamster-user/user/editUserData"

    //任务中心
    const val API_TASK_CENTER = "/hamster-task/taskInfo/selectTaskInfoList"

    //仓鼠任务
    const val API_TASK_HAMSTER = "/hamster-user/daily/job/checkVersion"

    //领取仓鼠任务
    const val API_TASK_HAMSTER_REWARD ="/hamster-user/daily/job/receiveReward"


    //领取任务奖励
    const val API_RECEIVE_TASK_REWARD = "/hamster-task/taskInfo/receiveTaskReward"


    //我的钱包
    const val API_MY_WALLET = "/hamster-user/userNum/getMyMoneyBag"

    //收益兑换
    const val API_EXCHANGE_EARNS = "/hamster-user/userNum/exchangeCoin"

    //钻石兑换
    const val API_EXCHANGE_DIAMOND = "/hamster-user/userNum/exchangeDiamond"

    //提现
    const val API_WITHDRAW_ITEM = "/hamster-payment/withdraw/withdrawDeposit"

    //金币转账
    const val API_TRANSFER_COIN = "/hamster-user/userNum/transferCoin"

    //松子转赠
    const val API_TRANSFER_PINE_NUTS = "/hamster-user/userNum/transferPineNuts"

    //代币转换 费率 预计获得
    const val API_USERNUM_EXPECTED_TRANSFER_CONVERSION = "/hamster-user/userNum/expectedTransferConversion"

    //代币转换
    const val API_USERNUM_TRANSFER_CONVERSION = "/hamster-user/userNum/transferConversion"

    //代币转赠 费率 预计获得
    const val API_USERNUM_EXPECTED_TRANSFER_ACCOUNTS = "/hamster-user/userNum/expectedTransferAccounts"

    //代币转账
    const val API_USERNUM_TRANSFER_ACCOUNTS = "/hamster-user/userNum/transferAccounts"

    //收益转账
    const val API_TRANSFER_REWARD = "/hamster-user/userNum/transferAccountBalance"

    //钻石转账
    const val API_TRANSFER_DIAMOND = "/hamster-user/userNum/transferDiamond"

    //根据用户展示id获取通用用户信息
    const val API_QUERY_USER_BY_DISPLAY_ID_RESPONSE = "/hamster-user/user/queryUserByDisplayIdResponse"

    //用户提现列表
    const val API_WithDrawList = "/hamster-payment/withdraw/withdrawList"

    //分页获取金币流水记录列表
    const val API_FLOW_RECORD_COIN_LIST = "/hamster-user/flowRecord/queryFlowRecordCoinPage"

    //分页获取收益列表
    const val API_FLOW_RECORD_EARNS_LIST = "/hamster-user/flowRecord/queryFlowRecordProfitPage"

    //分页获取钻石流水记录列表
    const val API_FLOW_RECORD_DIAMOND_LIST = "/hamster-user/flowRecord/queryFlowRecordDiamondPage"

    //获取用户魅力等级列表
    const val API_QUERY_USER_CHARM = "/hamster-user/grade/queryUserCharm"

    //获取用户财富等级列表
    const val API_QUERY_USER_CONSUME = "/hamster-user/grade/queryUserConsume"

    //绑定支付宝
    const val API_BIND_ALIPAY = "/hamster-user/authentication/accountBinding"

    //获取我的页面里面新消息状态
    const val API_QUERY_MESSAGE_STATUS = "/hamster-user/user/queryNewMessageStatus"

    //查询所有礼物
    const val API_QUERY_ALL_GIFT = "/hamster-user/giftInfo/queryGiftInfoNotBox"

    //设置/取消公会管理员
    const val API_FAMILY_SETTING_MANAGER = "/hamster-family/family/setFamilyAdmin"

    const val API_POINT_EXCHANGED = "/hamster-user/userCallingExternal/redeemPointsForDiamonds"

    const val API_POINT_ACCOUNT_INFO = "/hamster-user/userCallingExternal/selectUserIntegralByPhone"

    const val API_POINT_RECORD = "/hamster-user/userCallingExternal/redeemPointsRecords"

    //获取cp关系列表
    const val API_GET_RELATION_LIST = "/hamster-user/user-relationship/list"

    //获取cp关系排名
    const val API_GET_RELATION_RANKING = "/hamster-user/user-relationship/ranking"

    //获取领养仓鼠&仓鼠果园-松果银行列表
    const val API_HAMSTER_MARKET_QUERY_MARKET_LIST = "/hamster-center/hamsterMarket/queryMarketList"

    //分页获取采集记录列表 即松果/松子/钻石 转入转出
    const val API_QUERY_COLLECT_RECORD_LIST = "/hamster-center/hamsterMarket/queryColletRecordList"

    //记录查询
    const val API_CENTER_HMS_NUM_QUERY_NUM_CHANGE_RECORD = "/hamster-center/hms/num/queryNumChangeRecord"

    //获取用户当前每日可获得松果
    const val API_HAMSTER_MARKET_QUERY_DAY_INCOME = "/hamster-center/hamsterMarket/queryDayIncome"

    //领取松果
    const val API_HAMSTER_MARKET_RECEIVE_PINE_CONE = "/hamster-center/hamsterMarket/receivePinecone"

    //获取仓鼠养成培养消费面板
    const val API_HAMSTER_CULTIVATE_QUERY_CULTIVATION_PANEL = "/hamster-center/hamsters/cultivate/queryCultivationPanel"

    //获取当前仓鼠信息
    const val API_HAMSTER_CULTIVATION_HMSINFO = "/hamster-center/hamsters/cultivate/hmsInfo"

    //喂养仓鼠
    const val API_HAMSTER_CULTIVATE_IMPROVE_SATIETY = "/hamster-center/hamsters/cultivate/improveSatiety"

    //清洗仓鼠
    const val API_HAMSTER_CULTIVATE_IMPROVE_CLEANLINESS = "/hamster-center/hamsters/cultivate/improveCleanliness"

    //复活仓鼠
    const val API_HAMSTER_CULTIVATE_REVIVE = "/hamster-center/hamsters/cultivate/revive"

    //获取仓鼠复活消费面板
    const val API_HAMSTER_CULTIVATE_QUERY_REVIVEPANEL = "/hamster-center/hamsters/cultivate/queryRevivePanel"

    //用户查看饰品头像皮肤首页
    const val API_HAMSTER_ACCESSORIES_INFO_LIST = "/hamster-user/accessories/infoList"

    //用户佩戴皮肤
    const val API_HAMSTER_ACCESSORIES_WEAR_SKIN = "/hamster-user/accessories/wearSkin"

    //用户解锁皮肤
    const val API_HAMSTER_ACCESSORIES_UNLOCK_SKIN = "/hamster-user/accessories/unlockSkin"

    //购买商品
    const val API_HAMSTER_MARKET_BUY = "/hamster-center/hamsterMarket/buy"

    //背包物品激活
    const val API_HAMSTER_MARKET_ACTIVATION = "/hamster-center/hamsterMarket/activation"

    //获取皮肤信息(基础仓鼠)
    const val API_HAMSTER_MARKET_QUERY_BASE_INFO_LIST = "/hamster-center/hamsterMarket/queryBaseInfoList"

    //当前使用皮肤
    const val API_HAMSTER_ACCESSORIES_CURRENTLY_USE_SKIN = "/hamster-user/accessories/currentlyUseSkin"

    //点击仓鼠 获取松果
    const val API_HAMSTER_CULTIVATE_CLICK = "/hamster-center/hamsters/cultivate/click"

    //仓鼠交流语言获取
    const val API_HAMSTER_CULTIVATE_COMMUNICATE = "/hamster-center/hamsters/cultivate/communicate"

    //获取首页跳转链接
    const val API_CENTER_HAMSTERMARKET_FINDHAMSTERQUICKJUMPLIST = "/hamster-center/hamsterMarket/findHamsterQuickJumpList"

    //获取首页跳转链接
    const val API_CENTER_HAMSTERMARKET_FINDPROPRECEIVELIST = "/hamster-center/hamsterMarket/findPropReceiveList"


}