package com.kissspace.mine.ui.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import com.kissspace.common.base.BaseLazyFragment
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.ui.fragment.user.adapter.UserInfoAboutMeAdapter
import com.kissspace.mine.ui.fragment.user.adapter.UserInfoCarAdapter
import com.kissspace.mine.ui.fragment.user.adapter.UserInfoGiftTitleAdapter
import com.kissspace.mine.ui.fragment.user.adapter.UserInfoGiftWallAdapter
import com.kissspace.mine.viewmodel.UserProfileViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentUserInfoPageNewBinding
import kotlinx.coroutines.launch

/**
 * @description: 重构个人主页
 * @author: yxt
 * @create: 2024-11-18 11:48
 **/
class MineMainNew2Fragment : BaseLazyFragment(R.layout.mine_fragment_user_info_page_new) {

    private val userId: String? by lazy { arguments?.getString("userId") }


    private val mBinding by dataBinding<MineFragmentUserInfoPageNewBinding>()

    private val mViewModel by activityViewModels<UserProfileViewModel>()

    @Deprecated("")
    override fun initView(savedInstanceState: Bundle?) {
    }


    private val userInfoAboutMeAdapter by lazy {
        UserInfoAboutMeAdapter()
    }

    private val userInfoCarAdapter by lazy {
        UserInfoCarAdapter()
    }

    private val userInfoGiftTitleAdapter by lazy {
        UserInfoGiftTitleAdapter()
    }

    private val userInfoGiftWallAdapter by lazy {
        UserInfoGiftWallAdapter()
    }

    companion object {
        fun newInstance(userId: String?): MineMainNew2Fragment {
            val args = Bundle()
            args.putString("userId", userId)
            val fragment = MineMainNew2Fragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun lazyInitView() {
        val concatAdapter =
            ConcatAdapter(
                userInfoAboutMeAdapter,
                userInfoCarAdapter,
                userInfoGiftTitleAdapter,
                userInfoGiftWallAdapter
            )

        mBinding.rvUserInfo.adapter = concatAdapter
    }

    override fun lazyLoadData() {
    }

    override fun lazyClickListener() {
        super.lazyClickListener()
    }

    override fun lazyDataChangeListener() {
        super.lazyDataChangeListener()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mViewModel.userProfileEvent.collect { data ->

                    //个人信息
                    userInfoAboutMeAdapter.models = mutableListOf(
                        UserInfoAboutMeAdapter.UserInfoAboutMeModel(
                            list = mutableListOf(
                                UserInfoAboutMeAdapter.UserInfoAboutMeModel.UserInfoKeyValue(
                                    "UID：",
                                    data.displayId,
                                    canCopy = true
                                ),
                                UserInfoAboutMeAdapter.UserInfoAboutMeModel.UserInfoKeyValue(
                                    "出生日期：",
                                    data.birthday
                                ),
                                UserInfoAboutMeAdapter.UserInfoAboutMeModel.UserInfoKeyValue(
                                    "个性签名：",
                                    data.getActualPersonalSignature()
                                )
                            ),
                            userInfo = data
                        )
                    )

                    //坐驾
                    userInfoCarAdapter.models = mutableListOf(
                        UserInfoCarAdapter.UserInfoCarModel(data = data)

                    )
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mViewModel.giftListEvent.collect { data ->
                    val result = data.chunked(4)

                    val list = mutableListOf<UserInfoGiftWallAdapter.UserInfoGiftWallContentModel>()

                    result.forEach {
                        list.add(UserInfoGiftWallAdapter.UserInfoGiftWallContentModel(it))
                    }

                    userInfoGiftTitleAdapter.models =
                        mutableListOf(UserInfoGiftTitleAdapter.UserInfoGiftWallTitleModel())
                    userInfoGiftWallAdapter.models = list
                }
            }
        }
    }
}