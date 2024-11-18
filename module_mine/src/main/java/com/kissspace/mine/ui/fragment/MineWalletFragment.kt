package com.kissspace.mine.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.widget.MarqueeFactory
import com.kissspace.common.widget.SimpleNoticeMF
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWalletBinding

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 钱包滑动页 松果/松子/钻石
 *
 */
class MineWalletFragment : BaseFragment(R.layout.mine_fragment_wallet) {
    private val mBinding by viewBinding<MineFragmentWalletBinding>()
    private val mViewModel by activityViewModels<WalletViewModel>()
    private lateinit var mType: String
    private val list = mutableListOf<CharSequence>()
    private var marqueeFactory: MarqueeFactory<TextView, CharSequence>? = null
//    private val


    companion object {
        fun newInstance(type: String) = MineWalletFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mType = arguments?.getString("type")!!

    }

    override fun initView(savedInstanceState: Bundle?) {
        marqueeFactory = SimpleNoticeMF(context)
        mBinding.mtvTransfer.setMarqueeFactory(marqueeFactory)

        when (mType) {
            Constants.HamsterWalletType.PINE_CONE.type -> {
                mBinding.iconNuts.setImageResource(R.mipmap.icon_pine_cone)
                mBinding.btnRecharge.visibility = View.GONE
                mBinding.btnTransfer.visibility = View.GONE

            }
            Constants.HamsterWalletType.PINE_NUT.type -> {
                mBinding.iconNuts.setImageResource(R.mipmap.icon_pine_nut)
                mBinding.btnRecharge.visibility = View.GONE
            }
            else -> {
                mBinding.iconNuts.setImageResource(R.mipmap.icon_diamond)
                mBinding.btnRecharge.visibility = View.VISIBLE
            }
        }

        mBinding.btnRecharge.safeClick {
            jump(RouterPath.PATH_USER_WALLET_GOLD_RECHARGE)
        }

        mBinding.btnTransformation.safeClick {
            jump(RouterPath.PATH_WALLET_CONVERSION, "type" to mType)
        }

        mBinding.btnTransfer.safeClick {
            jump(RouterPath.PATH_WALLET_TRANSFER, "type" to mType)
        }

        mBinding.iconNutsNum.text = when (mType) {
            Constants.HamsterWalletType.PINE_CONE.type -> mViewModel.walletModel.value?.diamond.toString()
            Constants.HamsterWalletType.PINE_NUT.type -> mViewModel.walletModel.value?.accountBalance.toString()
            Constants.HamsterWalletType.DIAMONDS.type -> mViewModel.walletModel.value?.coin.toString()
            else -> mViewModel.walletModel.value?.coin.toString()
        }

        initData()

        //监听钱包数值变化
        mViewModel.walletModel.observe(this) {
            mBinding.iconNutsNum.text = when (mType) {
                Constants.HamsterWalletType.PINE_CONE.type -> mViewModel.walletModel.value?.diamond.toString()
                Constants.HamsterWalletType.PINE_NUT.type -> mViewModel.walletModel.value?.accountBalance.toString()
                Constants.HamsterWalletType.DIAMONDS.type -> mViewModel.walletModel.value?.coin.toString()
                else -> mViewModel.walletModel.value?.coin.toString()
            }
        }
    }

    private fun initData() {

        mViewModel.queryNumChangeRecord(
            when (mType) {
                Constants.HamsterWalletType.PINE_CONE.type -> "002"
                Constants.HamsterWalletType.PINE_NUT.type -> "004"
                Constants.HamsterWalletType.DIAMONDS.type -> "001"
                else -> "002"
            }, null, null, null, 1, 10
        ) {
            //重置跑马灯状态
            list.clear()
            if (mBinding.mtvTransfer.isFlipping) {
                mBinding.mtvTransfer.stopFlipping()
            }

            list.add(
                SpanUtils().appendImage(R.mipmap.icon_hamster_clock).append(when(mType){
                    Constants.HamsterWalletType.PINE_CONE.type -> "松果转入/转出记录"
                    Constants.HamsterWalletType.PINE_NUT.type -> "松子转入/转出记录"
                    Constants.HamsterWalletType.DIAMONDS.type -> "钻石转入/转出记录"
                    else -> "松果转入/转出记录"
                }).create()
            )
            for (record in it?.list!!) {
                list.add(record.amount)
            }

            marqueeFactory?.setData(list)
            mBinding.mtvTransfer.startFlipping()

            marqueeFactory?.setOnItemClickListener { _, _ ->
                //                ToastUtils.showShort(holder.data)
                //跳转转入转出记录页面
                jump(RouterPath.PATH_TRANSFER_RECORDS , "currency" to mType)
            }
        }


    }

    override fun onPause() {
        super.onPause()
//        mBinding.mtvTransfer.stopFlipping()
    }

    override fun onResume() {
        super.onResume()
        if (!mBinding.mtvTransfer.isFlipping) {
            mBinding.mtvTransfer.startFlipping()
        }
    }

    override fun createDataObserver() {
        super.createDataObserver()


    }


}