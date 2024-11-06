package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.Gravity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.drake.brv.sample.model.NestedListModel
import com.drake.brv.utils.*
import com.hamster.happyness.R
import com.hamster.happyness.databinding.DialogOrchardDailyRewardsBinding
import com.hamster.happyness.databinding.ItemNestedHorizontalRvBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.util.countDown
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.util.jsonDecoder
import com.kissspace.util.logE
import kotlinx.coroutines.Job
import kotlinx.serialization.json.decodeFromStream
import java.util.*

/**
 * 底部弹窗-果园领取每日奖励
 */
class OrchardDailyRewardsDialog : BaseDialogFragment<DialogOrchardDailyRewardsBinding>(DialogOrchardDailyRewardsBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()
    var mCountDown: Job? = null

    companion object {
        fun newInstance() = OrchardDailyRewardsDialog().apply {

        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_orchard_daily_rewards
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {

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

        mBinding.ibBack.safeClick {
            dismiss()
        }

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


        }
    }

    private fun initData() {
        mBinding.rv.models = jsonDecoder.decodeFromStream<List<NestedListModel>>(resources.openRawResource(R.raw.list_nested))
    }

    override fun observerData() {
        super.observerData()


    }

    private fun getTomorrowMills(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        // 改成这样就好了
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mCountDown?.cancel()
    }
}