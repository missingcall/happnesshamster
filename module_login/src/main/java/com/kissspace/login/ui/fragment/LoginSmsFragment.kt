package com.kissspace.login.ui.fragment

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.http.sendSms
import com.kissspace.common.http.verificationCode
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.RouterPath.PATH_FORGET_PASSWORD
import com.kissspace.common.router.jump
import com.kissspace.common.util.countDown
import com.kissspace.common.util.customToast
import com.kissspace.login.http.LoginApi
import com.kissspace.login.viewmodel.LoginViewModel
import com.kissspace.module_login.R
import com.kissspace.module_login.databinding.LoginActivityPasswordBinding
import com.kissspace.module_login.databinding.LoginFragmentPhoneCodeLoginBinding
import com.kissspace.module_login.databinding.LoginFragmentSmsLoginBinding
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
class LoginSmsFragment : BaseFragment(R.layout.login_fragment_sms_login) {
    private val mBinding by viewBinding<LoginFragmentSmsLoginBinding>()
    private val mViewModel by viewModels<LoginViewModel>()

    var mCountDown: Job? = null

    companion object {
        fun newInstance(): LoginSmsFragment {
            return LoginSmsFragment()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.vm = mViewModel
        mBinding.ivClear.setOnClickListener {
            mBinding.xetPhone.setText("")
        }
        mBinding.xetPhone.setSeparator(" ")
        mBinding.xetPhone.setPattern(intArrayOf(3, 4, 4))
        mBinding.xetPhone.addAfterTextChanged {
//            mViewModel.phoneIconState.set(it?.isNotEmpty())
            mViewModel.getCodeBtnState.set(it?.length == 13)
        }
        mBinding.xetVerify.addAfterTextChanged {
            mViewModel.btnEnable.set(it?.length == 6 && mBinding.xetPhone.text?.length == 13)
        }


        mBinding.tvVerify.safeClick {
            PermissionX.init(this).permissions(Manifest.permission.INTERNET)
                .onExplainRequestReason { scope, deniedList ->
                    val message =
                        "为了您能正常体验完整功能，需向你申请网络权限"
                    scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
                }
                .explainReasonBeforeRequest()
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        if (mViewModel.getCodeBtnState.get()!!) {
                            val phoneNumber = mBinding.xetPhone.text.toString().trim()
                            sendSms(phoneNumber.replace(" ", ""), "2") {
                                customToast(
                                    StringUtils.getString(
                                        R.string.login_input_sms_code_tips,
                                        phoneNumber
                                    )
                                )
                                startCountDown(60)
                            }
                        }
                    } else {
                        customToast("请打开网络权限")
                    }
                }
        }

        mBinding.tvLogin.safeClick {
            if (mBinding.xetPhone.text.toString().isEmpty()) {
                customToast("请输入手机号")
                return@safeClick
            }
            showLoading("正在登录")
            verificationCode(
                mBinding.xetPhone.text.toString().replace(" ", ""),
                mBinding.xetVerify.text.toString(),
                "2",
                onError = {
                    hideLoading()
                }) {
                mViewModel.requestUserListByPhone(
                    mBinding.xetPhone.text.toString().replace(" ", "")
                )
            }
        }

        mBinding.tvForgetPassword.safeClick {
            jump(RouterPath.PATH_FORGET_PASSWORD_STEP_1)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDown?.cancel()
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
            ToastUtils.showLong("登录失败${it.errorMsg}")
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
        }, onEmpty = {
            hideLoading()
            //填写邀请码
            jump(
                RouterPath.PATH_INPUT_INVITE_CODE,

                "phone" to mBinding.xetPhone.text.toString().trim().replace(" ", ""),
                "smsCode" to mBinding.xetVerify.text.toString().trim().replace(" ", "")
            )

        })
    }

    private fun startCountDown(countDownTime: Long) {
        mCountDown = countDown(countDownTime, reverse = false, scope = lifecycleScope, onTick = {
            mBinding.tvVerify.text = "重新发送 (${it}s)"
            mViewModel.sendSmsEnable.set(false)
        }, onFinish = {
            mViewModel.sendSmsEnable.set(true)
            mBinding.tvVerify.text = "重新获取"
            mCountDown?.cancel()
        })
    }
}