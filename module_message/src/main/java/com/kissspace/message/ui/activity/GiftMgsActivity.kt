package com.kissspace.message.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.GiftEmailMessageModel
import com.kissspace.common.model.GiftEmailReceiveState
import com.kissspace.common.router.RouterPath
import com.kissspace.message.viewmodel.GiftEmailViewModel
import com.kissspace.message.widget.GiftMailDialog
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.MessageActivityGiftBinding
import com.kissspace.network.result.collectData

/**
 * @description:礼物邮件页面
 * @author: yxt
 * @create: 2024-10-28 11:38
 **/
@Router(path = RouterPath.PATH_GIFT_MAIL)
class GiftMgsActivity :BaseActivity(R.layout.message_activity_gift){
    private val mBinding by viewBinding<MessageActivityGiftBinding>()
    private val mViewModel by viewModels<GiftEmailViewModel>()


    private val mGiftEmailAdapter by lazy {
        GiftAdapter()
    }

    class GiftAdapter:BaseQuickAdapter<GiftEmailMessageModel,BaseViewHolder>(R.layout.message_item_gift){
        override fun convert(holder: BaseViewHolder, item: GiftEmailMessageModel) {
            /*if (holder.layoutPosition % 2 ==0){
                holder.getView<ImageView>(R.id.imgGift).setImageResource(R.mipmap.message_icon_has_gift)
            } else{
                holder.getView<ImageView>(R.id.imgGift).setImageResource(R.mipmap.message_icon_un_gift)
            }*/
            holder.setText(R.id.tvGiftName,"${item.title}")
            holder.setText(R.id.tvGiftContent,item.remark.replace("\n\n",""))

            holder.setText(R.id.tvGiftTime,"xxx天后过期")



            when(item.receiveState){
                GiftEmailReceiveState.WAIT->{//待领取
                    holder.setVisible(R.id.imgHasGift,true)
                    holder.setGone(R.id.tvGiftStatus,true)
                }
                GiftEmailReceiveState.RECEIVE->{//已领取
                    holder.setGone(R.id.imgHasGift,true)
                    holder.setVisible(R.id.tvGiftStatus,true)
                }
                else->{//已失效
                    holder.setGone(R.id.imgHasGift,true)
                    holder.setGone(R.id.tvGiftStatus,true)
                }
            }
        }
    }



    override fun initView(savedInstanceState: Bundle?) {
       mBinding.rvGift.adapter = mGiftEmailAdapter
       mViewModel.requestGiftEmailMessage(1,10)

        mGiftEmailAdapter.setOnItemClickListener { adapter, view, position ->
            GiftMailDialog.newInstance(this.mGiftEmailAdapter.data[position]).show(supportFragmentManager)
        }
        //删除所有已领取
        mBinding.tvDeleteRead.safeClick {
            mViewModel.deleteAllReceivedGiftEmail(success = {})
        }

        //一键领取所有礼物邮件
        mBinding.tvAllReceive.safeClick {
            mViewModel.receiveGiftEmailAll(success = {})
        }
    }

    override fun createDataObserver() {
        super.createDataObserver()
        //礼物邮件
        collectData(mViewModel.giftemailMessageEvent, onSuccess = {
            if (!it.list.isNullOrEmpty()) {
                mGiftEmailAdapter.setList(it.list)
            } else {

            }
            FlowBus.post(Event.RefreshUnReadMsgCount)
        })

    }

}