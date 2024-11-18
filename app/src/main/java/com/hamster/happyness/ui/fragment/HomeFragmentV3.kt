package com.hamster.happyness.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.layoutmanager.HoverGridLayoutManager
import com.drake.brv.layoutmanager.HoverLinearLayoutManager
import com.drake.brv.utils.*
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentMainHomeV3Binding
import com.hamster.happyness.http.Api
import com.hamster.happyness.viewmodel.GameQuickEnterModel
import com.hamster.happyness.viewmodel.HamsterViewModel
import com.hamster.happyness.widget.*

import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.model.wallet.HmsInfoModel
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.loadImage
import com.kissspace.util.logD
import kotlinx.coroutines.Job

//探索
class HomeFragmentV3 : BaseFragment(R.layout.fragment_main_home_v3) {

    private val mBinding by viewBinding<FragmentMainHomeV3Binding>()
    private val mMineViewModel by activityViewModels<MineViewModel>()
    private val mWalletViewModel by activityViewModels<WalletViewModel>()
    private val mHamsterViewModel by activityViewModels<HamsterViewModel>()

    private var mCountDown: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.setMarginStatusBar()
        mBinding.mvm = mMineViewModel
        mBinding.wvm = mWalletViewModel
        mBinding.lifecycleOwner = activity

        initRecyclerView()
        initData()
        refreshUserinfo()
        queryDayIncome()
        getHamsterStatus(true)
        getCurrentHamsterSkin()

        mBinding.ivAvatar.safeClick {
            //TODO 需要替换为可滑动 抽屉式
            ChangeAccountDialog.newInstance().show(childFragmentManager)
        }

        mBinding.ivCopy.safeClick {
            copyClip(mBinding.tvUserId.text.toString())
        }

        //产能说明
        /*    mBinding.ivDailyComeDescription.safeClick {
                CapacityDescriptionDialog.newInstance().show(childFragmentManager)
            }*/

        //养成说明
        mBinding.clBalanceDescription.safeClick {
            DevelopmentDescriptionDialog.newInstance().show(childFragmentManager)
        }

        //皮肤
        mBinding.clSkin.safeClick {
            jump(RouterPath.PATH_HAMSTER_SKIN)
        }

        //皮肤
        mBinding.clQuest.safeClick {
            jump(RouterPath.PATH_TASK_CENTER_LIST)
        }


        //设置字体
        mBinding.tvCleanliness.typeface = Typeface.createFromAsset(activity?.assets, "fonts/AlimamaShuHeiTi-Bold.ttf")
        mBinding.tvSatiety.typeface = Typeface.createFromAsset(activity?.assets, "fonts/AlimamaShuHeiTi-Bold.ttf")
    }


    override fun createDataObserver() {
        super.createDataObserver()

        //喂食,清洗仓鼠事件
        FlowBus.observerEvent<Event.HamsterFeedingOrCleaningEvent>(this) {
            getHamsterStatus(false)
        }

        //复活仓鼠事件
        FlowBus.observerEvent<Event.HamsterReviveEvent>(this) {
            getHamsterStatus(false)
        }

        //当前皮肤设置成功
        FlowBus.observerEvent<Event.WearSkinEvent>(this) {
            getCurrentHamsterSkin()
        }

        FlowBus.observerEvent<Event.RefreshCoin>(this) {
            getMoney()
        }

        FlowBus.observerEvent<Event.CommunicateEvent>(this) {
            mHamsterViewModel.communicate {
                if (it.isNullOrBlank()) {
                    mBinding.tvCommunicate.text = it
                    mBinding.tvCommunicate.visibility = View.VISIBLE
                    //倒计时10秒
                    mCountDown = countDown(10, reverse = false, scope = lifecycleScope, onTick = {
                    }, onFinish = {
                        mBinding.tvCommunicate.visibility = View.GONE
                        mCountDown?.cancel()
                    })
                }
            }
        }

        FlowBus.observerEvent<Event.RefreshChangeAccountEvent>(this) {
            initRecyclerView()
            initData()
            refreshUserinfo()
            queryDayIncome()
            getHamsterStatus(true)
            getCurrentHamsterSkin()
        }
    }

    override fun onResume() {
        super.onResume()
        getMoney()
    }

    private fun getMoney() {
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
//            mBinding.tvDailyComeNum.text = it.toString()
        })
    }

    //获取松鼠状态
    private fun getHamsterStatus(isFirst: Boolean) {

        mWalletViewModel.hmsInfo(onSuccess = {
            changeHamsterUIStatus()

        }, onError = {
            if (!isFirst) {
                customToast(it?.errorMsg)
            }
            logD("errCode : " + it?.errCode + " , errMSg : " + it?.errorMsg)
            if (it?.errCode == "54001" || it?.errCode == "1000" || it?.errCode == "503") {
                //仓鼠不存在或者请求失败时默认为004过期状态
                mWalletViewModel.hmsInfoModel.set(HmsInfoModel())
                changeHamsterUIStatus()
            }
        })

    }


    private fun changeHamsterUIStatus() {
        when (mWalletViewModel.hmsInfoModel.get()?.hamsterStatus) {
            //（001 正常 002 濒死 003 已死亡 004 已到期）
            Constants.HamsterStatusType.NORMAL, Constants.HamsterStatusType.NEAR_DEATH -> {
                //右边栏
                mBinding.clBalanceDescription.visibility = View.VISIBLE
                mBinding.clSkin.visibility = View.VISIBLE
                mBinding.clQuest.visibility = View.VISIBLE
                //蒙版锁
                mBinding.nivHamsterMask.visibility = View.GONE
                mBinding.ivLock.visibility = View.GONE
                //清洁喂食
                mBinding.clHomeCleanliness.visibility = View.VISIBLE
                mBinding.clHomeSatiety.visibility = View.VISIBLE
                //购买复活
                mBinding.btnPurchaseOrRevive.visibility = View.GONE

                //清洁弹窗
                mBinding.clHomeCleanliness.safeClick {
                    HomeCleanDialog.newInstance().show(childFragmentManager)
                }

                //喂食弹窗
                mBinding.clHomeSatiety.safeClick {
                    HomeFeedDialog.newInstance().show(childFragmentManager)
                }

                //点击领取松果
                mBinding.ivHamsterDevelopment.safeClick {
                    mHamsterViewModel.click {
                        if (it) {
                            customToast("领取成功")
                            FlowBus.post(Event.RefreshCoin)
                        }
                    }
                }

            }
            Constants.HamsterStatusType.DEAD -> {
                //右边栏
                mBinding.clBalanceDescription.visibility = View.VISIBLE
                mBinding.clSkin.visibility = View.VISIBLE
                mBinding.clQuest.visibility = View.VISIBLE
                //蒙版锁
                mBinding.nivHamsterMask.visibility = View.GONE
                mBinding.ivLock.visibility = View.GONE
                //清洁喂食
                mBinding.clHomeCleanliness.visibility = View.GONE
                mBinding.clHomeSatiety.visibility = View.GONE
                //购买复活
                mBinding.btnPurchaseOrRevive.visibility = View.VISIBLE
                mBinding.btnPurchaseOrRevive.setBackgroundResource(R.mipmap.app_icon_home_hamster_revive)

                mBinding.btnPurchaseOrRevive.safeClick {
                    HomeRebornDialog.newInstance().show(childFragmentManager)
                }
            }
            Constants.HamsterStatusType.EXPIRED -> {
                //仓鼠不存在或仓鼠已过期 隐藏右边栏/展示蒙版/展示锁

                //右边栏
                mBinding.clBalanceDescription.visibility = View.GONE
                mBinding.clSkin.visibility = View.GONE
//                mBinding.clSkin.visibility = View.VISIBLE //测试用
                mBinding.clQuest.visibility = View.GONE
                //蒙版锁
                mBinding.nivHamsterMask.visibility = View.VISIBLE
                mBinding.ivLock.visibility = View.VISIBLE
                //清洁喂食
                mBinding.clHomeCleanliness.visibility = View.GONE
                mBinding.clHomeSatiety.visibility = View.GONE
                //购买复活
                mBinding.btnPurchaseOrRevive.visibility = View.VISIBLE
                mBinding.btnPurchaseOrRevive.setBackgroundResource(R.mipmap.app_icon_home_hamster_purchase)

                mBinding.btnPurchaseOrRevive.safeClick {
                    //切换首页底下tab
                    FlowBus.post(Event.HamsterPurchaseEvent)
                }
            }

        }

    }

    private fun getCurrentHamsterSkin() {
        //设置仓鼠当前皮肤
        mHamsterViewModel.currentlyUseSkin {
            mBinding.ivHamsterDevelopment.loadImage(it.icon, R.mipmap.app_icon_hamster)
        }
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
                "财神驾到"
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

            var list: MutableList<GameQuickEnterModel.Game> = mutableListOf()
            list.add(game1)
            list.add(game2)
            list.add(game3)
            list.add(game4)

/*            var game5: GameQuickEnterModel.Game = GameQuickEnterModel.Game(
                gameId = "5",
                "https://fastly.picsum.photos/id/668/200/200.jpg?hmac=mVqr1fc4nHFre2QMZp5cuqUKLIRSafUtWt2vwlA9jG0",
                "大逃杀4"
            )
            list.add(game5)*/

            mBinding.recyclerView.addModels(list)
            if (list.size <= 4) {
                mBinding.recyclerView.layoutManager = HoverGridLayoutManager(context, 4, RecyclerView.VERTICAL, false).apply {
                    setScrollEnabled(false)
                    this.stackFromEnd = stackFromEnd
                }

            } else {
                mBinding.recyclerView.layoutManager = HoverLinearLayoutManager(context, RecyclerView.HORIZONTAL, false).apply {
                    setScrollEnabled(true)
                    this.stackFromEnd = stackFromEnd
                }
            }
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

    override fun onStop() {
        super.onStop()
        mCountDown?.cancel()

    }
}