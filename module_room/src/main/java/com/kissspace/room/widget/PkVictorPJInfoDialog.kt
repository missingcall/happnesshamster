package com.kissspace.room.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.divider
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.TeamPKInfo
import com.kissspace.common.model.TeamPKUserInfo
import com.kissspace.common.util.countDown
import com.kissspace.common.widget.BaseBottomSheetDialogFragment
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomDialogCharmCleanBinding
import com.kissspace.module_room.databinding.RoomDialogPkPingjuBinding
import com.kissspace.module_room.databinding.RoomDialogPkVictorBinding
import com.kissspace.room.game.bean.CharmValueBean
import com.kissspace.room.viewmodel.CharmValueModel
import com.kissspace.util.getParcelable
import com.kissspace.util.getSerialized
import com.kissspace.util.loadImage
import com.kissspace.util.logE
import com.qmuiteam.qmui.kotlin.onClick
import kotlinx.coroutines.Job

/**
 * param["chatRoomId"] = chatRoomId
 *         param["microphoneNumber"] = microphoneNumber
 */
class PkVictorPJInfoDialog() : BaseDialogFragment<RoomDialogPkPingjuBinding>(RoomDialogPkPingjuBinding::inflate,Gravity.CENTER) {

    companion object {
        fun newInstance() = PkVictorPJInfoDialog()
    }

    override fun getLayoutId(): Int {
        return R.layout.room_dialog_pk_pingju
    }


    private var mCountDown: Job? = null
    @SuppressLint("SetTextI18n")
    override fun initView() {


        mBinding.tvPkVictorClose.onClick {
            dismiss()
            mCountDown?.cancel()
        }

        mCountDown = countDown(3, reverse = false, scope = lifecycleScope, onTick = {
            mBinding.tvPkVictorClose.text = "关闭(${it}s)"
        }, onFinish = {
            dismiss()
        })

    }

}