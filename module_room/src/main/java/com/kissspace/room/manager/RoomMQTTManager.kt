package com.kissspace.room.manager

import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.RoomPKInfoMessage
import com.kissspace.common.model.immessage.RoomStartPkMessage
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.util.fromJson
import com.kissspace.util.logE
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.lang.Exception

object RoomMQTTManager {
    private var mClient: MqttClient? = null
    var roomStartPkMessage: RoomStartPkMessage? = null
    var callback: IMQTTMessageCallBack? = null

    var tLast: String ? = null
    var mqtMsg: String ? = null

    private fun createClient(url: String, callback: IMQTTMessageCallBack) {
        this.callback = callback
        val mqttCallback = object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                logE("mqtt连接成功------")
            }

            override fun connectionLost(cause: Throwable) {
                logE("mqtt连接失败------${cause}")
            }

            @Throws(Exception::class)
            override fun messageArrived(t: String, message: MqttMessage) {
                logE("收到mqtt消息---${String(message.payload)}")
                tLast = t
                String(message.payload).let {
                    mqtMsg = String(message.payload)
                    FlowBus.post(Event.PkChanged(fromJson<RoomPKInfoMessage>(mqtMsg!!)))
//                    callback.onMQTTMessageArrived(t, mqtMsg!!)
                }

            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
            }
        }
        try {
            if (mClient == null) {
                mClient = MqttClient(url, MMKVProvider.userId, MemoryPersistence())
                mClient?.setCallback(mqttCallback) //设置回调函数
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }




    fun connect(message: RoomStartPkMessage, callback: IMQTTMessageCallBack) {
        if(!message.isPK||message.topic.isEmpty()) return
        if(mClient!=null&& mClient?.isConnected == true) return
        if (mClient == null) {
            createClient("tcp://${message.host}:${message.port}", callback)
        }
        roomStartPkMessage = message
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = false
            isCleanSession = true
            connectionTimeout = 10
            keepAliveInterval = 20
            userName = message.userName
            password = message.password.toCharArray()
            mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
        }
        try {
            mClient?.connect(options)
            mClient?.subscribe(message.topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

//    fun resume(callback: IMQTTMessageCallBack) {
//        if (mClient?.isConnected == true) {
//            this.callback = callback
//            mqtMsg?.let {
//                FlowBus.post(Event.PkChanged(fromJson<RoomPKInfoMessage>(mqtMsg!!)))
////                callback.onMQTTMessageArrived(tLast!!, it)
//            }
//        }
//    }

    fun release() {
        if (mClient?.isConnected == true) {
            mqtMsg = null
            tLast = null
            mClient?.disconnect()
        }
        mClient = null
    }
}

interface IMQTTMessageCallBack {
    fun onMQTTMessageArrived(topic: String, msg: String)
}