package com.kissspace.common.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import coil.load
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonDialogBosomFriendBinding

class BosonFriendDialog : BaseDialogFragment<CommonDialogBosomFriendBinding>(CommonDialogBosomFriendBinding::inflate,Gravity.CENTER){

    val giftInfo:GiftInfo? by lazy {
         arguments?.getParcelable("giftInfo")
    }

    private val rightName:String? by lazy {
        arguments?.getString("rightName")
    }

    private val rightIcon:String? by lazy {
        arguments?.getString("rightIcon")
    }



    companion object{
        fun newInstance(giftInfo: GiftInfo,rightName:String,rightIcon:String): BosonFriendDialog {

            val fragment = BosonFriendDialog()
            val args = Bundle()
            args.putParcelable("giftInfo", giftInfo)
            args.putString("rightName", rightName)
            args.putString("rightIcon", rightIcon)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.common_dialog_bosom_friend
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.tvOk.setOnClickListener {
            dismiss()
        }
          mBinding.tvFriendLeftIcon.load(MMKVProvider.userInfo?.profilePath)
          mBinding.tvFriendLeftName.text = MMKVProvider.userInfo?.nickname
          mBinding.tvFriendRightIcon.load(rightIcon)
          mBinding.tvFriendRightName.text = rightName
          mBinding.tvContent.text = "恭喜你成功激活与（"+rightName+"）提升了羁绊至“"+giftInfo?.relationTypeName+"”("+giftInfo?.expireDate+"天）"
    }
}