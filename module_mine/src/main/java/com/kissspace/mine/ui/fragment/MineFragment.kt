package com.kissspace.mine.ui.fragment

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setDrawable
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.model.LevelModel
import com.kissspace.common.model.RoomTagListBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.CommonHintDialog
import com.kissspace.mine.viewmodel.FamilyViewModel
import com.kissspace.mine.viewmodel.LevelViewModel
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.FragmentMineNewBinding
import com.kissspace.network.result.ResultState
import com.kissspace.network.result.collectData
import com.kissspace.util.logE
import com.kissspace.util.orFalse
import com.umeng.socialize.utils.CommonUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.random.Random
import kotlin.time.toDuration


class MineFragment : BaseFragment(R.layout.fragment_mine_new) {

    private val _tagListEvent = MutableSharedFlow<ResultState<List<RoomTagListBean>>>()
    val tagListEvent = _tagListEvent.asSharedFlow()

    private val mBinding by viewBinding<FragmentMineNewBinding>()

    private val mViewModel by activityViewModels<MineViewModel>()

    private var player: MediaPlayer? = null

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.setMarginStatusBar()
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this
        mViewModel.isShowFirstRecharge.value = MMKVProvider.firstRecharge
        // mBinding.tvHour.text = MMKVProvider.userHour.toString() +"h"

        initRecyclerView()
        initWallet()

        mBinding.ivCopy.safeClick {
            copyClip(mBinding.tvUserId.text.toString())
        }

        mBinding.ivAvatar.safeClick {
            jump(RouterPath.PATH_USER_PROFILE, "userId" to MMKVProvider.userId)
        }

        mBinding.tvFollow.safeClick {
            jump(RouterPath.PATH_MY_FOLLOW)
        }

        mBinding.tvFollowTip.safeClick {
            jump(RouterPath.PATH_MY_FOLLOW)
        }
        mBinding.tvFans.safeClick {
            jump(RouterPath.PATH_MY_FANS)
        }
        mBinding.tvFansTip.safeClick {
            jump(RouterPath.PATH_MY_FANS)
        }

        mBinding.tvVisitor.safeClick {
            jump(RouterPath.PATH_MY_VISITOR)
        }
        mBinding.tvVisitorTip.safeClick {
            jump(RouterPath.PATH_MY_VISITOR)
        }
        mBinding.tvFriend.safeClick {
            jump(RouterPath.PATH_MY_COLLECT)
        }
        mBinding.tvFriendTip.safeClick {
            jump(RouterPath.PATH_MY_COLLECT)
        }

    }

    private fun initWallet() {
        mBinding.vpWallet.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int) =
                when (position) {
                    0 -> MineWalletFragment.newInstance("001")
                    1 -> MineWalletFragment.newInstance("002")
                    else -> MineWalletFragment.newInstance("003")
                }
        }
        ViewPager2Delegate.install(mBinding.vpWallet, mBinding.dtlWallet)

        //待领取松果
//        queryDayIncome()

/*
        mBinding.btnReceive.safeClick {
            //需判断仓鼠状态

            mViewModel.receivePinecone(onSuccess = {
                //TODO 仓鼠健康则领取成功并播放采集动画
                ToastUtils.showLong("领取成功")
                //刷新钱包数据 包括松果余额(钱包)/转入转出记录/待领取松果
                refreshWallet()
            } , onError = {
                //若仓鼠在濒死中（未喂食进入复活期）
                ToastUtils.showLong("领取失败，请先领养仓鼠")
//                refreshWallet()//测试刷新数据
            })
        }
*/
    }

/*
    private fun refreshWallet() {
        queryDayIncome()
        FlowBus.post(Event.MsgRefreshWalletEvent)
    }

    private fun queryDayIncome() {
        mViewModel.queryDayIncome(onSuccess = {
            mBinding.tvToBeCollectedNum.text = it
        })
    }
*/

    private fun initRecyclerView() {
        val data = mutableListOf<MineInletItem>()
        data.add(MineInletItem(R.mipmap.mine_ic_account_information, "个人信息"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_warehouse, "我的房间"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_level, "我的等级"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_warehouse, "我的仓库"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_task, "任务中心"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_activity, "活动中心"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_dress, "装扮商城"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_invite, "邀请好友"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_blacklist, "黑名单"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_hello, "打招呼"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_feedback, "意见反馈"))
        data.add(MineInletItem(R.mipmap.mine_ic_account_setting, "设置"))

        mBinding.recyclerView.grid(4).setup {
            addType<MineInletItem> { R.layout.mine_item_inlet }
            onBind {
                val clInlet = findView<ConstraintLayout>(R.id.cl_inlet)
                val tvInlet = findView<TextView>(R.id.tv_inlet)
                val ivInlet = findView<ImageView>(R.id.iv_inlet)
                val model = getModel<MineInletItem>()
                tvInlet.text = model.title
                ivInlet.setImageResource(model.icon)
                clInlet.safeClick {
                    when (model.title) {
                        "个人信息" -> jump(RouterPath.PATH_USER_PROFILE, "userId" to MMKVProvider.userId)
                        "我的房间" -> jumpRoom(roomType = Constants.ROOM_TYPE_PARTY)
                        "我的等级" -> jump(RouterPath.PATH_MY_LEVEL)
                        "我的仓库" -> jump(RouterPath.PATH_MY_WAREHOUSE)
                        "任务中心" -> jump(RouterPath.PATH_TASK_CENTER_LIST)
                        "活动中心" -> LogUtils.d("活动中心")
                        "装扮商城" -> jump(RouterPath.PATH_STORE)
                        "邀请好友" -> jump(RouterPath.PATH_INVITE)
                        "黑名单" -> jump(RouterPath.PATH_BLACK_LIST)
                        "打招呼" -> jump(RouterPath.PATH_SAY_HI_SETTING)
                        "意见反馈" -> jump(
                            RouterPath.PATH_FEEDBACK_TYPE_LIST,
                            "showFeedBack" to mViewModel.feedBackNewMessage.value.orFalse()
                        )
                        "设置" -> jump(RouterPath.PATH_SETTING)
                    }
                }


            }
        }.models = data
    }

    private fun initCircleAnim(view: ConstraintLayout) {
        var layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        val circleAngle = layoutParams.circleAngle
        val valueAnimator = ValueAnimator.ofFloat(circleAngle, circleAngle - 20)
        valueAnimator.addUpdateListener {
            if (isAdded) {
                val animatedValue = it.animatedValue as Float
                val layoutParams1 = view.layoutParams as ConstraintLayout.LayoutParams
                layoutParams1.circleAngle = animatedValue
                view.layoutParams = layoutParams1
            }
        }
        valueAnimator.duration = 3000L
        valueAnimator.interpolator = AnticipateOvershootInterpolator()
        valueAnimator.repeatMode = ValueAnimator.REVERSE
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.startDelay = Random.Default.nextLong(1000L)
        valueAnimator.start()
    }

    private fun setTextViewStyles(textView: TextView) {
        ColorUtil.setCommonTextColorWithGradientShade(textView)
    }


    private fun playVoice() {
        try {
            if (player == null) {
                player = MediaPlayer()
                val openFd = requireContext().assets.openFd("mine_2098.mp3")
                player?.let {
                    it.setDataSource(openFd.fileDescriptor, openFd.startOffset, openFd.length)
                    it.prepare()
                    it.start()
                }
            } else if (player!!.isPlaying) {
                player?.let {
                    it.stop()
                    it.prepare()
                    it.start()
                }
            } else {
                player?.start()
            }
        } catch (e: Exception) {

        }
    }


    override fun onResume() {
        super.onResume()
        refreshUserinfo()
        mViewModel.isShowFirstRecharge.value = MMKVProvider.firstRecharge
        //获取我的页面新消息状态
        /*     mViewModel.queryNewMessageStatus {
                 mBinding.layoutFeedback.showMessageTips(it.familyMessage != null && it.familyMessage != 0)
                 mBinding.layoutTaskCenter.showMessageTips(it.taskCenterMessage != null && it.taskCenterMessage != 0)
                 mBinding.layoutFeedback.showMessageTips(it.feedbackMessage != null && it.feedbackMessage != 0)
                 mBinding.layoutActionCenter.showMessageTips(it.activityCenterMessage != null && it.activityCenterMessage != 0)
             }*/

    }

    private fun refreshUserinfo() {
        getUserInfo(onSuccess = {
            mViewModel.userInfo.value = it
        })
    }

    override fun onDestroy() {
        player?.let {
            if (it.isPlaying) {
                it.stop();
            }
            it.release();
        }
        super.onDestroy()
    }

    data class MineInletItem(var icon: Int, var title: String = "") : BaseObservable()

    override fun createDataObserver() {
        super.createDataObserver()

    }
}