package com.hamster.happyness.ui.fragment

import android.os.Bundle
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.SpanUtils
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.hamster.happyness.databinding.FragmentFirmOrchardBankBinding
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.CommonApi
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.MineViewModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWarehouseBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.customToast
import com.kissspace.util.toast
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 我的收藏fragment
 *
 */
class FirmOrchardBankFragment : BaseFragment(com.hamster.happyness.R.layout.fragment_firm_orchard_bank) {
    private val mBinding by viewBinding<FragmentFirmOrchardBankBinding>()
    private val mViewModel by viewModels<WalletViewModel>()
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
        mBinding.recyclerView.grid(2).setup {
            addType<QueryMarketListItem> { com.hamster.happyness.R.layout.rv_item_firm }
            onBind {
                val model = getModel<QueryMarketListItem>()
                val button = findView<Button>(com.hamster.happyness.R.id.btn)
                button.safeClick {
                    //001 商品可购买 002 商品已售磬 003 用户未解锁 004 用户已拥有(待激活) 005 用户已拥有(生效中)
                    when (model.goodsStatue) {
                        "001" -> {
                            //底部拉起说明详情并展示购买选项

                        }
                        "002" -> com.kissspace.common.util.customToast("已售罄")
                        "004" -> {
                            //底部拉起说明详情并展示购买选项

                        }
                        "005" -> {
                            //点击跳转领取页面
                        }
                    }

                }


            }
        }.models = mutableListOf()
    }

    private fun initData() {
        mViewModel.queryMarketList(type, onSuccess = {
            mBinding.pageRefreshLayout.addData(it, isEmpty = {
                it.isEmpty()
            })
        })
    }
}