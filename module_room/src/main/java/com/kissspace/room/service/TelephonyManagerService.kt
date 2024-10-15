package com.kissspace.room.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus

class TelephonyManagerService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //android12以上
            val listener = MyCallStateListener()
            telephonyManager.registerTelephonyCallback(this.mainExecutor, listener)
        } else {
            val listener = MyPhoneStateListener()
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private class MyCallStateListener : TelephonyCallback(),
        TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {

                }


                TelephonyManager.CALL_STATE_RINGING -> {
                    FlowBus.post(Event.PhoneInCome)
                }

                TelephonyManager.CALL_STATE_OFFHOOK -> {}
                }
            }
        }

    private class MyPhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                }

                TelephonyManager.CALL_STATE_RINGING -> {
                    FlowBus.post(Event.PhoneInCome)
                }

                TelephonyManager.CALL_STATE_OFFHOOK ->{}
            }
            super.onCallStateChanged(state, phoneNumber)
        }
    }
}