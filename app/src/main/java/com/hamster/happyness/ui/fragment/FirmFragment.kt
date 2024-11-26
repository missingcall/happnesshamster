package com.hamster.happyness.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.hamster.happyness.R

import com.hamster.happyness.databinding.FragmentFirmBinding
import com.hamster.happyness.ui.activity.MainActivity
import com.hamster.happyness.viewmodel.HomeViewModel
import com.hamster.happyness.viewmodel.MainViewModel
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.mine.viewmodel.WalletViewModel

@Router(path = RouterPath.PATH_FIRM)
class FirmFragment : BaseFragment(R.layout.fragment_firm) {

    private val mBinding by dataBinding<FragmentFirmBinding>()
    private val mMineViewModel by activityViewModels<MineViewModel>()
    private val mWalletViewModel by activityViewModels<WalletViewModel>()
    private val mMainViewModel by activityViewModels<MainViewModel>()


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.setMarginStatusBar()
        mBinding.mvm = mMineViewModel
        mBinding.wvm = mWalletViewModel
        mBinding.lifecycleOwner = activity

        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int) = when (position) {
                0 -> FirmOrchardBankFragment.newInstance(Constants.HamsterFirmCommodityTypes.BASIC_HAMSTER) //001-基础仓鼠&仓鼠庄园
                else -> FirmOrchardBankFragment.newInstance(Constants.HamsterFirmCommodityTypes.BANK) //003-仓鼠银行
            }
        }

        ViewPager2Delegate.install(mBinding.viewPager, mBinding.dslTabLayout)

        mBinding.ivAvatar.safeClick {
            if (activity is MainActivity) {
                (activity as MainActivity).changeDrawLayout()
            }
        }

        mBinding.clWareHouse.safeClick {
            jump(RouterPath.PATH_MY_WAREHOUSE)
        }

    }

    override fun createDataObserver() {
        super.createDataObserver()

        //商行待领取红点
        FlowBus.observerEvent<Event.PineConeCollectionEvent>(this) {
            mMainViewModel.findUserPropWaitReceiveList {
                mBinding.vRedShape.visibility = if (it == true) View.VISIBLE else View.GONE
            }
        }
    }


}