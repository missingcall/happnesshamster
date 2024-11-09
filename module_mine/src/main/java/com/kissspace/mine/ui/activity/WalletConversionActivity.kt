package com.kissspace.mine.ui.activity

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
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


        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> pineConesToPineNuts()
                    1 -> pineNutsToPineCones()
                    else -> pineNutsToDiamonds()
                }
            }
        })

        mBinding.viewPager.currentItem = type.toInt() - 1

        //获取钱包
        getMoney()
        initData()

    }

    private fun getMoney() {
        mViewModel.getMyMoneyBag {
            it?.let {
                mViewModel.walletModel.value = it
            }
        }
    }

    private fun initData() {
        //TODO 获取兑换比例 放到WalletViewModel中 下级fragment直接取

    }


    private fun pineConesToPineNuts() {
        mBinding.tvTransformLeft.text = "松果"
        mBinding.tvTransformLeft.setDrawable(R.mipmap.icon_pine_cone, Gravity.START)

        mBinding.tvTransformRight.text = "松子"
        mBinding.tvTransformRight.setDrawable(R.mipmap.icon_pine_nut, Gravity.START)
    }

    private fun pineNutsToPineCones() {
        mBinding.tvTransformLeft.text = "松子"
        mBinding.tvTransformLeft.setDrawable(R.mipmap.icon_pine_nut, Gravity.START)

        mBinding.tvTransformRight.text = "松果"
        mBinding.tvTransformRight.setDrawable(R.mipmap.icon_pine_cone, Gravity.START)
    }

    private fun pineNutsToDiamonds() {
        mBinding.tvTransformLeft.text = "松子"
        mBinding.tvTransformLeft.setDrawable(R.mipmap.icon_pine_nut, Gravity.START)

        mBinding.tvTransformRight.text = "钻石"
        mBinding.tvTransformRight.setDrawable(R.mipmap.icon_diamond, Gravity.START)
    }


    override fun createDataObserver() {
        super.createDataObserver()


    }


    override fun onResume() {
        super.onResume()

    }


}