package com.kissspace.message.widget

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.Date

class GiftMailDialog : BaseDialogFragment<MessageDialogGiftBinding>(
    MessageDialogGiftBinding::inflate,
    Gravity.CENTER
) {
    private val giftModel by lazy {
        arguments?.getBundleParcelable("giftmodel") as GiftEmailMessageModel?
    }
    private val mViewModel by viewModels<GiftEmailViewModel>()

    private var mCallback:()->Unit = {}

    companion object {
        fun newInstance(
            giftmodel: GiftEmailMessageModel,
            callback:()->Unit
        ) = GiftMailDialog().apply {
            arguments = Bundle().apply { putParcelable("giftmodel", giftmodel) }
            mCallback = callback
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

            mBinding.tvGiftTime.text = getTime(it.createTime)

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

                    mBinding.tvReceiveMasking.visibility=View.GONE

                    mBinding.tvOption.safeClick {
                        mViewModel.receiveGiftEmailSingle(it.recordId, success = {
                            this.dismiss()
                            mCallback.invoke()
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

                    //显示已领取蒙层
                    if(it.receiveState==GiftEmailReceiveState.RECEIVE){
                        mBinding.tvReceiveMasking.visibility=View.VISIBLE
                    }


                    mBinding.tvOption.safeClick {
                        mViewModel.deleteGiftEmailSingle(it.recordId, success = {
                         this.dismiss()
                          mCallback.invoke()
                        })
                    }
                }
            }

            when (it.recordType) {
                GiftEmailRecordType.CARD -> {
                    try {
                        val bean = GsonUtils.fromJson(it.gift, GiftJsonCard::class.java)
                        mBinding.imgGift.loadwithGlide(bean.skinIcon, round = SizeUtils.dp2px(5f))
                        mBinding.tvTitle.text = "${it.title}x1" //仓鼠卡片数量只有1

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                else -> {
                    try {
                        val bean = GsonUtils.fromJson(it.gift, GiftJsonMoney::class.java)
                        when(bean.amountType){
                            GiftJsonMoney.GiftMoneyType.COIN->{
                                mBinding.imgGift.loadwithGlide(R.mipmap.message_icon_zs)
                            }
                            GiftJsonMoney.GiftMoneyType.DIAMOND->{
                                mBinding.imgGift.loadwithGlide(R.mipmap.message_icon_sg)
                            }
                            GiftJsonMoney.GiftMoneyType.INCOME->{
                                mBinding.imgGift.loadwithGlide(R.mipmap.message_icon_sz)
                            }
                            else->{
                            mBinding.imgGift.loadwithGlide(com.kissspace.module_common.R.mipmap.common_app_logo)
                            }
                        }

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


    /**
     * 获得 年 月 日
     */
    private fun getTime(data:String?):String{
        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val time = data?.substring(0,19)?.replace("T"," ")
            val d = format.parse(time)
            val timestamp = d?.time?:0L
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("yyyy年MM月dd日")
            val formattedDate = sdf.format(date)
            return formattedDate
        }catch (ex:Exception){
            return ""
        }
    }
}