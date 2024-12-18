package com.kissspace.login.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ToastUtils
import com.didi.drouter.annotation.Router
import com.drake.net.NetConfig
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.callback.ActivityResult
import com.kissspace.common.config.*
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.CommonBottomConfirmDialog
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.common.widget.CommonHintDialog
import com.kissspace.login.umeng.QuickLoginManager
import com.kissspace.login.umeng.callback.QuickLoginCallback
import com.kissspace.login.viewmodel.LoginViewModel
import com.kissspace.login.widget.LoginDialog
import com.kissspace.login.widget.PrivacyDialog
import com.kissspace.module_login.R
import com.kissspace.module_login.databinding.LoginActivityLoginBinding
import com.kissspace.network.result.collectData
import com.kissspace.room.manager.RoomServiceManager
import com.kissspace.util.finishAllActivitiesExceptNewest
import com.kissspace.util.immersiveStatusBar
import com.qmuiteam.qmui.kotlin.onClick
import java.util.*


@Router(path = RouterPath.PATH_LOGIN)
class LoginActivity : BaseActivity(R.layout.login_activity_login),
    QuickLoginCallback {
    private val mBinding by viewBinding<LoginActivityLoginBinding>()
    private val mViewModel by viewModels<LoginViewModel>()
    private var sdkAvailable = true
    private var dialog: Dialog? = null
    private val mQuickLoginManager by lazy {
        QuickLoginManager()
    }
    protected var tfRegular: Typeface? = null // 定义字体


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetConfig.host = BaseUrlConfig.BASEURL_RELEASE
        RoomServiceManager.releaseRoom()
        finishAllActivitiesExceptNewest()
    }


    @SuppressLint("ResourceAsColor")
    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.vm = mViewModel
        initAgreement()

        mBinding.tvLogin.setOnClickListener {
            if (mBinding.cbAgree.isChecked) {
//                jump(RouterPath.PATH_LOGIN_PHONE_CODE)
                LoginDialog.newInstance().show(supportFragmentManager)
            } else {
//                customToast("请勾选同意后登录")
                CommonBottomConfirmDialog.newInstance().apply {
                    this.setCallBackk { this@LoginActivity.mBinding.cbAgree.isChecked = it }
                    this.show(supportFragmentManager)
                }
            }
        }

        mBinding.tvAccountCreate.setOnClickListener {
            if (mBinding.cbAgree.isChecked) {
                jump(RouterPath.PATH_ACCOUNT_CREATE)
            } else {
//                customToast("请勾选同意后注册")
                CommonBottomConfirmDialog.newInstance().apply {
                    this.setCallBackk { this@LoginActivity.mBinding.cbAgree.isChecked = it }
                    this.show(supportFragmentManager)
                }
            }
        }

        mBinding.tvLoginPassword.onClick {
            if (mBinding.cbAgree.isChecked) {
                jump(RouterPath.PATH_LOGIN_PASSWORD)
            } else {
//                customToast("请勾选同意后登录")
                CommonBottomConfirmDialog.newInstance().apply {
                    this.setCallBackk { this@LoginActivity.mBinding.cbAgree.isChecked = it }
                    this.show(supportFragmentManager)
                }
            }
        }

//        if (isAppDebug) {
//            mBinding.ivLogo.setOnLongClickListener {
//                jump(RouterPath.PATH_BASE_URL_SETTING)
//                true
//            }
//        }
        showAgreeProtocol()
    }


    /**
     * 同意隐私弹窗
     */
    private fun showAgreeProtocol() {
        if (!MMKVProvider.isAgreeProtocol) {
            showPrivacyDialog()
        }
    }

    private fun showPrivacyDialog() {
        if (dialog == null) {
            dialog = PrivacyDialog(this@LoginActivity) { agree ->
                if (agree) {
                    FlowBus.post(Event.InitApplicationTaskEvent)
                    MMKVProvider.isAgreeProtocol = true
                } else {
                    MMKVProvider.isAgreeProtocol = false
                    exitApp()
                }
            }
        }

        if (!this@LoginActivity.isFinishing && dialog?.isShowing == false) {
            dialog?.show()
        }

    }


    private fun initAgreement() {
        mBinding.cbAgree.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.isAgree.set(isChecked)
        }
        mBinding.tvUserProtocol.safeClick {
            jump(
                RouterPath.PATH_WEBVIEW,
                "url" to getH5Url(Constants.H5.userAgreementUrl),
                "showTitle" to true
            )
        }
        mBinding.tvPrivacyProtocol.safeClick {
            jump(
                RouterPath.PATH_WEBVIEW,
                "url" to getH5Url(Constants.H5.privacyUrl),
                "showTitle" to true
            )
        }
    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.token, onSuccess = {
            mViewModel.loginIm(it, onSuccess = {
                finish()
            }, onError = {

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
    }

    private var mPlayingPos = 0

    override fun onPause() {
        mPlayingPos = mBinding.videoView.currentPosition
        mBinding.videoView.pause()
        mBinding.videoView.pause()
        super.onPause()

    }


    override fun onResume() {
        super.onResume()
        if (mPlayingPos > 0) {
            mBinding.videoView.start()
            mBinding.videoView.seekTo(mPlayingPos)
            mPlayingPos = 0
        } else {
            mBinding.videoView.start()
        }
    }

    override fun initializationSuccess() {
        sdkAvailable = true
    }

    override fun onQuickLoginSuccess(token: String) {
        mViewModel.requestUserListByUMeng(token)
        mQuickLoginManager.quitLogin()
    }

    override fun onQuickLoginFail() {
        hideLoading()
        sdkAvailable = false
        jump(RouterPath.PATH_LOGIN_PHONE_CODE)
    }

    override fun onDestroy() {
        super.onDestroy()
        mQuickLoginManager.release()
        kotlin.runCatching {
            mBinding.videoView.stopPlayback()
        }
    }
}