package com.kissspace.mine.ui.activity

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.ext.canSendMsgToOther
import com.kissspace.common.router.jump
import com.kissspace.common.model.*
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.http.MineApi
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityVisitorBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.uc.crashsdk.export.LogType.addType

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/6 17:03
 * @Description: 萌新大厅
 *
 */
@Router(path = RouterPath.PATH_NEWBIE)
class NewbieActivity : com.kissspace.common.base.BaseActivity(R.layout.mine_activity_visitor) {
    private val mBinding by viewBinding<MineActivityVisitorBinding>()

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.titleBar.title = "萌新大厅"

        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
        mBinding.pageRefreshLayout.apply {
            onRefresh {
                initData(true)
            }
            onLoadMore {
                initData(false)
            }
        }
        initRecyclerView()
        initData(true)
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.linear().setup {
            addType<NewBieBean> { R.layout.mine_layout_newbie_item }
            onClick(R.id.iv_avatar) {
                val model = getModel<NewBieBean>()
                jump(RouterPath.PATH_USER_PROFILE, "userId" to model.userId)
            }
            onClick(R.id.iv_chat) {
                if(canSendMsgToOther()){
                    val model = getModel<NewBieBean>()
                    jump(RouterPath.PATH_CHAT, "account" to model.accid, "userId" to model.userId)
                }
            }
        }.models = mutableListOf()
    }


    private fun initData(isRefresh: Boolean) {
        val param = mutableMapOf<String, Any?>()
        param["pageNum"] = mBinding.pageRefreshLayout.index
        param["pageSize"] = 20
        request<NewBieResponse>(MineApi.API_QUERY_NEWBIE, Method.GET, param, onSuccess = {
            if (isRefresh) {
                mBinding.recyclerView.bindingAdapter.mutable.clear()
            }
            MMKVProvider.currentVisitorCount = it.total
            mBinding.pageRefreshLayout.addData(it.records, hasMore = {
                mBinding.pageRefreshLayout.index * 20 < it.total
            }, isEmpty = {
                it.records.isEmpty()
            })
        })
    }

}