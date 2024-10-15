package com.kissspace.room.ui.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.didi.drouter.annotation.Router
import com.drake.softinput.setWindowSoftInput
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.parseIntent
import com.kissspace.common.util.fromJson
import com.kissspace.module_room.R
import com.kissspace.room.manager.RoomServiceManager
import com.kissspace.room.ui.fragment.LiveAudioFragment
import com.kissspace.room.viewmodel.LiveViewModel
import com.kissspace.util.hasNotificationPermission
import com.kissspace.util.immersiveStatusBar
import com.kissspace.util.isNotEmpty
import com.kissspace.util.lifecycleOwner
import com.kissspace.util.logE
import com.kissspace.util.requestNotificationPermission
import java.lang.Exception

@Router(path = RouterPath.PATH_LIVE_AUDIO)
class LiveAudioActivity : BaseActivity(R.layout.room_activity_audio) {
    private val mViewModel:LiveViewModel by lazy { ViewModelProvider(this)[LiveViewModel::class.java] }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.let {
            loadRoom()
        }
    }

    private fun loadRoom(){
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        intent.getStringExtra("roomInfo").let {
            if (it.isNotEmpty()) {
                mViewModel.roomInfoBean.value = it?.let { fromJson(it) }
            }
        }
        mViewModel.crId.value  = intent.getStringExtra("crId")
        logE("crId:${mViewModel.crId.value}")
        supportFragmentManager.commit(true) {
            for (fragment in supportFragmentManager.fragments) {
                remove(fragment)
            }
            add(R.id.layout_contain,LiveAudioFragment.newInstance(intent.getStringExtra("crId"), intent.getStringExtra("stochastic"), intent.getStringExtra("userId"),intent.getStringExtra("roomInfo")))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RoomServiceManager.quitRoomTime = System.currentTimeMillis()
    }

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(false)
        mViewModel.mViewStatus.observe(lifecycleOwner){
            if(it){
                showLoading()
            }else{
                hideLoading()
            }
        }
        loadRoom()
        try {
            if (Build.VERSION.SDK_INT >= 33&&!hasNotificationPermission(this)) {
                requestNotificationPermission(activity = this){ success->
                    if(success){
                        FlowBus.post(Event.NotificationEventOpen)
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun handleBackPressed() {
        RoomServiceManager.roomInfo?.let {
            FlowBus.post(Event.ShowRoomFloating(it.crId, it.roomIcon))
        }
        super.handleBackPressed()
    }
}