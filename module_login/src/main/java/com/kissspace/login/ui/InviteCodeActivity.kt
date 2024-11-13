package com.kissspace.login.ui

import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ToastUtils
import com.didi.drouter.annotation.Router
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.parseIntent
import com.kissspace.login.viewmodel.LoginViewModel
import com.kissspace.module_login.R
import com.kissspace.module_login.databinding.LoginActivityInviteCodeBinding
import com.kissspace.network.result.collectData
import com.kissspace.util.toast

/**
 *@author: adan
 *@date: 2023/4/6
 *@Description:
 */
@Router(path = RouterPath.PATH_INPUT_INVITE_CODE)
class InviteCodeActivity :
    com.kissspace.common.base.BaseActivity(R.layout.login_activity_invite_code) {

    private val mBinding by viewBinding<LoginActivityInviteCodeBinding>()
    private val mViewModel by viewModels<LoginViewModel>()
    var phone by parseIntent<String>()
    var smsCode by parseIntent<String>()
    private var mChooseUserId = ""
    private var mTokenHead = ""
    private var mToken = ""

    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
        mBinding.tvSignUp.safeClick {
            createAccount()
        }

    }


    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.token, onSuccess = {
            hideLoading()
            mViewModel.loginIm(it, onSuccess = {
                finish()
            })
        }, onError = {
            hideLoading()
            ToastUtils.showLong("登录失败${it.errorMsg}")
        })

        collectData(mViewModel.createAccounts, onSuccess = {
            mViewModel.loginByUserId(it.userId, mTokenHead, mToken)
        }, onError = {
            hideLoading()
            toast("创建账号失败,请稍后重试")
        })
    }

    private fun createAccount() {
        showLoading("正在注册")
        mViewModel.createAccount(
            phone,
            "",
            mBinding.xetInviteCode.text.toString().trim(),
            smsCode
        )
    }

}