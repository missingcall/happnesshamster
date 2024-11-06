package com.hamster.happyness.ui.fragment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.hamster.happyness.R
import com.hamster.happyness.databinding.ActivityFirmBinding
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.mine.ui.fragment.WareHouseFragment
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.util.immersiveStatusBar

@Router(path = RouterPath.PATH_FIRM)
class FirmFragment : BaseFragment(R.layout.activity_firm) {

    private val mBinding by dataBinding<ActivityFirmBinding>()
    private val mMineViewModel by activityViewModels<MineViewModel>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.setMarginStatusBar()
        mBinding.mvm = mMineViewModel

        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int) = when(position) {
                0 -> FirmOrchardBankFragment.newInstance(Constants.FirmCommodityTypes.BASIC_HAMSTER)
                else -> FirmOrchardBankFragment.newInstance(Constants.FirmCommodityTypes.HAMSTER_BANK)
            }
        }

        ViewPager2Delegate.install(mBinding.viewPager, mBinding.dslTabLayout)

        mBinding.clWareHouse.safeClick {
            jump(RouterPath.PATH_MY_WAREHOUSE)
        }

    }

}