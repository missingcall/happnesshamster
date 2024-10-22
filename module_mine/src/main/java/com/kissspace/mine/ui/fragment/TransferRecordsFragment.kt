package com.kissspace.mine.ui.fragment

import android.os.Bundle
import android.text.format.DateFormat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.github.gzuliyujiang.calendarpicker.CalendarPicker
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.*
import com.kissspace.common.model.wallet.CollectRecordModel
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentTransferRecordsBinding

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
    private lateinit var type: String
    private var mStartTimeLong: Long = 0
    private var mStartTime: String? = ""
    private var mEndTime: String? = ""

    companion object {
        fun newInstance(type: String) = TransferRecordsFragment().apply {
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

    private fun selectData() {
        val picker = CalendarPicker(activity)
        //默认前后一年
        //                picker.setRangeDate(TimeUtils.string2Date(ApiConstant.DATE_START_TIME) , TimeUtils.string2Date(ApiConstant.DATE_END_TIME));
        //默认前后一年
        //                picker.setRangeDate(TimeUtils.string2Date(ApiConstant.DATE_START_TIME) , TimeUtils.string2Date(ApiConstant.DATE_END_TIME));
        if (mStartTimeLong == 0L) {
            mStartTimeLong = System.currentTimeMillis()
        }
        picker.setSelectedDate(mStartTimeLong)
        picker.setOnSingleDatePickListener { }
        picker.setOnRangeDatePickListener { startDate, endDate ->
            val dateFormat = DateFormat()
            mStartTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", startDate).toString()
            mEndTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", endDate).toString()
            mBinding.btnDateStart.text = mStartTime
            mBinding.btnDateEnd.text = (mEndTime)
        }
        picker.show()
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.linear().setup {
            addType<CollectRecordModel.Record> { R.layout.mine_layout_transfer_records_item }
            onBind {

            }
        }.models = mutableListOf()
    }

    private fun initData() {
        mViewModel.queryCollectRecordList(mStartTime, mEndTime, mBinding.pageRefreshLayout.index, 20, onSuccess = {
            mBinding.pageRefreshLayout.addData(it?.records, isEmpty = {
                it?.records!!.isEmpty()
            })
        })
    }
}