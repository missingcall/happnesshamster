package com.kissspace.mine.ui.fragment


import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.SpanUtils
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.IBaseCommonPage
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.bean.BosomFriendBean
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.UserProfileViewModel

import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentBosomFriendBinding


class BosomFriendFragment : BaseFragment(R.layout.mine_fragment_bosom_friend),IBaseCommonPage {
    private val mViewModel by activityViewModels<UserProfileViewModel>()

    private val mBinding by viewBinding<MineFragmentBosomFriendBinding>()
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.vm = mViewModel
        initPageView()
        mBinding.recyclerView.linear().setup {
            addType<BosomFriendBean> { R.layout.rv_bosom_friend }
            onBind {
                findView<TextView>(R.id.tv_cp_name).apply {
                    setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,if(mViewModel.userInfo.get()?.userId == MMKVProvider.userId) R.mipmap.ic_cp_gift else 0,0)
                }
            }

            onClick(R.id.tv_cp_name){
                if(mViewModel.userInfo.get()?.userId == MMKVProvider.userId){
                    GiftToCpFragment.newInstance(getModel<BosomFriendBean>(modelPosition).userId,getModel<BosomFriendBean>(modelPosition).userAvator).show(childFragmentManager)
                }
            }
        }

        FlowBus.observerEvent<Event.CPChanged>(this) {
            getPageRecycleView().refresh()
        }


        mBinding.ivGiftToCp.safeClick {
            mViewModel.userInfo.get()?.userId?.let { GiftToCpFragment.newInstance(it,mViewModel.userInfo.get()?.profilePath).show(childFragmentManager) }
        }
        getPageRecycleView().showLoading()
    }

    override fun getPageRecycleView(): PageRefreshLayout {
           return mBinding.pageRefreshLayout
    }

    override fun loadData(page: Int, size: Int) {
          scopeNetLife {
             val data = Post<List<BosomFriendBean>>(MineApi.API_GET_RELATION_RANKING){
                  json("userId" to mViewModel.userInfo.get()?.userId)
              }.await()
              if(mViewModel.userInfo.get()?.userId != MMKVProvider.userId) {
                  val bean = data.firstOrNull{ it.userId == MMKVProvider.userId }
                  mBinding.tvRelationIntro.text = if(bean == null) "您未与对方建立密友关系" else
                      SpanUtils().append("您与对方的密友关系：守护  目前排名第").append((data.indexOf(bean)+1).toString()).setForegroundColor(Color.parseColor("#FF8B55FF")).create()

              }
              setListData(data)
          }
    }


}