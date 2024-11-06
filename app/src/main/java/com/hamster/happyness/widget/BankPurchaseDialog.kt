package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.hamster.happyness.databinding.DialogBankPurchaseBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.util.customToast
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.mine.viewmodel.WalletViewModel

/**
 * 底部弹窗-银行存折购买
 */
class BankPurchaseDialog : BaseDialogFragment<DialogBankPurchaseBinding>(DialogBankPurchaseBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()
    private var mTotalPrice = 0.0

    companion object {
        fun newInstance() = BankPurchaseDialog().apply {

        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_bank_purchase
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.m = mViewModel
        val item = mViewModel.queryMarketItem.value

        mBinding.nvCount.min = item?.coinPrice!!.toInt()
        mBinding.nvCount.setOnValueChangeListener {
            mTotalPrice = it * item?.coinPrice!!
        }

        //购买
        mBinding.tvConditionsDetail.text = "消耗" + when (item?.payType) {
            "001" -> "松果"
            "002" -> "松子"
            "003" -> "三方"
            else -> "勋章"
        } + item?.coinPrice + "/" + mViewModel.walletModel.value?.diamond

        mBinding.ivConditionsAvailable.visibility = if (item?.coinPrice!! <= mViewModel.walletModel.value?.diamond!!) View.VISIBLE else View.GONE

        mBinding.ibBack.safeClick {
            dismiss()
        }

        mBinding.btnCancel.safeClick {
            dismiss()
        }

        mBinding.btnConfirm.safeClick {
            CommonConfirmDialog(
                requireContext(),
                "您确定要消耗${mTotalPrice}个" + when (item.payType) {
                    "001" -> "松果"
                    "002" -> "松子"
                    "003" -> "三方"
                    else -> "勋章"
                } + "购买" + mBinding.nvCount.currentValue + "个${item.commodityName}吗？",
                ""
            ) {
                if (this) {
                    mViewModel.buy(item.commodityInfoId, item.coinPrice, 1, onSuccess = {
                        //购买成功
                        customToast("购买成功")
                        dismiss()
                        FlowBus.post(Event.OrchardPurchaseEvent)
                    })
                }
            }.show()

        }

    }

    override fun observerData() {
        super.observerData()


    }
}