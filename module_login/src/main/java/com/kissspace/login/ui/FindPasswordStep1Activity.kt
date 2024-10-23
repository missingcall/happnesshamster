package com.kissspace.login.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.http.sendSms
import com.kissspace.common.http.verificationCode
import com.kissspace.common.model.LoginResultBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.countDown
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.login.http.LoginApi
import com.kissspace.login.viewmodel.ForgetPasswordViewModel
import com.kissspace.module_login.R
import com.kissspace.module_login.databinding.LoginActivityFindPasswordStep1Binding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.toast
import kotlinx.coroutines.Job


@Router(path = RouterPath.PATH_FORGET_PASSWORD_STEP_1)
class FindPasswordStep1Activity : BaseActivity(R.layout.login_activity_find_password_step1) {
    private val mBinding by viewBinding<LoginActivityFindPasswordStep1Binding>()
    private val mViewModel by viewModels<ForgetPasswordViewModel>()
    private var mCountDown: Job? = null
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.m = mViewModel
        setTitleBarListener(mBinding.titleBar)
        mBinding.btnVerify.safeClick {
            sendSms(mViewModel.phoneNumber.get().toString(), "5") {
                //倒计时60秒
                mCountDown = countDown(60, reverse = false, scope = lifecycleScope, onTick = {
                    mBinding.btnVerify.text = "重新发送 (${it}s)"
                    mViewModel.sendSmsEnable.set(false)
                }, onFinish = {
                    mViewModel.sendSmsEnable.set(true)
                    mBinding.btnVerify.text = "重新获取"
                    mCountDown?.cancel()
                })
            }
        }

        mBinding.btnNext.safeClick {
            if (mViewModel.phoneNumber.get().toString().isNullOrEmpty()) {
                toast("请输入手机号")
                return@safeClick
            }


            if (mBinding.llVerifyOut.visibility == View.GONE) {
                queryPhoneExist(onSuccess = {
                    if (it) {
                        mBinding.etPhone.isEnabled = false
                        mBinding.tvPhoneVerified.visibility = View.VISIBLE
                        mBinding.llVerifyOut.visibility = View.VISIBLE
                    } else {
                        customToast(R.string.login_phone_num_incorrect_or_not_registered)
                    }

                })
            } else {
                if (mViewModel.verificationCode.get().toString().isNullOrEmpty()) {
                    toast("请输入验证码")
                    return@safeClick
                }

                verificationCode(
                    mBinding.etPhone.text.toString(), mBinding.etVerify.text.toString(), "5"
                ) {
                    //调登录接口->跳转
                    mViewModel.phoneCodeLogin(mViewModel.phoneNumber.get().toString(), mViewModel.verificationCode.get().toString(), onSuccess = {
                        MMKVProvider.loginResult = LoginResultBean(it.token,"","",it.tokenHead,0,false,false,"")
                        jump(RouterPath.PATH_FORGET_PASSWORD_STEP_2)
                        finish()
                    })
                }
            }


        }
    }


    private fun queryPhoneExist(onSuccess: ((Boolean) -> Unit)) {
        val param = mutableMapOf<String, Any?>("phone" to mViewModel.phoneNumber.get().toString())
        request<Boolean>(LoginApi.API_QUERY_PHONE_EXIST, Method.GET, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            customToast(it.errorMsg)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDown?.cancel()
    }
}