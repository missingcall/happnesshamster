package com.kissspace.login.ui

import android.Manifest
import android.os.Bundle
import android.os.TokenWatcher
import android.text.Editable
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
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
import com.kissspace.module_login.databinding.LoginActivityAccountCreateBinding
import com.kissspace.module_login.databinding.LoginActivityPhoneCodeLoginBinding
import com.kissspace.network.result.collectData
import com.kissspace.util.addAfterTextChanged
import com.kissspace.util.logD
import com.kissspace.util.toJson
import com.luck.picture.lib.permissions.PermissionUtil
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Job

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/17
 * @Description: 注册用户页面
 *
 */
@Router(path = RouterPath.PATH_ACCOUNT_CREATE)
class AccountCreateActivity : BaseActivity(R.layout.login_activity_account_create) {
    private val mBinding by dataBinding<LoginActivityAccountCreateBinding>()
    private val mViewModel by viewModels<LoginViewModel>()

    var mCountDown: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.vm = mViewModel
        mBinding.ivClearPhone.setOnClickListener {
            mBinding.xetPhone.setText("")
        }

        mBinding.ivClearPsw.setOnClickListener {
            mBinding.xetPassword.setText("")
        }

        mBinding.xetPhone.setSeparator(" ")
        mBinding.xetPhone.setPattern(intArrayOf(3, 4, 4))
        mBinding.xetPhone.addAfterTextChanged {
//            mViewModel.phoneIconState.set(it?.isNotEmpty())
            mViewModel.getCodeBtnState.set(it?.length == 13 && (mCountDown?.isActive == false || mCountDown == null))
        }
        mBinding.xetPassword.addAfterTextChanged {
            mViewModel.btnEnable.set(it?.length!! >= 8 && mBinding.xetPhone.text?.length == 13 && mBinding.xetInvite.length() == 6)
        }
        mBinding.etVerify.addAfterTextChanged {
            mViewModel.btnEnable.set(it?.length == 6 && mBinding.xetPhone.text?.length == 13 && mBinding.xetInvite.length() == 6)
        }

        mBinding.xetInvite.addAfterTextChanged {
            mViewModel.btnEnable.set(it?.length == 6 && mBinding.xetPhone.text?.length == 13 && mBinding.xetInvite.length() == 6)
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
                            sendSms(phoneNumber.replace(" ", ""), "3") {
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

        mBinding.tvConfirm.safeClick {
            if (mBinding.xetPhone.text.toString().isEmpty()) {
                customToast("请输入验证码")
                return@safeClick
            }
            showLoading("正在注册")
            verificationCode(
                mBinding.xetPhone.text.toString().replace(" ", ""),
                mBinding.etVerify.text.toString(),
                "3",
                onError = {
                    hideLoading()
                }) {
                mViewModel.createAccount(
                    mBinding.xetPhone.text.toString().replace(" ", ""),
                    mBinding.xetPassword.text.toString(),
                    mBinding.etVerify.text.toString(),
                    mBinding.xetInvite.text.toString()
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
            val userAccountBean = it[0]
            mViewModel.loginByUserId(
                userAccountBean.userId,
                userAccountBean.tokenHead,
                userAccountBean.token
            )

        }, onError = {
            hideLoading()
        }, onEmpty = {
            hideLoading()
        })

        collectData(mViewModel.createAccounts, onSuccess = {
            mViewModel.requestUserListByPhone(it.mobile)

        }, onError = {
            hideLoading()
            customToast(it.errorMsg)
        }, onEmpty = {
            hideLoading()
        })
    }

    private fun startCountDown(countDownTime: Long) {
        mCountDown = countDown(countDownTime, reverse = false, scope = lifecycleScope, onTick = {
            mBinding.tvVerify.text = "重新发送 (${it}s)"
            mViewModel.getCodeBtnState.set(false)
        }, onFinish = {
            mViewModel.getCodeBtnState.set(true)
            mBinding.tvVerify.text = "重新获取"
            mCountDown?.cancel()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDown?.cancel()
    }

}