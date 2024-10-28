package com.kissspace.message.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.router.RouterPath
import com.kissspace.message.widget.GiftMailDialog
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.MessageActivityGiftBinding

/**
 * @description:礼物邮件页面
 * @author: yxt
 * @create: 2024-10-28 11:38
 **/
@Router(path = RouterPath.PATH_GIFT_MAIL)
class GiftMgsActivity :BaseActivity(R.layout.message_activity_gift){
    private val mBinding by viewBinding<MessageActivityGiftBinding>()

    class GiftAdapter:BaseQuickAdapter<String,BaseViewHolder>(R.layout.message_item_gift){
        override fun convert(holder: BaseViewHolder, item: String) {
            if (holder.layoutPosition % 2 ==0){
                holder.getView<ImageView>(R.id.imgGift).setImageResource(R.mipmap.message_icon_has_gift)
            } else{
                holder.getView<ImageView>(R.id.imgGift).setImageResource(R.mipmap.message_icon_un_gift)
            }
            holder.setText(R.id.tvGiftName,"仓鼠卡片")
            holder.setText(R.id.tvGiftContent,"随风来（UID：123456）转赠您一...")
        }
    }



    override fun initView(savedInstanceState: Bundle?) {
        val adpter = GiftAdapter()
        mBinding.rvGift.adapter = adpter
        adpter.setList(listOf("1","2","3","4","5"))

        adpter.setOnItemClickListener { adapter, view, position ->
            GiftMailDialog().show(supportFragmentManager)
        }
    }

}