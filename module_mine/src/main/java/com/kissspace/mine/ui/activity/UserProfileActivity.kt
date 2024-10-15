package com.kissspace.mine.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.didi.drouter.api.DRouter
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.util.dp
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.adapter.BannerAdapter
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.canSendMsgToOther
import com.kissspace.common.router.jump
import com.kissspace.common.router.parseIntent
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.CommonGiftInfo
import com.kissspace.common.model.RoomListBannerBean
import com.kissspace.common.provider.IMessageProvider
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.copyClip
import com.kissspace.common.util.customToast
import com.kissspace.common.util.handleSchema
import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.previewPicture
import com.kissspace.mine.viewmodel.UserProfileViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMineProfileBinding
import com.kissspace.util.immersiveStatusBar
import com.kissspace.util.statusBarHeight
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.listener.OnBannerListener


/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/30 15:33
 * @Description: 个人主页
 *
 */
//@Router(path = RouterPath.PATH_USER_PROFILE)
class UserProfileActivity : BaseActivity(R.layout.mine_activity_mine_profile) {
    private val userId by parseIntent<String>()
    private val mBinding by viewBinding<MineActivityMineProfileBinding>()
    private val mViewModel by viewModels<UserProfileViewModel>()
    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.vm = mViewModel
        initTitleBar()
        initGiftRecyclerView()
        initEvent()
    }


    override fun onResume() {
        super.onResume()
        mViewModel.requestUserInfo(userId)
    }


    private fun initEvent() {
        mBinding.tvChat.safeClick {
            if(canSendMsgToOther()){
                mViewModel.userInfo.get()?.let {
                    jump(RouterPath.PATH_CHAT, "account" to it.accid, "userId" to it.userId)
                }
            }
        }
        mBinding.tvIdValue.safeClick {
            mViewModel.userInfo.get()?.let {
                copyClip(it.displayId)
            }

        }

        mBinding.ivFollowRoom.safeClick {
            jumpRoom(crId = mViewModel.userInfo.get()!!.followRoomId)
        }

        mBinding.tvDynamic.safeClick {
            mViewModel.userInfo.get()?.let {
                jump(
                    RouterPath.PATH_My_DYNAMIC,
                    "userId" to mViewModel.userInfo.get()!!.userId,
                    "nickname" to mViewModel.userInfo.get()!!.nickname,
                    "profilePath" to mViewModel.userInfo.get()!!.profilePath,
                    "sex" to mViewModel.userInfo.get()!!.sex
                )
            }
//            var isSelf=false
//            if(mViewModel.userInfo.get()?.userId==MMKVProvider.userId){
//                isSelf=true
//            }
//            val nickname=mViewModel.userInfo.get()?.nickname.orEmpty()
//            jump(
//                RouterPath.PATH_WEBVIEW,
//                "url" to getH5Url(Constants.H5.dynamicIndex +"?userId=${userId}&isSelf=${isSelf}&nickName=${URLEncoder.encode(nickname,"UTF-8")}"),
//                "showTitle" to true,
//                "showTitleBarMargin" to true
//            )
        }

    }

    private fun initTitleBar() {
        if (userId == MMKVProvider.userId) {
            mBinding.titleBar.setRightIcon(R.mipmap.mine_icon_profile_room)
        }
        val lp = mBinding.viewStatusBar.layoutParams
        lp.height = statusBarHeight
        mBinding.viewStatusBar.layoutParams = lp
        val height = 200.dp
        mBinding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = mBinding.scrollView.scrollY.toFloat()
            var scrollYPercent:Float = scrollY/height
            scrollYPercent = if(scrollYPercent>1) 1f else scrollYPercent
            mBinding.layoutStatusBar.background.alpha = (255*scrollYPercent).toInt()
//            if (scrollY <= 0) {
//                setStatusBarBlackText()
//                mBinding.titleBar.leftIcon.setTint(Color.WHITE)
//                if (userId == MMKVProvider.userId) {
//                    mBinding.titleBar.rightIcon.setTint(Color.WHITE)
//                }
//                mBinding.viewStatusBar.setBackgroundColor(Color.TRANSPARENT)
//                mBinding.layoutStatusBar.setBackgroundColor(Color.TRANSPARENT)
//                mBinding.titleBar.setBackgroundColor(Color.TRANSPARENT)
//            } else if (scrollY > 0 && scrollY <= 100.dp) {
//                setStatusBarWhiteText()
//                mBinding.layoutStatusBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
//                mBinding.titleBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
//                val rate = (scrollY.toFloat() / 100.dp)
//                mBinding.layoutStatusBar.background.alpha =
//                    if (scrollY >= 100.dp.toInt()) 255 else (rate * 255).toInt()
//                mBinding.titleBar.background.alpha =  if (scrollY >= 100.dp.toInt()) 255 else (rate * 255).toInt()
//            } else {
//                setStatusBarWhiteText()
//                mBinding.titleBar.leftIcon.setTint(Color.WHITE)
//                if (userId == MMKVProvider.userId) {
//                    mBinding.titleBar.rightIcon.setTint(Color.WHITE)
//                }
//                mBinding.viewStatusBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
//                mBinding.layoutStatusBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
//                mBinding.titleBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
//                mBinding.layoutStatusBar.background.alpha = 255
//
//            }
        }
        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                finish()
            }

            override fun onRightClick(titleBar: TitleBar?) {
                super.onRightClick(titleBar)
                if (userId == MMKVProvider.userId) {
                    jumpRoom(roomType = Constants.ROOM_TYPE_PARTY)
                }
            }
        })
    }

    private fun initGiftRecyclerView() {
        mBinding.recyclerGift.grid(4).setup {
            addType<CommonGiftInfo> { R.layout.mine_layout_profile_gift_item }
            onBind {
                val model = getModel<CommonGiftInfo>()
                if (model.giftNum == 0) {
                    findView<ImageView>(R.id.giftIcon).alpha = 0.3f
                    findView<TextView>(R.id.giftName).alpha = 0.3f
                } else {
                    findView<ImageView>(R.id.giftIcon).alpha = 1f
                    findView<TextView>(R.id.giftName).alpha = 1f
                }
            }
        }.models = mutableListOf()
    }


    override fun createDataObserver() {
        super.createDataObserver()
        lifecycleScope.launchWhenResumed {
            mViewModel.userProfileEvent.collect {
                MMKVProvider.userProfileInfo = it
                mBinding.recyclerGift.bindingAdapter.mutable.clear()
                mBinding.recyclerGift.bindingAdapter.addModels(it.giftWall)
                val images = mutableListOf<String>()
                images.addAll(it.bgPath)
                if (images.isEmpty()) {
                    images.add(it.profilePath)
                }
                mBinding.bannerPicture.apply {
                    setAdapter(BannerAdapter(images,true))
                    indicator = RectangleIndicator(this@UserProfileActivity)
                    setIndicatorSelectedColorRes(com.kissspace.module_common.R.color.common_white)
                    setIndicatorNormalColorRes(com.kissspace.module_common.R.color.color_80FFFFFF)
                    setIndicatorWidth(12.dp.toInt(), 12.dp.toInt())
                    setIndicatorHeight(4.dp.toInt())
                    setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, 28.dp.toInt()))
                    setOnBannerListener { _item, position ->
                        previewPicture(this@UserProfileActivity, position, null, images)
                    }
                }.addBannerLifecycleObserver(this@UserProfileActivity)
            }
        }

        lifecycleScope.launchWhenResumed {
            mViewModel.giftListEvent.collect {
                mBinding.recyclerGift.bindingAdapter.addModels(it)
            }
        }
    }


}

data class GiftWallBean(var url: String, var name: String, var count: Int? = 0)