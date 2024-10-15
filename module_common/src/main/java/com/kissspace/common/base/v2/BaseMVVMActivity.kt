package com.kissspace.common.base.v2

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType

abstract class BaseMVVMActivity<VM: BaseViewModel,VB:ViewBinding>(inflateFunc: (LayoutInflater) -> VB) :
    BaseVBActivity<VB>(inflateFunc) {

    protected val mViewModel:VM by lazy { initViewModel() }

    override fun initSystem(savedInstanceState: Bundle?) {
        super.initSystem(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                mViewModel.mUILoadModel.collect{
                    if(it) showLoading() else hideLoading()
                }
            }
        }
    }

     private fun initViewModel(): VM {
        if (this.javaClass.genericSuperclass is ParameterizedType && (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.isNotEmpty()
        ) {
            val mPresenterClass = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
            return ViewModelProvider(this)[mPresenterClass]
        }
        throw RuntimeException("VM 获取异常")
     }
}