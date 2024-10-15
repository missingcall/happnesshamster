package com.kissspace.room.ui.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.base.CustomNotificationObserver
import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.config.Constants
import com.tencent.qgame.animplayer.util.ScaleType
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.model.GameGiftInfoBean
import com.kissspace.common.model.PrizeModel
import com.kissspace.common.model.RoomInfoBean
import com.kissspace.common.model.UserInfoBean
import com.kissspace.common.model.immessage.BaseAttachment
import com.kissspace.common.model.immessage.ChangeBackgroundMessage
import com.kissspace.common.model.immessage.GiftNotifyMessage
import com.kissspace.common.util.fromJson
import com.kissspace.common.util.getMP4Path

import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.parseCustomMessage
import com.kissspace.util.resToColor
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomFragmentAudioBinding
import com.kissspace.network.result.collectData
import com.kissspace.room.manager.RoomServiceManager
import com.kissspace.room.viewmodel.LiveViewModel
import com.kissspace.util.ifNullOrEmpty
import com.kissspace.util.isNotEmpty
import com.kissspace.util.loadImage
import com.kissspace.util.logE
import com.kissspace.util.toast
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.net.URL

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/2
 * @Description: 九麦房
 *
 */
class LiveAudioFragment : BaseFragment(R.layout.room_fragment_audio),
    Observer<List<ChatRoomMessage>> {
    private val mBinding by viewBinding<RoomFragmentAudioBinding>()
    private val mViewModel by activityViewModels<LiveViewModel>()
    private var stochastic: String? = null
    private var userId: String? = null
    var needRefresh: Boolean = false
    var mUserInfo: UserInfoBean? = null

    companion object {
        fun newInstance(
            crId: String?,
            stochastic: String?,
            userId: String?,
            roomInfo: String?
        ) = LiveAudioFragment().apply {
            arguments = bundleOf(
                "crId" to crId,
                "stochastic" to stochastic,
                "userId" to userId,
                "roomInfo" to roomInfo
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stochastic = arguments?.getString("stochastic")
        userId = arguments?.getString("userOId")
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeReceiveMessage(this, true)
    }

    override fun initView(savedInstanceState: Bundle?) {
        MMKVProvider.lastGiftTabIndex = 0
        MMKVProvider.lastGiftIds = ""
    }
    override fun bindData() {
        mViewModel.getUserInfo()
        mViewModel.roomInfoBean.value?.let {
            refreshRoomInfo(it)
        }?:run {
            mViewModel.handleRoomInfo(mViewModel.crId.value, Constants.ROOM_TYPE_PARTY, stochastic)
        }
        loginSystemRoom()
    }


    private fun loginSystemRoom() {
        getAppConfigByKey<String>(AppConfigKey.DAEMON_NETEASE_ROOM_ID) {
            val enterRoomData = EnterChatRoomData(it)
            NIMClient.getService(ChatRoomService::class.java).enterChatRoomEx(enterRoomData, 3)
        }
    }

    private fun onChangeBackgroundMessage(message: ChangeBackgroundMessage) {
        if (message.chatRoomId == getRoomInfo().crId) {
            getRoomInfo().backgroundDynamicImage = message.dynamicImage.ifNullOrEmpty("")
            getRoomInfo().backgroundStaticImage = message.staticImage.ifNullOrEmpty("")
            loadBackground(message.dynamicImage.ifNullOrEmpty(message.staticImage))
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(mViewModel.roomInfoBean.value != null){
            outState.putParcelable("roomInfoBean", getRoomInfo())
        }
    }

    fun getRoomInfo():RoomInfoBean{
        return mViewModel.roomInfoBean.value!!
    }

    private fun loadBackground(url: String) {
        if (url.endsWith(".mp4")) {
            mBinding.animBackground.visibility = View.VISIBLE
            mBinding.animBackground.setScaleType(ScaleType.CENTER_CROP)
            mBinding.animBackground.setLoop(Int.MAX_VALUE)
            getMP4Path(mBinding.animBackground,url) {
                lifecycleScope.launch {
                    mBinding.animBackground.startPlay(File(it))
                }
            }
        } else {
            mBinding.animBackground.visibility = View.GONE
            mBinding.ivBackground.loadImage(url)
        }
    }

    private fun initViewPager() {
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@LiveAudioFragment) {
                override fun getItemCount(): Int = 3
                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> RoomUserListFragment.newInstance(
                            getRoomInfo().crId, getRoomInfo().userRole
                        )

                        1 -> LiveAudioMainFragment()

                        else -> RoomRankFragment.newInstance(
                            getRoomInfo().crId,
                            getRoomInfo().userRole
                        )
                    }
                }

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    recyclerView.setItemViewCacheSize(3)
                }

            }
            setCurrentItem(1, false)
        }
    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.getUserInfoEvent, onSuccess = {
            mUserInfo = it
        })

        collectData(mViewModel.getRoomInfoEvent, onSuccess = {
            needRefresh = false
            hideLoading()
            mViewModel.roomInfoBean.value = it
            loadBackground(it.backgroundDynamicImage.ifEmpty { it.backgroundStaticImage })
            initViewPager()
        })

        collectData(mViewModel.refreshRoomInfoEvent, onSuccess = {
            refreshRoomInfo(it)
        })

        FlowBus.observerEvent<Event.ShowPrizeEvent>(this) {
            getAppConfigByKey<String>(AppConfigKey.DAEMON_NETEASE_ROOM_ID) { roomId ->
                it.prizeModel.chatRoomId = getRoomInfo().crId
                it.prizeModel.roomTitle = getRoomInfo().roomTitle
                val attr = BaseAttachment(Constants.IMMessageType.MSG_GAME_PRIZE, it.prizeModel)
                val message = ChatRoomMessageBuilder.createChatRoomCustomMessage(roomId, attr)
                NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
                it.prizeModel.giftInfo?.let { it1 -> playPrizeAnimation(it1,it.prizeModel.nickname,it.prizeModel.chatRoomId,it.prizeModel.roomTitle) }
            }
        }
    }

    private fun refreshRoomInfo(room: RoomInfoBean) {
        needRefresh = true
        hideLoading()
        mViewModel.roomInfoBean.value = room
        loadBackground(room.backgroundDynamicImage.ifEmpty { room.backgroundStaticImage })
        initViewPager()
    }

    private fun playGiftNotifyAnimation(message: GiftNotifyMessage) {
        if(mViewModel.roomInfoBean.value == null){
            return
        }
        val span = buildSpannedString {
            color(com.kissspace.module_common.R.color.common_white.resToColor()) {
                append("哇哦！")
            }
            color(com.kissspace.module_common.R.color.color_E9C1FF.resToColor()) {
                append(message.sourceUserNickname)
            }
            color(com.kissspace.module_common.R.color.common_white.resToColor()) {
                append("赠送给")
            }
            color(com.kissspace.module_common.R.color.color_E9C1FF.resToColor()) {
                append(message.targetUserNickname)
            }
            color(com.kissspace.module_common.R.color.common_white.resToColor()) {
                append(message.giftName + "X" + message.number + "。豪气云天，火速去围观！")
            }
        }

        try {
            mBinding.giftNotifyView.playAnimation(
                message.floatTipUrl,
                message.giftUrl,
                span,
                lifecycleScope,
                "003",
                message.chatRoomId,
                message.roomTitle,
                getRoomInfo().crId
            )
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun playPrizeAnimation(prizeModels: List<GameGiftInfoBean>,nickname: String,chatRoomId:String,chatRoomTitle:String) {
//        lifecycleScope.launch {
//
//        }

        prizeModels.forEach {
            //val drawable = buildGiftDrawable(it.url)
            val span = buildSpannedString {
                color(com.kissspace.module_common.R.color.white.resToColor()) {
                    append("人品爆发,恭喜")
                }
                color(com.kissspace.module_common.R.color.color_FAE421.resToColor()) {
                    append(nickname)
                }
                color(com.kissspace.module_common.R.color.common_white.resToColor()) {
                    append("在游戏中获得")
                }

                color(com.kissspace.module_common.R.color.color_FAE421.resToColor()) {
                    append(it.giftName+"x" + it.number)
                }
            }


            try {
                if(!isDetached&&!isRemoving) {
                    mBinding.giftNotifyView.playAnimation(
                        it.svg,
                        it.url,
                        span,
                        lifecycleScope,
                        "003",
                        chatRoomId,
                        chatRoomTitle,
                        getRoomInfo().crId
                    )
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private suspend fun buildGiftDrawable(url: String): Drawable? {
        return try {
            withContext(Dispatchers.IO) {
                Drawable.createFromStream(URL(url).openStream(), "image.jpg")
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onEvent(t: List<ChatRoomMessage>?) {
        t?.forEach {
            kotlin.runCatching {
                val json = JSONObject(it.attachStr)
                val type = json.getString("type")
                BaseAttachment(type, json.getJSONObject("data"))
            }.onSuccess {

                logE("onEvent:"+it.type)
                when (it.type) {
                    Constants.IMMessageType.MSG_CHANGE_BACKGROUND -> {
                        val data = parseCustomMessage<ChangeBackgroundMessage>(it.data)
                        onChangeBackgroundMessage(data)
                    }
                    Constants.IMMessageType.MSG_ROOM_BAN -> {
                        toast("该房间被封禁")
                        exitRoom()
                    }
                    Constants.IMMessageType.MSG_NOTIFY_GIFT -> {
                        val data = parseCustomMessage<GiftNotifyMessage>(it.data)
                        playGiftNotifyAnimation(data)
                    }
//                    Constants.IMMessageType.MSG_NOTIFY_WATER -> {
//                        val data = parseCustomMessage<WaterNotifyMessage>(it.data)
//                        playWaterNotifyAnimation(data)
//                    }
                    Constants.IMMessageType.MSG_GAME_PRIZE -> {
                        if(MMKVProvider.consumeLevel<2) return
                        val data = parseCustomMessage<PrizeModel>(it.data)
                        data.giftInfo?.let { it1 -> playPrizeAnimation(it1,data.nickname,data.chatRoomId,data.roomTitle) }
                    }

                    CustomNotificationObserver.MESSAGE_CANDY_GAME_START, CustomNotificationObserver.MESSAGE_CANDY_GAME_END ->{
                        FlowBus.post(Event.H5CandyInterstellarEvent(it.data.toString()))
                    }

                    CustomNotificationObserver.MESSAGE_GAME_DESTROYED ->{
                        FlowBus.post(Event.H5EventInterstellarEvent(it.data.toString()))
                    }

                    CustomNotificationObserver.DRAGON_WRATH_GAME_BEGIN,CustomNotificationObserver.DRAGON_WRATH_GAME_END ->{
                        FlowBus.post(Event.DragonEventInterstellarEvent(it.data.toString()))
                    }
                }

            }.onFailure {
                LogUtils.e("消息格式错误！${it.message}")
            }
        }
    }


    private fun exitRoom() {
        RoomServiceManager.release()
        requireActivity().finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeReceiveMessage(this, false)
    }

}