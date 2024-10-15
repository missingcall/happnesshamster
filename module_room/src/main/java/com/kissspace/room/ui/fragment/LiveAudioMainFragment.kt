package com.kissspace.room.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.scopeNetLife
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.didi.drouter.api.DRouter
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.time.Interval
import com.drake.net.utils.scope
import com.drake.net.utils.scopeLife
import com.drake.net.utils.scopeNetLife
import com.google.gson.reflect.TypeToken
import com.kissspace.common.config.CommonApi
import com.kissspace.common.config.Constants
import com.kissspace.common.config.UserConfig
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.scrollToBottom
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.AitBean
import com.kissspace.common.model.MicUserModel
import com.kissspace.common.model.RoomInfoBean
import com.kissspace.common.model.RoomPKInfoMessage
import com.kissspace.common.model.RoomScreenMessageModel
import com.kissspace.common.model.UserInfoBean
import com.kissspace.common.model.WaterGamePoolInfo
import com.kissspace.common.model.config.RoomGameConfig
import com.kissspace.common.model.immessage.GiftMessage
import com.kissspace.common.model.immessage.RoomChatMessageModel
import com.kissspace.common.model.immessage.RoomStartPkMessage
import com.kissspace.common.model.immessage.UserEnterMessage
import com.kissspace.common.provider.IMessageProvider
import com.kissspace.common.util.GiftAnimationTaskQueue
import com.kissspace.common.util.customToast
import com.kissspace.common.util.getH5Url
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.parse2CountDown
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomFragmentAudioMainBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.collectData
import com.kissspace.room.http.RoomApi
import com.kissspace.room.interfaces.RoomBroadCastCallBack
import com.kissspace.room.manager.IMQTTMessageCallBack
import com.kissspace.room.manager.RoomMQTTManager
import com.kissspace.room.manager.RoomServiceManager
import com.kissspace.room.util.AnimationTaskQueue
import com.kissspace.room.util.GiftFlyAnimationUtil
import com.kissspace.room.viewmodel.LiveViewModel
import com.kissspace.room.widget.AnchorMicQueueDialog
import com.kissspace.room.widget.GiftDialog
import com.kissspace.room.widget.InputTextDialogFragment
import com.kissspace.room.widget.PkVictorInfoDialog
import com.kissspace.room.widget.PkVictorPJInfoDialog
import com.kissspace.room.widget.RoomBroadcastDialog
import com.kissspace.room.widget.RoomEmojiDialog
import com.kissspace.room.widget.RoomGuideDialogFragment
import com.kissspace.room.widget.RoomPredictionDialog
import com.kissspace.room.widget.RoomUserProfileDialog
import com.kissspace.room.widget.TaskRewardListDialogFragment
import com.kissspace.room.widget.UserMicQueueDialog
import com.kissspace.util.fromJson
import com.kissspace.util.isNotEmpty
import com.kissspace.util.logE
import com.kissspace.util.runOnUi
import com.kissspace.webview.widget.BrowserDialog
import com.permissionx.guolindev.PermissionX
import com.qmuiteam.qmui.kotlin.onClick
import com.zhpan.bannerview.BannerViewPager
import java.util.concurrent.TimeUnit


/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/13 17:54
 * @Description: 九麦房main fragment
 *
 */
class LiveAudioMainFragment : BaseLiveFragment(R.layout.room_fragment_audio_main), IMQTTMessageCallBack {
    private val mBinding by viewBinding<RoomFragmentAudioMainBinding>()
    private val mViewModel by activityViewModels<LiveViewModel>()
//    private  val mViewModel:LiveViewModel by lazy { ViewModelProvider(requireActivity())[LiveViewModel::class.java] }
//    private  val mViewModel = ViewModelProvider(requireActivity())[LiveViewModel::class.java]

    //坐骑进场动效队列
    private lateinit var carAnimationTaskQueue: AnimationTaskQueue

    //礼物动效队列
    private lateinit var giftAnimationTaskQueue: GiftAnimationTaskQueue

    override fun initView(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            mViewModel.roomInfoBean.value = it.getParcelable("roomInfoBean")
        }
        super.initView(savedInstanceState)
        mBinding.vm = mViewModel
        lifecycle.addObserver(mViewModel)
        //初始化礼物动效View
        initAnimView()
        //初始化动效队列
        initAnimationTask()
        //初始化聊天tabLayout
        initChatTabLayout()
        //设置点击事件
        initClickEvents()
        //设置上麦按钮状态
        updateMicStatus()
        //初始化输入监听
        initSoftInput()
        //初始化event监听
        initEventObserver()
        initWaterGame()
        initBroadCastView()
        mViewModel.getCurrentMessage()
        mBinding.pkInfoView.tag = getRoomInfo().crId
    }


    override fun tipUserAit() {
        customToast("您被@了,快去看谁@你")
//        mBinding.ivNewAit.visibility = View.VISIBLE
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogUtils.e("onSaveInstanceState"+(mViewModel.roomInfoBean.value == null))
        outState.putParcelable("roomInfoBean", getRoomInfo())
    }

    override fun getRoomInfo(): RoomInfoBean {
        return mViewModel.roomInfoBean.value!!
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAnimView() {
        mBinding.animView.setOnTouchListener { _, _ -> false }
        mBinding.giftView.setOnTouchListener { _, _ -> false }
//        mBinding.giftView.enableVersion1(true)
////        mBinding.animCar.enableVersion1(true)
//        mBinding.giftView.setScaleType(ScaleType.CENTER_CROP)
////        mBinding.animCar.setScaleType(ScaleType.CENTER_CROP)
    }

    private fun initAnimationTask() {
//        carAnimationTaskQueue = CarAnimationTaskQueue(mBinding.animCar, mBinding.userEnterRoomView)
        carAnimationTaskQueue = AnimationTaskQueue(mBinding.animView, mBinding.userEnterRoomView)
        giftAnimationTaskQueue = GiftAnimationTaskQueue(this, mBinding.giftView)
        viewLifecycleOwner.lifecycle.addObserver(carAnimationTaskQueue)
        viewLifecycleOwner.lifecycle.addObserver(giftAnimationTaskQueue)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initChatTabLayout() {
        mBinding.tabChat.configTabLayoutConfig {
            onSelectItemView = { _, index, selected, _ ->
                if (selected) {
//                    val newMessage = mutableListOf<RoomChatMessageModel>()
                    val newIndex = when (index) {
                        0 -> "001"
                        1 -> "002"
                        else -> "003"
                    }
                    RoomServiceManager.allMessageList.filter { it.messageKindList.contains(newIndex) }.let {
                        mBinding.recyclerChat.mutable.clear()
                        if(it.isEmpty()){
                            mBinding.recyclerChat.bindingAdapter.notifyDataSetChanged()
                        }else{
                            mBinding.recyclerChat.models = it
                        }
                        mBinding.recyclerChat.scrollToBottom()
                    }
//                    newMessage.addAll(RoomServiceManager.allMessageList)
//                    newMessage.removeAll { !it.messageKindList.contains(newIndex) }
//                    mBinding.recyclerChat.mutable.clear()
//                    if (newMessage.isEmpty()) {
//                        mBinding.recyclerChat.bindingAdapter.notifyDataSetChanged()
//                    } else {
//                        mBinding.recyclerChat.addModels(newMessage)
//                    }

                }
                false
            }
        }
    }

    override fun connectMQTT(mqttConfig: RoomStartPkMessage) {
        val total = (mqttConfig.pkTimePunish - System.currentTimeMillis()) / 1000
        if(total<0) return
        if(mqttConfig.topic.isEmpty()) return
        mBinding.pkInfoView.initData()
        if(mqttConfig.isPK){
            showPkUI(true)
            mBinding.pkInfoView.setTimeTypeHit("房内对战")
            startCountDown(mqttConfig.pkTimeCountdown,viewLifecycleOwner)
        }
        RoomMQTTManager.connect(mqttConfig,this)
    }



    private var mInterval: Interval? = null

    private fun startCountDown(countDownTime: Long, lifecycleOwner: LifecycleOwner) {
        val total = (countDownTime - System.currentTimeMillis()) / 1000

        if(total<=0){
            RoomMQTTManager.roomStartPkMessage?.let { it1 -> startPunishCountDown((it1.pkTimePunish-System.currentTimeMillis())/1000,viewLifecycleOwner) }
        }else {
            if (mInterval == null) {
                mInterval = Interval(0, 1, TimeUnit.SECONDS, total).life(lifecycleOwner)
            }
            mInterval?.subscribe {
                mBinding.pkInfoView.setTimeHit(it)
                if (it == 0L) {
                    mInterval?.cancel()
                    mInterval = null
                }
            }?.start()
        }
    }
    private fun startPunishCountDown(){
        mInterval?.cancel()
        mInterval = null
        RoomMQTTManager.roomStartPkMessage?.let { it1 ->
            startPunishCountDown(
                (it1.pkTimePunish - System.currentTimeMillis()) / 1000,
                viewLifecycleOwner
            )
        }
    }
    private fun startPunishCountDown(countDownTime: Long, lifecycleOwner: LifecycleOwner) {
        logE("countDownTime:$countDownTime")
        mBinding.pkInfoView.setTimeTypeHit("惩罚时间")
        if (mInterval == null) {
            mInterval = Interval(0, 1, TimeUnit.SECONDS, countDownTime).life(lifecycleOwner)
            mInterval?.subscribe {
                mBinding.pkInfoView.setTimeHit(it)
                if(it == 0L){
                    endPk()
                    showPkUI(false)
                    RoomMQTTManager.release()
                    mInterval?.cancel()
                    mInterval = null
                }
            }?.start()
        }
    }

    private fun endPk(){
        if(getRoomInfo().userRole == Constants.ROOM_USER_TYPE_ANCHOR || getRoomInfo().userRole == Constants.ROOM_USER_TYPE_MANAGER) {
            scopeNetLife {
                Get<String>(RoomApi.API_END_PK) {
                    param("roomId", getRoomInfo().crId)
                }.await()
            }.catch {
                customToast(it.message)
            }
        }
    }


    override fun gotoAitUser(chatMessageModel: RoomChatMessageModel) {
        super.gotoAitUser(chatMessageModel)
        if(chatMessageModel.userId == MMKVProvider.userId) return
        showInputDialog(chatMessageModel)
    }

    private fun initSoftInput() {
        mBinding.layoutChat.onClick {
            showInputDialog(null)
        }
    }

    private var inputTextDialogFragment : InputTextDialogFragment? = null

    private fun showInputDialog(chatMessageModel: RoomChatMessageModel?) {
        if(inputTextDialogFragment == null) {
            inputTextDialogFragment = InputTextDialogFragment()
            inputTextDialogFragment?.setConfirmCallback {
                mViewModel.checkCanChat(getRoomInfo().crId) {
                    sendTextMessage(
                        getRoomInfo().neteaseChatId,
                        it,
                        getUserInfo()!!,
                        inputTextDialogFragment?.getAitList()
                    ){
                        inputTextDialogFragment?.clean()
                    }
                }
            }
        }
        chatMessageModel?.let {
            inputTextDialogFragment?.aitBean = AitBean(chatMessageModel.userId!!,chatMessageModel.nickname!!)
        }
        inputTextDialogFragment?.showNow(childFragmentManager,"input")
    }

    private fun initClickEvents() {
        //点击送礼
        mBinding.ivGift.safeClick {
            //val url = "http://192.168.0.31:8080/water/#/pages/ferrisWheel/index?userId=657be48fe4b09d7e6b58960e&prizePoolId=657422cee4b05c21d36aa858&fixedHeight=0"
//            val  url = "http://112.124.23.219/water/#/pages/ferrisWheel/index?userId="+MMKVProvider.userId+"&chatRoomId="+ getRoomInfo().crId
//            BrowserDialog.newInstance(url)
//                .show(childFragmentManager, "BrowserDialog")
            GiftDialog.newInstance(
                getRoomInfo().crId, "", false, Constants.GiftDialogFrom.FROM_PARTY
            ).show(childFragmentManager)
        }
        //点击上麦
        mBinding.tvUpMic.setOnClickListener {
            if (isUserOnMic(MMKVProvider.userId)) {
                if (RoomServiceManager.isPublishStream) {
                    RoomServiceManager.stopPublishStream()
                    updateMicStatus()
                } else {
                    mViewModel.checkCanOpenMic(getRoomInfo()!!.crId)
                }
            } else {
                if (RoomServiceManager.isInMicQueue) {
                    UserMicQueueDialog.newInstance(getRoomInfo()!!.crId).show(childFragmentManager)
                } else {
                    mViewModel.upMic(getRoomInfo()!!.crId, Constants.ROOM_TYPE_PARTY)
                }
            }
        }
        //点击排麦
        mBinding.tvQueue.safeClick {
            AnchorMicQueueDialog.newInstance(getRoomInfo().crId).show(childFragmentManager)
        }
        //点击消息
        mBinding.ivMessage.safeClick {
            val service = DRouter.build(IMessageProvider::class.java).getService()
            service.showChatDialog(childFragmentManager, "")
        }
        //点击更多
        mBinding.ivMore.safeClick {
            showRoomSettingDialog()
        }


//        //点击发送消息
//        mBinding.tvChatSend.safeClick {
//            val content = mBinding.editChat.text.toString().trim()
//            if (content.isEmpty()) {
//                customToast("请输入消息")
//            } else {
//                mViewModel.checkCanChat(getRoomInfo()!!.crId) {
//                    sendTextMessage(
//                        getRoomInfo()!!.neteaseChatId,
//                        content,
//                        getUserInfo()!!,
//                    )
//                    mBinding.editChat.hideKeyboard()
//                    mBinding.layoutChatInput.visibility = View.GONE
//                    mBinding.editChat.setText("")
//                }
//            }
//        }
        //点击新消息
        mBinding.ivNewMessage.setOnClickListener {
            mBinding.ivNewMessage.visibility = View.GONE
            mBinding.recyclerChat.scrollToBottom()
        }

        mBinding.ivNewAit.setOnClickListener {
//            mBinding.ivNewAit.visibility = View.GONE
//            mBinding.recyclerChat.scrollToBottom()
        }

        //点击右上角积分竞猜
        mBinding.predictionView.safeClick {
            RoomPredictionDialog.newInstance(
                getRoomInfo()!!.crId, getRoomInfo()!!.userRole, getRoomInfo()!!.roomTagCategory
            ).show(childFragmentManager)
        }
        //领取积分
        mBinding.clIntegral.safeClick {
            TaskRewardListDialogFragment().show(
                childFragmentManager, "TaskRewardListDialogFragment"
            )
        }
        //发送表情
        mBinding.ivEmoji.setOnClickListener {
            mViewModel.checkCanChat(getRoomInfo()!!.crId) {
                val dialog = RoomEmojiDialog()
                dialog.setCallBack {
                    sendEmojiMessage(
                        getRoomInfo().neteaseChatId,
                        it.name,
                        it.emojiGameIndex,
                        it.isEmojiLoop,
                        it.emojiGameImage,
                        it.dynamicImage,
                        getUserInfo()!!,
                    )
                }
                dialog.show(childFragmentManager)
            }
        }

    }

    private fun initEventObserver() {
        FlowBus.observerEvent<Event.RefreshPoints>(this) {
            //获取积分任务列表
            mViewModel.getChatRoomTaskRewardList()
        }

        FlowBus.observerEvent<Event.GiveGiftByCoinLocal>(this) {
            //获取积分任务列表
            mBinding.userLuckCoin.setNum(it.tags,it.number,it.giftInfo,getRoomInfo().crId,it.giftType)
        }

        FlowBus.observerEvent<Event.GiveGiftByCoinNet>(this) {
            //获取积分任务列表
            mBinding.userLuckCoinNet.setNum(it.source,it.tagName,it.userTagId,it.gift)
        }

        FlowBus.observerEvent<Event.PkChanged>(this) {
            showPkUI(true)
            val data = it.roomPKInfoMessage
            mBinding.pkInfoView.mBinding.ivPkResultLeft.apply {
                this.visibility = if("001" == data.victoriousTeam||"002" == data.victoriousTeam)View.VISIBLE else View.GONE
                this.setImageResource( if("001" == data.victoriousTeam)R.mipmap.ic_pk_victor else R.mipmap.ic_pk_fail)
            }

            mBinding.pkInfoView.mBinding.ivPkResultRight.apply {
                this.visibility = if("001" == data.victoriousTeam||"002" == data.victoriousTeam)View.VISIBLE else View.GONE
                this.setImageResource( if("002" == data.victoriousTeam) R.mipmap.ic_pk_victor else R.mipmap.ic_pk_fail)
            }

            mBinding.pkInfoView.mBinding.ivPkResultCenter.apply {
                this.visibility =  if("003" == data.victoriousTeam) View.VISIBLE else View.GONE
            }

            if("001" == data.victoriousTeam||"002" == data.victoriousTeam){
                startPunishCountDown()
                PkVictorInfoDialog.newInstance(if("001" == data.victoriousTeam) data.redTeamDto else data.blueTeamDto ).show(childFragmentManager)
            }else if("003" == data.victoriousTeam){
                startPunishCountDown()
                PkVictorPJInfoDialog.newInstance().show(childFragmentManager)
            }

            logE("data:"+data.victoriousTeam)
            getRoomInfo().wheatPositionList.forEachIndexed { index, micUserModel ->
                if (index > 0 && data.microphonePkValueDtoList.isNotEmpty()) {
                    micUserModel.pkValue = data.microphonePkValueDtoList!![index].microphonePkValue
                    micUserModel.notifyChange()
                }
            }
            mBinding.pkInfoView.updatePKInfo(data)
        }

        FlowBus.observerEvent<Event.UseTaskRewardPointsEvent>(this) {
            RoomPredictionDialog.newInstance(
                getRoomInfo()!!.crId, getRoomInfo()!!.userRole, getRoomInfo()!!.roomTagCategory
            ).show(childFragmentManager)
        }

        FlowBus.observerEvent<Event.OpenBlindBoxEvent>(this) {
            GiftDialog.newInstance(
                getRoomInfo()!!.crId, MMKVProvider.userId, true, Constants.GiftDialogFrom.FROM_PARTY
            ).show(childFragmentManager)
        }

        FlowBus.observerEvent<Event.InsertUserInMessage>(this) {
            if (it.message.carPath.isNotEmpty()) {
                playCarAnimation(it.message)
            }
            insertChatModel(parseUserJoinMessage(it.message))
        }

        FlowBus.observerEvent<Event.ShowPrizeEvent>(this) {
            insertLocalModels(parsePrizeMessage(it.prizeModel))
        }

        FlowBus.observerEvent<Event.OpenUserInfoDialog>(this) {
            RoomUserProfileDialog.newInstance(
                it.userId, getRoomInfo().userRole, getRoomInfo().crId
            ).show(childFragmentManager)
        }


    }


    private fun initBroadCastView(){
        mBinding.layoutBroadcast.callBack = object: RoomBroadCastCallBack{
            override fun showBroadCast(costCoin: Int, start: Boolean) {
                RoomBroadcastDialog.newInstance(costCoin,start).show(childFragmentManager)
            }
        }
    }

    private fun initWaterGame() {
        if(true) return
//        mBinding.ivWaterGame.visibility =
//            if (MMKVProvider.isShowGame) View.VISIBLE else View.GONE
        val type = object : TypeToken<List<RoomGameConfig>>() {}.type
        val games = GsonUtils.fromJson<List<RoomGameConfig>>(MMKVProvider.gameConfig, type)
        val waterUrl = games.find { it.name == "神奇浇水" }?.url
        mBinding.ivWaterGame.safeClick {
            waterUrl?.let {
                val url = "${
                    getH5Url(it, true)
                }&chatRoomId=${getRoomInfo().crId}"
                BrowserDialog.newInstance(url)
                    .show(childFragmentManager, "BrowserDialog")
            }
        }

        request<WaterGamePoolInfo>(RoomApi.API_QUERY_WATER_GAME_POOL, Method.POST, onSuccess = {
            if (it.type == "001") {
                mBinding.ivWaterGame.setImageResource(R.mipmap.room_icon_green_tree)
            } else {
                mBinding.ivWaterGame.setImageResource(R.mipmap.room_icon_gold_tree)
            }
        }, onError = {
            mBinding.ivWaterGame.setImageResource(R.mipmap.room_icon_green_tree)
        })
    }

    override fun getMicrophoneLayout() = mBinding.layoutMicrophone
    override fun getChatRecyclerView() = mBinding.recyclerChat
    override fun getPredictionView() = mBinding.predictionView
    override fun getActivityBannerView(): BannerViewPager<Any> = mBinding.bannerActivity

    override fun getActionBarView() = mBinding.layoutActionBar

    override fun showNewMessageButton(isShow: Boolean) {
        mBinding.ivNewMessage.visibility = if (isShow) View.VISIBLE else View.GONE
    }


    override fun updateMicQueueAmount(amount: Int) {
        mBinding.tvQueueAmount.visibility =
            if (amount > 0 && getRoomInfo()!!.userRole != Constants.ROOM_USER_TYPE_NORMAL) View.VISIBLE else View.GONE
        mBinding.tvQueueAmount.text = amount.toString()
    }


    override fun lockMic(isLock: Boolean, isAll: Boolean, index: Int) {
        if (isLock) {
            mViewModel.lockMic(isAll, index, getRoomInfo()?.crId, Constants.ROOM_TYPE_PARTY)
        } else {
            mViewModel.unLockMic(isAll, index, getRoomInfo()?.crId, Constants.ROOM_TYPE_PARTY)
        }
    }

    override fun updateMicStatus() {
        mBinding.tvUpMic.visibility =
            if (isNormalUser() || isUserOnMic(MMKVProvider.userId)) View.VISIBLE else View.GONE
        mBinding.tvQueue.visibility = if (isNormalUser()) View.GONE else View.VISIBLE
        if (isUserOnMic(MMKVProvider.userId)) {
            if (RoomServiceManager.isPublishStream) {
                mBinding.tvUpMic.setImageResource(R.mipmap.room_icon_mic_talking)
            } else {
                mBinding.tvUpMic.setImageResource(R.mipmap.room_icon_mic_mute)
            }
        } else {
            if (RoomServiceManager.isInMicQueue) {
                mBinding.tvUpMic.setImageResource(R.mipmap.room_icon_waiting_mic)
            } else {
                mBinding.tvUpMic.setImageResource(R.mipmap.room_icon_up_mic)
            }
        }
    }

    override fun kickOut(micModel: MicUserModel) {
        mViewModel.kickOutMic(
            getRoomInfo().crId,
            micModel.wheatPositionId,
            Constants.ROOM_TYPE_PARTY,
            micModel.onMicroPhoneNumber
        )
    }

    override fun playCarAnimation(message: UserEnterMessage) {
        carAnimationTaskQueue.addTask(message)
    }

    override fun playGiftAnimation(url: String?) {
        if (!MMKVProvider.room_gift_effect_show) {
            return
        }
        giftAnimationTaskQueue.addTask(url)
    }

    override fun changeTreeState(state: String) {
        mBinding.ivWaterGame.setImageResource(if (state == "001") R.mipmap.room_icon_green_tree else R.mipmap.room_icon_gold_tree)
    }

    override fun updateRoomBroadMsg(msg: RoomScreenMessageModel?) {
        mBinding.layoutBroadcast.updateInfo(msg)
    }

    override fun updateUnReadCount() {
        mViewModel.updateUnReadCount()
    }

    override fun updateVideoDirection() {

    }

    override fun getChatTabLayout() = mBinding.tabChat

    override fun isOldRoom(): Boolean = (requireParentFragment() as LiveAudioFragment).needRefresh

    override fun playGiftFlyAnimation(message: GiftMessage) {
        val source = getRoomInfo().wheatPositionList.find { t -> t.wheatPositionId == message.sourceUser.userId }
        val startView: View? = if (source != null) {
            val recyclerView = mBinding.layoutMicrophone.getRecyclerView()
            getMicrophoneItemView(recyclerView, source.onMicroPhoneNumber)
        } else {
            mBinding.ivGift
        }
        message.targetUsers.forEach {
            val model = getRoomInfo().wheatPositionList.find { t -> t.wheatPositionId == it.userId }
            if (model != null) {
                val recyclerView = mBinding.layoutMicrophone.getRecyclerView()
                val targetView = if (model != null) {
                    getMicrophoneItemView(recyclerView, model.onMicroPhoneNumber)
                } else {
                    mBinding.giftFlyEnd
                }
                if (isUserOnMic(source?.wheatPositionId) || isUserOnMic(model?.wheatPositionId)) {
                    GiftFlyAnimationUtil.playAnimation(
                        requireContext(),
                        startView!!,
                        targetView!!,
                        mBinding.root,
                        message.targetUsers[0].giftInfos[0].url,
                        1200
                    )
                }
            }
        }
    }


    private fun getMicrophoneItemView(recyclerView: RecyclerView, micPosition: Int): View? {
        val layoutManager = recyclerView.layoutManager
        return layoutManager?.findViewByPosition(micPosition)?.findViewById(R.id.iv_user_avatar)
    }


    override fun upMic(index: Int) {
        mViewModel.upMic(getRoomInfo().crId, Constants.ROOM_TYPE_PARTY, index)
    }

    override fun quitMic(index: Int) {
        RoomServiceManager.stopPublishStream()
        mViewModel.quitMic(getRoomInfo()!!.crId, index, Constants.ROOM_TYPE_PARTY)
    }

    override fun createDataObserver() {
        super.createDataObserver()

        //监听上麦
        collectData(mViewModel.upMicEvent, onSuccess = {
            if (it.status != "002") {
                UserMicQueueDialog.newInstance(getRoomInfo().crId).show(childFragmentManager)
            }
        }, onError = {
            if (it.errCode == "50038") {
                //显示排麦弹窗
                UserMicQueueDialog.newInstance(getRoomInfo().crId).show(childFragmentManager)
            } else {
                customToast(it.message)
            }
        })

        collectData(mViewModel.quitMicEvent, onSuccess = {
            //下麦成功
            getRoomInfo().wheatPositionList.forEach {
                if (it.wheatPositionId == MMKVProvider.userId) {
                    clearMicInfo(it)
                }
            }
        }, onError = {
            customToast(it.message)
        })

        collectData(mViewModel.homeMessage, onSuccess = {
             mBinding.layoutBroadcast.updateInfo(it)
        })

        collectData(mViewModel.checkCanOpenMicEvent, onSuccess = {
            if (!it.isForbiddenMike&&isAdded) {
                PermissionX.init(this).permissions(mutableListOf( Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE))
                    .onExplainRequestReason { scope, deniedList ->
                        val message =
                            "为了您能正常体验【房间语音聊天】功能，趣玩需向你申请麦克风权限和电话权限"
                        scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
                    }.explainReasonBeforeRequest()

                    .request { allGranted, _, _ ->
                        if (allGranted) {
                            RoomServiceManager.startPublishStream(
                                getUserInfo()?.userId.orEmpty(), false
                            )
                            updateMicStatus()
                        } else {
                            customToast("请打开麦克风权限")
                        }
                    }
            } else {
                customToast("您已被禁麦")
            }
        })

        collectData(mViewModel.gameListEvent, onSuccess = {
            MMKVProvider.gameConfig = GsonUtils.toJson(it)
        })
        //获取积分列表弹窗
//        collectData(mViewModel.taskRewardListEvent, onSuccess = {
//            it.filter { item1 -> item1.finishStatus == Constants.TaskStatus.TOBE_COLLECTED.type }
//                .let { list ->
//                    logE("tvIntegralCount$list.size")
//                    if (list.isNotEmpty()) {
//                        mBinding.tvIntegralCount.text = list.size.toString()
//                        mBinding.clIntegral.visibility = View.VISIBLE
//                    } else {
//                        //隐藏领取积分接口
//                        mBinding.clIntegral.visibility = View.GONE
//                    }
//                }
//
//        })
    }


    override fun getUserInfo() = (requireParentFragment() as LiveAudioFragment).mUserInfo?:MMKVProvider.userInfo

    override fun onResume() {
        super.onResume()
        if (!MMKVProvider.isShowRoomGuide) {
            RoomGuideDialogFragment().show(childFragmentManager)
            MMKVProvider.isShowRoomGuide = true
        }
//        showPkUI(false)
//        RoomMQTTManager.resume(this)
    }

    override fun onPause() {
        super.onPause()
//        RoomMQTTManager.callback = null
    }

    override fun onDestroy() {
        //(requireActivity() as BaseActivity).unRegisterActivityTouchEvent(this)
        inputTextDialogFragment?.let {
            if(inputTextDialogFragment!!.isAdded){
                childFragmentManager.commit(true){
                    remove(inputTextDialogFragment!!)
                    inputTextDialogFragment = null
                }
            }
        }
        RoomMQTTManager.release()
        super.onDestroy()
    }

//    override fun onTouchEvent(event: MotionEvent) {
//
//    }
//
//    override fun onDispatchTouchEvent(ev: MotionEvent) {
//
////        if (isClickThisArea(mBinding.recyclerChat, ev)) {
////            (requireParentFragment().requireActivity().window.decorView as ViewGroup).requestDisallowInterceptTouchEvent(
////                true
////            )
////            logE("我要拦截")
////        } else {
////            (requireParentFragment().requireActivity().window.decorView as ViewGroup).requestDisallowInterceptTouchEvent(
////                true
////            )
////            logE("我不拦截")
////        }
//    }

    override fun onMQTTMessageArrived(topic: String, msg: String) {
        //if (!isAdded) return
        runOnUi {
            showPkUI(true)
            val data = fromJson<RoomPKInfoMessage>(msg)
            logE("data:"+data.victoriousTeam)
            getRoomInfo().wheatPositionList.forEachIndexed { index, micUserModel ->
                if (index > 0 && data.microphonePkValueDtoList.isNotEmpty()) {
                    micUserModel.pkValue = data.microphonePkValueDtoList!![index].microphonePkValue
                    micUserModel.notifyChange()
                }
            }
            mBinding.pkInfoView.updatePKInfo(data)
        }
    }

    private fun showPkUI(isShow: Boolean){
        mBinding.pkInfoView.visibility = if(isShow) View.VISIBLE else View.GONE
        mBinding.layoutMicrophone.mBinding.gpPkBg.visibility =  if(isShow) View.VISIBLE else View.GONE
    }


}
