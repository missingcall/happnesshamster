package com.kissspace.common.http

import androidx.recyclerview.widget.DiffUtil
import com.drake.brv.PageRefreshLayout

interface IBaseCommonPage {

     fun initPageView() {
        getPageRecycleView().apply {
            onRefresh {
                loadData(true)
            }

            onLoadMore {
                loadData(false)
            }
        }
    }

    fun getPageRecycleView():PageRefreshLayout

    private fun loadData(isRefresh: Boolean){
        loadData(getPageRecycleView().index,getPageSize())
    }

    fun getPageSize():Int{
        return 20
    }

    fun setListData(data:List<Any>?){
        data?.let {
            getPageRecycleView().addData(data, hasMore = {
                data.size == getPageSize()
            }, isEmpty = {
                getPageRecycleView().index == 1 && data.isEmpty()
            })
        }?:run {
            if(getPageRecycleView().index == 1){
                getPageRecycleView().showEmpty()
                getPageRecycleView().finishRefresh()
            }else{
                getPageRecycleView().finishLoadMore()
            }
        }
    }

    abstract fun loadData(page:Int,size:Int)

}