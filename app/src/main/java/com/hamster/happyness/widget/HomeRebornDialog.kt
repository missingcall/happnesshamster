package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import com.hamster.happyness.databinding.DialogHomeRebornBinding
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.util.customToast
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.mine.viewmodel.WalletViewModel

/**
 * 底部弹窗-产能说明
 */
class HomeRebornDialog : BaseDialogFragment<DialogHomeRebornBinding>(DialogHomeRebornBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()

    //数据绑定有问题,dialog单独建
    //type 1:喂养 2.清洁 3.重生
//    private var type: String? = null

    companion object {
        fun newInstance(/*type: String*/) = HomeRebornDialog().apply {
//            arguments = bundleOf("type" to type)
        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_home_reborn
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        /*    arguments?.let {
                type = it.getString("type")
            }*/

        mBinding.vm = mViewModel
        mBinding.lifecycleOwner = activity

        mBinding.ibBack.safeClick {
            dismiss()
        }

        mBinding.btnConfirmLeft.safeClick {
            mViewModel.cultivateRevive(onSuccess = {
                if (it) {
                    reviveSuccess()
                }
            }, payType = Constants.HamsterPayType.PINE_NUT)
        }

        mBinding.btnConfirmRight.safeClick {
            mViewModel.cultivateRevive(onSuccess = {
                if (it) {
                    reviveSuccess()
                }
            }, payType = Constants.HamsterPayType.PINE_CONE)

        }
        mViewModel.queryRevivePanel {

        }

    }

    private fun reviveSuccess() {
        customToast("复活成功")
        //复活成功,刷新HomeFragment喂食度
        FlowBus.post(Event.HamsterReviveEvent)
        FlowBus.post(Event.RefreshCoin)
    }

    override fun observerData() {
        super.observerData()


    }
}