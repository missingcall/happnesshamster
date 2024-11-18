package com.kissspace.mine.ui.fragment.user.adapter

import android.view.View
import android.widget.LinearLayout
import com.blankj.utilcode.util.SizeUtils
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.kissspace.common.model.PersonCar
import com.kissspace.common.model.UserProfileBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.glide.loadwithGlide
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineItemUserinfoBinderCarBinding
import com.kissspace.module_mine.databinding.MineLayoutUserProfileCarItemNewBinding

/**
 * @description:坐驾Adapter
 * @author: yxt
 * @create: 2024-11-18 14:24
 **/
class UserInfoCarAdapter : BindingAdapter() {
    data class UserInfoCarModel(val data: UserProfileBean)

    init {
        addType<UserInfoCarModel>(R.layout.mine_item_userinfo_binder_car)

        onCreate {
            val binding = getBinding<MineItemUserinfoBinderCarBinding>()
            binding.rvCar.linear(orientation = LinearLayout.HORIZONTAL).setup {
                addType<PersonCar>(R.layout.mine_layout_user_profile_car_item_new)
                onBind {
                    val b = getBinding<MineLayoutUserProfileCarItemNewBinding>()
                    val m = getModel<PersonCar>()
                    b.imgCar.loadwithGlide(m.carIcon, round = SizeUtils.dp2px(4f))
                }
            }
        }



        onBind {
            val binding = getBinding<MineItemUserinfoBinderCarBinding>()
            val model = getModel<UserInfoCarModel>()
            if (model.data.car.isEmpty()) {
                binding.tvBuyCar.visibility = View.VISIBLE
            } else {
                binding.tvBuyCar.visibility = View.GONE
                binding.rvCar.models = model.data.car
            }
        }

        onClick(R.id.tvBuyCar){
            jump(RouterPath.PATH_STORE)
        }



    }
}