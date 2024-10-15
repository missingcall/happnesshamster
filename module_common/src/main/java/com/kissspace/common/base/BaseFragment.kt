package com.kissspace.common.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kissspace.common.widget.ShowProgressDialog
import com.kissspace.util.isAppDebug
import com.kissspace.util.postRunnable


/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/2 15:14
 * @Description:
 *
 */
abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(isAppDebug)
           Log.e("onAttach", "Fragment:"+this.javaClass.simpleName)
    }

    fun showLoading(context: String = "加载中") {
        if(requireActivity() is BaseActivity){
            (requireActivity() as BaseActivity).showLoading(context)
        }
    }

    fun hideLoading() {
        if(requireActivity() is BaseActivity){
            (requireActivity() as BaseActivity).hideLoading()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        createDataObserver()
        bindData()
    }


    open fun bindData() {

    }
    /**
     * 初始化UI
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 监听数据源
     */
    open fun createDataObserver() {

    }


    override fun onDestroy() {
        super.onDestroy()
    }


}