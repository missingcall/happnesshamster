 package com.hamster.happyness.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.TabGradientCallback
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.kissspace.mine.widget.FirstChargeDialog
import com.kissspace.pay.utils.PayUtils
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentMainPartyBinding
import com.hamster.happyness.databinding.FragmentMainPartyV2Binding
import com.hamster.happyness.viewmodel.PartyViewModel
import com.kissspace.common.base.BaseFragment
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
import com.kissspace.common.http.getSelectPayChannelList
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.util.toast
import okhttp3.Route

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
        mViewModel.getHomeBanner()
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



       /* mBinding.tabLayout.configTabLayoutConfig {
            tabGradientCallback= object :TabGradientCallback(){
                override fun onGradientColor(
                    view: View?,
                    startColor: Int,
                    endColor: Int,
                    percent: Float
                ) {

                    if (percent == 1.0f){
                            for (i in 0 until mBinding.tabLayout.childCount){
                                (mBinding.tabLayout[i] as TextView).let {

                                    val text = it.text
                                    val spannableString = SpannableString(text)
                                    // 创建渐变Shader
                                    val startColor = Color.parseColor("#6699FD") // 渐变起始颜色
                                    val endColor = Color.parseColor("#6699FD") // 済变结束颜色

                                    val shader = LinearGradient(
                                        0f, 0f, 0f, it.height.toFloat(),
                                        startColor, endColor,Shader.TileMode.MIRROR
                                    )
                                    it.paint.shader = shader
                                    it.text = spannableString


                                }
                            }

                        (view as? TextView)?.let {
                            val text = it.text
                            val spannableString = SpannableString(text)
                            // 创建渐变Shader
                            val startColor = Color.parseColor("#9C4AFA") // 渐变起始颜色
                            val endColor = Color.parseColor("#6699FD") // 済变结束颜色

                            val shader = LinearGradient(
                                0f, 0f, 0f, it.height.toFloat(),
                                startColor, endColor,Shader.TileMode.MIRROR
                            )
                            it.paint.shader = shader
                            it.text = spannableString
                        }
                    }

                }
            }
        }*/

        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
    }

    override fun createDataObserver() {
        super.createDataObserver()
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
        mViewModel.getRoomTagList()
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