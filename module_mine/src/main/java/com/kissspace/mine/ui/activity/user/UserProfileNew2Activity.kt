package com.kissspace.mine.ui.activity.user

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.adapter.BannerAdapter
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.parseIntent
import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.previewPicture
import com.kissspace.mine.ui.fragment.user.MineMainNew2Fragment
import com.kissspace.mine.viewmodel.UserProfileViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMineProfileNew2Binding
import com.kissspace.util.dp
import com.kissspace.util.immersiveStatusBar
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import kotlinx.coroutines.launch

/**
 * @description: 个人信息页面 重构
 * @author: yxt
 * @create: 2024-11-18 11:00
 **/
@Router(path = RouterPath.PATH_USER_PROFILE_NEW)
class UserProfileNew2Activity : BaseActivity(R.layout.mine_activity_mine_profile_new2) {
    private val mBinding by viewBinding<MineActivityMineProfileNew2Binding>()
    private val mViewModel by viewModels<UserProfileViewModel>()
    private val userId by parseIntent<String>()

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.titleBar.setMarginStatusBar()
        initTitleBar()


        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@UserProfileNew2Activity) {
                override fun getItemCount(): Int = 1
                override fun createFragment(position: Int): Fragment =
                    MineMainNew2Fragment.newInstance(userId)

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    recyclerView.setItemViewCacheSize(1)
                }
            }
        }
    }


    /**
     * 初始化titleBar
     */
    private fun initTitleBar() {
        if (userId == MMKVProvider.userId) {
            mBinding.titleBar.setRightIcon(R.mipmap.mine_icon_profile_room)
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


    override fun createDataObserver() {
        super.createDataObserver()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                mViewModel.requestUserInfo(userId)

                mViewModel.userProfileEvent.collect {

                    MMKVProvider.userProfileInfo = it
                    val images = mutableListOf<String>()
                    images.addAll(it.bgPath)
                    if (images.isEmpty()) {
                        images.add(it.profilePath)
                    }
                    mBinding.bannerPicture.apply {
                        setAdapter(BannerAdapter(images, true))
                        indicator = RectangleIndicator(this@UserProfileNew2Activity)
                        setIndicatorSelectedColorRes(com.kissspace.module_common.R.color.common_white)
                        setIndicatorNormalColorRes(com.kissspace.module_common.R.color.color_80FFFFFF)
                        setIndicatorWidth(12.dp.toInt(), 12.dp.toInt())
                        setIndicatorHeight(4.dp.toInt())
                        setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, 28.dp.toInt()))
                        setOnBannerListener { _item, position ->
                            previewPicture(this@UserProfileNew2Activity, position, null, images)
                        }
                    }.addBannerLifecycleObserver(this@UserProfileNew2Activity)

                    mBinding.tvNickname.text =it.nickname
                    mBinding.tvNickname.helper.apply {
                        setIconNormalRight(
                            if (it.sex == Constants.SEX_MALE) ContextCompat.getDrawable(
                                this@UserProfileNew2Activity,
                                R.mipmap.mine_icon_sex_male
                            ) else
                                ContextCompat.getDrawable(
                                    this@UserProfileNew2Activity,
                                    R.mipmap.mine_sex_female
                                )
                        )
                    }
                }
            }
        }
    }
}