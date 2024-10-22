package com.kissspace.mine.ui.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.CommonApi
import com.kissspace.common.model.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.http.MineApi
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWarehouseBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.toast
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 我的收藏fragment
 *
 */
class WareHouseFragment : BaseFragment(R.layout.mine_fragment_warehouse) {
    private val mBinding by viewBinding<MineFragmentWarehouseBinding>()
    val queryMarketListEvent = MutableSharedFlow<MutableList<QueryMarketListItem>>()
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

    private fun initRecyclerView() {
        mBinding.recyclerView.grid().setup {
            addType<QueryMarketListItem> { R.layout.mine_layout_warehouse_item }
            onBind {

            }
        }.models = mutableListOf()
    }

    private fun initData() {
        val param = mutableMapOf<String, Any?>()
        param["type"] = type
        request<MutableList<QueryMarketListItem>>(MineApi.API_HAMSTER_MARKET_QUERY_MARKET_LIST, Method.GET, param, onSuccess = {

            mBinding.pageRefreshLayout.addData(it, isEmpty = {
                it.isEmpty()
            }
            )

        }, onError = {
        })
    }
}