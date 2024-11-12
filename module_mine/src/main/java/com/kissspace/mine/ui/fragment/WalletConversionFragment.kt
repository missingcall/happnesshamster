package com.kissspace.mine.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.SpanUtils
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.CommonApi
import com.kissspace.common.ext.setDrawable
import com.kissspace.common.model.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWalletConversionBinding
import com.kissspace.module_mine.databinding.MineFragmentWarehouseBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.addAfterTextChanged
import com.kissspace.util.lifecycleOwner
import com.kissspace.util.toast
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 转换页面
 *
 */
class WalletConversionFragment : BaseFragment(R.layout.mine_fragment_wallet_conversion) {
    private val mBinding by viewBinding<MineFragmentWalletConversionBinding>()
    private val mViewModel by activityViewModels<WalletViewModel>()
    private lateinit var type: String

    companion object {
        fun newInstance(type: String) = WalletConversionFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")!!
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.m = mViewModel

        mViewModel.walletModel.observe(this) {
            when (type) {
                "001" -> pineConesToPineNuts()
                "002" -> pineNutsToPineCones()
                else -> pineNutsToDiamonds()
            }
        }

        mBinding.etConvertUp.addAfterTextChanged {
            mBinding.etConvertDown.setText(it.toString())
        }



    }

    private fun pineConesToPineNuts() {
        mBinding.tvConversionUp.text = SpanUtils()
            .append("我的松果")
            .appendImage(R.mipmap.icon_pine_cone_small)
            .append(mViewModel.walletModel.value?.diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvConversionDown.text = SpanUtils()
            .append("我的松子")
            .appendImage(R.mipmap.icon_pine_nut_small)
            .append(mViewModel.walletModel.value?.accountBalance.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvConvertUp.text = "松果"
        mBinding.etConvertUp.hint = "请输入松果"
        mBinding.tvConvertDown.text = "松子"
        mBinding.etConvertDown.hint = "输入松果查看转换松子数"
        //TODO 设置兑换比例/转换手续费/预计获得
//        mBinding.tvConversionRatioNum.text =
//        mBinding.tvConversionFee.text =
//        mBinding.tvConversionFeeNum.text =
//        mBinding.tvExpectedToObtainNum.text =
    }

    private fun pineNutsToPineCones() {
        mBinding.tvConversionUp.text = SpanUtils()
            .append("我的松子")
            .appendImage(R.mipmap.icon_pine_nut_small)
            .append(mViewModel.walletModel.value?.accountBalance.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvConversionDown.text = SpanUtils()
            .append("我的松果")
            .appendImage(R.mipmap.icon_pine_cone_small)
            .append(mViewModel.walletModel.value?.diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvConvertUp.text = "松子"
        mBinding.etConvertUp.hint = "请输入松子"
        mBinding.tvConvertDown.text = "松果"
        mBinding.etConvertDown.hint = "输入松子查看转换松子数"


    }

    private fun pineNutsToDiamonds() {
        mBinding.tvConversionUp.text = SpanUtils()
            .append("我的松子")
            .appendImage(R.mipmap.icon_pine_nut_small)
            .append(mViewModel.walletModel.value?.accountBalance.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvConversionDown.text = SpanUtils()
            .append("我的钻石")
            .appendImage(R.mipmap.icon_diamond_small)
            .append(mViewModel.walletModel.value?.coin.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvConvertUp.text = "松子"
        mBinding.etConvertUp.hint = "请输入松子"
        mBinding.tvConvertDown.text = "钻石"
        mBinding.etConvertDown.hint = "输入松子查看转换钻石数"
    }

}