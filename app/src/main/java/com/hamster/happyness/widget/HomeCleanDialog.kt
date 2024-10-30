package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.SpanUtils
import com.hamster.happyness.databinding.DialogHomeCapacityDescriptionBinding
import com.hamster.happyness.databinding.DialogHomeCleanBinding
import com.hamster.happyness.databinding.DialogHomeFeedBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.ui.BosonFriendDialog
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.login.ui.fragment.LoginPswFragment
import com.kissspace.login.ui.fragment.LoginSmsFragment
import com.kissspace.mine.viewmodel.UserProfileViewModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.mine.widget.FamilyNoticeDialog
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonDialogBosomFriendBinding
import com.kissspace.module_login.databinding.LoginDialogLoginBinding

/**
 * 底部弹窗-产能说明
 */
class HomeCleanDialog : BaseDialogFragment<DialogHomeCleanBinding>(DialogHomeCleanBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()

    //数据绑定有问题,dialog单独建
    //type 1:喂养 2.清洁 3.重生
//    private var type: String? = null

    companion object {
        fun newInstance(/*type: String*/) = HomeCleanDialog().apply {
//            arguments = bundleOf("type" to type)
        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_home_clean
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
    /*    arguments?.let {
            type = it.getString("type")
        }*/

        mBinding.vm = mViewModel
        mBinding.ibBack.safeClick {
            dismiss()
        }

        mBinding.btnConfirmLeft.safeClick {
            mViewModel.improveCleanliness(onSuccess = {
                //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                FlowBus.post(Event.FeedingOrCleaningEvent)
            })

        }

        mBinding.btnConfirmRight.safeClick {
            mViewModel.improveCleanliness(onSuccess = {
                //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                FlowBus.post(Event.FeedingOrCleaningEvent)
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