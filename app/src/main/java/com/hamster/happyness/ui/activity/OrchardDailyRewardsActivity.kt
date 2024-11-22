package com.hamster.happyness.ui.activity

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.TimeUtils
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.hamster.happyness.R
import com.hamster.happyness.databinding.ActivityOrchardDailyRewardsBinding
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.model.FindPropReceiveListItem
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.parseIntent
import com.kissspace.common.util.countDown
import com.kissspace.common.util.customToast
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.databinding.RvItemFindPropHorizontalBinding
import com.kissspace.util.*
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.luck.picture.lib.utils.BitmapUtils
import kotlinx.coroutines.Job
import java.util.*

@Router(path = RouterPath.PATH_ORCHARD_DAILY_REWARDS)
class OrchardDailyRewardsActivity : BaseActivity(R.layout.activity_orchard_daily_rewards) {
    private val propId by parseIntent<String>()
    private val timeLimit by parseIntent<String>()
    private val dayIncome by parseIntent<String>()
    private val mBinding by dataBinding<ActivityOrchardDailyRewardsBinding>()
    private val mViewModel by viewModels<WalletViewModel>()
    var mCountDown: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mBinding.titleBar.setMarginStatusBar()
        setTitleBarListener(mBinding.titleBar)

        mBinding.m = mViewModel
        val item = mViewModel.queryMarketItem.value

        mBinding.tvTop.text =
            SpanUtils().append("周期：${timeLimit}天\n日产：${dayIncome}").appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_little).create()

        val originalBitmap: Bitmap = BitmapFactory.decodeResource(resources, com.kissspace.module_common.R.mipmap.common_app_logo)
        mBinding.ivIcon.setImageBitmap(ImageUtils.skew(originalBitmap, -0.25f, 0.1f))

        val typeface = Typeface.createFromAsset(assets, "fonts/AlimamaShuHeiTi-Bold.ttf")
        mBinding.tvTop.typeface = typeface
        mBinding.tvTitle.typeface = typeface


        mBinding.rv.setup {
            addType<FindPropReceiveListItem>(com.kissspace.module_mine.R.layout.rv_item_find_prop_horizontal)
            onCreate {
                getBinding<RvItemFindPropHorizontalBinding>().rv.setup {
                    addType<FindPropReceiveListItem.Item>(com.kissspace.module_mine.R.layout.rv_item_find_prop)

                }
            }

            onBind {
                val model = getModel<FindPropReceiveListItem>()

                getBinding<RvItemFindPropHorizontalBinding>().rv.models = model.list
                getBinding<RvItemFindPropHorizontalBinding>().tvStatus.safeClick {
                    //领取
                    showGiftDialog(model)

                }

                //空指针 先注释
//                findViewById<TextView>(R.id.tvStatus).typeface = typeface
            }
        }

        initData()

        /* mBinding.btn.safeClick {
             //TODO 领取成功 刷新数据+刷新button文字 开启计时器
             *//*mBinding.rv.mutable.clear()
            mBinding.rv.bindingAdapter.notifyDataSetChanged()
            mBinding.rv.addModels(it)*//*


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
        }*/


    }


    //receiveContent	当日可领取内容json
    private fun showGiftDialog(item: FindPropReceiveListItem) {
        val mutableList = fromJson<MutableList<FindPropReceiveListItem.Item>>(item.receiveContent)
        mViewModel.receivePineCone(item.commodityDay, propId, onSuccess = {

            //已修改为activity 省略 ×默认的View方式无法显示在DialogFragment上方 所以此处修改为window方式FLOATING_ACTIVITY
            CustomDialog.build().setDialogImplMode(DialogX.IMPL_MODE.VIEW).apply {
                maskColor = Color.parseColor("#6D000000")
                setCustomView(object :
                    OnBindView<CustomDialog>(com.kissspace.module_mine.R.layout.dialog_custom_orchard_daily_rewards) {
                    override fun onBind(dialog: CustomDialog?, v: View?) {
                        v?.findViewById<ImageView>(com.kissspace.module_message.R.id.iv_close)
                            ?.setOnClickListener {
                                dialog?.dismiss()
                            }

                        v?.findViewById<RecyclerView>(R.id.rv)?.linear()?.setup {
                            addType<FindPropReceiveListItem.Item>(com.kissspace.module_mine.R.layout.rv_item_find_prop)
                        }?.models = mutableList

                    }
                })
            }.show()

            //刷新列表状态
            initData()

            //TODO 取消当前红点状态



        }, onError = {
            customToast(it?.message)
        })

    }

    private fun initData() {
        mViewModel.findPropReceiveList(propId) {
            mBinding.rv.models = it
        }
        //测试数据 嵌套列表
//        mBinding.rv.models = jsonDecoder.decodeFromStream<List<NestedListModel>>(resources.openRawResource(R.raw.list_nested))


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