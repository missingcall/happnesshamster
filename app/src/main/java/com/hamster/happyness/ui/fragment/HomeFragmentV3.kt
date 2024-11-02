package com.hamster.happyness.ui.fragment

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.ColorUtils
import com.drake.brv.utils.*
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentMainHomeV3Binding
import com.hamster.happyness.http.Api
import com.hamster.happyness.viewmodel.GameQuickEnterModel
import com.hamster.happyness.viewmodel.HomeViewModel
import com.hamster.happyness.widget.*

import com.kissspace.common.base.BaseFragment
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.*
import com.kissspace.common.util.mmkv.MMKVProvider
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
        initData()
        refreshUserinfo()
        queryDayIncome()
        getHamsterStatus()

        mBinding.ivAvatar.safeClick {
            ChangeAccountDialog.newInstance().show(childFragmentManager)
        }

        mBinding.ivCopy.safeClick {
            copyClip(mBinding.tvUserId.text.toString())
        }

        //产能说明
        mBinding.ivDailyComeDescription.safeClick {
            CapacityDescriptionDialog.newInstance().show(childFragmentManager)
        }

        //养成说明
        mBinding.ivBalanceDescription.safeClick {
            DevelopmentDescriptionDialog.newInstance().show(childFragmentManager)
        }

        //皮肤
        mBinding.clSkin.safeClick {
            jump(RouterPath.PATH_HAMSTER_SKIN)
        }

        //皮肤
        mBinding.clQuest.safeClick {
            jump(RouterPath.)
        }

    }

    override fun createDataObserver() {
        super.createDataObserver()

        //喂食,清洗仓鼠事件
        FlowBus.observerEvent<Event.HamsterFeedingOrCleaningEvent>(this) {
            getHamsterStatus()
        }

        //复活仓鼠事件
        FlowBus.observerEvent<Event.HamsterReviveEvent>(this) {
            getHamsterStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        initWalletBalance()
    }

    private fun initWalletBalance() {
        mWalletViewModel.getMyMoneyBag {
            it?.let {
                mWalletViewModel.walletModel.value = it

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

    //获取松鼠状态
    private fun getHamsterStatus() {

        mWalletViewModel.hmsInfo(onSuccess = {
            when (it?.hamsterStatus) {
                //（001 正常 002 濒死 003 已死亡 004 已到期）
                "001", "002" -> {
                    if (it?.cleanliness!! in 0..29) {
                        mBinding.wlvClean.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_FF1A16)
                    } else if (it.satiety!! in 30..60) {
                        mBinding.wlvClean.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_FF8C5C)
                    } else if (it.satiety!! in 60..90) {
                        mBinding.wlvClean.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_8C28FF)
                    } else {
                        mBinding.wlvClean.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_5A60FF)
                    }

                    if (it?.satiety!! in 0..29) {
                        mBinding.wlvFood.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_FF1A16)
                    } else if (it.satiety!! in 30..60) {
                        mBinding.wlvFood.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_FF8C5C)
                    } else if (it.satiety!! in 60..90) {
                        mBinding.wlvFood.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_8C28FF)
                    } else {
                        mBinding.wlvFood.allColor = ColorUtils.getColor(com.kissspace.module_common.R.color.color_5A60FF)
                    }

                    mBinding.wlvClean.progressValue = it!!.cleanliness
                    mBinding.wlvFood.progressValue = it.satiety

                    mBinding.wlvClean.centerTitle = it.cleanliness.toString() + "%"
                    mBinding.wlvFood.centerTitle = it.satiety.toString() + "%"

                    //喂食弹窗
                    mBinding.ivSatiety.safeClick {
                        HomeFeedDialog.newInstance().show(childFragmentManager)
                    }
                    mBinding.wlvFood.safeClick {
                        HomeFeedDialog.newInstance().show(childFragmentManager)
                    }

                    //清洁弹窗
                    mBinding.ivCleanliness.safeClick {
                        HomeCleanDialog.newInstance().show(childFragmentManager)
                    }
                    mBinding.wlvClean.safeClick {
                        HomeCleanDialog.newInstance().show(childFragmentManager)
                    }
                }
                "003" -> {

                    mBinding.ivSatiety.safeClick {
                        HomeRebornDialog.newInstance().show(childFragmentManager)
                    }
                    mBinding.wlvFood.safeClick {
                        HomeRebornDialog.newInstance().show(childFragmentManager)
                    }
                    mBinding.ivCleanliness.safeClick {
                        HomeRebornDialog.newInstance().show(childFragmentManager)
                    }
                    mBinding.wlvClean.safeClick {
                        HomeRebornDialog.newInstance().show(childFragmentManager)
                    }
                }
                "004" -> {

                }

            }


        }, onError = {
            //TODO 测试 仓鼠不存在 或者请求失败
            mBinding.ivSatiety.safeClick {
                HomeRebornDialog.newInstance().show(childFragmentManager)
            }
            mBinding.wlvFood.safeClick {
                HomeRebornDialog.newInstance().show(childFragmentManager)
            }
            mBinding.ivCleanliness.safeClick {
                HomeRebornDialog.newInstance().show(childFragmentManager)
            }
            mBinding.wlvClean.safeClick {
                HomeRebornDialog.newInstance().show(childFragmentManager)
            }
        })
    }

    private fun initData() {
        //请求首页游戏接口
        val param = mutableMapOf<String, Any?>()
        request<MutableList<GameQuickEnterModel.Game>>(Api.API_GAME_QUICK_ENTER, Method.GET, param, onSuccess = {
            mBinding.recyclerView.addModels(it)
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
            var list: MutableList<GameQuickEnterModel.Game> = mutableListOf()
            list.add(game1)
            list.add(game2)
            list.add(game3)
            list.add(game4)
            list.add(game5)

            mBinding.recyclerView.addModels(list)
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