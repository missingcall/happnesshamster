package com.kissspace.mine.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.kissspace.util.dp
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.adapter.BannerAdapter
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.Constants
import com.kissspace.common.router.jump
import com.kissspace.common.router.parseIntent
import com.kissspace.common.ext.safeClick

import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.mmkv.MMKVProvider

import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.previewPicture
import com.kissspace.dynamic.ui.fragment.DynamicListFragment
import com.kissspace.mine.ui.fragment.BosomFriendFragment
import com.kissspace.mine.ui.fragment.MineMainFragment
import com.kissspace.mine.viewmodel.UserProfileViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMineProfileNewBinding
import com.kissspace.util.immersiveStatusBar
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import kotlinx.coroutines.launch


/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/30 15:33
 * @Description: 个人主页
 *
 */
@Router(path = RouterPath.PATH_USER_PROFILE)
class UserProfileNewActivity : BaseActivity(R.layout.mine_activity_mine_profile_new) {
    private val userId by parseIntent<String>()
    private val mBinding by viewBinding<MineActivityMineProfileNewBinding>()
    private val mViewModel by viewModels<UserProfileViewModel>()
    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.vm = mViewModel
        initTitleBar()
        initEvent()
        initViewPager()
    }
    private fun initViewPager() {
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@UserProfileNewActivity) {
                override fun getItemCount(): Int = 3
                override fun createFragment(position: Int): Fragment =
                    when (position) {
                        0 -> MineMainFragment.newInstance(userId)
                        1 -> DynamicListFragment.newInstance(userId,mViewModel.userInfo.get()?.nickname,mViewModel.userInfo.get()?.profilePath,mViewModel.userInfo.get()?.sex)
                        else -> BosomFriendFragment()
                    }
                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    recyclerView.setItemViewCacheSize(1)
                }
            }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
    }



    private fun initEvent() {
        mBinding.tvDynamic.safeClick {
            mViewModel.userInfo.get()?.let {
                jumpRoom(crId = it.followRoomId)
//                jump(
//                    RouterPath.PATH_My_DYNAMIC,
//                    "userId" to mViewModel.userInfo.get()!!.userId,
//                    "nickname" to mViewModel.userInfo.get()!!.nickname,
//                    "profilePath" to mViewModel.userInfo.get()!!.profilePath,
//                    "sex" to mViewModel.userInfo.get()!!.sex
//                )
            }
        }
    }

    private fun initTitleBar() {
        if (userId == MMKVProvider.userId) {
            mBinding.titleBar.setRightIcon(R.mipmap.mine_icon_profile_room)
        }
//        val lp = mBinding.viewStatusBar.layoutParams
//        lp.height = statusBarHeight
//        mBinding.viewStatusBar.layoutParams = lp
//        val height = 200.dp
//        mBinding.scrollView.viewTreeObserver.addOnScrollChangedListener {
//            val scrollY = mBinding.scrollView.scrollY.toFloat()
//            var scrollYPercent:Float = scrollY/height
//            scrollYPercent = if(scrollYPercent>1) 1f else scrollYPercent
//            mBinding.layoutStatusBar.background.alpha = (255*scrollYPercent).toInt()
//
//
//
////            if (scrollY <= 0) {
////                setStatusBarBlackText()
////                mBinding.titleBar.leftIcon.setTint(Color.WHITE)
////                if (userId == MMKVProvider.userId) {
////                    mBinding.titleBar.rightIcon.setTint(Color.WHITE)
////                }
////                mBinding.viewStatusBar.setBackgroundColor(Color.TRANSPARENT)
////                mBinding.layoutStatusBar.setBackgroundColor(Color.TRANSPARENT)
////                mBinding.titleBar.setBackgroundColor(Color.TRANSPARENT)
////            } else if (scrollY > 0 && scrollY <= 100.dp) {
////                setStatusBarWhiteText()
////                mBinding.layoutStatusBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
////                mBinding.titleBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
////                val rate = (scrollY.toFloat() / 100.dp)
////                mBinding.layoutStatusBar.background.alpha =
////                    if (scrollY >= 100.dp.toInt()) 255 else (rate * 255).toInt()
////                mBinding.titleBar.background.alpha =  if (scrollY >= 100.dp.toInt()) 255 else (rate * 255).toInt()
////            } else {
////                setStatusBarWhiteText()
////                mBinding.titleBar.leftIcon.setTint(Color.WHITE)
////                if (userId == MMKVProvider.userId) {
////                    mBinding.titleBar.rightIcon.setTint(Color.WHITE)
////                }
////                mBinding.viewStatusBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
////                mBinding.layoutStatusBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
////                mBinding.titleBar.setBackgroundColor(com.kissspace.module_common.R.color.color_E6000000.resToColor())
////                mBinding.layoutStatusBar.background.alpha = 255
////
////            }
//        }
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


    override fun createDataObserver() {
        super.createDataObserver()
        lifecycleScope.launchWhenResumed {
            mViewModel.userProfileEvent.collect {
                MMKVProvider.userProfileInfo = it
                val images = mutableListOf<String>()
                images.addAll(it.bgPath)
                if (images.isEmpty()) {
                    images.add(it.profilePath)
                }
                mBinding.bannerPicture.apply {
                    setAdapter(BannerAdapter(images,true))
                    indicator = RectangleIndicator(this@UserProfileNewActivity)
                    setIndicatorSelectedColorRes(com.kissspace.module_common.R.color.common_white)
                    setIndicatorNormalColorRes(com.kissspace.module_common.R.color.color_80FFFFFF)
                    setIndicatorWidth(12.dp.toInt(), 12.dp.toInt())
                    setIndicatorHeight(4.dp.toInt())
                    setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, 28.dp.toInt()))
                    setOnBannerListener { _item, position ->
                        previewPicture(this@UserProfileNewActivity, position, null, images)
                    }
                }.addBannerLifecycleObserver(this@UserProfileNewActivity)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                 mViewModel.requestUserInfo(userId)
            }
        }
    }

}

