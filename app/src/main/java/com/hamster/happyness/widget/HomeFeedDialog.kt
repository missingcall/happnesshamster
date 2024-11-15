package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hamster.happyness.databinding.DialogHomeFeedBinding
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.wallet.CultivationPanelModel
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.mine.viewmodel.WalletViewModel
import okhttp3.internal.notifyAll

/**
 * 底部弹窗-产能说明
 */
class HomeFeedDialog : BaseDialogFragment<DialogHomeFeedBinding>(DialogHomeFeedBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()

    //数据绑定有问题,dialog单独建
    //type 1:喂养 2.清洁 3.重生
//    private var type: String? = null

    companion object {
        fun newInstance(/*type: String*/) = HomeFeedDialog().apply {
//            arguments = bundleOf("type" to type)
        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_home_feed
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
            mViewModel.improveSatiety(Constants.HamsterCultivatePayType.PROP,onSuccess = {
                //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                FlowBus.post(Event.HamsterFeedingOrCleaningEvent)
                FlowBus.post(Event.RefreshCoin)
            })

        }

        mBinding.btnConfirmRight.safeClick {
            mViewModel.improveSatiety(Constants.HamsterCultivatePayType.PINE_CONE,onSuccess = {
                //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                FlowBus.post(Event.HamsterFeedingOrCleaningEvent)
                FlowBus.post(Event.RefreshCoin)
            })

        }

        //TODO 需要配置消耗道具字段
        mViewModel.queryCultivationPanel {

        }

    }

    override fun observerData() {
        super.observerData()


    }
}