package com.kissspace.mine.ui.fragment.user.adapter

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.layoutmanager.HoverGridLayoutManager
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.kissspace.common.model.CommonGiftInfo
import com.kissspace.common.util.glide.loadwithGlide
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineItemUserinfoBinderGiftwallContentBinding
import com.kissspace.module_mine.databinding.MineLayoutProfileGiftItemNewBinding


/**
 * @description:礼物墙 内容Adapter
 * @author: yxt
 * @create: 2024-11-18 14:24
 **/
class UserInfoGiftWallAdapter : BindingAdapter() {
    class UserInfoGiftWallContentModel(val data: List<CommonGiftInfo>)


    init {
        // val mSharedPool = RecyclerView.RecycledViewPool()
        addType<UserInfoGiftWallContentModel>(R.layout.mine_item_userinfo_binder_giftwall_content)
        onCreate {
            // val binding = getBinding<MineItemUserinfoBinderGiftwallContentBinding>()
            /* binding.rvGift.grid(4).setup {
                 addType<CommonGiftInfo>(R.layout.mine_layout_profile_gift_item_new)
                 onBind {
                     val b = getBinding<MineLayoutProfileGiftItemNewBinding>()
                     val m = getModel<CommonGiftInfo>()
                     b.giftIcon.loadwithGlide(m.url)
                     b.giftName.text = m.giftName
                     //  b.tvGiftCount.text = m.giftNum Log.d("==============>", "inner1 onBind")
                 }
             }*/


        }
        onBind {
            val binding = getBinding<MineItemUserinfoBinderGiftwallContentBinding>()
            val model = getModel<UserInfoGiftWallContentModel>()
            //  binding.rvGift.models = model.data
            model.data.forEachIndexed { index, commonGiftInfo ->
                when (index) {
                    0 -> bindingView(
                        binding.includOne.giftIcon,
                        binding.includOne.giftName,
                        binding.includOne.tvGiftCount,
                        commonGiftInfo
                    )

                    1 -> bindingView(
                        binding.includeTwo.giftIcon,
                        binding.includeTwo.giftName,
                        binding.includeTwo.tvGiftCount,
                        commonGiftInfo
                    )

                    2 -> bindingView(
                        binding.includeThree.giftIcon,
                        binding.includeThree.giftName,
                        binding.includeThree.tvGiftCount,
                        commonGiftInfo
                    )

                    3 -> bindingView(
                        binding.includeFour.giftIcon,
                        binding.includeFour.giftName,
                        binding.includeFour.tvGiftCount,
                        commonGiftInfo
                    )
                }
            }

        }
    }

    private fun bindingView(
        img: ImageView,
        nameTv: TextView,
        countTv: TextView,
        data: CommonGiftInfo?
    ) {
        data?.let {
            img.loadwithGlide(it.url)
            nameTv.text = it.giftName
            countTv.text = "x${it.giftNum}"
            if (data.lightUp){
                img.alpha = 1f
                nameTv.alpha =1f
                countTv.alpha =1f
            }else{
                img.alpha = 0.3f
                nameTv.alpha =0.3f
                countTv.alpha =0.3f
            }
            //countTv.text = it.giftNum
        }


    }
}