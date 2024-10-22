package com.kissspace.mine.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.model.*
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.widget.MarqueeFactory
import com.kissspace.common.widget.SimpleNoticeMF
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentWalletBinding
import kotlin.io.path.Path

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 我的收藏fragment
 *
 */
class MineWalletFragment : BaseFragment(R.layout.mine_fragment_wallet) {
    private val mBinding by viewBinding<MineFragmentWalletBinding>()
    private val mViewModel by viewModels<WalletViewModel>()
    private lateinit var type: String
    private val list = mutableListOf<String>()
    private var marqueeFactory: MarqueeFactory<TextView, String>? = null
//    private val


    companion object {
        fun newInstance(type: String) = MineWalletFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type")!!
        marqueeFactory = SimpleNoticeMF(context)
    }

    override fun initView(savedInstanceState: Bundle?) {
        when (type) {
            "001" -> mBinding.iconNuts.setImageResource(R.mipmap.icon_pine_cone)
            "002" -> mBinding.iconNuts.setImageResource(R.mipmap.icon_pine_nut)
            else -> mBinding.iconNuts.setImageResource(R.mipmap.icon_diamond)
        }

        initData()
    }

    private fun initData() {
        when (type) {
            //松果转入转出查询
            "001" -> {
                mViewModel.queryCollectRecordList("", "", 0, 5, onSuccess = {
                    list.add("松果转入/转出记录")
                    var s = ""
                    for (record in it?.records!!) {
                        when (record.coinType) {
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_ALL -> s = getString(R.string.coinType_all)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_001 -> s = getString(R.string.coinType_001)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_002 -> s = getString(R.string.coinType_002)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_003 -> s = getString(R.string.coinType_003)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_004 -> s = getString(R.string.coinType_004)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_005 -> s = getString(R.string.coinType_005)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_006 -> s = getString(R.string.coinType_006)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_007 -> s = getString(R.string.coinType_007)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_008 -> s = getString(R.string.coinType_008)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_009 -> s = getString(R.string.coinType_009)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_010 -> s = getString(R.string.coinType_010)
                            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_011 -> s = getString(R.string.coinType_011)

                        }
                        list.add(s + " :   " + record.coinNumber)
                    }
                })

            }
            //TODO 松子转入转出查询 最好的情况就是原接口加个参数type 请求到的就是松果/松子/钻石
            "002" -> LogUtils.d("002")

            //TODO 钻石转入转出查询
            else -> LogUtils.d("000")
        }
        mBinding.mtvTransfer.setMarqueeFactory(marqueeFactory)
        marqueeFactory?.setData(list)
        mBinding.mtvTransfer.startFlipping()

        marqueeFactory?.setOnItemClickListener { _, _ ->
            //                ToastUtils.showShort(holder.data)
            //跳转转入转出记录页面
            jump(RouterPath.PATH_TRANSFER_RECORDS)
        }

    }

    override fun onPause() {
        super.onPause()
//        mBinding.mtvTransfer.stopFlipping()
    }

    override fun onResume() {
        super.onResume()
        if (!mBinding.mtvTransfer.isFlipping) {
            mBinding.mtvTransfer.startFlipping()
        }
    }

}