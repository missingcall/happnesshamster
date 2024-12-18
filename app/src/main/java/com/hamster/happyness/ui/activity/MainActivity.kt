package com.hamster.happyness.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hamster.happyness.databinding.ActivityMainV2Binding
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.msg.MsgService
import com.petterp.floatingx.assist.FxGravity
import com.petterp.floatingx.assist.helper.ScopeHelper
import com.hamster.happyness.R
import com.hamster.happyness.ui.fragment.FirmFragment
import com.hamster.happyness.ui.fragment.HomeFragmentV3
import com.hamster.happyness.ui.fragment.PartyV2Fragment
import com.hamster.happyness.viewmodel.MainViewModel
import com.hamster.happyness.widget.NoticeDialog
import com.hamster.happyness.widget.UpgradeDialog

import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.config.Constants
import com.kissspace.common.router.jump
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.flowbus.FlowBus.observerEvent
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.mmkv.isShowUpgrade
import com.kissspace.message.ui.fragment.MessageFragmentV3
import com.kissspace.mine.ui.fragment.MineFragment
import com.kissspace.network.result.collectData
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.model.UserAccountBean
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.room.manager.RoomServiceManager
import com.kissspace.setting.viewmodel.ChangeAccountViewModel
import com.kissspace.util.*
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.petterp.floatingx.listener.control.IFxScopeControl
import java.io.File
import java.text.DecimalFormat
import java.util.*

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/4
 * @Description: main activity
 *
 */
@Router(uri = RouterPath.PATH_MAIN)
class MainActivity : com.kissspace.common.base.BaseActivity(R.layout.activity_main_v2) {
    private val mBinding by viewBinding<ActivityMainV2Binding>()
    private val mViewModel by viewModels<MainViewModel>()
    private val mChangeAccountViewModel by viewModels<ChangeAccountViewModel>()
    private var roomFloating: IFxScopeControl<Activity>? = null
    private var index = 0

    private var userPhone: String = ""
    private var isCreateAccount = false
    private var loginAccountPosition = 0;

    private val onlineStatusObserver = Observer<StatusCode> {
        logE("${it}云信code")
        if (it.wontAutoLogin()) {
            toast("您的账号已在另一台设备登录")
            loginOut()
        } else if (it.shouldReLogin()) {
            mViewModel.loginNim()
        } else {
            logE("IM自动重连")
        }
    }

    private val recentContactObserver = Observer<List<RecentContact>> {
        if (it != null && it.isNotEmpty()) {
            // parseData(it, true)
            FlowBus.post(Event.RefreshUnReadMsgCount)
            // updateUnReadCount()
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        index = intent?.getIntExtra("index", 0).orZero()
        refreshBottomBar(index)
        mBinding.viewPager.setCurrentItem(index.orZero(), false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        pressBackTwiceToExitApp()
        initViewPager()
        mBinding.collectRoomView.setOnClickListener {
            jump(RouterPath.PATH_MY_COLLECT)
        }
        initViewClick()
        initDrawerLayout()

    }

    private fun initDrawerLayout() {
        mChangeAccountViewModel.mViewStatus.observe(this) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        initDrawerInfo()

        mBinding.ivCopy.safeClick {
            copyClip(mBinding.tvUserId.text.toString())
        }

        mBinding.conAdd.safeClick {
            CommonConfirmDialog(this, "您确定要创建一个新的身份？ 新身份的资产将不与旧身份互通，您确定要创建嘛？") {
                if (this) {
                    createAccount()
                }
            }.show()

        }
        initRecyclerView()
        userPhone = MMKVProvider.userPhone
        mChangeAccountViewModel.requestUserListByPhone(MMKVProvider.userPhone)

        // 禁止手势滑动
//        mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private fun initDrawerInfo() {
        mBinding.ivAvatar.loadImageCircle(MMKVProvider.userInfo?.profilePath, com.kissspace.module_util.R.drawable.common_ic_default)
        //        mBinding.ivAvatar.load(MMKVProvider.userInfo?.profilePath)
        mBinding.tvNickname.text = MMKVProvider.userInfo?.nickname
        mBinding.tvUserId.text = MMKVProvider.userInfo?.displayId
    }

    //切换抽屉状态
    fun changeDrawLayout() {
        if (mBinding.drawer.isDrawerOpen(Gravity.LEFT)) {
            mBinding.drawer.closeDrawers()
        } else {
            mBinding.drawer.openDrawer(Gravity.LEFT)
        }
    }

    private fun initRecyclerView() {
        mBinding.recycler.linear()
            //.divider(com.kissspace.module_common.R.drawable.common_user_account_divider_item)
            .setup {
                addType<UserAccountBean> {
                    R.layout.layout_change_account_list_item
                }
                onBind {
                    findView<ConstraintLayout>(R.id.root).safeClick {
                        val model = getModel<UserAccountBean>()
                        if (model.checked) {
                            toast("当前账号已登录")
                            return@safeClick
                        }
                        loginAccountPosition = adapterPosition
                        showLoading("切换中...")
                        mChangeAccountViewModel.loginByUserId(model.userId)
                    }
                }
                onChecked { position, isChecked, _ ->
                    val model = getModel<UserAccountBean>(position)
                    model.checked = isChecked
                    model.notifyChange()
                }
                singleMode = true
                clickThrottle = 500
            }.models = mutableListOf()
    }

    private fun changeAccountSuccess() {
        FlowBus.post(Event.RefreshChangeAccountEvent)
        FlowBus.post(Event.CloseRoomFloating)
        mBinding.recycler.bindingAdapter.setChecked(loginAccountPosition, true)
        customToast("切换账号成功")
        mBinding.drawer.closeDrawers()
    }

    private fun createAccount() {
        isCreateAccount = true
        showLoading()
        mChangeAccountViewModel.createAccountNew(userPhone)
    }

    fun makeFriend() {
        mBinding.tabDynamic.performClick()
    }

    private fun initViewClick() {
        mBinding.tabHome.setOnClickListener {
            if (index == 0) return@setOnClickListener
            mBinding.viewPager.setCurrentItem(0, false)
            bottomTabStyleChange(false)
        }

        mBinding.tabSquare.setOnClickListener {
            if (index == 1) return@setOnClickListener
            mBinding.viewPager.setCurrentItem(1, false)
            bottomTabStyleChange(false)
        }

        mBinding.tabDynamic.setOnClickListener {
            if (index == 2) return@setOnClickListener
            mBinding.viewPager.setCurrentItem(2, false)
            bottomTabStyleChange(true)
        }
        mBinding.tabMessage.setOnClickListener {
            if (index == 3) return@setOnClickListener
            mBinding.viewPager.setCurrentItem(3, false)
            bottomTabStyleChange(false)
        }
        mBinding.tabMine.setOnClickListener {
            if (index == 4) return@setOnClickListener
            mBinding.viewPager.setCurrentItem(4, false)
            bottomTabStyleChange(false)
        }
    }

    private fun initData() {
        /*     val dialog = UpgradeDialog.newInstance(
                 UpgradeBean("新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本新版本","https://cos.pgyer.com/f579a0f5b6c94a99c7ca5483fd7f2cdc.apk?sign=35ed8b14b45a2fa57257b47dc989a14f&sign2=448735d6f57edca4b2e5e8e7ee553976&t=1704708041&response-content-disposition=attachment%3Bfilename%3D%22%E7%B1%B3%E8%AF%BA%E6%98%9F%E7%90%83_1.0.4.apk"
                                 ,"1.0.4",
                                 4,1,"\"1.修复了已知bug\\n2.优化了用户体验\\n3.新增了功能\"",1,"1.0.4"
             )
             )
             dialog.setDismissCallback {
                 showOtherDialog()
             }
             dialog.show(supportFragmentManager)
      */


        mViewModel.checkVersion()
        mViewModel.sayHi()
        //更新用户配置
        mViewModel.getUserInfo()
        initConfig()
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeOnlineStatus(onlineStatusObserver, true)


        NIMClient.getService(MsgServiceObserve::class.java)
            .observeRecentContact(recentContactObserver, true)


    }

    private fun initConfig() {
        getAppConfigByKey<Int>(AppConfigKey.CHAT_MIN_LEVEL) {
            MMKVProvider.userChatMinLevel = it
        }
    }

    private fun initAppConfig() {
        Constants.isTokenExpired = false
        //获取微信公众号
        getAppConfigByKey<String>(AppConfigKey.COMMERCE_WECHAT) {
            MMKVProvider.wechatPublicAccount = it
        }

        getAppConfigByKey<String>(AppConfigKey.WITHDRAW_DEPOSIT_FEE) {
            try {
                val pattern = "0.##%"
                val decimalFormat = DecimalFormat(pattern)
                MMKVProvider.withdrawDepositFee = decimalFormat.format(it.toFloat())
            } catch (_: Exception) {

            }
        }

//        request<WxConfig>(AppConfigKey.WECHAT_APP_INFO,Method.POST, onSuccess = {
//            logE("wx_config---$it")
//            MMKVProvider.wxConfig = it
//            it.let {
//                if(it.wechat_app_id.isNotEmpty()&&it.wechat_app_secret.isNotEmpty()){
//                    PlatformConfig.setWeixin(it.wechat_app_id, it.wechat_app_secret)
//                }
//            }
//        })

//        getAppConfigByKey<List<RoomGameConfig>>(AppConfigKey.ROOM_ACTIVE_PATH_CONFIG) { gameConfig ->
//            logE("config---$gameConfig")
//            MMKVProvider.gameConfig = GsonUtils.toJson(gameConfig)
//        }

        val currentTimeString = System.currentTimeMillis().millis2String(YYYY_MM_DD)
        if (MMKVProvider.userHourDate != currentTimeString) {
            MMKVProvider.userHour = MMKVProvider.userHour + 1
            MMKVProvider.userHourDate = currentTimeString
        }
    }

    private fun showOtherDialog() {
        mViewModel.requestNotice()

//        if (showAdolescentDialog(MMKVProvider.userId)) {
//            TeenagerDialog(this).show()
//        }
    }

    private fun initViewPager() {
        val fragments =
            arrayOf(HomeFragmentV3(), FirmFragment(), PartyV2Fragment(), MessageFragmentV3(), MineFragment())
        mBinding.viewPager.apply {
            offscreenPageLimit = fragments.size
            isUserInputEnabled = false
            adapter = object : FragmentStateAdapter(this@MainActivity) {
                override fun getItemCount(): Int = fragments.size
                override fun createFragment(position: Int): Fragment = fragments[position]
                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    recyclerView.setItemViewCacheSize(fragments.size)
                }
            }
        }
        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                refreshBottomBar(position)
                index = position
            }
        })

        initTab()
        refreshBottomBar(0)
    }

    private fun initTab() {
//        mBinding.animviewExplore.stopPlay()
//        mBinding.animviewExplore.visibility = View.GONE
//        mBinding.ivHomeTop.visibility = View.VISIBLE
//
//        mBinding.animviewDynamic.stopPlay()
//        mBinding.animviewDynamic.visibility = View.GONE
//        mBinding.ivDynamic.visibility = View.VISIBLE
//
//        mBinding.animviewMessage.stopPlay()
//        mBinding.animviewMessage.visibility = View.GONE
//        mBinding.ivMessage.visibility = View.VISIBLE
//
//        mBinding.animviewMine.stopPlay()
//        mBinding.animviewMine.visibility = View.GONE
//        mBinding.ivMine.visibility = View.VISIBLE
    }

    fun refreshBottomBar(index: Int) {
        mBinding.appSelectExplore.isSelected = index == 0
        mBinding.appSelectSquare.isSelected = index == 1
        mBinding.appSelectParty.isSelected = index == 2
        mBinding.appSelectMessage.isSelected = index == 3
        mBinding.appSelectMine.isSelected = index == 4
        //initTab()
    }


    override fun onResume() {
        super.onResume()
        updateMessageCount()
        mViewModel.requestCollectList()
    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.checkVersionEvent, onSuccess = {
            if (it.intVersion > appVersionCode && it.isShowUpgrade()) {
                val dialog = UpgradeDialog.newInstance(it)
                dialog.setDismissCallback {
                    showOtherDialog()
                }
                dialog.show(supportFragmentManager)
            } else {
                showOtherDialog()
            }
        }, onError = {
            deleteDir(File(apkAbsolutePath))
            showOtherDialog()
        }, onEmpty = {
            deleteDir(File(apkAbsolutePath))
            showOtherDialog()
        })

        collectData(mViewModel.collectListEvent, onSuccess = {
            if (it.total > 0 && mBinding.viewPager.currentItem == 2) {
                mBinding.collectRoomView.visibility = View.VISIBLE
                mBinding.collectRoomView.initData(it.records[0].roomIcon, it.total)
            } else {
                mBinding.collectRoomView.visibility = View.GONE
            }
        })

        //监听通知弹框事件
        collectData(mViewModel.noticeEvent, onSuccess = {
            if (it.noticeFrequency == "001") {//一直弹出
                NoticeDialog.newInstance(it.noticeParam).show(supportFragmentManager)
                MMKVProvider.noticeCache = it.intVersion
            } else {
                //缓存版本小就弹出
                if (MMKVProvider.noticeCache < it.intVersion) {
                    NoticeDialog.newInstance(it.noticeParam).show(supportFragmentManager)
                    MMKVProvider.noticeCache = it.intVersion
                }
            }
        }, onError = {}, onEmpty = {}
        )


        //当电话进来时帮他退出房间


        //监听开启房间悬浮窗事件
        observerEvent<Event.ShowRoomFloating>(this) { room ->
            if (roomFloating == null) {
                showFloating(room.crId, room.roomCover)
            } else {
                roomFloating?.show()
            }
        }

        //监听悬浮窗关闭事件
        observerEvent<Event.CloseRoomFloating>(this) {
            roomFloating?.cancel()
            roomFloating = null
        }
        //监听悬浮窗刷新封面
        observerEvent<Event.RefreshRoomFloatingCoverEvent>(this) {
            val image = roomFloating?.getView()?.findViewById<ImageView>(R.id.image)
            image?.loadImageWithDefault(it.cover)
        }

        //刷新未读消息数量
        observerEvent<Event.RefreshUnReadMsgCount>(this) {
            updateMessageCount()
        }

        //切换账号
        observerEvent<Event.RefreshChangeAccountEvent>(this) {
            initConfig()
            initAppConfig()
            initDrawerInfo()
        }

        observerEvent<Event.PhoneInCome>(this) {
            if (roomFloating != null && roomFloating?.isShow() == true) {
                RoomServiceManager.release()
            }
        }

        observerEvent<Event.HamsterPurchaseEvent>(this) {
            mBinding.viewPager.currentItem = 1
        }

        //商行待领取红点
        observerEvent<Event.PineConeCollectionEvent>(this) {
            mViewModel.findUserPropWaitReceiveList {
                mBinding.vRedShape.visibility = if (it == true) View.VISIBLE else View.GONE
            }
        }

        //拉取身份列表
        collectData(mChangeAccountViewModel.accounts, onSuccess = {
            it.forEachIndexed { index, userAccountBean ->
                if (userAccountBean.userId == MMKVProvider.userId) {
                    loginAccountPosition = index
                }
            }
            if (it.size < 10) {
                mBinding.conAdd.visibility = View.VISIBLE
            } else {
                mBinding.conAdd.visibility = View.GONE
            }
            mBinding.recycler.bindingAdapter.addModels(it)
            mBinding.recycler.bindingAdapter.setChecked(loginAccountPosition, true)

        })

        //创建账号
        collectData(mChangeAccountViewModel.createAccounts, onSuccess = {
            mChangeAccountViewModel.loginByUserId(it.userId)
        }, onError = {
            hideLoading()
            toast("创建账号失败,请稍后重试")
        })

        //切换账号
        collectData(mChangeAccountViewModel.token, onSuccess = {
            //先退出之前账号的房间
            oldAccountExit {
                mChangeAccountViewModel.loginIm(it, onSuccess = {
                    //切换身份
                    if (!isCreateAccount) {
                        isCreateAccount = false
                        changeAccountSuccess()
                    } else {
                        //新建身份
                        finish()
                    }
                })
            }
        }, onError = {
            hideLoading()
            toast("切换账号失败")
        })

        initData()
        initAppConfig()
    }

    override fun onDestroy() {
        super.onDestroy()
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeOnlineStatus(onlineStatusObserver, false)

        NIMClient.getService(MsgServiceObserve::class.java)
            .observeRecentContact(recentContactObserver, false)

        roomFloating?.cancel()


    }

    private fun showFloating(crId: String, cover: String) {
        roomFloating = ScopeHelper.build {
            val view =
                LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_room_floating, null)
            val imageView = view?.findViewById<ImageView>(R.id.image)
            imageView?.loadImageWithDefault(cover)
            val animation = AnimationUtils.loadAnimation(
                this@MainActivity, com.kissspace.module_common.R.anim.common_room_floating_rotate
            )
            imageView?.startAnimation(animation)
            setLayoutView(view)
            setGravity(FxGravity.RIGHT_OR_BOTTOM)
            setEnableAssistDirection(0f, 600f, 0f, 40f)
            setEnableEdgeAdsorption(false)
            val ivClose: ImageView = view.findViewById(R.id.iv_close)
            ivClose.safeClick {
                RoomServiceManager.release()
            }

            view.safeClick {
                jumpRoom(crId = crId)
            }
        }.toControl(this@MainActivity)
        roomFloating?.show()
    }


    private fun updateMessageCount() {
        try {
            if (NIMClient.getStatus() == StatusCode.LOGINED) {
                val unReadCount = NIMClient.getService(MsgService::class.java).totalUnreadCount  //+ MMKVProvider.systemMessageUnReadCount
                mBinding.tvNotifyCount.visibility = if (unReadCount > 0) View.VISIBLE else View.GONE
                mBinding.tvNotifyCount.text =
                    if (unReadCount > 99) "99+" else unReadCount.toString()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun pressBackTwiceToExitApp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            private var lastBackTime: Long = 0
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackTime > 2000) {
                    toast("再次点击退出应用")
                    lastBackTime = currentTime
                } else {
                    RoomServiceManager.release {
                        finishAllActivities()
                    }
                }
            }
        })
    }


    /**
     *  @param isSlectPart 是否选中party tab
     */
    private fun bottomTabStyleChange(isSlectPart: Boolean) {
        if (isSlectPart) {
            mBinding.appSelectExplore.setImageResource(R.mipmap.app_ic_home_purple)
            mBinding.appSelectSquare.setImageResource(R.mipmap.app_ic_company_purple)
            mBinding.appSelectParty.setImageResource(R.mipmap.app_ic_party_purple)
            mBinding.appSelectMessage.setImageResource(R.mipmap.app_ic_message_purple)
            mBinding.appSelectMine.setImageResource(R.mipmap.app_ic_mine_purple)
            mBinding.bottomBar.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    com.kissspace.module_common.R.color.color_1B0753
                )
            )

        } else {
            mBinding.appSelectExplore.setImageResource(R.drawable.app_select_home)
            mBinding.appSelectSquare.setImageResource(R.drawable.app_select_company)
            mBinding.appSelectParty.setImageResource(R.drawable.app_select_party)
            mBinding.appSelectMessage.setImageResource(R.drawable.app_select_message)
            mBinding.appSelectMine.setImageResource(R.drawable.app_select_mine)
            mBinding.bottomBar.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    com.kissspace.module_common.R.color.color_262A2E
                )
            )


        }

    }

}

