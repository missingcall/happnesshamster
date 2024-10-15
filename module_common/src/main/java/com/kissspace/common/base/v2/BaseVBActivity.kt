package com.kissspace.common.base.v2

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding

abstract class BaseVBActivity<VB:ViewBinding>(inflateFunc: (LayoutInflater) -> VB) : BaseActivity(-1) {

     val mViewBinding by lazy { inflateFunc(layoutInflater) }

     override fun getContentView(): View {
        return mViewBinding.root
     }


}