package com.kissspace.dynamic.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.SizeUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.model.RoomTagListBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.areCollectionsDifferent
import com.kissspace.common.util.getH5Url
import com.kissspace.module_dynamic.R
import com.kissspace.module_dynamic.databinding.DynamicFragmentMainV2Binding
import com.kissspace.module_dynamic.databinding.DynamicFragmentMainV3Binding
import com.kissspace.util.resToColor
import com.qmuiteam.qmui.kotlin.onClick

class DynamicFragmentV2:BaseFragment(R.layout.dynamic_fragment_main_v3) {
    private val mBinding by viewBinding<DynamicFragmentMainV3Binding>()
    private val mTabList = mutableListOf<String>()
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.setMarginStatusBar()
        initViewPager()
        addTab( mutableListOf("推荐","关注"))
    }

    private fun  initViewPager(){
        mBinding.viewPager.apply {
            offscreenPageLimit = 2
            isUserInputEnabled = false
            adapter = object : FragmentStateAdapter(this@DynamicFragmentV2) {
                override fun getItemCount(): Int = 3
                override fun createFragment(position: Int): Fragment = DynamicListFragment(position)
                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    recyclerView.setItemViewCacheSize(3)
                }
            }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
    }

//    private fun initTab(){
//        mBinding.tabRecommend.onClick {
//            currentIndex = 0
//            resetTab(0)
//            mBinding.viewPager.setCurrentItem(0,false)
//        }
//
//        mBinding.tabFollow.onClick {
//            currentIndex = 1
//            resetTab(1)
//            mBinding.viewPager.setCurrentItem(1,false)
//        }
//
//        mBinding.tabNearly.onClick {
//            currentIndex = 2
//            resetTab(2)
//            mBinding.viewPager.setCurrentItem(2,false)
//        }
//
//    }
//
//    private fun resetTab(item:Int){
//        mBinding.tabRecommend.isChecked = item == 0
//        mBinding.tabFollow.isChecked = item == 1
//        mBinding.tabNearly.isChecked = item == 2
//
//        mBinding.tabRecommend.textSize  =  if(item == 0) 15f else 14f
//        mBinding.tabFollow.textSize  =  if(item == 1) 15f else 14f
//        mBinding.tabNearly.textSize  =  if(item == 2) 15f else 14f
////        mBinding.tabRecommend.setBackgroundResource(R.drawable.dynamic_tab_normal)
////        mBinding.tabFollow.setBackgroundResource(R.drawable.dynamic_tab_normal)
////
////        mBinding.tabRecommend.paint.isFakeBoldText = false
////        mBinding.tabFollow.paint.isFakeBoldText = false
////
////        mBinding.tabRecommend.setTextColor(com.kissspace.module_common.R.color.color_80FFFFFF.resToColor())
////        mBinding.tabFollow.setTextColor(com.kissspace.module_common.R.color.color_80FFFFFF.resToColor())
////
////        textView.setBackgroundResource(R.drawable.dynamic_tab_selected)
////        textView.paint.isFakeBoldText = true
////        textView.setTextColor(com.kissspace.module_common.R.color.color_FFEB71.resToColor())
//
//
//    }

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
    }
}