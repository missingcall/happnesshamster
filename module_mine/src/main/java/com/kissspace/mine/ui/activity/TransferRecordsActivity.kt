package com.kissspace.mine.ui.activity

import android.os.Bundle
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
import com.kissspace.mine.ui.fragment.TransferRecordsFragment
import com.kissspace.mine.ui.fragment.WalletCoinGainFragment
import com.kissspace.mine.ui.fragment.WareHouseFragment
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMyWarehouseBinding
import com.kissspace.module_mine.databinding.MineActivityTransferRecordsBinding
import com.kissspace.util.immersiveStatusBar

@Router(path = RouterPath.PATH_TRANSFER_RECORDS)
class TransferRecordsActivity : BaseActivity(R.layout.mine_activity_transfer_records) {

    private val mBinding by dataBinding<MineActivityTransferRecordsBinding>()

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.titleBar.setMarginStatusBar()
        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                finish()
            }
        })

        mBinding.viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int) = when(position) {
                1 -> TransferRecordsFragment.newInstance("001")
                2 -> TransferRecordsFragment.newInstance("002")
                else -> TransferRecordsFragment.newInstance("000")
            }
        }

        ViewPager2Delegate.install(mBinding.viewpager, mBinding.dslTabLayout)


    }

}