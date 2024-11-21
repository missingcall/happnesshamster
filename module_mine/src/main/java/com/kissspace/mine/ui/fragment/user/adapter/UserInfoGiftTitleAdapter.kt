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
 * @description:礼物墙 head Adapter
 * @author: yxt
 * @create: 2024-11-18 14:24
 **/
class UserInfoGiftTitleAdapter :BindingAdapter(){
    class UserInfoGiftWallTitleModel
    init {
        addType<UserInfoGiftWallTitleModel>(R.layout.mine_item_userinfo_binder_giftwall_title)
        onCreate {
        }
        onBind {
        }
    }
}