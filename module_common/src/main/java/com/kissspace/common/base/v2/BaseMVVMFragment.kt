package com.kissspace.common.base.v2

import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseMVVMFragment<VM: BaseViewModel,VB:ViewBinding>(inflateFunc: (LayoutInflater) -> VB) : BaseVBFragment<VB>(inflateFunc) {

    protected val mViewModel:VM by lazy { initViewModel() }


    private fun initViewModel():VM {
        if (this.javaClass.genericSuperclass is ParameterizedType && (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.isNotEmpty()
        ) {
            val mPresenterClass = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
            return ViewModelProvider(requireActivity())[mPresenterClass as Class<VM>]
            //return ViewModelProvider(this)[mPresenterClass as Class<VM>]
        }
        throw RuntimeException("VM 获取异常")
    }
}