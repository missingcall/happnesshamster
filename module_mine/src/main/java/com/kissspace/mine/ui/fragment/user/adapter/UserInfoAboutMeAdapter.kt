package com.kissspace.mine.ui.fragment.user.adapter

import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineItemUserinfoBinderAboutmeBinding
import com.kissspace.module_mine.databinding.MineItemUserinfoItemAboutmeBinding

/**
 * @description: 用户信息 关于xxx
 * @author: yxt
 * @create: 2024-11-18 13:12
 **/
class UserInfoAboutMeAdapter : BindingAdapter() {

    /**
     * 关于我model
     * @param list 个人信息列表
     */
    data class UserInfoAboutMeModel(val list: List<UserInfoKeyValue>){
        data class UserInfoKeyValue(val key: String, val value: String)
    }


    init {
        addType<UserInfoAboutMeModel>(R.layout.mine_item_userinfo_binder_aboutme)
        onCreate {
            val binding = getBinding<MineItemUserinfoBinderAboutmeBinding>()


            binding.rvAboutMe.linear().setup {
                addType<UserInfoAboutMeModel.UserInfoKeyValue>(R.layout.mine_item_userinfo_item_aboutme)
                onBind {
                    val b = getBinding<MineItemUserinfoItemAboutmeBinding>()
                    val m = getModel<UserInfoAboutMeModel.UserInfoKeyValue>()
                    b.tvKey.text = m.key
                    b.tvValue.text = m.value
                }
            }
        }
        onBind {
            val model = getModel<UserInfoAboutMeModel>()
            val binding = getBinding<MineItemUserinfoBinderAboutmeBinding>()
            binding.rvAboutMe.models = model.list
        }
    }
}