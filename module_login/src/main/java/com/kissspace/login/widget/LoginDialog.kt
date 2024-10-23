package com.kissspace.login.widget

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.ui.BosonFriendDialog
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.login.ui.fragment.LoginPswFragment
import com.kissspace.login.ui.fragment.LoginSmsFragment
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonDialogBosomFriendBinding
import com.kissspace.module_login.databinding.LoginDialogLoginBinding

class LoginDialog : BaseDialogFragment<LoginDialogLoginBinding>(LoginDialogLoginBinding::inflate, Gravity.BOTTOM) {
    companion object {
        fun newInstance(): LoginDialog {
            return LoginDialog()
        }
    }

    override fun getLayoutId(): Int {
        return com.kissspace.module_login.R.layout.login_dialog_login
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@LoginDialog) {
                override fun getItemCount(): Int = 2
                override fun createFragment(position: Int): Fragment =
                    if (position == 0) {
                        LoginPswFragment.newInstance()
                    } else {
                        LoginSmsFragment.newInstance()
                    }

                override fun getItemId(position: Int): Long = position.toLong()

            }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
        mBinding.viewPager.offscreenPageLimit = 2
    }
}