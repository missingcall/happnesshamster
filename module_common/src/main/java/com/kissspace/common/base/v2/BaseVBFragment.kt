package com.kissspace.common.base.v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BaseVBFragment<VB:ViewBinding>(val inflateFunc: (LayoutInflater) -> VB) :
    BaseFragment(-1) {

     protected val mViewBinding by lazy { inflateFunc(layoutInflater) }

     override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
     ): View? {
          return mViewBinding.root
     }
}