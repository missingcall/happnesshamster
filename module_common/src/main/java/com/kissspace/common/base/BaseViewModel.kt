package com.kissspace.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/2 15:14
 * @Description:
 *
 */
open class BaseViewModel : ViewModel() {

     val mViewStatus = MutableLiveData<Boolean>()

    open fun showLoading() {
        mViewStatus.postValue(true)
    }

    open fun hideLoading() {
        mViewStatus.postValue(false)
    }
}