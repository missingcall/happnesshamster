package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hamster.happyness.databinding.DialogHomeSkinUnlockBinding
import com.hamster.happyness.viewmodel.HamsterViewModel
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.mine.viewmodel.WalletViewModel

/**
 * 底部弹窗-购买皮肤
 */
class HomeSkinUnlockDialog : BaseDialogFragment<DialogHomeSkinUnlockBinding>(DialogHomeSkinUnlockBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<HamsterViewModel>()
    private val mWalletViewModel by activityViewModels<WalletViewModel>()

    private var pageNum = 0
    private var skinId = 0
    private var diamond = 0.0


    companion object {
        fun newInstance(pageNum: Int, skinId: Int, diamond: Double?) = HomeSkinUnlockDialog().apply {
            arguments = bundleOf("pageNum" to pageNum, "skinId" to skinId , "diamond" to diamond)
        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_home_skin_unlock
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        arguments?.let {
            pageNum = it.getInt("pageNum", 0)
            skinId = it.getInt("skinId", 0)
            diamond = it.getDouble("diamond")
        }
        mBinding.m = mViewModel
        mBinding.wvm = mWalletViewModel
        mWalletViewModel.walletModel.value?.diamond = diamond
        mBinding.ibBack.safeClick {
            dismiss()
        }

        mBinding.btnConfirm.safeClick {
            mViewModel.unLockSkin(pageNum = pageNum, skinId = skinId, onSuccess = {
                dismiss()
                FlowBus.post(Event.UnLockSkinEvent)
            })
        }

        mBinding.btnCancel.safeClick {
            dismiss()
        }

    }

    override fun observerData() {
        super.observerData()


    }
}