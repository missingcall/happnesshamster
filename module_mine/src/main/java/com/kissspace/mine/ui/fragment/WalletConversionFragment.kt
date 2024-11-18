package com.kissspace.mine.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.SpanUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWalletConversionBinding
import com.kissspace.util.*

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
    private var sourceAmountType: String = "松果"//源货币类型
    private var targetAmountType: String = "松子"//目标货币类型

    companion object {
        fun newInstance(type: String) = WalletConversionFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")!!
    }

    override fun onResume() {
        super.onResume()


    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = activity

        mViewModel.walletModel.observe(this) {
            when (type) {
                Constants.HamsterConversionType.PINE_CONES_TO_PINE_NUTS -> pineConesToPineNuts()
                Constants.HamsterConversionType.PINE_NUTS_TO_PINE_CONES -> pineNutsToPineCones()
                Constants.HamsterConversionType.PINE_NUTS_TO_DIAMONDS -> pineNutsToDiamonds()
                else -> pineConesToPineNuts()
            }

            updateUI(true, 1.0)
        }

        mViewModel.expectedTransferConversionModel.observe(this) {
            when (type) {
                Constants.HamsterConversionType.PINE_CONES_TO_PINE_NUTS -> pineConesToPineNuts()
                Constants.HamsterConversionType.PINE_NUTS_TO_PINE_CONES -> pineNutsToPineCones()
                Constants.HamsterConversionType.PINE_NUTS_TO_DIAMONDS -> pineNutsToDiamonds()
                else -> pineConesToPineNuts()
            }
            updateWallet()
            updateUI(true, 1.0)

        }

        mBinding.etConvertUp.addAfterTextChanged {
            if (it.toString().isNotBlank()) {
                updateUI(false, it.toString().toDouble())
            } else {

            }
        }

        mBinding.btnConfirm.safeClick {
            if (mBinding.etConvertUp.text.isNotBlank()) {
                mViewModel.expectedTransferConversion(mBinding.etConvertUp.text.toString().toDouble(), type) {
                    mViewModel.transferConversion(mBinding.etConvertUp.text.toString().toDouble(), type) {
                        if (it) {
                            com.kissspace.common.util.customToast("交易成功")
                            //刷新钱包
                            FlowBus.post(Event.RefreshCoin)
                            mBinding.etConvertDown.setText("")
                            mBinding.etConvertUp.setText("")
                        } else {
                            com.kissspace.common.util.customToast("交易失败")
                        }
                    }
                }
            } else {
                com.kissspace.common.util.customToast("转换数量不能为空")
            }

        }


    }

    private fun updateWallet() {


    }

    private fun updateUI(isFirst: Boolean, sourceAccount: Double) {
        if (sourceAccount < 1) {
            return
        }
        mBinding.tvConversionUp.text = SpanUtils()
            .append("我的${sourceAmountType}")
            .appendImage(
                when (type) {
                    Constants.HamsterConversionType.PINE_CONES_TO_PINE_NUTS -> R.mipmap.icon_pine_cone_small
                    Constants.HamsterConversionType.PINE_NUTS_TO_PINE_CONES -> R.mipmap.icon_pine_nut_small
                    Constants.HamsterConversionType.PINE_NUTS_TO_DIAMONDS -> R.mipmap.icon_pine_nut_small

                    else -> {
                        R.mipmap.icon_pine_cone_small
                    }
                }
            )
            .append(
                when (type) {
                    Constants.HamsterConversionType.PINE_CONES_TO_PINE_NUTS -> mViewModel.walletModel.value?.diamond.toString()
                    Constants.HamsterConversionType.PINE_NUTS_TO_PINE_CONES -> mViewModel.walletModel.value?.accountBalance.toString()
                    Constants.HamsterConversionType.PINE_NUTS_TO_DIAMONDS -> mViewModel.walletModel.value?.accountBalance.toString()
                    else -> mViewModel.walletModel.value?.diamond.toString()

                }
            )
            .setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvConversionDown.text = SpanUtils()
            .append("我的${targetAmountType}")
            .appendImage(
                when (type) {
                    Constants.HamsterConversionType.PINE_CONES_TO_PINE_NUTS -> R.mipmap.icon_pine_nut_small
                    Constants.HamsterConversionType.PINE_NUTS_TO_PINE_CONES -> R.mipmap.icon_pine_nut_small
                    Constants.HamsterConversionType.PINE_NUTS_TO_DIAMONDS -> R.mipmap.icon_diamond_small

                    else -> {
                        R.mipmap.icon_pine_nut_small
                    }
                }
            )
            .append(
                when (type) {
                    Constants.HamsterConversionType.PINE_CONES_TO_PINE_NUTS -> mViewModel.walletModel.value?.accountBalance.toString()
                    Constants.HamsterConversionType.PINE_NUTS_TO_PINE_CONES -> mViewModel.walletModel.value?.diamond.toString()
                    Constants.HamsterConversionType.PINE_NUTS_TO_DIAMONDS -> mViewModel.walletModel.value?.coin.toString()
                    else -> mViewModel.walletModel.value?.accountBalance.toString()

                }
            )
            .setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        if (isFirst) {
            mBinding.etConvertDown.setText("")
        } else {
            mBinding.etConvertDown.setText("${mViewModel.expectedTransferConversionModel.value?.targetAmount?.times(sourceAccount)}")
        }

        mBinding.tvConvertUp.text = sourceAmountType
        mBinding.etConvertUp.hint = "请输入${sourceAmountType}"
        mBinding.tvConvertDown.text = targetAmountType
        mBinding.etConvertDown.hint = "输入${sourceAmountType}查看转换${targetAmountType}数"

//        mBinding.etConvertDown.setText("${mViewModel.expectedTransferConversionModel.value?.targetAmount?.times(sourceAccount)}")
        //转换比例num
        mBinding.tvConversionRatioNum.text =
            "${mViewModel.expectedTransferConversionModel.value?.configSourceAmount?.times(sourceAccount)}${sourceAmountType}=${
                mViewModel.expectedTransferConversionModel.value?.configTargetAmount?.times(
                    sourceAccount
                )
            }${targetAmountType}"

        //转换手续费
        mBinding.tvConversionFee.text = "转换手续费（${mViewModel.expectedTransferConversionModel.value?.configHandingFee}%）"
        //转换手续费num
        mBinding.tvConversionFeeNum.text = "${mViewModel.expectedTransferConversionModel.value?.handingFee?.times(sourceAccount)}${targetAmountType}"
        //预计获得num
        mBinding.tvExpectedToObtainNum.text =
            "${mViewModel.expectedTransferConversionModel.value?.targetAmount?.times(sourceAccount)}${targetAmountType}"
    }

    private fun pineConesToPineNuts() {
        sourceAmountType = "松果"
        targetAmountType = "松子"

    }

    private fun pineNutsToPineCones() {
        sourceAmountType = "松子"
        targetAmountType = "松果"

    }

    private fun pineNutsToDiamonds() {
        sourceAmountType = "松子"
        targetAmountType = "钻石"

    }

}