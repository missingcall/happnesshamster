package com.kissspace.setting.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.router.jump
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.module_setting.R
import com.kissspace.module_setting.databinding.SettingActivityLogOffAccountBinding
import com.kissspace.common.callback.ActivityResult.CancelAccount
import com.kissspace.common.config.Constants
import com.kissspace.common.util.customToast
import com.kissspace.common.util.getH5Url

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/28 13:55
 * @Description: 注销账号页面
 *
 */
@Router(path = RouterPath.PATH_LOG_OFF_ACCOUNT)
class LogOffAccountActivity : com.kissspace.common.base.BaseActivity(R.layout.setting_activity_log_off_account) {
    private val mBinding by viewBinding<SettingActivityLogOffAccountBinding>()

    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)

        val privacyTips: String = "同意 《" + getString(R.string.setting_about_cancel_account_privacy) + "》"
        val privacyKey: String = getString(R.string.setting_about_cancel_account_privacy)
        val index = privacyTips.indexOf(privacyKey)

        //需要显示的字串
        val spannedString = SpannableString(privacyTips)
        //设置点击字体颜色
        val colorSpan = ForegroundColorSpan(ColorUtils.getColor(com.kissspace.module_common.R.color.color_5ACBFF))
        spannedString.setSpan(
            colorSpan,
            index,
            index + privacyKey.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        //设置点击事件
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                jump(
                    RouterPath.PATH_WEBVIEW,
                    "url" to getH5Url(Constants.H5.userAgreementUrl),
                    "showTitle" to true
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                //点击事件去掉下划线
                ds.isUnderlineText = false
            }
        }
        spannedString.setSpan(
            clickableSpan,
            index,
            index + privacyKey.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        //设置点击后的颜色为透明，否则会一直出现高亮
        mBinding.tvPrivacy.highlightColor = Color.TRANSPARENT
        //开始响应点击事件
        mBinding.tvPrivacy.movementMethod = LinkMovementMethod.getInstance()

        mBinding.tvPrivacy.text = spannedString



        mBinding.tvSubmit.safeClick {
            if (mBinding.cbAgree.isChecked) jump(RouterPath.PATH_SEND_SMS_CODE, "type" to CancelAccount)
            else customToast("请勾选协议")
        }



    }
}