package com.kissspace.mine.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.router.RouterPath
import com.kissspace.mine.ui.fragment.GoodsListFragment
import com.kissspace.mine.ui.fragment.WalletCoinGainFragment
import com.kissspace.mine.ui.fragment.WareHouseFragment
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMyWarehouseBinding
import com.kissspace.util.immersiveStatusBar

@Router(path = RouterPath.PATH_MY_WAREHOUSE)
class MyWarehouseActivity : BaseActivity(R.layout.mine_activity_my_warehouse) {

    private val mBinding by dataBinding<MineActivityMyWarehouseBinding>()
    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.titleBar.setMarginStatusBar()
        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                finish()
            }
        })

        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int) = when(position) {
                0 -> WareHouseFragment.newInstance("001")
                else -> WareHouseFragment.newInstance("003")
            }
        }

        ViewPager2Delegate.install(mBinding.viewPager, mBinding.dslTabLayout)


    }

}