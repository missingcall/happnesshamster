package com.kissspace.common.widget

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.drake.net.utils.scopeDialog
import com.kissspace.common.ext.safeClick
import com.kissspace.common.util.copyClip
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonBottomDialogHintBinding
import com.kissspace.module_common.databinding.CommonDialogHintBinding

/**
 * @Author gaohangbo
 * @Date 2022/12/28 07:55.
 * @Describe 底部弹窗
 */
class CommonBottomHintDialog : BaseDialogFragment<CommonBottomDialogHintBinding>(CommonBottomDialogHintBinding::inflate,Gravity.BOTTOM) {

    private var hintContent: String? = null
    private var isShowButton: Boolean? = true
    var callBack: (() -> Unit?)? = null
    override fun getLayoutId(): Int {
        return R.layout.common_bottom_dialog_hint
    }

    companion object {
        fun newInstance(
            hintContent: String? = null,
            isShowButton: Boolean? = true
        ) = CommonBottomHintDialog().apply {
            arguments = bundleOf(
                "hintContent" to hintContent,
                "isShowButton" to isShowButton
            )
        }
    }

    override fun initView() {
        arguments?.let {
            hintContent = it.getString("hintContent")
            isShowButton = it.getBoolean("isShowButton", true)
        }
        if (isShowButton == true) {
            mBinding.clBottom.visibility = View.VISIBLE
            mBinding.clBottom.safeClick {
                dismiss()
                callBack?.invoke()
            }
        } else {
            mBinding.clBottom.visibility = View.GONE
        }
        mBinding.tvHint.text = hintContent
        mBinding.ivClose.safeClick {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
    }

}