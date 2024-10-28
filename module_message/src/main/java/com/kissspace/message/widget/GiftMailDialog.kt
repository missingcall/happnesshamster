package com.kissspace.message.widget

import android.graphics.Color
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.StringUtils
import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.ext.safeClick
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.MessageDialogGiftBinding
import com.kissspace.module_message.databinding.MessageDialogLoveWallTipsBinding

class GiftMailDialog : BaseDialogFragment<MessageDialogGiftBinding>(MessageDialogGiftBinding::inflate,Gravity.CENTER) {
    override fun getLayoutId() = R.layout.message_dialog_gift
    override fun initView() {
        mBinding.imgClose.safeClick {
            this.dismiss()
        }
        mBinding.vRoot.helper.backgroundColorNormalArray = intArrayOf(Color.parseColor("#B8BBFF"), Color.parseColor("#FFFFFF"))
    }

}