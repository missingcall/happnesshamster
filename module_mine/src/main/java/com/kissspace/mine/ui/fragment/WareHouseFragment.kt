package com.kissspace.mine.ui.fragment

import android.os.Bundle
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.*
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWarehouseBinding

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 我的收藏fragment
 *
 */
class WareHouseFragment : BaseFragment(R.layout.mine_fragment_warehouse) {
    private val mBinding by viewBinding<MineFragmentWarehouseBinding>()
    private val mViewModel by viewModels<WalletViewModel>()
    private lateinit var type: String

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
                        when (model.goodsStatue) {
                            Constants.HamsterGoodsStatusType.ALREADY_OWNED_PENDING_ACTIVATION -> {
                                //激活
//                                OrchardPurchaseDialog.newInstance(2, model.commodityType == "001").show(childFragmentManager)
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
}