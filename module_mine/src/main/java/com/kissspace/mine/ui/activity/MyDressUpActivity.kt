package com.kissspace.mine.ui.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.kissspace.common.router.parseIntent
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.mine.ui.fragment.MyDressUpFragment
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMyDressUpBinding
import com.kissspace.util.setStatusBarColor

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/5 10:56
 * @Description: 我的装扮
 *
 */
@Router(path = RouterPath.PATH_MY_DRESS_UP)
class MyDressUpActivity :
    com.kissspace.common.base.BaseActivity(R.layout.mine_activity_my_dress_up) {

    private val mBinding by viewBinding<MineActivityMyDressUpBinding>()
    private val position: Int by parseIntent()
    override fun initView(savedInstanceState: Bundle?) {
        setStatusBarColor(
            ContextCompat.getColor(
                this,
                com.kissspace.module_common.R.color.color_232323
            ), isStatusBlackText = false
        )
        setTitleBarListener(mBinding.titleBar)

        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int) =
                when (position) {
                    0 -> MyDressUpFragment.newInstance("001")
                    1 -> MyDressUpFragment.newInstance("002")
                    else -> MyDressUpFragment.newInstance("003")
                }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
        if (position != 0) {
            mBinding.viewPager.currentItem = position
            mBinding.tabLayout.setCurrentItem(position)
        }
    }

}