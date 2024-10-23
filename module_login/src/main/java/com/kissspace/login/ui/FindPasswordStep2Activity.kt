package com.kissspace.login.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.http.sendSms
import com.kissspace.common.http.verificationCode
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.countDown
import com.kissspace.common.util.customToast
import com.kissspace.login.http.LoginApi
import com.kissspace.login.viewmodel.ForgetPasswordViewModel
import com.kissspace.module_login.R
import com.kissspace.module_login.databinding.LoginActivityFindPasswordStep1Binding
import com.kissspace.module_login.databinding.LoginActivityFindPasswordStep2Binding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.toast
import kotlinx.coroutines.Job


@Router(path = RouterPath.PATH_FORGET_PASSWORD_STEP_2)
class FindPasswordStep2Activity : BaseActivity(R.layout.login_activity_find_password_step2) {
    private val mBinding by viewBinding<LoginActivityFindPasswordStep2Binding>()
    private val mViewModel by viewModels<ForgetPasswordViewModel>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.m = mViewModel
        setTitleBarListener(mBinding.titleBar)


        mBinding.btnConfirm.safeClick {

            if (mViewModel.password.get().toString().isNullOrEmpty()) {
                toast("请输入密码")
                return@safeClick
            }
            if (mViewModel.confirmPwd.get().toString().isNullOrEmpty()) {
                toast("请确认密码")
                return@safeClick
            }
            if (mViewModel.password.get().toString() != mViewModel.confirmPwd.get().toString()) {
                toast("两次密码输入不一致")
                return@safeClick
            }

            updatePwd()

        }
    }

    private fun updatePwd() {
        val param = mutableMapOf<String, Any?>("password" to mViewModel.password.get().toString())
        request<Int>(LoginApi.API_RESET_LOGIN_PASSWORD, Method.POST, param, onSuccess = {
            customToast("设置成功")
            finish()
        }, onError = {
            customToast(it.errorMsg)
        })
    }
}