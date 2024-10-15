package com.kissspace.common.base.v2

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.kissspace.common.widget.ShowProgressDialog
import com.kissspace.util.getNavigationBarsHeight
import com.kissspace.util.isAppDebug
import com.kissspace.util.postRunnable
import com.noober.background.BackgroundLibrary

abstract class BaseActivity(@androidx.annotation.LayoutRes val layoutId:Int) : AppCompatActivity(),
    IBack {


    private var loadingDialog: ShowProgressDialog? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        if(isAppDebug) Log.e("onAttach", "Activity:"+this.javaClass.simpleName)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        BackgroundLibrary.inject(this)
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackCallBack)
        setWindow()
        if(layoutId != -1){
            setContentView(layoutId)
        }else
            setContentView(getContentView())
        initSystem(savedInstanceState)
        initView(savedInstanceState)
        createDataObserver()
        bindData()
    }


    open fun setWindow(){
        //false 表示沉浸，true表示不沉浸
        WindowCompat.setDecorFitsSystemWindows(window, !isStatusBarHide().also {
            window.statusBarColor = if(it) Color.TRANSPARENT else Color.WHITE
        })
        WindowCompat.getInsetsController(window, findViewById<FrameLayout>(android.R.id.content).apply {
               post {
                   setPadding(0,0,0,if(isStatusBarHide()) getNavigationBarsHeight() else 0)
               }
        }).let { controller ->
            controller.show(WindowInsetsCompat.Type.statusBars())
            controller.isAppearanceLightStatusBars = true //true字体黑色,false白色
        }
    }

    open fun isStatusBarHide():Boolean{
        return false
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




    open fun isStatusBack():Boolean{ return true }
    abstract fun getContentView():View

    private val onBackCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPressed()
        }
    }
    open fun initSystem(savedInstanceState: Bundle?){}
    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun createDataObserver()
    abstract fun bindData()
    override fun handleBackPressed() { finish() }

}