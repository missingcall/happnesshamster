package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.hamster.happyness.databinding.DialogHomeCapacityDescriptionBinding
import com.hamster.happyness.databinding.DialogHomeDevelopmentDescriptionBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.ui.BosonFriendDialog
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.login.ui.fragment.LoginPswFragment
import com.kissspace.login.ui.fragment.LoginSmsFragment
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonDialogBosomFriendBinding
import com.kissspace.module_login.databinding.LoginDialogLoginBinding

/**
 * 底部弹窗-产能说明
 */
class DevelopmentDescriptionDialog : BaseDialogFragment<DialogHomeDevelopmentDescriptionBinding>(DialogHomeDevelopmentDescriptionBinding::inflate, Gravity.BOTTOM) {
    companion object {
        fun newInstance(): DevelopmentDescriptionDialog {
            return DevelopmentDescriptionDialog()
        }
    }

    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_home_development_description
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.ibBack.safeClick {
            dismiss()
        }
    }
}