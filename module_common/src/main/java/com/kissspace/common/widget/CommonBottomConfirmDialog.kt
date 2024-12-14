package com.kissspace.common.widget

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.drake.net.utils.scopeDialog
import com.kissspace.common.callback.ActivityResult
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.copyClip
import com.kissspace.common.util.customToast
import com.kissspace.common.util.getH5Url
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonBottomDialogConfirmBinding
import com.kissspace.module_common.databinding.CommonBottomDialogHintBinding
import com.kissspace.module_common.databinding.CommonDialogHintBinding

/**
 * @Author gaohangbo
 * @Date 2022/12/28 07:55.
 * @Describe 仓鼠风格 底部确/取消弹窗
 */
class CommonBottomConfirmDialog : BaseDialogFragment<CommonBottomDialogConfirmBinding>(CommonBottomDialogConfirmBinding::inflate, Gravity.BOTTOM) {

    /*private var hintContent: String? = null
    private var confirmContent: String? = null
    private var cancelContent: String? = null*/
    var callBack: ((Boolean) -> Unit?)? = null
    override fun getLayoutId(): Int {
        return R.layout.common_bottom_dialog_confirm
    }

    companion object {
        fun newInstance(
         /*   hintContent: String? = null,
            confirmContent: String? = null,
            cancelContent: String? = null*/
        ) = CommonBottomConfirmDialog().apply {
            arguments = bundleOf(
           /*     "hintContent" to hintContent,
                "confirmContent" to confirmContent,
                "cancelContent" to cancelContent*/
            )
        }
    }

    override fun initView() {
        arguments?.let {
          /*  hintContent = it.getString("hintContent")
            confirmContent = it.getString("confirmContent")
            cancelContent = it.getString("cancelContent")*/
        }


        initPrivacy()

        mBinding.tvConfirm.safeClick {
            dismiss()
            callBack?.invoke(true)
        }

        mBinding.tvCancel.safeClick {
            dismiss()
        }

 /*       mBinding.tvHint.text = hintContent
        mBinding.tvConfirm.text = confirmContent
        mBinding.tvCancel.text = cancelContent*/
    }

    private fun initPrivacy() {
        val privacyTips: String = "为了更好的保障您的合法权益，请您阅读并同意《用户使用协议》和《隐私协议》"
        val privacyKey1: String = "《用户使用协议》"
        val privacyKey2: String = "《隐私协议》"

        val index1 = privacyTips.indexOf(privacyKey1)
        val index2 = privacyTips.indexOf(privacyKey2)
        //需要显示的字串
        val spannedString = SpannableString(privacyTips)
        //设置点击字体颜色
        val colorSpan1 =
            ForegroundColorSpan(requireContext().resources.getColor(com.kissspace.module_common.R.color.color_5ACBFF))
        spannedString.setSpan(
            colorSpan1,
            index1,
            index1 + privacyKey1.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        val colorSpan2 =
            ForegroundColorSpan(requireContext().resources.getColor(com.kissspace.module_common.R.color.color_5ACBFF))
        spannedString.setSpan(
            colorSpan2,
            index2,
            index2 + privacyKey2.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        //设置点击事件
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
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
            clickableSpan1,
            index1,
            index1 + privacyKey1.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                jump(
                    RouterPath.PATH_WEBVIEW,
                    "url" to getH5Url(Constants.H5.privacyUrl),
                    "showTitle" to true
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                //点击事件去掉下划线
                ds.isUnderlineText = false
            }
        }
        spannedString.setSpan(
            clickableSpan2,
            index2,
            index2 + privacyKey2.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )


        //设置点击后的颜色为透明，否则会一直出现高亮
        mBinding.tvHint.highlightColor = Color.TRANSPARENT
        //开始响应点击事件
        mBinding.tvHint.movementMethod = LinkMovementMethod.getInstance()

        mBinding.tvHint.text = spannedString

        mBinding.ivClose.safeClick {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun setCallBackk(block: (Boolean) -> Unit) {
        callBack = block
    }

}