package com.kissspace.common.widget

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.kissspace.common.base.BaseActivity
import com.kissspace.module_common.R
import com.kissspace.util.djsUniqueTag
import com.kissspace.util.isAppDebug

/**
 */
abstract class BaseDialogFragment<DB : ViewDataBinding>(
    inflateFunc: (LayoutInflater) -> DB,
    private val gravity: Int,
    private val fullScreen: Boolean = false,
    private val withScreen: Boolean = true
) :
    DialogFragment() {

    val mBinding by lazy { inflateFunc(layoutInflater) }

    abstract fun getLayoutId(): Int

    abstract fun initView()


    open fun observerData() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_CustomDialogFragment)
        if (isAppDebug)
            Log.e("onAttach", "Dialog:" + this.javaClass.simpleName + " isadd:" + isAdded + "tag:" + tag)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initView()
        observerData()
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val attr = attributes
            attr.width = if (withScreen) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT
            attr.height = if (fullScreen) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT
            attr.gravity = gravity
            attributes = attr
            if (gravity == Gravity.BOTTOM) {
                setWindowAnimations(R.style.DialogBottomInAnimation)
            } else if (gravity == Gravity.LEFT){
                setWindowAnimations(R.style.DialogLeftInAnimation)
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val c = Class.forName("androidx.fragment.app.DialogFragment")
            val obj: Any = this
            val dismissed = c.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            dismissed[obj] = false
            val shownByMe = c.getDeclaredField("mShownByMe")
            shownByMe.isAccessible = true
            shownByMe[obj] = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    fun showLoading(context: String = "加载中") {
        if (requireActivity() is BaseActivity) {
            (requireActivity() as BaseActivity).showLoading(context)
        }
    }

    fun hideLoading() {
        if (requireActivity() is BaseActivity) {
            (requireActivity() as BaseActivity).hideLoading()
        }
    }

    override fun dismiss() {
        try {
            dismissAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun show(manager: FragmentManager) {
        try {
            if (manager.isDestroyed || manager.isStateSaved) {
                return
            }
            show(manager, manager.djsUniqueTag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}