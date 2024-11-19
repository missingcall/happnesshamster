package com.kissspace.mine.ui.fragment.user.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.kissspace.common.model.UserProfileBean
import com.kissspace.common.util.copyClip
import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.mmkv.MMKVProvider
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
    data class UserInfoAboutMeModel(val list: List<UserInfoKeyValue>,val userInfo:UserProfileBean) {
        data class UserInfoKeyValue(
            val key: String,
            val value: String,
            val canCopy: Boolean = false
        )
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

                    b.tvValue.helper.apply {
                        iconNormalRight = if (m.canCopy){
                            ContextCompat.getDrawable(context, R.mipmap.mine_icon_copy)
                        }else{
                            null
                        }
                    }
                }

                onClick(R.id.tvValue){
                    val m = getModel<UserInfoAboutMeModel.UserInfoKeyValue>()
                    if (m.canCopy){
                        copyClip(m.value)
                    }
                }
            }
        }
        onBind {
            val model = getModel<UserInfoAboutMeModel>()
            val binding = getBinding<MineItemUserinfoBinderAboutmeBinding>()
            binding.rvAboutMe.models = model.list

            if (MMKVProvider.userId==model.userInfo.userId) {
                binding.tvAboutMe.text = "关于我"
            }else{
                binding.tvAboutMe.text = "关于TA"
                binding.tvRoomName.text = model.userInfo.followRoomName
                if (model.userInfo.followRoomId.isNotBlank()){
                    binding.rcRoomRoot.visibility =View.VISIBLE
                }else{
                    binding.rcRoomRoot.visibility =View.GONE
                }
            }
        }
        onClick(R.id.tvGoRoom){
            val model = getModel<UserInfoAboutMeModel>()
            jumpRoom(crId = model.userInfo.followRoomId)
        }
    }
}