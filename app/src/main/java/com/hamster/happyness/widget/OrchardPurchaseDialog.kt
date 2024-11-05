package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import androidx.fragment.app.activityViewModels
import com.hamster.happyness.databinding.DialogOrchardPurchaseBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.util.customToast
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.mine.viewmodel.WalletViewModel

/**
 * 底部弹窗-果园购买
 */
class OrchardPurchaseDialog : BaseDialogFragment<DialogOrchardPurchaseBinding>(DialogOrchardPurchaseBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()

    companion object {
        fun newInstance() = OrchardPurchaseDialog().apply {

        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_orchard_purchase
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.m = mViewModel
        val item = mViewModel.queryMarketItem.value
        mBinding.tvConditionsDetail.text = "消耗" + when (item?.payType) {
            "001" -> "松果"
            "002" -> "松子"
            "003" -> "三方"
            else -> "勋章"
        } + item?.coinPrice + "/" +mViewModel.walletModel.value?.diamond

        mBinding.ivConditionsAvailable.visibility = if (item?.coinPrice!! <= mViewModel.walletModel.value?.diamond!!) View.VISIBLE else View.GONE

        mBinding.ibBack.safeClick {
            dismiss()
        }

        mBinding.btnCancel.safeClick {
            dismiss()
        }

        mBinding.btnConfirm.safeClick {
            mViewModel.buy(item.commodityInfoId, item.coinPrice, 1, onSuccess = {
                //购买成功
                customToast("购买成功")
                dismiss()
                FlowBus.post(Event.OrchardPurchaseSuccessful)
            })

        }


    }

    override fun observerData() {
        super.observerData()


    }
}