 package com.hamster.happyness.broadcast

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.telephony.TelephonyManager
import com.blankj.utilcode.util.LogUtils
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if(it.action =="android.intent.action.PHONE_STATE"){
//                context?.let {
//                    stopAudioVoice(context,intent)
//                }
                checkPhoneState(context)
            }
        }
    }


//    private  fun stopAudioVoice(context: Context,intent:Intent) {
//        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        val state: String = intent.getStringExtra(TelephonyManager.EXTRA_STATE)!!
//        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
//            // 停止音频声音以接听来电
//            audioManager.mode = AudioManager.MODE_IN_CALL
//            audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true)
//        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
//            // 恢复音频声音以结束来电
//            audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false)
//            audioManager.mode = AudioManager.MODE_NORMAL
//        }
//    }

    @Synchronized
    private fun checkPhoneState(context: Context?) {
        context?.let { con ->
                    val telephonyManager = con.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
                    when (telephonyManager.callState) {
                        TelephonyManager.CALL_STATE_RINGING -> {
                            LogUtils.e("checkPhoneState" + "电话进来")
                        }

                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            FlowBus.post(Event.PhoneInCome)
                            LogUtils.e("checkPhoneState" + "接电话")
                        }
                        TelephonyManager.CALL_STATE_IDLE -> LogUtils.e("checkPhoneState" + "无任何状态")
                    }
                    return
                }
    }
}