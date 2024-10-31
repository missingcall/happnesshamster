package com.kissspace.setting.ui.activity

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.Constants
import com.kissspace.common.router.jump
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.*
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.module_setting.R
import com.kissspace.module_setting.databinding.SettingActivitySettingBinding
import com.kissspace.util.clearAllCache
import com.kissspace.util.getTotalCacheSize

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/28 11:00
 * @Description: 设置页
 *
 */
@Router(uri = RouterPath.PATH_SETTING)
class SettingActivity : BaseActivity(R.layout.setting_activity_setting) {
    private val mBinding by viewBinding<SettingActivitySettingBinding>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.stvSettingClearCache.rightTextView.text = getTotalCacheSize()
        initClickEvents()

    }


    private fun initClickEvents() {
        setTitleBarListener(mBinding.titleBar)

        //账号安全
        mBinding.stvSettingAccountSecurity.safeClick {
            jump(RouterPath.PATH_ACCOUNT)
        }

        //消息通知
        mBinding.stvSettingMessageNotification.safeClick {
            jump(RouterPath.PATH_MESSAGE_NOTIFY)
        }

        //清除缓存
        mBinding.stvSettingClearCache.setOnClickListener {
            CommonConfirmDialog(this, title = "是否清除缓存?") {
                if (this) {
                    clearAllCache()
                    customToast("清除成功")
                    mBinding.stvSettingClearCache.rightTextView.text = getTotalCacheSize()
                }
            }.show()
        }

        //青少年模式
        mBinding.stvSettingTeenageMode.safeClick {
            jump(RouterPath.PATH_TEENAGER_DESCRIBE)
        }


        //个人信息收集清单
        mBinding.stvSettingPersonalInformationCollectionChecklist.safeClick {
            jump(
                RouterPath.PATH_WEBVIEW,
                "url" to getH5Url(Constants.H5.personalInformationUrl),
                "showTitle" to true
            )
        }
        //第三方信息共享清单
        mBinding.stvSettingListOfThirdPartyInformationSharing.safeClick {
            jump(
                RouterPath.PATH_WEBVIEW,
                "url" to getH5Url(Constants.H5.threePartyResourcesUrl),
                "showTitle" to true
            )
        }

        //关于我们
        mBinding.stvSettingAboutUs.safeClick {
            jump(RouterPath.PATH_ABOUT_US)
        }

        //退出登录
        mBinding.tvLoginOut.setOnClickListener {
            CommonConfirmDialog(this, title = "确定要退出登录？") {
                if (this) loginOut()
            }.show()
        }

    }

    override fun onResume() {
        super.onResume()


    }

    override fun createDataObserver() {
        super.createDataObserver()

    }
}