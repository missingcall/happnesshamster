package com.kissspace.mine.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.*
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.customToast
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.mine.widget.OrchardPurchaseDialog
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWarehouseBinding
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback
import com.kongzue.dialogx.interfaces.OnBindView

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 仓库-果园/银行
 *
 */
class WareHouseFragment : BaseFragment(R.layout.mine_fragment_warehouse) {
    private val mBinding by viewBinding<MineFragmentWarehouseBinding>()
    private val mViewModel by activityViewModels<WalletViewModel>()
    private lateinit var type: String
    val rewardList: MutableList<FindPropReceiveListItem.Item> = mutableListOf()

    companion object {
        fun newInstance(type: String) = WareHouseFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")!!
    }

    override fun initView(savedInstanceState: Bundle?) {
        initData()
        mBinding.pageRefreshLayout.apply {
            setEnableLoadMore(false)
            onRefresh {
                initData()
            }

            /* onLoadMore {
                 initData()
             }*/
        }
        initRecyclerView()
    }

    //v1.0.0 ui mine_layout_warehouse_item
    private fun initRecyclerView() {
        if (type == Constants.HamsterFirmCommodityTypes.BASIC_HAMSTER) {
            mBinding.recyclerView.grid(2).setup {
                addType<QueryMarketListItem> { R.layout.rv_item_firm_orchard }
                onBind {
                    val model = getModel<QueryMarketListItem>()
                    findView<Button>(R.id.btn).safeClick {
                        mViewModel.queryMarketItem.value = model
                        when (model.goodsStatue) {
                            Constants.HamsterGoodsStatusType.ALREADY_OWNED_PENDING_ACTIVATION -> {
                                //激活
                                OrchardPurchaseDialog.newInstance(2, model.commodityType == "001").show(childFragmentManager)
                            }
                            Constants.HamsterGoodsStatusType.ALREADY_OWNED_IN_EFFECT -> {
                                //领取
                                jump(
                                    RouterPath.PATH_ORCHARD_DAILY_REWARDS,
                                    "propId" to model.propId,
                                    "timeLimit" to model.timeLimit.toString(),
                                    "dayIncome" to model.dayIncome.toString()
                                )
                            }
                        }
                    }
                }
            }.models = mutableListOf()
        } else {
            mBinding.recyclerView.linear().setup {
                addType<QueryMarketListItem> { R.layout.mine_layout_warehouse_item }
                onBind {
                    findView<Button>(R.id.btn).safeClick {
                        val model = getModel<QueryMarketListItem>()
                        val item: FindPropReceiveListItem.Item = FindPropReceiveListItem.Item("", "", "松果", model.totalIncome, "001")
                        val rewardList: MutableList<FindPropReceiveListItem.Item> = mutableListOf()
                        rewardList.add(item)
                        mViewModel.receivePassbook(model.propId, onSuccess = {
                            //已修改为activity 省略 ×默认的View方式无法显示在DialogFragment上方 所以此处修改为window方式FLOATING_ACTIVITY
                            val customDialog = CustomDialog.build().setDialogImplMode(DialogX.IMPL_MODE.VIEW).apply {
                                maskColor = Color.parseColor("#6D000000")
                                setCustomView(object :
                                    OnBindView<CustomDialog>(R.layout.dialog_custom_orchard_daily_rewards) {
                                    override fun onBind(dialog: CustomDialog?, v: View?) {
                                        v?.findViewById<ImageView>(R.id.iv_close)
                                            ?.setOnClickListener {
                                                dialog?.dismiss()
                                            }

                                        v?.findViewById<RecyclerView>(R.id.rv)?.linear()?.setup {
                                            addType<FindPropReceiveListItem.Item>(com.kissspace.module_mine.R.layout.rv_item_find_prop)
                                        }?.models = rewardList

                                    }
                                })
                            }.show()

                            customDialog.dialogLifecycleCallback = object : DialogLifecycleCallback<CustomDialog>() {
                                override fun onDismiss(dialog: CustomDialog?) {
                                    super.onDismiss(dialog)
                                    LogUtils.d("propId : " + model.propId)
                                    mBinding.pageRefreshLayout.autoRefresh()
                                }
                            }
                        }, onError = {
                            customToast(it?.errorMsg)
                        })
                    }
                }
            }.models = mutableListOf()
        }

    }

    private fun initData() {
        mViewModel.queryMarketList(type, onSuccess = {
            if (type == Constants.HamsterFirmCommodityTypes.BASIC_HAMSTER) {
                //果园待激活和生效中
                mBinding.pageRefreshLayout.addData(
                    it.filter { queryMarketListItem -> queryMarketListItem.goodsStatue == Constants.HamsterGoodsStatusType.ALREADY_OWNED_PENDING_ACTIVATION || queryMarketListItem.goodsStatue == Constants.HamsterGoodsStatusType.ALREADY_OWNED_IN_EFFECT },
                    isEmpty = {
                        it.isEmpty()
                    })
                //银行只有生效中状态
            } else if (type == Constants.HamsterFirmCommodityTypes.BANK) {
                mBinding.pageRefreshLayout.addData(
                    it.filter { queryMarketListItem -> queryMarketListItem.goodsStatue == Constants.HamsterGoodsStatusType.ALREADY_OWNED_IN_EFFECT },
                    isEmpty = {
                        it.isEmpty()
                    })
            }

        })
    }

    override fun createDataObserver() {
        super.createDataObserver()

        //激活成功
        FlowBus.observerEvent<Event.OrchardActivationEvent>(this) {
            mBinding.pageRefreshLayout.autoRefresh()
        }
    }
}