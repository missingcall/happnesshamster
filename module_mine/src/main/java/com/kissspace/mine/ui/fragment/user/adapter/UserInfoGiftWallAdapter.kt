package com.kissspace.mine.ui.fragment.user.adapter

import android.util.Log
import android.widget.LinearLayout
import com.blankj.utilcode.util.SizeUtils
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.kissspace.common.model.CommonGiftInfo
import com.kissspace.common.model.PersonCar
import com.kissspace.common.util.glide.loadwithGlide
import com.kissspace.mine.ui.fragment.user.adapter.UserInfoCarAdapter.UserInfoCarModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineItemUserinfoBinderCarBinding
import com.kissspace.module_mine.databinding.MineItemUserinfoBinderGiftwallContentBinding
import com.kissspace.module_mine.databinding.MineLayoutProfileGiftItemNewBinding
import com.kissspace.module_mine.databinding.MineLayoutUserProfileCarItemNewBinding

/**
 * @description:礼物墙 内容Adapter
 * @author: yxt
 * @create: 2024-11-18 14:24
 **/
class UserInfoGiftWallAdapter : BindingAdapter() {
    class UserInfoGiftWallContentModel(val data: List<CommonGiftInfo>)

    init {
        addType<UserInfoGiftWallContentModel>(R.layout.mine_item_userinfo_binder_giftwall_content)

        onCreate {

            val binding = getBinding<MineItemUserinfoBinderGiftwallContentBinding>()
            if (binding.rvGift.adapter == null) {
                binding.rvGift.grid(4).setup {
                    addType<CommonGiftInfo>(R.layout.mine_layout_profile_gift_item_new)
                    onBind {
                        val b = getBinding<MineLayoutProfileGiftItemNewBinding>()
                        val m = getModel<CommonGiftInfo>()
                        b.giftIcon.loadwithGlide(m.url)
                        b.giftName.text = m.giftName
                        //  b.tvGiftCount.text = m.giftNum
                        Log.d("==============>", "inner onBind")
                    }
                }
            }

        }
        onBind {
            val binding = getBinding<MineItemUserinfoBinderGiftwallContentBinding>()
            val model = getModel<UserInfoGiftWallContentModel>()
            binding.rvGift.models = model.data
            Log.d("==============>", "out onBind")
        }
    }
}