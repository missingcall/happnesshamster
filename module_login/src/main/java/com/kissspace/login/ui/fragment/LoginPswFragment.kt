package com.kissspace.login.ui.fragment

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.http.sendSms
import com.kissspace.common.http.verificationCode
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.RouterPath.PATH_FORGET_PASSWORD
import com.kissspace.common.router.RouterPath.PATH_FORGET_PASSWORD_STEP_1
import com.kissspace.common.router.jump
import com.kissspace.common.util.countDown
import com.kissspace.common.util.customToast
import com.kissspace.login.http.LoginApi
import com.kissspace.login.viewmodel.AccountCreateResponse
import com.kissspace.login.viewmodel.LoginViewModel
import com.kissspace.module_login.R
import com.kissspace.module_login.databinding.LoginActivityPasswordBinding
import com.kissspace.module_login.databinding.LoginFragmentPhoneCodeLoginBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.collectData
import com.kissspace.util.addAfterTextChanged
import com.kissspace.util.toJson
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Job

/**
 * @Describe 密码登录
 */
class LoginPswFragment : BaseFragment(R.layout.login_fragment_phone_code_login) {
    private val mBinding by viewBinding<LoginFragmentPhoneCodeLoginBinding>()
    private val mViewModel by viewModels<LoginViewModel>()


    companion object {
        fun newInstance(): LoginPswFragment {
            return LoginPswFragment()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.vm = mViewModel
        mBinding.ivClear.setOnClickListener {
            mBinding.xetPhone.setText("")
        }
        mBinding.ivClearPsw.setOnClickListener {
            mBinding.xetPsw.setText("")
        }
        mBinding.xetPhone.addAfterTextChanged {
            setBtnEnable()
        }
        mBinding.xetPsw.addAfterTextChanged {
            setBtnEnable()
        }


        mBinding.tvLogin.safeClick {
            val phoneNumber = mBinding.xetPhone.text.toString().trim()
            val password = mBinding.xetPsw.text.toString().trim()
            if (phoneNumber.isNullOrEmpty()) {
                customToast("请输入手机号")
                return@safeClick
            }
            if (password.isNullOrEmpty()) {
                customToast("请输入密码")
                return@safeClick
            }
            if (phoneNumber.length != 11) {
                customToast("请输入正确的手机号")
                return@safeClick
            }
            showLoading("登录中")
            val param = mutableMapOf<String, Any?>()
            param["mobile"] = phoneNumber
            param["password"] = password
            param["machineCode"] = DeviceUtils.getUniqueDeviceId()
            request<AccountCreateResponse>(LoginApi.API_PASSWORD_LOGIN, Method.POST, param, onSuccess = {
                mViewModel.requestUserListByPhone(phoneNumber.replace(" ", ""))
            }, onError = {
                hideLoading()
                customToast(it.message)
            })

        }

        mBinding.tvForgetPassword.safeClick {
            jump(PATH_FORGET_PASSWORD_STEP_1)
        }


    }

    private fun setBtnEnable() {
        mViewModel.btnEnable.set(mBinding.xetPhone.text?.length!! == 11 && mBinding.xetPsw.text?.length!! >= 6)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.token, onSuccess = {
            hideLoading()
            mViewModel.loginIm(it, onSuccess = {
                activity?.finish()
            })
        }, onError = {
            hideLoading()
            customToast("登录失败${it.message}")
        })

        collectData(mViewModel.accounts, onSuccess = {
            hideLoading()
            when (it.size) {
                1 -> {
                    val userAccountBean = it[0]
                    mViewModel.loginByUserId(
                        userAccountBean.userId,
                        userAccountBean.tokenHead,
                        userAccountBean.token
                    )
                }
                //多于一个身份时直接登录最近的那个
                else -> {
                    val userAccountBean = it[0]
                    mViewModel.loginByUserId(
                        userAccountBean.userId,
                        userAccountBean.tokenHead,
                        userAccountBean.token
                    )
                }
            }
        }, onError = {
            hideLoading()
        })
    }
}