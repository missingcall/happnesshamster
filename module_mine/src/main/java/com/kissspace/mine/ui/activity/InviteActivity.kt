package com.kissspace.mine.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.model.InfoListModel
import com.kissspace.common.model.InvitePeopleListModel
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.copyClip
import com.kissspace.common.util.countDown
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.ui.fragment.GoodsListFragment
import com.kissspace.mine.ui.fragment.WalletCoinGainFragment
import com.kissspace.mine.ui.fragment.WareHouseFragment
import com.kissspace.mine.viewmodel.HamsterViewModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityInviteBinding
import com.kissspace.module_mine.databinding.MineActivityMyWarehouseBinding
import com.kissspace.util.immersiveStatusBar
import com.kissspace.util.logE
import kotlinx.coroutines.Job

@Router(path = RouterPath.PATH_INVITE)
class InviteActivity : BaseActivity(R.layout.mine_activity_invite) {

    private val mBinding by dataBinding<MineActivityInviteBinding>()
    private val mViewModel by viewModels<HamsterViewModel>()
    private var mCountDown: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this
        mBinding.pageRefreshLayout.apply {
            onRefresh {
                initData(true)
            }
            onLoadMore {
                initData(false)
            }
        }.refresh()
        initRecyclerView()

        mBinding.tvMineInviteCodeDetail.text = "邀请码：" + MMKVProvider.inviteCode

        //刷新列表
        mBinding.ivRefresh.safeClick {
            mBinding.pageRefreshLayout.autoRefresh()
        }

        //邀请好友
        mBinding.btnInvite.safeClick {
            jump(RouterPath.PATH_INVITATION_POSTER)
        }

        //复制邀请码
        mBinding.btnCopy.safeClick {
            copyClip(MMKVProvider.inviteCode)
        }

        //倒计时10秒
        mCountDown = countDown(10, reverse = false, scope = lifecycleScope, onTick = {

        }, onFinish = {

            mCountDown?.cancel()
        })
    }

    private fun initData(isRefresh: Boolean) {
        if (isRefresh) {
            mBinding.pageRefreshLayout.index = 1
        }
        mViewModel.getInvitePeopleList(mBinding.pageRefreshLayout.index) {
            if (isRefresh) {
                mBinding.recyclerView.bindingAdapter.mutable.clear()
                if (it?.records?.size == 0) {
                    mBinding.pageRefreshLayout.showEmpty()
                } else {
                    mBinding.pageRefreshLayout.addData(
                        it?.records
                    )
                    mBinding.pageRefreshLayout.showContent()
                }
                mBinding.pageRefreshLayout.finishRefresh()
            } else {
                mBinding.pageRefreshLayout.addData(
                    it?.records
                )
                mBinding.pageRefreshLayout.finishLoadMore()
            }
            logE("it.total" + it?.total)
            if (mBinding.recyclerView.bindingAdapter.models?.size == it?.total) {
                mBinding.pageRefreshLayout.finishLoadMoreWithNoMoreData()
            } else {
                mBinding.pageRefreshLayout.setNoMoreData(false)
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.linear().setup {
            addType<InvitePeopleListModel.Record> { R.layout.rv_item_invited_friends }

        }.models = mutableListOf()
    }


}