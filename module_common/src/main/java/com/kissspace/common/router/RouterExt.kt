package com.kissspace.common.router

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import com.blankj.utilcode.util.ToastUtils
import com.didi.drouter.api.DRouter
import com.didi.drouter.router.Result
import com.kissspace.common.config.Constants
import com.kissspace.common.model.family.FamilyDetailInfoModel
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.isArrayListOfType
import com.kissspace.util.logE
import java.util.ArrayList
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 页面跳转
 * @param path 跳转路径
 * @param params 添加参数
 */
fun jump(
    path: String,
    vararg params: Pair<String, Any>,
    activity: Activity ?= null,
    resultLauncher: ActivityResultLauncher<Intent>? = null,
    block: ((Result) -> Unit)? = null,
) {
    DRouter.build(path).run {
        params.forEach {
            when {
                it.second is String -> putExtra(it.first, it.second as String)
                it.second is Int -> putExtra(it.first, it.second as Int)
                it.second is Long -> putExtra(it.first, it.second as Long)
                it.second is Boolean -> putExtra(it.first, it.second as Boolean)
                it.second is Double -> putExtra(it.first, it.second as Double)
                it.second is Float -> putExtra(it.first, it.second as Float)
                it.second is Parcelable -> putExtra(it.first, it.second as Parcelable)
                isArrayListOfType<String>(it.second) -> putStringArrayList(
                    it.first, it.second as ArrayList<String>
                )

                isArrayListOfType<Int>(it.second) -> putIntegerArrayList(
                    it.first, it.second as ArrayList<Int>
                )

                isArrayListOfType<Parcelable>(it.second) -> {
                    putParcelableArrayList(
                        it.first, it.second as ArrayList<Parcelable>
                    )
                }
            }
        }
        resultLauncher?.let {
            activityResultLauncher = it
        }
        start(activity) {
            block?.invoke(it)
        }
    }
}

fun jumpToFamily(){
    request<FamilyDetailInfoModel?>(
        "/djsoul-family/family/selectFamilyInfo",
        Method.GET,
        onSuccess = {
            if (it != null) {
                if (it.userFamilyStatus == Constants.FAMILY_MEMBER || it.userFamilyStatus == Constants.FAMILY_LICENSED_HOMEOWNER || it.userFamilyStatus == Constants.FAMILY_HEADER) {
                    //跳转到公会详情页面
                    jump(RouterPath.PATH_FAMILY_DETAIL, "familyId" to it.familyId.orEmpty())
                } else {
                    //跳转到申请公会列表
                    jump(RouterPath.PATH_FAMILY_LIST)
                }
            } else {
                logE("公会信息查询失败")
                jump(RouterPath.PATH_FAMILY_LIST)
            }
        },
        onError = {
            ToastUtils.showLong(it.errorMsg)
        })
}


/**
 * 解析页面参数
 */
inline fun <reified T : Any> parseIntent(defaultValue: Any? = null) =
    ParseIntent(T::class.java, defaultValue)

class ParseIntent<V : Any>(
    private val clazz: Class<V>, private var defaultValue: Any?
) : ReadWriteProperty<Activity, V?> {
    override fun getValue(thisRef: Activity, property: KProperty<*>): V {
        if (defaultValue == null) {
            defaultValue = when (clazz) {
                java.lang.Boolean::class.java -> false
                java.lang.Integer::class.java -> 0
                java.lang.Long::class.java -> 0L
                java.lang.Double::class.java -> 0.0
                java.lang.Float::class.java -> 0.0
                java.lang.String::class.java -> ""
                else -> ""
            }
        }
        val extra = thisRef.intent.extras
        return if (extra?.get(property.name) == null) {
            defaultValue as V
        } else {
            extra?.get(property.name) as V
        }

    }

    override fun setValue(thisRef: Activity, property: KProperty<*>, value: V?) {

    }


}






