package com.kissspace.message.widget

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SizeUtils
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.GiftEmailMessageModel
import com.kissspace.common.model.GiftEmailReceiveState
import com.kissspace.common.model.GiftEmailRecordType
import com.kissspace.common.model.GiftJsonCard
import com.kissspace.common.model.GiftJsonMoney
import com.kissspace.common.util.getBundleParcelable
import com.kissspace.common.util.glide.loadwithGlide
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.message.viewmodel.GiftEmailViewModel
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.MessageDialogGiftBinding

class GiftMailDialog : BaseDialogFragment<MessageDialogGiftBinding>(
    MessageDialogGiftBinding::inflate,
    Gravity.CENTER
) {
    private val giftModel by lazy {
        arguments?.getBundleParcelable("giftmodel") as GiftEmailMessageModel?
    }
    private val mViewModel by viewModels<GiftEmailViewModel>()

    companion object {
        fun newInstance(
            giftmodel: GiftEmailMessageModel,
        ) = GiftMailDialog().apply {
            arguments = Bundle().apply { putParcelable("giftmodel", giftmodel) }
        }
    }


    override fun getLayoutId() = R.layout.message_dialog_gift


    override fun initView() {
        mBinding.imgClose.safeClick {
            this.dismiss()
        }
        giftModel?.let {
            mBinding.tvGiftTitle.text = "${it.title}"
            mBinding.tvGiftContent.text = it.remark.replace("\n\n", "")
            mBinding.tvGiftTime.text = it.createTime

            //设置已读
            if (!it.isRead()) {
                mViewModel.setGiftEmailReadStatus(it.recordId)
            }

            when (it.receiveState) {
                GiftEmailReceiveState.WAIT -> {
                    mBinding.tvOption.helper.backgroundColorNormalArray =
                        intArrayOf(
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_936DDE
                            ),
                            com.kissspace.module_common.R.color.color_6C74FB
                        )
                    mBinding.tvOption.text = "领取"

                    mBinding.tvOption.safeClick {
                        mViewModel.receiveGiftEmailSingle(it.recordId, success = {
                            this.dismiss()
                        })
                    }
                }

                else -> {
                    mBinding.tvOption.text = "删除"
                    mBinding.tvOption.helper.backgroundColorNormalArray =
                        intArrayOf(
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_4D936DDE
                            ),
                            com.kissspace.module_common.R.color.color_4D6C74FB
                        )

                    mBinding.tvOption.safeClick {
                        mViewModel.deleteGiftEmailSingle(it.recordId, success = {
                         this.dismiss()
                        })
                    }
                }
            }


            when (it.recordType) {
                GiftEmailRecordType.CARD -> {
                    try {
                        val bean = GsonUtils.fromJson(it.gift, GiftJsonCard::class.java)
                        mBinding.imgGift.loadwithGlide(bean.skinIcon, round = SizeUtils.dp2px(5f))
                        mBinding.tvTitle.text = "${it.title}x1"

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                else -> {
                    try {
                        val bean = GsonUtils.fromJson(it.gift, GiftJsonMoney::class.java)
                        //mBinding.imgGift.loadwithGlide(bean.skinIcon, round = SizeUtils.dp2px(5f))
                        mBinding.tvTitle.text = "${it.title}x${bean.amount}"
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }

            }


        }

        mBinding.vRoot.helper.backgroundColorNormalArray =
            intArrayOf(Color.parseColor("#B8BBFF"), Color.parseColor("#FFFFFF"))
    }

}