package com.kissspace.setting.ui.activity

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ColorUtils
import com.didi.drouter.annotation.Router
import com.kissspace.util.activityresult.registerForStartActivityResult
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.callback.ActivityResult.BindPhone
import com.kissspace.common.ext.*
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.CommonHintDialog

import com.kissspace.module_setting.databinding.SettingActivityAccountSafeBinding
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.router.jump
import com.kissspace.module_setting.R
import com.kissspace.util.logE

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/28 11:39
 * @Description: 身份认证activity
 *
 */
@Router(path = RouterPath.PATH_ACCOUNT)
class AccountSafeActivity : BaseActivity(R.layout.setting_activity_account_safe) {
    private val mBinding by viewBinding<SettingActivityAccountSafeBinding>()
    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)

        //更换手机号
        mBinding.stvChangePhoneNumber.rightTextView.text = MMKVProvider.userPhone
        mBinding.stvChangePhoneNumber.safeClick {
            CommonHintDialog.newInstance(
                "手机号绑定",
                "已绑定手机号：${MMKVProvider.userPhone}",
                isShowButton = true
            ).apply {
                this.callBack = {
                    jump(
                        RouterPath.PATH_SEND_SMS_CODE,
                        "type" to BindPhone,
                        activity = activity,
                        resultLauncher = startActivityLauncher
                    )
                    dismiss()
                }
                this.show(supportFragmentManager)
            }
        }

        //登录密码
        mBinding.stvLoginPassword.safeClick {
            getUserInfo(onSuccess = {
                if (it.isSetPassword) {
                    jump(RouterPath.PATH_UPDATE_LOGIN_PASSWORD)
                } else {
                    jump(RouterPath.PATH_SETTING_LOGIN_PASSWORD)
                }
            })
        }

        //实名认证
        if (MMKVProvider.authentication) {
            mBinding.stvRealNameAuthentication.rightTextView.text = "已认证"
            mBinding.stvRealNameAuthentication.rightTextView.setTextColor(ColorUtils.getColor(R.color.white))
        } else {
            mBinding.stvRealNameAuthentication.rightTextView.text = "未认证"
            mBinding.stvRealNameAuthentication.rightTextView.setTextColor(ColorUtils.getColor(com.kissspace.module_common.R.color.color_ui_sub_text))
        }

        mBinding.stvRealNameAuthentication.safeClick {
            if (!MMKVProvider.authentication) {
                jump(RouterPath.PATH_USER_IDENTITY_AUTH)
            } else {
                jump(RouterPath.PATH_USER_IDENTITY_AUTH_SUCCESS)
            }
        }


        //注销账号
        mBinding.stvCancelAccount.safeClick {
            jump(RouterPath.PATH_LOG_OFF_ACCOUNT)
        }

    }

    private val startActivityLauncher = registerForStartActivityResult { result ->
        logE("result.resultCode" + result.resultCode)
        if (result.resultCode == RESULT_OK) {
            //重新请求用户信息，刷新用户
            getUserInfo(onSuccess = { userinfo ->
                mBinding.stvChangePhoneNumber.rightTextView.text = MMKVProvider.userPhone
            })
        }
    }
}