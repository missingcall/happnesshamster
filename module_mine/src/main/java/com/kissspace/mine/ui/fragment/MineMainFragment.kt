package com.kissspace.mine.ui.fragment

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.canSendMsgToOther
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.CommonGiftInfo
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.copyClip
import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.viewmodel.UserProfileViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentUserInfoPageBinding

/**
 * @Describe 个人主页
 */
class MineMainFragment : BaseFragment(R.layout.mine_fragment_user_info_page) {
    private val userId :String ?  by lazy { arguments?.getString("userId") }
    private val mViewModel by activityViewModels<UserProfileViewModel>()
    private val mBinding by dataBinding<MineFragmentUserInfoPageBinding>()

    companion object {
        fun newInstance(userId: String?): MineMainFragment {
            val args = Bundle()
            args.putString("userId", userId)
            val fragment = MineMainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.vm = mViewModel
        initGiftRecyclerView()
        initEvent()
    }

    private fun initGiftRecyclerView() {
        mBinding.recyclerGift.grid(4).setup {
            addType<CommonGiftInfo> { R.layout.mine_layout_profile_gift_item }
            onBind {
                Log.d("==========>","onBind")
                val model = getModel<CommonGiftInfo>()
                if (model.giftNum == 0) {
                    findView<ImageView>(R.id.giftIcon).alpha = 0.3f
                    findView<TextView>(R.id.giftName).alpha = 0.3f
                } else {
                    findView<ImageView>(R.id.giftIcon).alpha = 1f
                    findView<TextView>(R.id.giftName).alpha = 1f
                }
            }
        }.models = mutableListOf()
    }


    private fun initEvent() {
        mBinding.tvChat.safeClick {
            if(canSendMsgToOther()){
                mViewModel.userInfo.get()?.let {
                    jump(RouterPath.PATH_CHAT, "account" to it.accid, "userId" to it.userId)
                }
            }
        }
        mBinding.tvIdValue.safeClick {
            mViewModel.userInfo.get()?.let {
                copyClip(it.displayId)
            }

        }

        mBinding.ivFollowRoom.safeClick {
            jumpRoom(crId = mViewModel.userInfo.get()!!.followRoomId)
        }

    }

    override fun createDataObserver() {
        super.createDataObserver()
        lifecycleScope.launchWhenResumed {
            mViewModel.userProfileEvent.collect {
                MMKVProvider.userProfileInfo = it
                mBinding.recyclerGift.bindingAdapter.mutable.clear()
                mBinding.recyclerGift.bindingAdapter.addModels(it.giftWall)
                val images = mutableListOf<String>()
                images.addAll(it.bgPath)
                if (images.isEmpty()) {
                    images.add(it.profilePath)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            mViewModel.giftListEvent.collect {
                mBinding.recyclerGift.bindingAdapter.addModels(it)
            }
        }




    }
}