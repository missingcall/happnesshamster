package com.kissspace.message.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.GiftEmailMessageModel
import com.kissspace.common.router.RouterPath
import com.kissspace.message.viewmodel.GiftEmailViewModel
import com.kissspace.message.widget.GiftMailDialog
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.MessageActivityGiftBinding
import com.kissspace.module_message.databinding.MessageItemGiftBinding
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


    private val adapter by lazy {
        GiftAdapter()
    }

    class GiftAdapter:BaseQuickAdapter<GiftEmailMessageModel,BaseViewHolder>(R.layout.message_item_gift){
        override fun convert(holder: BaseViewHolder, item: GiftEmailMessageModel) {
            /*if (holder.layoutPosition % 2 ==0){
                holder.getView<ImageView>(R.id.imgGift).setImageResource(R.mipmap.message_icon_has_gift)
            } else{
                holder.getView<ImageView>(R.id.imgGift).setImageResource(R.mipmap.message_icon_un_gift)
            }*/
            holder.setText(R.id.tvGiftName,"仓鼠卡片")
            holder.setText(R.id.tvGiftContent,"随风来（UID：123456）转赠您一...")
        }
    }



    override fun initView(savedInstanceState: Bundle?) {
       mBinding.rvGift.adapter = adapter
       mViewModel.requestGiftEmailMessage(1,10)

        adapter.setOnItemClickListener { adapter, view, position ->
            GiftMailDialog().show(supportFragmentManager)
        }
    }

    override fun createDataObserver() {
        super.createDataObserver()
        //礼物邮件
        collectData(mViewModel.giftemailMessageEvent, onSuccess = {
            if (!it.list.isNullOrEmpty()) {
                adapter.setList(it.list)
            } else {

            }
            FlowBus.post(Event.RefreshUnReadMsgCount)
        })

    }

}