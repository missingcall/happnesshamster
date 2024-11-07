package com.hamster.happyness.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.didi.drouter.annotation.Router
import com.drake.brv.sample.model.NestedListModel
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.hamster.happyness.R
import com.hamster.happyness.databinding.ActivityOrchardDailyRewardsBinding
import com.hamster.happyness.databinding.ItemNestedHorizontalRvBinding
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.countDown
import com.kissspace.mine.ui.fragment.WareHouseFragment
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.util.immersiveStatusBar
import com.kissspace.util.jsonDecoder
import com.kissspace.util.loadImage
import com.kissspace.util.logE
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import kotlinx.coroutines.Job
import kotlinx.serialization.json.decodeFromStream
import java.util.*

@Router(path = RouterPath.PATH_ORCHARD_DAILY_REWARDS)
class OrchardDailyRewardsActivity : BaseActivity(R.layout.activity_orchard_daily_rewards) {

    private val mBinding by dataBinding<ActivityOrchardDailyRewardsBinding>()
    private val mViewModel by viewModels<WalletViewModel>()
    var mCountDown: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.titleBar.setMarginStatusBar()
        setTitleBarListener(mBinding.titleBar)

        mBinding.m = mViewModel
        val item = mViewModel.queryMarketItem.value

        mBinding.rv.setup {
            addType<NestedListModel>(R.layout.item_nested_horizontal_rv)
            onCreate {
                getBinding<ItemNestedHorizontalRvBinding>().rv.setup {
                    addType<String>(R.layout.item_simple_horizontal)
                }
            }

            onBind {
                val model = getModel<NestedListModel>()
                getBinding<ItemNestedHorizontalRvBinding>().rv.models = model.list
            }
        }

        initData()

        mBinding.btn.safeClick {
            //TODO 领取成功 刷新数据+刷新button文字 开启计时器
            /*mBinding.rv.mutable.clear()
            mBinding.rv.bindingAdapter.notifyDataSetChanged()
            mBinding.rv.addModels(it)*/


            mCountDown = countDown(
                TimeUtils.getTimeSpanByNow(getTomorrowMills(), TimeConstants.SEC),
                60000,
                reverse = false,
                scope = lifecycleScope,
                onTick = {
                    mBinding.btn.text = "距离明日可领取还剩" + TimeUtils.getFitTimeSpanByNow(getTomorrowMills(), 3)
                    logE("mBinding.btn.text$it")
                },
                onFinish = {
                    finishCountDown()
                })

//            mBinding.btn.text = "距离明日可领取还剩" + TimeUtils.getFitTimeSpanByNow(getTomorrowMills(), 3)
            mBinding.btn.isEnabled = false

            showGiftDialog()
        }


    }

    private fun showGiftDialog() {
        //已修改为activity 省略 ×默认的View方式无法显示在DialogFragment上方 所以此处修改为window方式FLOATING_ACTIVITY
        CustomDialog.build().setDialogImplMode(DialogX.IMPL_MODE.VIEW).apply {
            maskColor = Color.parseColor("#6D000000")
            setCustomView(object :
                OnBindView<CustomDialog>(R.layout.dialog_custom_orchard_daily_rewards) {
                override fun onBind(dialog: CustomDialog?, v: View?) {
                    v?.findViewById<ImageView>(com.kissspace.module_message.R.id.iv_close)
                        ?.setOnClickListener {
                            dialog?.dismiss()
                        }

                    v?.findViewById<ImageView>(com.kissspace.module_message.R.id.iv_gift)?.loadImage(R.mipmap.icon_party_glory_gift_box)

                }
            })
        }.show()
    }

    private fun initData() {
        mBinding.rv.models = jsonDecoder.decodeFromStream<List<NestedListModel>>(resources.openRawResource(R.raw.list_nested))
    }

    private fun getTomorrowMills(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)

        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

    private fun finishCountDown() {
        mBinding.btn.text = "立即领取"
        mCountDown?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDown?.cancel()
    }

}