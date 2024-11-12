 package com.hamster.happyness.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.SizeUtils
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentMainPartyV2Binding
import com.hamster.happyness.viewmodel.PartyViewModel
import com.kissspace.common.adapter.BannerAdapter
import com.kissspace.common.base.BaseLazyFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.router.jump
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.model.RoomTagListBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.*
import com.kissspace.network.result.collectData
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.RoomListBannerBean
import com.kissspace.util.dp
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.listener.OnBannerListener

 /**
 *

 *
 */
class PartyV2Fragment : BaseLazyFragment(R.layout.fragment_main_party_v2) {
    private val mBinding by viewBinding<FragmentMainPartyV2Binding>()
    private val mViewModel by viewModels<PartyViewModel>()
    private val mTabList = mutableListOf<RoomTagListBean>()
    override fun lazyInitView() {
        initRefresh()
        initViewPager()
    }

    override fun lazyLoadData() {
      //  mViewModel.getHomeBanner()
        mViewModel.getRoomTagList()
        mViewModel.getRoomListBanner()
    }

    override fun lazyClickListener() {
        super.lazyClickListener()
        mBinding.tvSearch.safeClick {
            jump(RouterPath.PATH_SEARCH)
        }

        mBinding.imgHouse.safeClick {
            jumpRoom(roomType = Constants.ROOM_TYPE_PARTY)
        }


    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.setMarginStatusBar()
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this
    }

    private fun initRefresh() {
        mBinding.refreshLayout.apply {
            setEnableRefresh(true)
            setEnableLoadMore(false)
            setOnRefreshListener {
                mViewModel.getHomeBanner()
                mViewModel.getRoomTagList()
//                val fragment =
//                    childFragmentManager.findFragmentByTag("f${mBinding.viewPager.currentItem}")
//                if (fragment != null) {
//                    (fragment as PartyPageListFragment).onRefresh()
//                }
            }
        }
    }


    private fun initViewPager() {
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@PartyV2Fragment) {
                override fun getItemCount(): Int = mTabList.size
                override fun createFragment(position: Int): Fragment =
                    PartyPageListFragment.newInstance(position, mTabList[position].roomTagId)

                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    recyclerView.setItemViewCacheSize(3)
                }
            }
        }



        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
    }

    override fun lazyDataChangeListener() {
        super.lazyDataChangeListener()
        collectData(mViewModel.tagListEvent, onSuccess = {
            mBinding.refreshLayout.finishRefresh()
            addTab(it)
        }, onEmpty = {

            mBinding.refreshLayout.finishRefresh()
        }, onError = {
            mBinding.refreshLayout.finishRefresh()
        })
        FlowBus.observerEvent<Event.RefreshChangeAccountEvent>(this) {
            mBinding.refreshLayout.autoRefresh()
        }

        collectData(mViewModel.roomListBannerEvent, onSuccess = {
            val banner =
                it.filter { item -> item.state == "001" && item.os == "001" }
            if (banner.isNotEmpty()) {
                mBinding.banner.apply {
                    visibility = View.VISIBLE
                    if(adapter == null) {
                        setAdapter(BannerAdapter(banner))
                        indicator = RectangleIndicator(requireActivity())
                        setIndicatorSelectedColorRes(com.kissspace.module_common.R.color.common_white)
                        setIndicatorNormalColorRes(com.kissspace.module_common.R.color.color_80FFFFFF)
                        setIndicatorWidth(12.dp.toInt(), 12.dp.toInt())
                        setIndicatorHeight(4.dp.toInt())
                        setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, 10.dp.toInt()))
                        setOnBannerListener(object : OnBannerListener<RoomListBannerBean> {
                            override fun OnBannerClick(data: RoomListBannerBean?, position: Int) {
                                handleSchema(data?.schema)
                            }
                        })
                        addBannerLifecycleObserver(requireActivity())
                    }else{
                        adapter.setDatas(banner)
                    }
                }
            }
        })


    }

    var index = 0

    @SuppressLint("NotifyDataSetChanged")
    private fun addTab(tabList: List<RoomTagListBean>) {
        if(tabList.isNotEmpty()) {
           mBinding.refreshLayout.setEnableRefresh(false)
        }
        if (areCollectionsDifferent(mTabList, tabList)) {
            mTabList.clear()
            mTabList.addAll(tabList.filter { it.state == "001" })
            mBinding.tabLayout.removeAllViews()
            mTabList.forEach {
                val param = DslTabLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                param.setMargins(SizeUtils.dp2px(10f), 0, SizeUtils.dp2px(10f), SizeUtils.dp2px(10f))
                val textView = TextView(context)
                textView.text = it.roomTag
                textView.gravity = Gravity.BOTTOM
                textView.layoutParams = param
                mBinding.tabLayout.addView(textView)
            }
            val adapter = mBinding.viewPager.adapter as FragmentStateAdapter
            adapter.notifyDataSetChanged()
        }
        val fragment = childFragmentManager.findFragmentByTag("f${mBinding.viewPager.currentItem}")
        if (fragment != null) {
            (fragment as PartyPageListFragment).onRefresh()
        }
    }
    fun finishRefresh() {
        mBinding.refreshLayout.finishRefresh()
    }
}