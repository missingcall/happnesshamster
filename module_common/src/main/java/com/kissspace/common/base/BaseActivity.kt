package com.kissspace.common.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.ToastUtils
import com.noober.background.BackgroundLibrary
import com.kissspace.common.callback.ActivityTouchEvent
import com.kissspace.common.widget.ShowProgressDialog
import com.kissspace.util.isAppDebug
import com.kissspace.util.postRunnable
import com.kissspace.util.setStatusBarColor
import com.kissspace.util.topActivity
import java.lang.ref.WeakReference

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/2 15:09
 * @Description: activity 基类
 *
 */
abstract class BaseActivity(layoutId: Int) : AppCompatActivity(layoutId) {
    private val mActTouchEvents = mutableListOf<ActivityTouchEvent>()
    private var loadingDialog: ShowProgressDialog? = null
    private val onBackCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPressed()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        if(isAppDebug)Log.e("onAttach", "Activity:"+this.javaClass.simpleName)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        BackgroundLibrary.inject(this)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        onBackPressedDispatcher.addCallback(onBackCallBack)
        setStatusBarColor(Color.TRANSPARENT, true)
        initView(savedInstanceState)
        createDataObserver()
    }

    fun showLoading(context: String = "加载中") {
        if (loadingDialog == null) {
            loadingDialog = ShowProgressDialog()
        }
        postRunnable { loadingDialog?.showDialog(this,context)  }
    }

    fun hideLoading() {
        postRunnable {
            loadingDialog?.dismiss()
        }
    }
    /**
     * 初始化UI
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     *  监听数据变化
     */
    open fun createDataObserver() {

    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mActTouchEvents.forEach {
            it.onDispatchTouchEvent(ev!!)
        }
        return super.dispatchTouchEvent(ev)
    }

    open fun registerActivityTouchEvent(activityTouchEvent: ActivityTouchEvent) {
        mActTouchEvents.add(activityTouchEvent)
    }

    open fun unRegisterActivityTouchEvent(activityTouchEvent: ActivityTouchEvent) {
        mActTouchEvents.remove(activityTouchEvent)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mActTouchEvents.forEach {
            it.onTouchEvent(event!!)
        }
        return super.onTouchEvent(event)
    }

    open fun handleBackPressed() {
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        ToastUtils.cancel()
    }
}