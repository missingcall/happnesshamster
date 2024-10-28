package com.hamster.happyness.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SizeUtils
import com.drake.net.utils.scopeLife
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentMainExploreBinding
import com.hamster.happyness.databinding.FragmentMainHomeV3Binding
import com.hamster.happyness.viewmodel.ExploreViewModel

import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.areCollectionsDifferent
import com.kissspace.common.util.getH5Url

//探索
class HomeFragmentV3 : BaseFragment(R.layout.fragment_main_home_v3) {

    private val mBinding by viewBinding<FragmentMainHomeV3Binding>()
    private val mViewModel by viewModels<ExploreViewModel>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.lifecycleOwner = this
        mBinding.ivExploreSearch.safeClick {
            jump(RouterPath.PATH_SEARCH)
        }

    }

    override fun createDataObserver() {
        super.createDataObserver()
        scopeLife {

        }
    }

}