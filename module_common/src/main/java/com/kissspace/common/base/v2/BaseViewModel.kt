package com.kissspace.common.base.v2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel :ViewModel() {
    fun getLoadSize(): Int {
        return 20
    }

    val mLoadState = MutableStateFlow(false)

    val mUILoadModel: StateFlow<Boolean> = mLoadState

    val mLoadingModel : MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
}