package com.kissspace.common.flowbus

import com.kissspace.common.model.DynamicsHandlerBean
import com.kissspace.common.model.PDModel
import com.kissspace.common.model.PrizeModel
import com.kissspace.common.model.RoomPKInfoMessage
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.kissspace.common.model.immessage.FinishBetMessage
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.model.immessage.GiftMessage
import com.kissspace.common.model.immessage.GiftSourceInfo
import com.kissspace.common.model.immessage.PredictionBetMessage
import com.kissspace.common.model.immessage.PredictionResultMessage
import com.kissspace.common.model.immessage.UserEnterMessage
import java.util.HashMap

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/14 14:46
 * @Description: flowbus发送的事件
 *
 */
sealed class Event {
    //显示房间浮窗
    data class ShowRoomFloating(var crId: String, var roomCover: String) : Event()

    //关闭悬浮窗
    object CloseRoomFloating : Event()

    //刷新悬浮窗封面
    data class RefreshRoomFloatingCoverEvent(var cover: String) : Event()

    //更换背景事件
    data class ChangeBackgroundEvent(var url: String) : Event()

    //刷新未读消息数量
    object RefreshUnReadMsgCount : Event()

    data class ChatRoomHistoryMessageEvent(var data: List<ChatRoomMessage>) : Event()

    //application任务
    object InitApplicationTaskEvent : Event()

    //一键登录通知
    object InitOnKeyLoginEvent : Event()

    //刷新任务积分
    object RefreshPoints : Event()

    class PkChanged(var roomPKInfoMessage: RoomPKInfoMessage) : Event()

    //刷新意见反馈
    object RefreshFeedBackType : Event()

    //使用积分
    object UseTaskRewardPointsEvent : Event()

    //打开盲盒
    object OpenBlindBoxEvent : Event()

    //派单
    data class MsgPDEvent(var data: PDModel) : Event()

    //系统通知
    object MsgSystemEvent : Event()

    //公会消息
    object MsgFamilyEvent : Event()

    //公会通过消息
    object MsgFamilyPassEvent : Event()

    //用户下注刷新竞猜列表
    data class RefreshPredictionBentEvent(var data: PredictionBetMessage) : Event()

    //结算竞猜消息
    data class RefreshPredictionResultEvent(var data: PredictionResultMessage) : Event()

    //终止竞猜消息
    data class RefreshPredictionFinishEvent(var data: FinishBetMessage) : Event()

    //刷新积分
    object RefreshIntegralEvent : Event()

    //送礼事件，通知私聊页面发送消息
    data class SendGiftEvent(var data: GiftMessage) : Event()


    //送礼轨迹动画
    data class PlayGiftFlyAnimationEvent(var index: Int, var url: String) : Event()

    //推流状态事件
    data class PublisherStateEvent(
        var streamID: String?
    ) : Event()

    //刷新金币
    object RefreshCoin : Event()

    //刷新树
    object RefreshTree : Event()

    //实名认证
    object RealNameAuthenticationEvent : Event()

    //重置礼物选择状态
    data class ClearGiftChecked(var tabId: String) : Event()

    //插入进场消息
    data class InsertUserInMessage(var message: UserEnterMessage) : Event()

    //潮播房主开播消息
    object AnchorStartLivestreamEvent : Event()

    //潮播房主下播消息
    object AnchorStopLivestreamEvent : Event()

    //歌曲列表播放
    data class ChangePlayMusicList(var currentIndex: Int) : Event()

    //歌曲收藏列表播放
    data class ChangePlayMusicCollect(var currentIndex: Int) : Event()

    //编辑收藏列表
    data class ChangeEditCollectList(var status: Boolean) : Event()

    //播放列表切换
    object MusicListChangeEvent : Event()

    //房间引导消息
    data class RoomGuideEvent(var position: Int? = 0, var isJump: Boolean) : Event()

    //微信h5支付结果
    data class WxPayResult(var payStatus: Int? = 0) : Event()

    /**
     * 本地音浪变化
     */
    data class CapturedSoundLevelUpdateEvent(var soundLevel: Float) : Event()

    /**
     * 远端音浪变化
     */
    data class RemoteSoundLevelUpdateEvent(var soundLevels: HashMap<String, Float>?) : Event()

    data class OpenUserInfoDialog(var userId: String) : Event()

    /**
     * 显示幸运礼物
     */
    object ShowLuckyGiftEvent : Event()

    object PhoneInCome : Event()

    /**
     * 幸运抽奖的礼物
     */
    data class ShowPrizeEvent(var prizeModel: PrizeModel) : Event()

    data class DynamicsHandlerEvent(var dynamicsHandlerBean: DynamicsHandlerBean) : Event()

    //切换账号刷新数据
    object RefreshChangeAccountEvent : Event()


    //刷新动态通知(点赞和互动消息)
    object MsgRefreshDynamicNoticeEvent : Event()


    //星际庄园游戏
    data class H5InterstellarEvent(var content: String) : Event()

    //糖果城堡
    data class H5CandyInterstellarEvent(var content: String) : Event()

    data class H5EventInterstellarEvent(var content: String) : Event()

    data class DragonEventInterstellarEvent(var content: String) : Event()

    //通知权限是否开启
    object NotificationEventOpen : Event()

    object CPChanged : Event()

    //赠送福袋
    data class GiveGiftByCoinNet(var source: GiftSourceInfo, val tagName: String, val userTagId: String, var gift: GiftInfo) : Event()
    data class GiveGiftByCoinLocal(var tags: List<String>, var number: Int, var giftInfo: GiftInfo, var giftType: String) : Event()
//    data class GiveGiftByCoinLocal(var tags:List<String>,var number:Int,var  giftInfo: GiftInfo,val source:String) : Event()

    //喂食清洗 合并事件
    object HamsterFeedingOrCleaningEvent : Event()

    //喂食
    object HamsterFeedingEvent : Event()

    //清洗
    object HamsterCleaningEvent : Event()

    //复活仓鼠
    object HamsterReviveEvent : Event()

    //购买仓鼠
    object HamsterPurchaseEvent : Event()

    //皮肤使用成功
    object WearSkinEvent : Event()

    //皮肤解锁购买成功
    object UnLockSkinEvent : Event()

    //果园或银行购买成功
    object OrchardPurchaseEvent : Event()

    //果园激活成功
    object OrchardActivationEvent : Event()

    //仓鼠需要清洁或喂食 /hamster-center/hamsters/cultivate/communicate 仓鼠交流语言获取
    object CommunicateEvent : Event()

}
