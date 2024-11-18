package com.kissspace.message.widget

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SizeUtils
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.common.model.GiftJsonCard
import com.kissspace.common.model.GiftJsonMoney
import com.kissspace.common.util.glide.loadwithGlide
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.MessageDialogGiftResultBinding
import com.kissspace.module_message.databinding.MessageItemGiftmailResultBinding


/**
 * 礼物邮件 领取结果dialog
 *
 *
 *   "data": [
 *         "{\"skinIcon\":\"xxx\",\"skinId\":\"003\"}", 仓鼠卡片
 *         "{\"amount\":\"888\",\"amountType\":\"003\"}",松果砖石等
 *         "{\"amount\":\"666\",\"amountType\":\"003\"}"
 *     ]
 */


class GiftMailResultDialog : BaseDialogFragment<MessageDialogGiftResultBinding>(
    MessageDialogGiftResultBinding::inflate,
    Gravity.CENTER
) {

    /**
     * 礼物展示实体
     */
    data class GiftResultItem(val name:String,val url:Any?)


    private val giftResultList by lazy {
        arguments?.getStringArrayList("list")
    }

    companion object {
        fun newInstance(
            list: ArrayList<String>
        ) = GiftMailResultDialog().apply {
            arguments = Bundle().apply { putStringArrayList("list", list) }
        }
    }


    override fun getLayoutId() = R.layout.message_dialog_gift_result


    override fun initView() {
        giftResultList?.let {
          val _list =  parsingData(it)
            when (_list.size) {
                0 -> {//没数据 不可能发生

                }
                in 1..3 -> { //居中显示
                    val lp = mBinding.rvGift.layoutParams
                    lp.width = RecyclerView.LayoutParams.WRAP_CONTENT
                    lp.height = RecyclerView.LayoutParams.WRAP_CONTENT
                    mBinding.rvGift.layoutParams = lp

                    mBinding.rvGift.grid(_list.size).dividerSpace(
                        SizeUtils.dp2px(12f), orientation = DividerOrientation.VERTICAL
                    ).setup {
                        addType<GiftResultItem>(R.layout.message_item_giftmail_result)
                        onBind {
                            val model = getModel<GiftResultItem>()
                            val bingding = getBinding<MessageItemGiftmailResultBinding>()
                            val lp_item = bingding.clRoot.layoutParams
                            lp_item.width = SizeUtils.dp2px(65f)
                            bingding.clRoot.layoutParams = lp_item
                            BinderAdapter(bingding.tvGiftName,bingding.imgGift,model)
                        }

                    }.mutable = _list.toMutableList()
                }


                in 4..5 -> {//居中显示

                    val lp = mBinding.rvGift.layoutParams
                    lp.width = RecyclerView.LayoutParams.WRAP_CONTENT
                    lp.height = RecyclerView.LayoutParams.WRAP_CONTENT
                    mBinding.rvGift.layoutParams = lp

                    mBinding.rvGift.grid(_list.size).dividerSpace(
                        SizeUtils.dp2px(5f), orientation = DividerOrientation.VERTICAL
                    ).setup {
                        addType<GiftResultItem>(R.layout.message_item_giftmail_result)
                        onBind {
                            val model = getModel<GiftResultItem>()
                            val bingding = getBinding<MessageItemGiftmailResultBinding>()
                            val lp_item = bingding.clRoot.layoutParams
                            lp_item.width = SizeUtils.dp2px(47f)
                            bingding.clRoot.layoutParams = lp_item
                            BinderAdapter(bingding.tvGiftName,bingding.imgGift,model)
                        }

                    }.mutable = _list.toMutableList()

                }

                else -> { //全屏显示
                    val lp = mBinding.rvGift.layoutParams
                    lp.width = RecyclerView.LayoutParams.MATCH_PARENT
                    lp.height = RecyclerView.LayoutParams.MATCH_PARENT
                    mBinding.rvGift.layoutParams = lp

                    mBinding.rvGift.setPadding(
                        SizeUtils.dp2px(10f),
                        SizeUtils.dp2px(40f),
                        SizeUtils.dp2px(10f),
                        0
                    )

                    mBinding.rvGift.grid(5).dividerSpace(
                        SizeUtils.dp2px(5f), orientation = DividerOrientation.GRID
                    ).setup {
                        addType<GiftResultItem>(R.layout.message_item_giftmail_result)
                        onBind {
                            val binding = getBinding<MessageItemGiftmailResultBinding>()
                            val model = getModel<GiftResultItem>()

                            BinderAdapter(binding.tvGiftName,binding.imgGift,model)

                        }
                    }.mutable = _list.toMutableList()
                }
            }

        }

    }

    /**
     * 数据绑定
     * @param tv 名称textView
     * @param img 视图imageView
     * @param item 实体
     */
    private fun BinderAdapter(tv:TextView,img:ImageView,item:GiftResultItem){
        tv.text = item.name
        img.loadwithGlide(item.url,round = SizeUtils.dp2px(5f))
    }

    /**
     *  将list<string> 转为list<GiftResultItem>
     */
    private fun parsingData(list: List<String>):List<GiftResultItem> {
       val result = mutableListOf<GiftResultItem>()
        list.forEach {
          try {//先解析钻石松果松子等
             val data = GsonUtils.fromJson(it,GiftJsonMoney::class.java)
              //gift名称
              val giftName:String = when(data.amountType){
                  GiftJsonMoney.GiftMoneyType.COIN->{
                      "钻石"
                  }
                  GiftJsonMoney.GiftMoneyType.DIAMOND->{
                      "松果"
                  }
                  GiftJsonMoney.GiftMoneyType.INCOME->{
                      "松子"
                  }
                  else->{
                      "积分"
                  }
              }
              //gift图片
              val giftUrl = when(data.amountType){
                  GiftJsonMoney.GiftMoneyType.COIN->{
                    R.mipmap.message_icon_zs
                  }
                  GiftJsonMoney.GiftMoneyType.DIAMOND->{
                    R.mipmap.message_icon_sg
                  }
                  GiftJsonMoney.GiftMoneyType.INCOME->{
                      R.mipmap.message_icon_sz
                  }
                  else->{
                    com.kissspace.module_common.R.mipmap.common_app_logo
                  }
              }
              result.add(GiftResultItem(name = "${giftName}x${data.amount}", url = giftUrl))

          }  catch (ex:Exception){
              ex.printStackTrace()
              try {//异常则解析仓鼠卡片
                  val csData = GsonUtils.fromJson(it, GiftJsonCard::class.java)
                  result.add(GiftResultItem(name = "仓鼠卡片x1", url = csData.skinIcon))
              }catch (ex:Exception){
                  ex.printStackTrace()
              }
          }
        }
        return result
    }


}