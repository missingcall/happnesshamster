package com.kissspace.mine.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.callback.ActivityResult.TransferAccount
import com.kissspace.common.config.CommonApi
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setDrawable
import com.kissspace.common.model.*
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWalletConversionBinding
import com.kissspace.module_mine.databinding.MineFragmentWalletTransferBinding
import com.kissspace.module_mine.databinding.MineFragmentWarehouseBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.*
import com.kissspace.util.activityresult.registerForStartActivityResult
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description:转赠页面 002松子转赠 003钻石转赠
 *
 */
class WalletTransferFragment : BaseFragment(R.layout.mine_fragment_wallet_transfer) {
    private val mBinding by viewBinding<MineFragmentWalletTransferBinding>()
    private val mViewModel by activityViewModels<WalletViewModel>()
    private lateinit var type: String

    companion object {
        fun newInstance(type: String) = WalletTransferFragment().apply {
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
                "002" -> pineNutsGift()
                else -> diamondGift()
            }
        }

        //失去焦点的时候根据uid查询用户昵称
        mBinding.etRecipientUID.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                mViewModel.loadUserByDisplayId(mBinding.etRecipientUID.text.toString(), onSuccess = {
                    mBinding.tvRecipientNickname.text = "昵称：" + it?.nickname
                    mBinding.tvRecipientNickname.setTextColor(ColorUtils.getColor(com.kissspace.module_common.R.color.color_FDC120))
                },
                    onError = {
                        mBinding.tvRecipientNickname.text = it?.errorMsg
                        mBinding.tvRecipientNickname.setTextColor(ColorUtils.getColor(com.kissspace.module_common.R.color.color_FE5F55))
                    }
                )
            }
        }

        mBinding.etGiftQuantity.onAfterTextChanged = {
            if (it.isNotEmptyBlank()) {
                mViewModel.transferUserNumber.value = it.trimString().toDouble()
            } else {
                mViewModel.transferUserNumber.value = 0.0
            }
        }

        mBinding.etRecipientUID.addAfterTextChanged { s ->
            mViewModel.transferUserId.value = s.toString()
        }


        mBinding.btnConfirm.safeClick {

            when (type) {
                Constants.HamsterPayType.PINE_NUT -> {
                    if (mViewModel.transferUserNumber.value!! > mViewModel.walletModel.value?.accountBalance!!) {
                        com.kissspace.common.util.customToast("松子数量不足")
                    } else {
                        logE("转账数目" + mViewModel.transferUserNumber.value.orZero())
                        jump(
                            RouterPath.PATH_SEND_SMS_CODE,
                            "type" to TransferAccount,
                            activity = activity,
                            resultLauncher = startActivityLauncher
                        )
                    }
                }

                else -> {
                    if (mBinding.etGiftQuantity.text.toString().toDouble() < mViewModel.walletModel.value?.coin!!) {
                        com.kissspace.common.util.customToast("钻石数量不足")
                    } else {

                    }
                }
            }
        }
    }

    private fun pineNutsGift() {
        mBinding.tvAssets.text = SpanUtils()
            .append("我的松子")
            .appendImage(R.mipmap.icon_pine_nut_small)
            .append(mViewModel.walletModel.value?.accountBalance.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvTransferorFee.text = "转赠人损耗（2%）"
        mBinding.tvTransferorFeeNum.hint = "损耗0.00松子"
        mBinding.tvExpectedToObtainNum.hint = "共计0.00松子"

        mBinding.tvReminder.text = getString(R.string.mine_transfer_tips_pine_nut)
    }

    private fun diamondGift() {
        mBinding.tvAssets.text = SpanUtils()
            .append("我的钻石")
            .appendImage(R.mipmap.icon_diamond_small)
            .append(mViewModel.walletModel.value?.coin.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvTransferorFee.text = "转赠人损耗（2%）"
        mBinding.tvTransferorFeeNum.hint = "损耗0.00钻石"
        mBinding.tvExpectedToObtainNum.hint = "共计0.00钻石"

        mBinding.tvReminder.text = getString(R.string.mine_transfer_tips_diamonds)
    }

    private val startActivityLauncher = registerForStartActivityResult { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            mViewModel.transferPineNuts(
                mViewModel.transferUserNumber.value.orZero(),
                mViewModel.transferUserId.value.orEmpty()
            ) { _ ->
                com.kissspace.common.util.customToast("赠送成功")
            }
        }
    }
}