 package com.hamster.happyness.widget

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.hamster.happyness.R
import com.hamster.happyness.databinding.DialogNoticeBinding
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.BaseDialogFragment

/**
 *
 * @Author: yxt
 * @CreateDate: 2024/11/20 10:22
 * @Description: 公告弹窗
 *
 */

class NoticeDialog : BaseDialogFragment<DialogNoticeBinding>(DialogNoticeBinding::inflate,Gravity.CENTER) {
    private var onDismissCallback: (() -> Unit?)? = null

    companion object {
        fun newInstance(notice: String) = NoticeDialog().apply {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun getLayoutId() = R.layout.dialog_notice

    override fun initView() {

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }


    }

    fun setDismissCallback(block: () -> Unit) {
        this.onDismissCallback = block
    }

    override fun show(fm: FragmentManager) {
        MMKVProvider.lastShowUpgradeDate = System.currentTimeMillis()
        show(fm, "UpgradeDialog")
    }


}