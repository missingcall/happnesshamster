package com.kissspace.common.config

import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.lifecycle.ViewModel
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.model.RoomTagListBean

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/12
 * @Description: App配置信息
 *
 */
object Constants {

    //性别男
    const val SEX_MALE = "002"

    //性别女
    const val SEX_FEMALE = "001"

    //房间类型 派对
    const val ROOM_TYPE_PARTY = "002"


    @StringDef(
        ROOM_TYPE_PARTY
    )
    annotation class RoomType

    //房间用户类型-房主
    const val ROOM_USER_TYPE_ANCHOR = "001"

    //房间用户类型-管理员
    const val ROOM_USER_TYPE_MANAGER = "002"

    //房间用户类型-游客
    const val ROOM_USER_TYPE_NORMAL = "003"


    //礼物背包tabId
    const val GIFT_TAB_ID_PACKAGE = "100001"


    const val PageSize = 20

    /**
     * 支付成功
     */
    const val SUCCESS_PAY = 0

    /**
     * 支付失败
     */
    const val ERROR_PAY = -1

    /**
     * 支付取消
     */
    const val CANCEL_PAY = -2

    /**
     * 未安装微信或微信版本过低
     */
    const val NO_OR_LOW_WX = -3

    /**
     * 公会成员
     */
    const val FAMILY_MEMBER = "001"

    /**
     * 公会长牌照房
     */
    const val FAMILY_LICENSED_HOMEOWNER = "004"

    /**
     * 公会长
     */
    const val FAMILY_HEADER = "005"

    /**
     * 已申请加入
     */
    const val FAMILY_APPLY = "002"

    /**
     * 没有申请
     */
    const val FAMILY_NOT_APPLY = "003"


    /**
     * 正常公会
     */
    const val FAMILY_NORMAL = "001"

    /**
     * 已解散
     */
    const val HAS_DISSOLUTION = "002"

    /**
     * 申请解散
     */
    const val APPLY_FOR_DISSOLUTION = "003"

    /**
     * 任务没有完成
     */
    const val TASK_NOT_FINISH = "001"

    /**
     * 待领取奖励
     */
    const val TASK_WAIT_TO_REWARD = "002"

    /**
     * 已完成
     */
    const val TASK_FINISH = "003"

    @StringDef(TASK_NOT_FINISH, TASK_WAIT_TO_REWARD, FAMILY_NOT_APPLY)
    annotation class TASK_MODE


    const val EXTRA = "extra"

    const val WECHAT_PAY_NAME = "微信"

    const val ALIPAY_NAME = "支付宝"


    /**
     * 分隔符
     */
    const val SPLIT = ","
    const val SPLIT_DOT = "、"
    const val COLOR_PREFIX = "#"
    const val SPLIT_SPACE = " "
    const val SPLIT_D = "."
    const val SPLIT_VERTICAL = "|"
    const val SPLIT_SLASH = "/"
    const val SPLIT_SEMICOLON = ";"
    const val SPLIT_WAVE = "~"
    const val SPLIT_LINE = "\n"

    const val SECOND_MILLIS: Long = 1000
    const val MINUTE_MILLIS = SECOND_MILLIS * 60
    const val HOUR_MILLIS = MINUTE_MILLIS * 60
    const val DAY_MILLIS = HOUR_MILLIS * 24

    //	设备类型(1.安卓/2.IOS/3.其他)
    const val OperationSystem = 1


    const val ALI_PAY = "aliPay"
    const val WECHAT_PAY = "wechatPay"
    const val SAND_PAY = "sandCardPay"
    const val SAND_ALI_PAY = "sandPay"
    const val SAND_WECHAT_PAY = "sandWechatPay"

    val ASSERT_ANIM_FILE = arrayOf(
        "bg_login_with_one_key.mp4",
        "common_gift_box.mp4",
        "common_home_background.mp4",
        "home_anim_explore.mp4",
        "home_anim_mine.mp4",
        "home_anim_msg.mp4",
        "home_anim_trends.mp4",
        "home_card_dynamic.mp4",
        "home_tab_line.mp4",
        "home_tab_party.mp4",
        "mine_auth_guide.mp4",
        "loading.mp4",
        "loading.pag"
    )


    @StringDef(ALI_PAY, WECHAT_PAY, SAND_PAY, SAND_ALI_PAY, SAND_WECHAT_PAY)
    annotation class PayChannelType


    const val Gold = 1
    const val Diamond = 2

    @IntDef(Gold, Diamond)
    annotation class WithdrawType

    //0.0389会精度丢失
    //金币转账手续费
//    const val Coin_Fee = 3.89 / 100

    enum class FamilyEarnsType(val type: String) {
        //公会内当天收益
        FamilyInToady("公会内当天收益"),

        //公会外当天收益
        FamilyInSevenToady("公会外当天收益"),

        //公会外当天收益
        FamilyOutToady("公会外七天收益"),

        //公会外七日收益
        FamilyOutSevenDay("公会外七日收益"),

        FamilyInLastWeek("上周流水"),
    }

    enum class WalletType(val type: String) {
        //钻石
        DIAMOND("钻石"),

        //金币
        COIN("金币"),

        //收益
        EARNS("收益"),

        EARNS_TRANSFER("收益转账")

    }



    enum class SuccessType(val type: String) {
        //兑换
        EXCHANGE("兑换"),

        //转账
        TRANSFER("转账"),

        //提现
        WITHDRAW("提现"),

        TRANSFERREWARD("收益转账")
    }

    enum class TaskStatus(val type: String, val value: String) {
        //001 待解锁 002 进行中 003 待领取 004 已领取
        //待解锁
        TOBE_UNLOCKED("001", "待解锁"),

        //进行中
        IN_PROGRESS("002", "进行中"),

        //待领取
        TOBE_COLLECTED("003", "待领取"),

        //已领取
        RECEIVED("004", "已领取")
    }

    //礼物类型
    enum class GiftType(val type: String, val value: String) {
        //001积分 002 礼物 003 礼包盲盒 004 坐骑 005 头饰 006 金币
        INTEGRAL("001", "积分"), PROPS("002", "礼物"), BLIND_BOX("003", "礼包盲盒"), MOUNT(
            "004", "坐骑"
        ),
        HEADGEAR("005", "头饰"), COIN("006", "钻石"),//金币->钻石
    }

    /**
     * 消息类型
     */
    sealed class ChatMessageType {
        //文本消息
        object TYPE_CONTENT : ChatMessageType()

        //图片消息
        object TYPE_PICTURE : ChatMessageType()

        //语音消息
        object TYPE_AUDIO : ChatMessageType()

        //送礼消息
        object TYPE_GIFT : ChatMessageType()

        //表情包消息
        object TYPE_EMOJI : ChatMessageType()
    }

    /**
     *  自定义消息类型
     */
    object IMMessageType {
        //送礼消息
        const val MSG_TYPE_GIFT = "001"

        //上麦消息
        const val MSG_TYPE_UP_MIC = "004"

        //下麦消息
        const val MSG_TYPE_QUIT_MIC = "005"

        //锁麦消息
        const val MSG_TYPE_LOCK_MIC = "006"

        //解锁麦消息
        const val MSG_TYPE_UNLOCK_MIC = "007"

        //抱上麦
        const val MSG_TYPE_INVITE_MIC = "008"

        //抱下麦
        const val MSG_TYPE_KICK_MIC = "009"

        //房间公聊文本消息
        const val MSG_TYPE_ROOM_CHAT_CONTENT = "101"

        //更新魅力值
        const val MSG_UPDATE_CHARM_VALUE = "015"

        //用户进场消息
        const val MSG_TYPE_USER_IN = "002"

        //设置管理员消息
        const val MSG_SETTING_MANAGER = "013"

        //修改房间信息消息
        const val MSG_UPDATE_ROOM_INFO = "012"

        //禁麦消息
        const val MSG_BAN_MIC = "017"

        //创建投注
        const val MSG_PREDICTION_CREATE = "018"

        //下注消息
        const val MSG_PREDICTION_BET = "019"

        //送礼飘条
        const val MSG_NOTIFY_GIFT = "020"

        //金树绿树切换
        const val MSG_WATER_TREE_CHANGE = "035"

        //聊天-表情包
        const val MSG_EMOJI = "202"

        //浇水飘条
        const val MSG_NOTIFY_WATER = "021"

        //浇水消息
        const val MSG_CHAT_WATER = "039"

        //刷新树
        const val MSG_REFRESH_TREE = "052"

        //修改房间背景消息
        const val MSG_CHANGE_BACKGROUND = "022"

        //竞猜结算消息
        const val MSG_PREDICTION_RESULT = "024"

        //用户排麦消息
        const val MSG_USER_MIC_QUEUE = "011"

        //用户主动取消排麦
        const val MSG_USER_CANCEL_MIC_QUEUE = "010"

        //管理员取消排麦
        const val MSG_MANAGER_CANCEL_MIC_QUEUE = "027"

        //终止投注
        const val MSG_FINISH_BET = "023"

        //踢出房间
        const val MSG_KICK_OUT_USER = "014"

        //删除竞猜
        const val MSG_DELETE_BET = "037"

        //私聊送礼
        const val MSG_CHAT_SEND_GIFT = "203"

        //开启或关闭魅力值
        const val MSG_SWITCH_INCOME_OPEN = "040"

        //私聊表情包
        const val MSG_CHAT_SEND_EMOJI = "201"

        //消息清屏
        const val MSG_CHAT_CLEAR_MESSAGE = "204"

        //系统消息
        const val MSG_SYSTEM = "051"

        //横竖屏切换消息
        const val MSG_UPDATE_VIDEO_DIRECTION = "053"

        //取消管理员
        const val MSG_CANCEL_MANAGER = "054"

        //礼物消息全房间广播
        const val MSG_GIFT_NOTIFY = "055"

        //礼物动效全房间广播
        const val MSG_GIFT_ANIMATION_NOTIFY = "056"

        //房间封禁消息
        const val MSG_ROOM_BAN = "057"

        //抽奖飘条
        const val MSG_GAME_PRIZE = "059"

        // 开启/结束PK消息
        const val MSG_IS_START_PK = "060"

        //游戏公聊文本消息
        const val MSG_TYPE_ROOM_CHAT_GAME = "108"

        //全房间广播
        const val MSG_TYPE_ROOM_NEW_BROADCAST_MESSAGE = "061"

        //全房间广播结束
        const val MSG_TYPE_ROOM_NEW_BROADCAST_END = "062"

        //派单
        const val MSG_ROOM_PD = "063"

        //连接mqtt
        const val MSG_ROOM_MQTT = "071"

        //CP消息
        const val MSG_CP = "090"

    }

//    操作类型 1.充值 2.提现 3.消费 4.兑换 5.转账 6.其他

    enum class FaceRecognitionType(val type: Int, val value: String) {
        //充值
        CHARGE(1, "充值"),

        //提现
        WITHDRAW(2, "提现"),

        //消费
        CONSUMPTION(3, "消费"),

        //兑换
        EXCHANGE(4, "兑换"),

        //转账
        BLIND_BOX(5, "转账"),

        //其他
        OTHER(6, "其他"),

    }

    enum class SharePlatform(val type: Int, val value: String) {

        WECHAT(0, "微信"),

        WECHAT_FRIEND(1, "微信朋友圈"),

    }


    /**
     * 用户权限
     */
    object UserPermission {
        //开积分竞猜权限
        const val PERMISSION_CREATE_PREDICTION = "002"

        //总消费榜查看权限
        const val PERMISSION_CHECK_RICH_RANK = "101"

        //礼物价值榜查看权限
        const val PERMISSION_CHECK_GIFT_RANK = "102"

        //积分消费榜查看权限
        const val PERMISSION_CHECK_POINTS_RANK = "103"

        //打招呼权限
        const val PERMISSION_SAY_HI = "201"

        //金币转账权限
        const val PERMISSION_TRANSFER_COIN = "301"

        //删除动态权限
        const val PERMISSION_DELETE_DYNAMIC = "401"

        //收益转增权限
        const val PERMISSION_TRANSFORMER_REWARD = "302"
    }

    @StringDef(
        UserPermission.PERMISSION_CREATE_PREDICTION,
        UserPermission.PERMISSION_CHECK_RICH_RANK,
        UserPermission.PERMISSION_SAY_HI,
        UserPermission.PERMISSION_TRANSFER_COIN,
        UserPermission.PERMISSION_DELETE_DYNAMIC,
        UserPermission.PERMISSION_TRANSFORMER_REWARD
    )
    annotation class UserPermissionType

    enum class ReportType(val type: String, val value: String) {
        //001积分 002 道具 003 礼包盲盒
        //用户
        USER("user", "用户"),

        //房间
        ROOM("room", "房间"),
    }


    /**
     * 是否完成聊天任务
     */
    var isFinishChatTask = false


    /**
     *  送礼弹窗来源
     */
    object GiftDialogFrom {
        //潮播
        const val FROM_CHAOBO = 1

        //九麦房
        const val FROM_PARTY = 2

        //名片
        const val FROM_PROFILE = 3

        //私聊
        const val FROM_CHAT = 4
    }

    object H5 {
        //活动中心
        const val centerActionUrl = "/water/#/pages/active/index"

        //隐私协议
        const val privacyUrl = "/water/static/pages/privacy-agreement.html"

        //用户协议
        const val userAgreementUrl = "/water/static/pages/user-protocol.html"

        //用户行为协议
        const val userActionAgreementUrl = "/water/static/pages/user-rule.html"

        //社区规范
        const val communityUrl = "/water/static/pages/community-rule.html"

        //派对管理规范
        const val partyRulUrl = "/water/static/pages/party-rule.html"

        //主播管理规则
        const val anchorRulUrl = "/water/static/pages/anchor-rule.html"

        //直播间管理规则
        const val liveRoomRulUrl = "/water/static/pages/live-room-rule.html"

        //应用权限申请与使用情况说明
        const val applicationRulUrl = "/water/static/pages/application-root.html"

        //直播间管理规则
        const val broadUrl = "/water/static/pages/tide-broadcasting-rule.html"

        //个人信息收集
        const val personalInformationUrl = "/water/static/pages/user-info.html"

        //三方资源共享清单
        const val threePartyResourcesUrl = "/water/static/pages/three-sources.html"

        //积分领取页面
        const val pointsReceiveUrl = "/water/static/pages/ponits-receive.html"

        //魅力等级
        const val consumeUrl = "/water/static/pages/rank.html"

        //房间榜
        const val roomRankUrl = "/water/#/pages/rank/rankPage"

        //浇水地址
        const val waterUrl = "/water/#/pages/index/index"

        //rankBossUrl 大佬榜
        const val rankBossUrl = "/water/#/pages/rank/ThreeRankings"

        //积分竞猜玩法说明
        const val PREDICTION_INFO_URL = "/water/static/pages/ponits-guessing-desc.html"

        //许愿池地址
        const val wishPoolUrl = "/water/#/pages/game/wishPool"


        //动态地址
        const val dynamicUrl = "/water/#/pages/dynamic/index"

        //动态主页
        const val dynamicIndex = "/water/#/pages/dynamic/list"

        //动态详情
        const val dynamicDetail = "/water/#/pages/dynamic/detail"

        //互动消息
        const val dynamicInteractiveMessages = "/water/#/pages/intMsgs/index"

        //互动类型
        const val dynamicInteractivityType = "/water/#/pages/intMsgs/common"
    }

    class MusicPlayMode {
        companion object {
            /**
             * 顺序播放-默认
             */
            const val ORDER_PLAY_MODE = 0

            /**
             * 单曲播放
             */
            const val SINGLE_PLAY_MODE = 1

            /**
             * 随机播放
             */
            const val RANDOM_PLAY_MODE = 2

            /**
             * 收藏播放列表
             */
            const val COLLECT_PLAY_LIST = 100

            /**
             * 精选播放列表
             */
            const val CONCENTRATION_PLAY_LIST = 101
        }
    }


    enum class HamsterWalletType(val type: String, val typeName: String) {
        //松果
        PINE_CONE("001", "松果"),

        //松子
        PINE_NUT("002", "松子"),

        //钻石
        DIAMONDS("003", "钻石"),

        //勋章
        MEDAL("004", "勋章")

    }

    /**
     * 	商品类型(001-基础仓鼠, 002-仓鼠庄园,003-仓鼠银行,004-皮肤,005-头像)
     */
    class HamsterFirmCommodityTypes {
        companion object {

            const val BASIC_HAMSTER = "001"; //001-基础仓鼠
            const val FARM = "002"; //002-仓鼠庄园
            const val BANK = "003"; //003-仓鼠银行
            const val SKIN = "004"; //004-皮肤
            const val AVATAR = "005"; //005-头像


        }
    }

    /**
     * 	支付类型：001：松果支付；002：松子支付；
     */
    class HamsterPayType {
        companion object {

            const val PINE_CONE = "001"//松果支付
            const val PINE_NUT = "002" //松子支付
            const val THREE_PARTY = "003" //三方支付
            const val MEDAL = "004" //勋章支付


        }
    }

    /**
     * 	costType：	001:松果 002 松子 003 道具
     */
    class HamsterCultivatePayType {
        companion object {

            const val PINE_CONE = "001"//松果
            const val PINE_NUT = "002" //松子
            const val PROP = "003" //道具


        }
    }


    /**
     * 	hamsterStatus：		仓鼠状态（001 正常 002 濒死 003 已死亡 004 已到期）
     */
    class HamsterStatusType {
        companion object {

            const val NORMAL = "001"
            const val NEAR_DEATH = "002"
            const val DEAD = "003"
            const val EXPIRED = "004"

        }
    }

    /**
     * goodsStatue 商品状态 001 商品可购买 002 商品已售磬 003 用户未解锁 004 用户已拥有(待激活) 005 用户已拥有(生效中)
     */
    class HamsterGoodsStatusType {
        companion object {

            const val AVAILABLE_FOR_PURCHASE = "001"
            const val SOLD_OUT = "002"
            const val NOT_UNLOCKED = "003"
            const val ALREADY_OWNED_PENDING_ACTIVATION = "004"
            const val ALREADY_OWNED_IN_EFFECT = "005"


        }
    }

    /**
     * 转换类型 009松果换松子, 002松子换松果  010松子转换钻石
     */
    class HamsterConversionType {
        companion object {

            const val PINE_CONES_TO_PINE_NUTS = "009"
            const val PINE_NUTS_TO_PINE_CONES = "002"
            const val PINE_NUTS_TO_DIAMONDS = "010"


        }
    }

    /**
     * 转赠类型 	转赠类型 017松子转赠, 018钻石转赠
     */
    class HamsterTransferAccountsType {
        companion object {

            const val PINE_NUTS = "017"
            const val DIAMONDS = "018"

        }
    }


    var sendSmsType: Pair<String?, String?>? = null

    //活体验证type
    var faceRecognitionType: String? = null

    const val TypeSendSms = "TypeSendSms"
    const val TypeFaceRecognition = "TypeFaceRecognition"

    //是否token过期
    var isTokenExpired: Boolean = false

    private var tags: List<RoomTagListBean>? = null

    private val roomTagID_PD: String?
        get() {
            return tags?.firstOrNull { bean -> bean.roomTag == "派单" }?.roomTagId
//            if(BuildConfig.DEBUG){
//                (tags?.firstOrNull { bean -> bean.roomTag == "闲聊" }?.roomTagId) ?:"8a72944df746d47ef02ffe72b1cc00e8"
//            }else (

        }

    //获取派单的roomId
    fun isPdRoom(roomTagId: String): Boolean {
        return roomTagId == roomTagID_PD
    }

    fun ViewModel.getTags(block: (List<RoomTagListBean>?) -> Unit) {
        synchronized(this) {
            if (tags.isNullOrEmpty()) {
                getAppConfigByKey<List<RoomTagListBean>>(AppConfigKey.KEY_PARTY_TAG_LIST) {
                    it.filter { bean -> bean.state == "001" }.let { ros ->
                        tags = ros
                        block(ros)
                    }
                }
            } else {
                block(tags)
            }
        }
    }

}

