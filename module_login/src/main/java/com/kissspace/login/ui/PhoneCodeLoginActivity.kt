package com.kissspace.login.ui

import android.Manifest
import android.os.Bundle
import android.os.TokenWatcher
import android.text.Editable
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.*
import com.kissspace.common.http.sendSms
import com.kissspace.common.http.verificationCode
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.RouterPath.PATH_INPUT_SMS_CODE
import com.kissspace.common.router.jump
import com.kissspace.common.util.countDown
import com.kissspace.common.util.customToast
import com.kissspace.login.viewmodel.LoginViewModel
import com.kissspace.module_login.R
import com.kissspace.module_login.databinding.LoginActivityPhoneCodeLoginBinding
import com.kissspace.network.result.collectData
import com.kissspace.util.addAfterTextChanged
import com.kissspace.util.toJson
import com.luck.picture.lib.permissions.PermissionUtil
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Job

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/17
 * @Description: 手机验证码登录页面
 *
 */
@Router(path = RouterPath.PATH_LOGIN_PHONE_CODE)
class PhoneCodeLoginActivity : BaseActivity(R.layout.login_activity_phone_code_login) {
    private val mBinding by dataBinding<LoginActivityPhoneCodeLoginBinding>()
    private val mViewModel by viewModels<LoginViewModel>()

    var mCountDown: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
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
                customToast("请输入验证码")
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

        collectData(mViewModel.accounts, onSuccess = {
            hideLoading()
            if (it.size == 1) {
                val userAccountBean = it[0]
                mViewModel.loginByUserId(
                    userAccountBean.userId,
                    userAccountBean.tokenHead,
                    userAccountBean.token
                )
            } else if (it.isEmpty()) {
                //填写邀请码
                jump(
                    RouterPath.PATH_INPUT_INVITE_CODE,
                    "accounts" to toJson(it),
                    "phone" to mBinding.xetPhone.text.toString().trim().replace(" ", "")
                )
            } else {
                //如果有多个账号,直接登录第一个,不需要选择
                /*jump(
                    RouterPath.PATH_CHOOSE_ACCOUNT,
                    "accounts" to toJson(it),
                    "phone" to mBinding.xetPhone.text.toString().trim().replace(" ", "")
                )*/

                val userAccountBean = it[0]
                mViewModel.loginByUserId(
                    userAccountBean.userId,
                    userAccountBean.tokenHead,
                    userAccountBean.token
                )
            }
        }, onError = {
            hideLoading()
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

    override fun onDestroy() {
        super.onDestroy()
        mCountDown?.cancel()
    }

}