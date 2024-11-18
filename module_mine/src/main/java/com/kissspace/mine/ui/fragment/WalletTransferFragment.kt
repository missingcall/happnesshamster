package com.kissspace.mine.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.callback.ActivityResult.TransferAccount
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.*
import com.kissspace.common.router.ParseIntent
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWalletTransferBinding
import com.kissspace.util.*
import com.kissspace.util.activityresult.registerForStartActivityResult

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description:转赠页面 017松子转赠 018钻石转赠
 *
 */
class WalletTransferFragment : BaseFragment(R.layout.mine_fragment_wallet_transfer) {
    private val mBinding by viewBinding<MineFragmentWalletTransferBinding>()
    private val mViewModel by activityViewModels<WalletViewModel>()
    private lateinit var type: String
    private var mRecipientInfoBean: UserInfoBean? = null
    private var amountType: String = "松子"//货币类型

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
        mBinding.lifecycleOwner = activity


        mViewModel.walletModel.observe(this) {
            when (type) {
                Constants.HamsterTransferAccountsType.PINE_NUTS -> pineNutsGift()
                Constants.HamsterTransferAccountsType.DIAMONDS -> diamondGift()
                else -> pineNutsGift()
            }
        }

        mViewModel.expectedTransferAccountsModel.observe(this) {
            when (type) {
                Constants.HamsterTransferAccountsType.PINE_NUTS -> pineNutsGift()
                Constants.HamsterTransferAccountsType.DIAMONDS -> diamondGift()
                else -> pineNutsGift()
            }
        }

        //失去焦点的时候根据uid查询用户昵称
        mBinding.etRecipientUID.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                mViewModel.loadUserByDisplayId(mBinding.etRecipientUID.text.toString(), onSuccess = {
                    mRecipientInfoBean = it
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


        mBinding.etGiftQuantity.addAfterTextChanged {
            if (it.isNotEmptyBlank()) {
                mViewModel.transferUserNumber.value = it.trimString().toDouble()
                mViewModel.transferUserId.value = it.trimString()
                updateUI(it.toString().toDouble())
            } else {
                mViewModel.transferUserNumber.value = 0.0
                mViewModel.transferUserId.value = ""
            }

        }

        mBinding.etRecipientUID.addAfterTextChanged {
            if (it.isNotEmptyBlank()) {
                mViewModel.transferUserNumber.value = it.trimString().toDouble()
                mViewModel.transferUserId.value = it.trimString()
                updateUI(it.toString().toDouble())
            } else {
                mViewModel.transferUserNumber.value = 0.0
                mViewModel.transferUserId.value = ""
            }

        }


        mBinding.btnConfirm.safeClick {
            if (mViewModel.transferUserNumber.value.isNullOrZero() && mViewModel.walletModel.value?.accountBalance.isNullOrZero()) {
                com.kissspace.common.util.customToast("赠送数量不能为空", true)
                return@safeClick
            }

            when (type) {
                Constants.HamsterTransferAccountsType.PINE_NUTS -> {
                    if (mViewModel.transferUserNumber.value!! > mViewModel.walletModel.value?.accountBalance!!) {
                        com.kissspace.common.util.customToast("松子数量不足")
                    } else {
                        confirmGive()
                    }
                }

                else -> {
                    if (mBinding.etGiftQuantity.text.toString().toDouble() > mViewModel.walletModel.value?.coin!!) {
                        com.kissspace.common.util.customToast("钻石数量不足")
                    } else {
                        confirmGive()
                    }
                }
            }
        }
    }

    private fun confirmGive() {
        mViewModel.transferUserId.value = mBinding.etRecipientUID.text.trimString()
        CommonConfirmDialog(
            requireContext(),
            "您确定要赠送${mViewModel.transferUserNumber.value}个${amountType}" + "给 " + mRecipientInfoBean?.nickname + "（UID：${mRecipientInfoBean?.displayId}）吗？",
            ""
        ) {
            if (this) {
                logE("转账数目" + mViewModel.transferUserNumber.value.orZero())
                jump(
                    RouterPath.PATH_SEND_SMS_CODE,
                    "type" to TransferAccount,
                    activity = activity,
                    resultLauncher = startActivityLauncher
                )
            }
        }.show()
    }

    private fun pineNutsGift() {
        amountType = "松子"
        mBinding.tvAssets.text = SpanUtils()
            .append("我的松子")
            .appendImage(R.mipmap.icon_pine_nut_small)
            .append(mViewModel.walletModel.value?.accountBalance.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvTransferorFee.text = "转赠人损耗（${mViewModel.expectedTransferAccountsModel.value?.lossRate}%）"
        mBinding.tvTransferorFeeNum.text = "损耗0.00松子"
        mBinding.tvExpectedToObtainNum.text = "共计0.00松子"

        mBinding.tvReminder.text = getString(R.string.mine_transfer_tips_pine_nut)
    }

    private fun diamondGift() {
        amountType = "钻石"
        mBinding.tvAssets.text = SpanUtils()
            .append("我的钻石")
            .appendImage(R.mipmap.icon_diamond_small)
            .append(mViewModel.walletModel.value?.coin.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

        mBinding.tvTransferorFee.text = "转赠人损耗（${mViewModel.expectedTransferAccountsModel.value?.lossRate}%）"
        mBinding.tvTransferorFeeNum.text = "损耗0.00钻石"
        mBinding.tvExpectedToObtainNum.text = "共计0.00钻石"

        mBinding.tvReminder.text = getString(R.string.mine_transfer_tips_diamonds)
    }

    private val startActivityLauncher = registerForStartActivityResult { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            mViewModel.transferAccounts(
//                result.data?.getStringExtra("smsCode"),
                mViewModel.transferUserNumber.value.orZero(),
                mViewModel.transferUserId.value.orEmpty(),
                type
            ) { _ ->
                com.kissspace.common.util.customToast("赠送成功")

                FlowBus.post(Event.RefreshCoin)
            }
        }
    }

    private fun updateUI(sourceAccount: Double) {
        if (sourceAccount < 1 || mBinding.etGiftQuantity.text.isNullOrBlank() || mBinding.etRecipientUID.text.isNullOrBlank()) {
            return
        }

        mBinding.tvTransferorFee.text = "转赠人损耗（${mViewModel.expectedTransferAccountsModel.value?.lossRate}%）"
        mBinding.tvTransferorFeeNum.text =
            "损耗${mBinding.etGiftQuantity.text.toString().toDouble() * mViewModel.expectedTransferAccountsModel.value?.lossRate!! / 100}${amountType}"
        mBinding.tvExpectedToObtainNum.text = "共计${
            mBinding.etGiftQuantity.text.toString().toDouble() + (mBinding.etGiftQuantity.text.toString()
                .toDouble() * mViewModel.expectedTransferAccountsModel.value?.lossRate!! / 100)
        }${amountType}"
    }

}