package com.kissspace.mine.ui.activity

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.setDrawable
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.parseIntent
import com.kissspace.mine.ui.fragment.WalletConversionFragment
import com.kissspace.mine.ui.fragment.WareHouseFragment
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityWalletConversionBinding

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/17
 * @Description: 手机验证码登录页面
 *
 */
@Router(path = RouterPath.PATH_WALLET_CONVERSION)
class WalletConversionActivity : BaseActivity(R.layout.mine_activity_wallet_conversion) {
    private val type by parseIntent<String>()
    private val mBinding by dataBinding<MineActivityWalletConversionBinding>()
    private val mViewModel by viewModels<WalletViewModel>()


    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this

        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int) = when (position) {
                0 -> WalletConversionFragment.newInstance("001")
                1 -> WalletConversionFragment.newInstance("002")
                else -> WalletConversionFragment.newInstance("003")
            }
        }

        ViewPager2Delegate.install(mBinding.viewPager, mBinding.dslTabLayout)
        mBinding.viewPager.currentItem = type.toInt() - 1


        when (type) {
            //松果转松子
            "001" -> {
                mBinding.tvTransformLeft.text = "松果"
                mBinding.tvTransformLeft.setDrawable(R.mipmap.icon_pine_cone, Gravity.START)

                mBinding.tvTransformRight.text = "松子"
                mBinding.tvTransformRight.setDrawable(R.mipmap.icon_pine_nut, Gravity.START)

            }

            //松子转松果
            "002" -> {
                mBinding.tvTransformLeft.text = "松子"
                mBinding.tvTransformLeft.setDrawable(R.mipmap.icon_pine_nut, Gravity.START)

                mBinding.tvTransformRight.text = "松果"
                mBinding.tvTransformRight.setDrawable(R.mipmap.icon_pine_cone, Gravity.START)
            }

            //松子转钻石
            else -> {
                mBinding.tvTransformLeft.text = "松子"
                mBinding.tvTransformLeft.setDrawable(R.mipmap.icon_pine_nut, Gravity.START)

                mBinding.tvTransformRight.text = "钻石"
                mBinding.tvTransformRight.setDrawable(R.mipmap.icon_diamond, Gravity.START)
            }
        }
    }


    override fun createDataObserver() {
        super.createDataObserver()


    }


    override fun onResume() {
        super.onResume()

    }


}