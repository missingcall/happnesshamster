package com.kissspace.common.base.v2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment(@androidx.annotation.LayoutRes val layoutId:Int): Fragment(layoutId)
{
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSystem(savedInstanceState)
        initView(savedInstanceState)
        createDataObserver()
        bindData()
    }

    open fun initSystem(savedInstanceState: Bundle?){

    }
    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun createDataObserver()
    abstract fun bindData()

    open fun getFragmentTag():String{
        return javaClass.simpleName
    }

}