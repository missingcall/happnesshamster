package com.hamster.happyness.ui.fragment

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.*
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentMainHomeV3Binding
import com.hamster.happyness.http.Api
import com.hamster.happyness.viewmodel.GameQuickEnterModel
import com.hamster.happyness.viewmodel.HomeViewModel

import com.kissspace.common.base.BaseFragment
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.*
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.network.net.Method
import com.kissspace.network.net.request

//探索
class HomeFragmentV3 : BaseFragment(R.layout.fragment_main_home_v3) {

    private val mBinding by viewBinding<FragmentMainHomeV3Binding>()
    private val mHomeViewModel by viewModels<HomeViewModel>()
    private val mMineViewModel by viewModels<MineViewModel>()
    private val mWalletViewModel by viewModels<WalletViewModel>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.setMarginStatusBar()
        mBinding.mvm = mMineViewModel
        mBinding.wvm = mWalletViewModel
        mBinding.lifecycleOwner = this

        initRecyclerView()

        mBinding.ivCopy.safeClick {
            copyClip(mBinding.tvUserId.text.toString())
        }

        //等级说明
        mBinding.ivLevelDescription.safeClick {
            jump(RouterPath.PATH_MY_LEVEL)
        }

        //产能说明
        mBinding.ivDailyComeDescription.safeClick {

        }


    }

    override fun createDataObserver() {
        super.createDataObserver()


    }

    override fun onResume() {
        super.onResume()
        initData()
        refreshUserinfo()
        queryDayIncome()
        initWalletBalance()

    }

    private fun initWalletBalance() {
        mWalletViewModel.getMyMoneyBag {
            it?.let {
                mWalletViewModel.walletModel.value = it
                mBinding.tvPineConeBalance.text = mWalletViewModel.walletModel.value?.diamond.toString()
                mBinding.tvPineNutBalance.text = mWalletViewModel.walletModel.value?.accountBalance.toString()

            }
        }

    }

    private fun refreshUserinfo() {
        getUserInfo(onSuccess = {
            mMineViewModel.userInfo.value = it
        })
    }

    private fun queryDayIncome() {
        mMineViewModel.queryDayIncome(onSuccess = {
            mBinding.tvDailyComeNum.text = it
        })
    }

    private fun initData() {
        //请求首页游戏接口
        val param = mutableMapOf<String, Any?>()
        request<MutableList<GameQuickEnterModel.Game>>(Api.API_GAME_QUICK_ENTER, Method.GET, param, onSuccess = {
            mBinding.recyclerView.mutable.addAll(it)
        }, onError = {

            //TODO 测试数据
            var game1: GameQuickEnterModel.Game = GameQuickEnterModel.Game(
                gameId = "1",
                "https://fastly.picsum.photos/id/668/200/200.jpg?hmac=mVqr1fc4nHFre2QMZp5cuqUKLIRSafUtWt2vwlA9jG0",
                "海盗船"
            )
            var game2: GameQuickEnterModel.Game = GameQuickEnterModel.Game(
                gameId = "2",
                "https://fastly.picsum.photos/id/668/200/200.jpg?hmac=mVqr1fc4nHFre2QMZp5cuqUKLIRSafUtWt2vwlA9jG0",
                "大逃杀1"
            )
            var game3: GameQuickEnterModel.Game = GameQuickEnterModel.Game(
                gameId = "3",
                "https://fastly.picsum.photos/id/668/200/200.jpg?hmac=mVqr1fc4nHFre2QMZp5cuqUKLIRSafUtWt2vwlA9jG0",
                "大逃杀2"
            )
            var game4: GameQuickEnterModel.Game = GameQuickEnterModel.Game(
                gameId = "4",
                "https://fastly.picsum.photos/id/668/200/200.jpg?hmac=mVqr1fc4nHFre2QMZp5cuqUKLIRSafUtWt2vwlA9jG0",
                "大逃杀3"
            )
            var game5: GameQuickEnterModel.Game = GameQuickEnterModel.Game(
                gameId = "5",
                "https://fastly.picsum.photos/id/668/200/200.jpg?hmac=mVqr1fc4nHFre2QMZp5cuqUKLIRSafUtWt2vwlA9jG0",
                "大逃杀4"
            )
            mBinding.recyclerView.mutable.add(game1)
            mBinding.recyclerView.mutable.add(game2)
            mBinding.recyclerView.mutable.add(game3)
            mBinding.recyclerView.mutable.add(game4)
            mBinding.recyclerView.mutable.add(game5)
        })
    }

    /**
     * 首页游戏列表
     */
    private fun initRecyclerView() {
        mBinding.recyclerView.linear(RecyclerView.HORIZONTAL).setup {
            addType<GameQuickEnterModel.Game> { R.layout.rv_item_home_game_quick_entry }
            onBind {
                findView<ConstraintLayout>(R.id.cl_quick_item_bg).safeClick {
                    //TODO 跳转快捷游戏页面
                    customToast("pso" + this.position)
                }
            }
        }.mutable = mutableListOf()
    }

}