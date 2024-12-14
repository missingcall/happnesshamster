package com.hamster.happyness.ui.fragment

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.hamster.happyness.databinding.FragmentFirmOrchardBankBinding
import com.hamster.happyness.widget.BankPurchaseDialog
import com.kissspace.mine.widget.OrchardPurchaseDialog
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.QueryMarketListItem
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.mine.viewmodel.WalletViewModel

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 商行-果园/银行 页面
 *
 */
class FirmOrchardBankFragment : BaseFragment(com.hamster.happyness.R.layout.fragment_firm_orchard_bank) {
    private val mBinding by viewBinding<FragmentFirmOrchardBankBinding>()
    private val mViewModel by activityViewModels<WalletViewModel>()
    private lateinit var type: String

    companion object {
        fun newInstance(type: String) = FirmOrchardBankFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")!!
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.lifecycleOwner = activity

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

    private fun initRecyclerView() {

        //基础仓鼠&庄园
        if (type == Constants.HamsterFirmCommodityTypes.BASIC_HAMSTER) {
            mBinding.recyclerView.grid(2).setup {
                addType<QueryMarketListItem> { com.kissspace.module_mine.R.layout.rv_item_firm_orchard }
                onBind {
                    val model = getModel<QueryMarketListItem>()
                    val button = findView<Button>(com.hamster.happyness.R.id.btn)
                    button.safeClick {
                        mViewModel.queryMarketItem.value = model
                        //001 商品可购买 002 商品已售磬 003 用户未解锁 004 用户已拥有(待激活) 005 用户已拥有(生效中)
                        when (model.goodsStatue) {
                            "001" -> {
                                OrchardPurchaseDialog.newInstance(1, model.commodityType == "001").show(childFragmentManager)
                                //测试 底部拉起说明详情并展示购买选项
//                            jump(RouterPath.PATH_ORCHARD_DAILY_REWARDS)
                            }
                            "002" -> com.kissspace.common.util.customToast("已售罄")
                            "004" -> {
                                //底部拉起说明详情并展示激活选项
                                OrchardPurchaseDialog.newInstance(2, model.commodityType == "001").show(childFragmentManager)
                            }
                            "005" -> {
                                //点击跳转领取页面 根据propid领取
                                jump(
                                    RouterPath.PATH_ORCHARD_DAILY_REWARDS,
                                    "propId" to model.propId,
                                    "timeLimit" to model.timeLimit.toString(),
                                    "dayIncome" to model.dayIncome.toString()

                                )
                            }
                        }

                    }

                    findView<TextView>(com.kissspace.module_mine.R.id.tv_sold_out).safeClick { com.kissspace.common.util.customToast("已售罄") }


                }
            }.models = mutableListOf()

            //银行
        } else if (type == Constants.HamsterFirmCommodityTypes.BANK) {
            mBinding.recyclerView.grid(2).setup {
                addType<QueryMarketListItem> { com.kissspace.module_mine.R.layout.rv_item_firm_bank }
                onBind {
                    val model = getModel<QueryMarketListItem>()

                    val button = findView<Button>(com.hamster.happyness.R.id.btn)
                    button.safeClick {
                        mViewModel.queryMarketItem.value = model
                        //001 商品可购买 002 商品已售磬 003 用户未解锁 004 用户已拥有(待激活) 005 用户已拥有(生效中)
                        when (model.goodsStatue) {
                            "001" -> BankPurchaseDialog.newInstance().show(childFragmentManager)
                            "002" -> com.kissspace.common.util.customToast("已售罄")

                        }

                    }


                }
            }.models = mutableListOf()

        }
    }

    private fun initData() {
        mViewModel.queryMarketList(type, onSuccess = {
            if (type == Constants.HamsterFirmCommodityTypes.BASIC_HAMSTER) {
                mBinding.pageRefreshLayout.addData(
                    it/*.filter { queryMarketListItem -> queryMarketListItem.goodsStatue == "010"}*/,
                    isEmpty = {
                        it.isEmpty()
                    })
                //银行只有可购买和已售罄两种状态
            } else if (type == Constants.HamsterFirmCommodityTypes.BANK) {
                mBinding.pageRefreshLayout.addData(
                    it.filter { queryMarketListItem -> queryMarketListItem.goodsStatue == "001" || queryMarketListItem.goodsStatue == "002" },
                    isEmpty = {
                        it.isEmpty()
                    })
            }

        })
    }

    override fun createDataObserver() {
        super.createDataObserver()

        FlowBus.observerEvent<Event.OrchardPurchaseEvent>(this) {
            mBinding.pageRefreshLayout.autoRefresh()
        }

        FlowBus.observerEvent<Event.OrchardActivationEvent>(this) {
            mBinding.pageRefreshLayout.autoRefresh()
        }

        FlowBus.observerEvent<Event.RefreshChangeAccountEvent>(this) {
            mBinding.pageRefreshLayout.autoRefresh()
        }

    }
}