package com.kissspace.mine.ui.fragment

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.TimeUtils
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.github.gzuliyujiang.calendarpicker.CalendarPicker
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.*
import com.kissspace.common.model.wallet.QueryNumChangeRecordModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentTransferRecordsBinding
import com.kissspace.util.logE
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 15:40
 * @Description: 我的收藏fragment
 *
 */
class TransferRecordsFragment : BaseFragment(R.layout.mine_fragment_transfer_records) {
    private val mBinding by viewBinding<MineFragmentTransferRecordsBinding>()
    private val mViewModel by viewModels<WalletViewModel>()
    private lateinit var currency: String
    private lateinit var flowKind: String
    private var mStartTimeLong: Long = 0
    private var mStartTime: String? = ""
    private var mEndTime: String? = ""

    companion object {
        fun newInstance(currency: String, flowKind: String) = TransferRecordsFragment().apply {
            arguments = bundleOf("currency" to currency, "flowKind" to flowKind)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currency = arguments?.getString("currency")!!
        flowKind = arguments?.getString("flowKind")!!
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.pageRefreshLayout.apply {
            onRefresh {
                initData(true)
            }
            onLoadMore {
                initData(false)
            }
        }.refresh()
        initRecyclerView()

        mBinding.btnDateStart.safeClick {
            selectData()
        }

        mBinding.btnDateEnd.safeClick {
            selectData()
        }

        mBinding.btnDateSelect.safeClick {
            mBinding.pageRefreshLayout.autoRefresh()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectData() {


        val picker = CalendarPicker(activity)
        if (mStartTimeLong == 0L) {
            mStartTimeLong = System.currentTimeMillis()
        }
        picker.setSelectedDate(mStartTimeLong, mStartTimeLong)
        picker.setOnRangeDatePickListener { startDate, endDate ->
            //判断开始和结束时间是否是今天,如果是今天 endtime设置成23:59
            if (TimeUtils.isToday(endDate)) {
                //获取当前日期
                val nowDate = LocalDate.now()
                //获取当天开始时间
                val beginTime = LocalDateTime.of(nowDate, LocalTime.MIN)
                //设置当天的结束时间
                val endTime: LocalDateTime = LocalDateTime.of(nowDate, LocalTime.MAX)
                if (TimeUtils.isToday(startDate)) {
                    mStartTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(beginTime).toString()
                } else {
                    mStartTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", startDate).toString()
                }
                mEndTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(endTime).toString()
            } else {
                mStartTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", startDate).toString()
                mEndTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", endDate).toString()
            }
            mBinding.btnDateStart.text = mStartTime
            mBinding.btnDateEnd.text = mEndTime

        }
        picker.show()
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.linear().setup {
            addType<QueryNumChangeRecordModel.Record> { R.layout.mine_layout_transfer_records_item }
            onBind {

            }
        }.models = mutableListOf()
    }


    private fun initData(isRefresh: Boolean) {
        if (isRefresh) {
            mBinding.pageRefreshLayout.index = 1
        }
        mViewModel.queryNumChangeRecord(
            when (currency) {
                Constants.HamsterWalletType.PINE_CONE.type -> "002"
                Constants.HamsterWalletType.PINE_NUT.type -> "004"
                Constants.HamsterWalletType.DIAMONDS.type -> "001"
                else -> "002"
            }, mStartTime, mEndTime,
            when (flowKind) {
                "0" -> null
                "1" -> "001"
                "2" -> "002"
                else -> "null"
            }, mBinding.pageRefreshLayout.index, 50
        ) {
            if (isRefresh) {
                mBinding.recyclerView.bindingAdapter.mutable.clear()
                if (it?.list?.size == 0) {
                    mBinding.pageRefreshLayout.showEmpty()
                } else {
                    mBinding.pageRefreshLayout.addData(
                        it?.list
                    )
                    mBinding.pageRefreshLayout.showContent()
                    //默认选中第一条
                    mBinding.recyclerView.bindingAdapter.setChecked(0, true)
                }
                mBinding.pageRefreshLayout.finishRefresh()
            } else {
                mBinding.pageRefreshLayout.addData(
                    it?.list
                )
                mBinding.pageRefreshLayout.finishLoadMore()
            }
            logE("it.total" + it?.total)
            if (mBinding.recyclerView.bindingAdapter.models?.size == it?.total) {
                mBinding.pageRefreshLayout.finishLoadMoreWithNoMoreData()
            } else {
                mBinding.pageRefreshLayout.setNoMoreData(false)
            }
        }
    }
}