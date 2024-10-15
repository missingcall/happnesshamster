package com.kissspace.mine.ui.activity.point

import android.graphics.Color
import android.os.Bundle
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.kissspace.common.base.BaseCommonRvActivity
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.http.BasePageResponse
import com.kissspace.common.router.RouterPath
import com.kissspace.mine.bean.RecordByPoint
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.PointViewModel
import com.kissspace.module_mine.R


@Router(uri = RouterPath.PATH_POINT_RECORD)
class PointExchangeRecordActivity : BaseCommonRvActivity<PointViewModel>(){
    override fun loadData(page: Int, size: Int) {
        scopeNetLife {
            val info = Get<BasePageResponse<RecordByPoint>>(MineApi.API_POINT_RECORD) {
                //状态（1.进行中2.已完成3.已撤销）
                param(
                    "pageNum" , page,
                )
                param(
                    "pageSize" , size,
                )
            }.await()
            info.list?.let { setListData(it) }
        }
    }

    override fun isStatusBarHide(): Boolean {
        return true
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewBinding.titleBar.title = "兑换记录"
        mViewBinding.titleBar.setMarginStatusBar()
        mViewBinding.recyclerView.linear().setup {
            addType<RecordByPoint> { R.layout.rv_record_by_point  }
        }
    }
}