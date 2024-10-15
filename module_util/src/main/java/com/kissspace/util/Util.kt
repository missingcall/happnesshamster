package com.kissspace.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import androidx.test.internal.util.LogUtil
import java.io.Serializable


/**
 * @Author gaohangbo
 * @Date 2023/8/22 15:16.
 * @Describe
 */
fun <T> swapWithHead(list: MutableList<T>?, index: Int): MutableList<T>?{
    if (list != null) {
        if (index >= 0 && index < list.size) {
            list[0] = list[index].also { list[index] = list[0] }
        } else {
            logE("Invalid index")
        }
    }
    return list
}

fun checkWifiProxy(): Boolean {
    val proxyAddress = System.getProperty("http.proxyHost")
    var proxyPort = 0
    val portStr = System.getProperty("http.proxyPort")
    if (!TextUtils.isEmpty(portStr)) {
        proxyPort = portStr?.toInt() ?: -1
    }
    logE("ProxyUtil:地址:$proxyAddress 端口：$proxyPort")
    return !TextUtils.isEmpty(proxyAddress) && proxyPort != 0
}


inline fun <reified T : Parcelable> Bundle.getParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getParcelable(key, T::class.java)
    else
        this.getParcelable(key) as? T
}

inline fun <reified T : Serializable> Intent.getParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getParcelableExtra(key, T::class.java)
    else
        this.getParcelableExtra(key) as? T
}

inline fun <reified T : Serializable> Bundle.getSerialized(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getSerializable(key, T::class.java)
    else
        this.getSerializable(key) as? T
}

inline fun <reified T : ArrayList<T>> Bundle.getParcelableArrayList(key: String): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getParcelableArrayList(key,T::class.java)
    else
        this.getParcelableArrayList<Parcelable>(key) as ArrayList<T>
}

inline fun <reified T : ArrayList<T>> Intent.getParcelableArrayList(key: String): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getParcelableArrayListExtra(key,T::class.java)
    else
        this.getParcelableArrayListExtra<Parcelable>(key) as ArrayList<T>
}