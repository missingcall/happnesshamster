package com.kissspace.mine.ui.fragment

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.ColorUtil
import com.kissspace.common.util.copyClip
import com.kissspace.common.util.getH5Url
import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.CommonHintDialog
import com.kissspace.mine.viewmodel.FamilyViewModel
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.FragmentMineNewBinding
import com.kissspace.util.logE
import com.kissspace.util.orFalse
import com.umeng.socialize.utils.CommonUtil
import kotlin.random.Random
import kotlin.time.toDuration


class MineFragment : BaseFragment(R.layout.fragment_mine_new) {

    private val mBinding by viewBinding<FragmentMineNewBinding>()

    private val mViewModel by viewModels<MineViewModel>()

    private val familyModel by viewModels<FamilyViewModel>()

    private var player : MediaPlayer? = null

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this
        mViewModel.isShowFirstRecharge.value = MMKVProvider.firstRecharge
        // mBinding.tvHour.text = MMKVProvider.userHour.toString() +"h"
        mBinding.layoutSettings.safeClick {
            jump(RouterPath.PATH_SETTING)
        }
        mBinding.ivEdit.setMarginStatusBar()
        setTextViewStyles(mBinding.tvRecharge)
//        mBinding.lltAuthentication.safeClick {
//            jump(RouterPath.PATH_MINE_AUTH)
//        }
        mBinding.layoutUnionHallInfo.safeClick {
            //判断当前是否已经加入到公会

            familyModel.getSelectFamilyInfo {
                if (it != null) {
                    if (it.userFamilyStatus == Constants.FAMILY_MEMBER || it.userFamilyStatus == Constants.FAMILY_LICENSED_HOMEOWNER || it.userFamilyStatus == Constants.FAMILY_HEADER) {
                        //跳转到公会详情页面
                        jump(RouterPath.PATH_FAMILY_DETAIL, "familyId" to it.familyId.orEmpty())
                    } else {
                        //跳转到申请公会列表
                        jump(RouterPath.PATH_FAMILY_LIST)
                    }
                } else {
                    logE("公会信息查询失败")
                    jump(RouterPath.PATH_FAMILY_LIST)
                }
            }
        }


        mBinding.tvPersonalPage.safeClick {
            mViewModel.userInfo.value?.let {
                jump(RouterPath.PATH_USER_PROFILE, "userId" to (it.userId))
            }
        }

        mBinding.ivCopy.safeClick {
            copyClip(mBinding.tvUserId.text.toString())
        }
        //新人优惠
//        mBinding.clCharge.safeClick {
//            getSelectPayChannelList {
//                val firstChargeDialog = FirstChargeDialog(requireContext())
//                firstChargeDialog.callBack = { payChannelType: String, payProductId: String ->
//                    DRouter.build(IPayProvider::class.java).getService().pay(
//                        payChannelType,
//                        payProductId,
//                        activity as AppCompatActivity
//                    ) { result ->
//                        if (result) {
//                            MMKVProvider.firstRecharge = false
//                            mViewModel.isShowFirstRecharge.value = false
//                        }
//                        firstChargeDialog.dismiss()
//                    }
//                }
//                firstChargeDialog.show()
//                if (it.isNotEmpty()) {
//                    if (it.size > 1) {
//                        firstChargeDialog.setData(
//                            it[0].firstRechargePayProductListResponses,
//                            it[1].firstRechargePayProductListResponses
//                        )
//                    }
//                }
//            }
//        }

        //充值
        mBinding.tvRecharge.safeClick {
            jump(RouterPath.PATH_USER_WALLET_GOLD_RECHARGE)
        }


        mBinding.layoutAccountSafe.safeClick {
            jump(RouterPath.PATH_ACCOUNT)
        }

        mBinding.layoutAboutUs.safeClick {
            jump(RouterPath.PATH_ABOUT_US)
        }

        mBinding.layoutWallet.setOnClickListener {
            jump(RouterPath.PATH_USER_WALLET)
        }
        mBinding.layoutStore.safeClick {
            jump(RouterPath.PATH_MY_LEVEL)
        }

        mBinding.layoutFeedback.safeClick {
            jump(
                RouterPath.PATH_FEEDBACK_TYPE_LIST,
                "showFeedBack" to mViewModel.feedBackNewMessage.value.orFalse()
            )
        }

        mBinding.layoutTaskCenter.safeClick {
           jump(RouterPath.PATH_TASK_CENTER_LIST)
        }


        mBinding.layoutGiftInfo.safeClick {
            jump(RouterPath.PATH_USER_PROFILE, "userId" to MMKVProvider.userId)
        }
        mBinding.ivAvatar.safeClick {
            jump(RouterPath.PATH_USER_PROFILE, "userId" to MMKVProvider.userId)
        }

        mBinding.ivEdit.safeClick {
            jump(RouterPath.PATH_SETTING)
        }

        mBinding.layoutVistor.safeClick {
            jump(RouterPath.PATH_MY_VISITOR)
        }

        mBinding.tvFollow.safeClick {
            jump(RouterPath.PATH_MY_FOLLOW)
        }

        mBinding.tvFollowTip.safeClick {
            jump(RouterPath.PATH_MY_FOLLOW)
        }
//        mBinding.lltFollow.safeClick {
//            jump(RouterPath.PATH_MY_FOLLOW)
//        }

//        mBinding.tvUserId.setOnClickListener {
//            mViewModel.userInfo.value?.let {
//                copyClip(it.beautifulId.ifEmpty { it.displayId })
//            }
//
//        }

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

//        mBinding.conVoice.safeClick {
//            playVoice()
//        }
        mBinding.tvFriend.safeClick {
            jump(RouterPath.PATH_MY_COLLECT)
        }
        mBinding.tvFriendTip.safeClick {
            jump(RouterPath.PATH_MY_COLLECT)
        }

        mBinding.layoutGradeInfo.safeClick {
            jump(RouterPath.PATH_STORE)

        }

//        mBinding.layoutGrade.safeClick {
//            jump(RouterPath.PATH_MY_LEVEL)
//        }

        mBinding.layoutActionCenter.safeClick {
            jump(
                RouterPath.PATH_WEBVIEW,
                "url" to getH5Url(Constants.H5.centerActionUrl, needToken = true),
                "showTitle" to true
            )
        }

       //我的房间
        mBinding.layoutHouseInfo.safeClick {
            jumpRoom(roomType = Constants.ROOM_TYPE_PARTY)
        }

        mBinding.layoutRoom.safeClick {
            jumpRoom(roomType = Constants.ROOM_TYPE_PARTY)
        }

        mBinding.layoutCustomerService.safeClick {
            CommonHintDialog.newInstance(
                "若有问题请关注微信公众号",
                MMKVProvider.wechatPublicAccount
            ).show(childFragmentManager)
        }

//        initCircleAnim(mBinding.conFans)
//        initCircleAnim(mBinding.conHour)
//        initCircleAnim(mBinding.conLike)
    }

    private fun initCircleAnim(view: ConstraintLayout) {
        var layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        val circleAngle = layoutParams.circleAngle
        val valueAnimator = ValueAnimator.ofFloat(circleAngle,  circleAngle - 20)
        valueAnimator.addUpdateListener {
            if (isAdded){
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
            if (player == null){
                player = MediaPlayer()
                val openFd = requireContext().assets.openFd("mine_2098.mp3")
                player?.let{
                    it.setDataSource(openFd.fileDescriptor,openFd.startOffset,openFd.length)
                    it.prepare()
                    it.start()
                }
            }else if (player!!.isPlaying){
                player?.let {
                    it.stop()
                    it.prepare()
                    it.start()
                }
            }else{
                player?.start()
            }
        }catch (e:Exception){

        }
    }


    override fun onResume() {
        super.onResume()
        refreshUserinfo()
        mViewModel.isShowFirstRecharge.value = MMKVProvider.firstRecharge
        //获取我的页面新消息状态
        mViewModel.queryNewMessageStatus {
            mBinding.layoutFeedback.showMessageTips(it.familyMessage != null && it.familyMessage != 0)
            mBinding.layoutTaskCenter.showMessageTips(it.taskCenterMessage != null && it.taskCenterMessage != 0)
            mBinding.layoutFeedback.showMessageTips(  it.feedbackMessage != null && it.feedbackMessage != 0)
            mBinding.layoutActionCenter.showMessageTips(  it.activityCenterMessage != null && it.activityCenterMessage != 0)
        }

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

}