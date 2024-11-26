package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.SpanUtils
import com.hamster.happyness.R
import com.hamster.happyness.databinding.DialogHomeCultivationBinding
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.mine.viewmodel.WalletViewModel

/**
 * 底部弹窗-1:喂养 2.清洁 3.重生
 */
@Deprecated("分开创建")
class HomeCultivationDialog : BaseDialogFragment<DialogHomeCultivationBinding>(DialogHomeCultivationBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()

    //type 1:喂养 2.清洁 3.重生
    private var type: Int? = 3

    companion object {
        fun newInstance(type: Int) = HomeCultivationDialog().apply {
            arguments = bundleOf("type" to type)
        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_home_cultivation
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        arguments?.let {
            type = it.getInt("type")
        }
        mBinding.vm = mViewModel

        when (type) {
            1 -> {
                mBinding.tvTitle.text = "清洗仓鼠"
                mBinding.tvAvailableProp.text = SpanUtils()
                    .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_cleanliness_small)
                    .append(mViewModel.cultivationPanelModel.get()?.cleanliness?.prop.toString())
                    .create()
                mBinding.ivIcon.setImageResource(R.mipmap.app_icon_home_cleanliness_hamster)
                mBinding.tvCurrent.text = "当前" + mViewModel.hmsInfoModel.get()?.cleanliness + "%"
                mBinding.tvTarget.text =
                    "恢复至" + ((mViewModel.cultivationPanelModel.get()?.cleanliness?.promote)?.plus((mViewModel.hmsInfoModel.get()?.cleanliness!!))) + "%"

                mBinding.pbProgress.progress = mViewModel.hmsInfoModel.get()?.cleanliness!!
                mBinding.tvChoose.text = "选择清洗方式"
                mBinding.btnConfirmLeft.text = SpanUtils()
                    .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_cleanliness_small)
                    .append(mViewModel.cultivationPanelModel.get()?.cleanliness?.costPropCount.toString())
                    .create()

                mBinding.btnConfirmRight.text = SpanUtils()
                    .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
                    .append(mViewModel.cultivationPanelModel.get()?.cleanliness?.consumption.toString())//.setForegroundColor(Color.parseColor("#FDC120"))
                    .create()

                mBinding.btnConfirmLeft.safeClick {
                    mViewModel.improveCleanliness(Constants.HamsterCultivatePayType.PROP, onSuccess = {
                        //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                        FlowBus.post(Event.HamsterFeedingOrCleaningEvent)
                    })

                }

                mBinding.btnConfirmRight.safeClick {
                    mViewModel.improveCleanliness(Constants.HamsterCultivatePayType.PINE_CONE, onSuccess = {
                        //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                        FlowBus.post(Event.HamsterFeedingOrCleaningEvent)
                    })

                }
            }

            2 -> {
                mBinding.tvTitle.text = "喂养仓鼠"
                mBinding.tvAvailableProp.text = SpanUtils()
                    .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_satiety_small)
                    .append(mViewModel.cultivationPanelModel.get()?.satiety?.prop.toString())
                    .create()
                mBinding.ivIcon.setImageResource(R.mipmap.app_icon_home_satiety_hamster)
                mBinding.tvCurrent.text = "当前" + mViewModel.hmsInfoModel.get()?.satiety + "%"
                mBinding.tvTarget.text =
                    "恢复至" + ((mViewModel.cultivationPanelModel.get()?.satiety?.promote)?.plus((mViewModel.hmsInfoModel.get()?.satiety!!))) + "%"

                mBinding.pbProgress.progress = mViewModel.hmsInfoModel.get()?.satiety!!
                mBinding.tvChoose.text = "选择喂养方式"
                mBinding.btnConfirmLeft.text = SpanUtils()
                    .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_satiety_small)
                    .append(mViewModel.cultivationPanelModel.get()?.satiety?.costPropCount.toString())
                    .create()

                mBinding.btnConfirmRight.text = SpanUtils()
                    .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
                    .append(mViewModel.cultivationPanelModel.get()?.satiety?.consumption.toString())
                    .create()

                mBinding.btnConfirmLeft.safeClick {
                    mViewModel.improveSatiety(Constants.HamsterCultivatePayType.PROP, onSuccess = {
                        //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                        FlowBus.post(Event.HamsterFeedingOrCleaningEvent)
                    })
                }

                mBinding.btnConfirmRight.safeClick {
                    mViewModel.improveSatiety(Constants.HamsterCultivatePayType.PINE_CONE, onSuccess = {
                        //喂食成功 刷新Dialog进度条,刷新HomeFragment喂食度
                        FlowBus.post(Event.HamsterFeedingOrCleaningEvent)
                    })

                }
            }

            3 -> {
                mBinding.tvTitle.text = "复活仓鼠"
                mBinding.tvAvailableProp.text = SpanUtils()
                    .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_nut_small)
                    .append(mViewModel.walletModel.value?.diamond.toString())
                    .create()
                mBinding.ivIcon.setImageResource(R.mipmap.app_icon_home_reborn_hamster)
                mBinding.tvCurrent.text = "当前0%"
                mBinding.tvTarget.text =
                    "恢复至100%"

                mBinding.pbProgress.progress = 0
                mBinding.tvChoose.text = "选择复活方式"
                mBinding.btnConfirmLeft.text = SpanUtils()
                    .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_nut_small)
                    .append(mViewModel.revivePanelModel.get()?.pineNuts.toString())
                    .create()

                mBinding.btnConfirmRight.text = SpanUtils()
                    .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
                    .append(mViewModel.revivePanelModel.get()?.pineCone.toString())
                    .create()

                mBinding.btnConfirmLeft.safeClick {
                    mViewModel.cultivateRevive(onSuccess = {
                        //复活成功,刷新HomeFragment喂食度
                        FlowBus.post(Event.HamsterReviveEvent)
                    }, payType = Constants.HamsterPayType.PINE_CONE)

                }

                mBinding.btnConfirmRight.safeClick {
                    mViewModel.cultivateRevive(onSuccess = {
                        //复活成功,刷新HomeFragment喂食度
                        FlowBus.post(Event.HamsterReviveEvent)
                    }, payType = Constants.HamsterPayType.PINE_NUT)

                }
            }

            else -> {

            }

        }

        mBinding.tvAvailablePineCones.text = SpanUtils()
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
            .append(mViewModel.walletModel.value?.diamond.toString())
            .create()

        mBinding.ibBack.safeClick {
            dismiss()
        }


        //TODO 需要配置消耗道具字段
        mViewModel.queryCultivationPanel {

        }

    }

    override fun observerData() {
        super.observerData()


    }
}