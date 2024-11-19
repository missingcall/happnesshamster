package com.kissspace.mine.ui.activity.user

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.kissspace.common.ext.canSendMsgToOther
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.model.UserProfileBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.router.parseIntent
import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.previewPicture
import com.kissspace.common.widget.UserLevelIconView
import com.kissspace.mine.ui.fragment.user.MineMainNew2Fragment
import com.kissspace.mine.viewmodel.UserProfileViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMineProfileNew2Binding
import com.kissspace.util.dp
import com.kissspace.util.immersiveStatusBar
import com.kissspace.util.loadImage
import com.kissspace.util.orZero
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import kotlinx.coroutines.launch

/**
 * @description: 个人信息页面 重构
 * @author: yxt
 * @create: 2024-11-18 11:00
 **/
@Router(path = RouterPath.PATH_USER_PROFILE)
class UserProfileNew2Activity : BaseActivity(R.layout.mine_activity_mine_profile_new2) {
    private val mBinding by viewBinding<MineActivityMineProfileNew2Binding>()
    private val mViewModel by viewModels<UserProfileViewModel>()
    private val userId by parseIntent<String>()

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.titleBar.setMarginStatusBar()
        initTitleBar()
        initOptionView()

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

    private fun initOptionView(){
        mBinding.tvEdit.safeClick {
            jump(RouterPath.PATH_EDIT_PROFILE)
        }

        mBinding.tvFollow.safeClick {
            mViewModel.followNew{
               mBinding.tvFollow.text =  if (it) "取消关注" else "关注"
            }
        }

        mBinding.tvChat.safeClick {
                if(canSendMsgToOther()){
                    mViewModel.userInfo.get()?.let {
                        jump(RouterPath.PATH_CHAT, "account" to it.accid, "userId" to it.userId)
                    }
                }

        }

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
                    mBinding.viewShade.visibility=View.VISIBLE
                    //底部操作View
                    mBinding.flBottom.visibility = View.VISIBLE
                    if (MMKVProvider.userId == userId) {
                        mBinding.tvEdit.visibility = View.VISIBLE
                        mBinding.tvFollow.visibility= View.GONE
                        mBinding.flChat.visibility= View.GONE

                    } else {
                        mBinding.tvEdit.visibility = View.GONE
                        mBinding.tvFollow.visibility= View.VISIBLE
                        mBinding.flChat.visibility= View.VISIBLE
                        mBinding.tvFollow.text = if (it.attention) "取消关注" else "关注"
                    }

                    showUserMedal(mBinding.flowLayout,it)
                }
            }
        }
    }

    fun showUserMedal(layout: com.nex3z.flowlayout.FlowLayout, model: UserProfileBean?) {
        model?.let {
            layout.removeAllViews()
            if (it.consumeLevel.orZero() > 0) {
                val wealthLevel = UserLevelIconView(layout.context)
                wealthLevel.setLeveCount(UserLevelIconView.LEVEL_TYPE_EXPEND, it.consumeLevel)
                layout.addView(wealthLevel)
            }
            if (it.charmLevel.orZero() > 0) {
                val charmLevel = UserLevelIconView(layout.context)
                charmLevel.setLeveCount(UserLevelIconView.LEVEL_TYPE_INCOME, it.charmLevel!!)
                layout.addView(charmLevel)
            }
            val lp =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 19.dp.toInt())
            it.medalList?.forEach { that ->
                val image = ImageView(layout.context)
                image.adjustViewBounds = true
                image.layoutParams = lp
                image.loadImage(that.url)
                layout.addView(image)
            }
        }
    }

}