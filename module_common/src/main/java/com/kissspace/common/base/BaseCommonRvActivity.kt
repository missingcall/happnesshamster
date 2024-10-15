package com.kissspace.common.base

import android.os.Bundle
import com.kissspace.common.base.v2.BaseMVVMActivity
import com.kissspace.common.base.v2.BaseViewModel
import com.kissspace.module_common.databinding.CommonActivityListBinding

abstract class BaseCommonRvActivity<VM: BaseViewModel>: BaseMVVMActivity<VM, CommonActivityListBinding>(CommonActivityListBinding::inflate) {

    override fun initSystem(savedInstanceState: Bundle?) {
        super.initSystem(savedInstanceState)
        mViewBinding.pageRefreshLayout.apply {
            onRefresh {
                loadData(true)
            }

            onLoadMore {
                loadData(false)
            }
        }
    }

    private fun loadData(isRefresh: Boolean){
        loadData(mViewBinding.pageRefreshLayout.index,mViewModel.getLoadSize())
    }


    protected fun setListData(data:List<Any>?){

        data?.let {
            mViewBinding.pageRefreshLayout.addData(data, hasMore = {
                data.size == mViewModel.getLoadSize()
            }, isEmpty = {
                mViewBinding.pageRefreshLayout.index == 1 && data.isEmpty()
            })
        }?:run {
            if(mViewBinding.pageRefreshLayout.index == 1){
                mViewBinding.pageRefreshLayout.showEmpty()
                mViewBinding.pageRefreshLayout.finishRefresh()
            }else{
                mViewBinding.pageRefreshLayout.finishLoadMore()
            }
        }
    }

    abstract fun loadData(page:Int,size:Int)

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun createDataObserver() {
    }

    override fun bindData() {
        mViewBinding.pageRefreshLayout.showLoading()
    }
}