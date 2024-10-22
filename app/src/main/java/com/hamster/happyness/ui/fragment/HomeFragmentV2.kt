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
class HomeFragmentV2 : BaseFragment(R.layout.fragment_main_explore) {

    private val mBinding by viewBinding<FragmentMainExploreBinding>()
    private val mViewModel by viewModels<ExploreViewModel>()
    private val mTabList = mutableListOf<String>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.lifecycleOwner = this
        mBinding.ivExploreSearch.safeClick {
            jump(RouterPath.PATH_SEARCH)
        }
        mBinding.titleBar.setMarginStatusBar()

        mBinding.tvAchievementRank.safeClick {
/*            val url =
                getH5Url(
                    Constants.H5.roomRankUrl,
                    true
                ) + "&fixedHeight=${BarUtils.getStatusBarHeight()}"
            jump(RouterPath.PATH_WEBVIEW, "url" to url)*/

            val url = "http://192.168.0.131:7456/web-mobile/web-mobile/index.html"
            jump(RouterPath.PATH_WEBVIEW, "url" to url)
        }
        initViewPager()
    }

    override fun createDataObserver() {
        super.createDataObserver()
        scopeLife {
            mViewModel.tagListEvent.collect {
                addTab(it)
            }
        }
        mViewModel.getExploreFlags()
    }


    private fun initViewPager() {
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@HomeFragmentV2) {
                override fun getItemCount(): Int = mTabList.size
                override fun createFragment(position: Int): Fragment = ExplorePageListFragment.newInstance(position, "")

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    recyclerView.setItemViewCacheSize(3)
                }
            }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addTab(tabList: List<String>) {
        if (areCollectionsDifferent(mTabList, tabList)) {
            mTabList.clear()
            mTabList.addAll(tabList)
            mBinding.tabLayout.removeAllViews()
            mTabList.forEach {
                val param = DslTabLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                param.setMargins(SizeUtils.dp2px(10f), 0, SizeUtils.dp2px(10f), SizeUtils.dp2px(10f))
                val textView = TextView(context)
                textView.text = it
                textView.gravity = Gravity.BOTTOM
                textView.layoutParams = param
                mBinding.tabLayout.addView(textView)
            }
            val adapter = mBinding.viewPager.adapter as FragmentStateAdapter
            adapter.notifyDataSetChanged()
        }
        val fragment = childFragmentManager.findFragmentByTag("f${mBinding.viewPager.currentItem}")
        if (fragment != null) {
            (fragment as ExplorePageListFragment).onRefresh()
        }
    }

}