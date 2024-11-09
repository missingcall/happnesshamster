package com.kissspace.mine.ui.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.CommonApi
import com.kissspace.common.model.*
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWalletConversionBinding
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
class WalletConversionFragment : BaseFragment(R.layout.mine_fragment_wallet_conversion) {
    private val mBinding by viewBinding<MineFragmentWalletConversionBinding>()
    private val mViewModel by viewModels<WalletViewModel>()
    private lateinit var type: String

    companion object {
        fun newInstance(type: String) = WalletConversionFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")!!
    }

    override fun initView(savedInstanceState: Bundle?) {


    }

}