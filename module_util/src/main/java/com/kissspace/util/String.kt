package com.kissspace.util

import android.net.Uri
import android.text.format.Formatter
import androidx.annotation.ColorRes
import androidx.core.util.PatternsCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.UUID

const val REGEX_PHONE_EXACT: String =
    "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$"

const val REGEX_ID_CARD_18: String =
    "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$"

inline val randomUUIDString: String
    get() = UUID.randomUUID().toString()

inline fun @receiver:ColorRes Int.resToString(): String = StringUtils.getString(this)

fun Long.toFileSizeString(): String = Formatter.formatFileSize(application, this)

fun Long.toShortFileSizeString(): String = Formatter.formatShortFileSize(application, this)

fun String.limitLength(length: Int): String =
    if (this.length <= length) this else substring(0, length)

fun String.isPhone(): Boolean = REGEX_PHONE_EXACT.toRegex().matches(this)

fun String.isDomainName(): Boolean = PatternsCompat.DOMAIN_NAME.matcher(this).matches()

fun String.isEmail(): Boolean = PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()

fun String.isIP(): Boolean = PatternsCompat.IP_ADDRESS.matcher(this).matches()

/**
 *  Regular expression pattern to match most part of RFC 3987
 *  Internationalized URLs, aka IRIs.
 */
fun String.isWebUrl(): Boolean = PatternsCompat.WEB_URL.matcher(this).matches()


fun addParameter(url: String, parameterName: String, newValue: String): Uri {
    LogUtils.e("url:origin--$url")
    return if (url.lastIndexOf("?") == -1) {
        Uri.parse("$url?$parameterName=$newValue")
    } else {
        (!url.contains("?$parameterName") && !url.contains("&$parameterName")).let {
            Uri.parse(if (it) "$url&$parameterName=$newValue" else url)
        }
    }.apply {
        LogUtils.e("url:changed--$this")
    }
//    return addParameter(Uri.parse(url), parameterName, newValue).apply {
//        LogUtils.e("url:changed$this")
//    }
}

fun addParameter(uri: Uri, parameterName: String, newValue: String): Uri {
    val builder = uri.buildUpon()
    builder.clearQuery()
    var isChanged = false
    uri.queryParameterNames.forEach { parameter ->
        if (parameter == parameterName) {
            isChanged = true
            builder.appendQueryParameter(parameter, newValue)
        } else {
            builder.appendQueryParameter(parameter, uri.getQueryParameter(parameter))
        }
    }
    if (!isChanged) {
        builder.appendQueryParameter(parameterName, newValue)
    }
    return builder.build()
}


fun String.isIDCard18(): Boolean = REGEX_ID_CARD_18.toRegex().matches(this)

fun String.isJson(): Boolean = try {
    JSONObject(this)
    true
} catch (e: Exception) {
    false
}

fun Float.toNumberString(
    fractionDigits: Int = 2,
    minIntDigits: Int = 1,
    isGrouping: Boolean = false,
    isHalfUp: Boolean = true
): String = toDouble().toNumberString(fractionDigits, minIntDigits, isGrouping, isHalfUp)

fun Double.toNumberString(
    fractionDigits: Int = 2,
    minIntDigits: Int = 1,
    isGrouping: Boolean = false,
    isHalfUp: Boolean = true
): String = (NumberFormat.getInstance() as DecimalFormat).apply {
    isGroupingUsed = isGrouping
    roundingMode = if (isHalfUp) RoundingMode.HALF_UP else RoundingMode.DOWN
    minimumIntegerDigits = minIntDigits
    minimumFractionDigits = fractionDigits
    maximumFractionDigits = fractionDigits
}.format(this)


fun String.md516(): String = this.encryptMD5().let {
    it.substring(it.length - 24)
}

fun String.ellipsizeString(size: Int): String {
    return if (this.length <= size) this else this.substring(0, size - 1) + "..."
}

/**
 * 手机号码前三后四脱敏
 */
fun mobileEncrypt(mobile: String): String? {
    return if (mobile.isEmpty() || mobile.length != 11) {
        mobile.replace("(\\w{3})\\w*(\\w{4})".toRegex(), "$1****$2")
    } else mobile.replace("(\\w{3})\\w*(\\w{4})".toRegex(), "$1****$2")
}

/**
 * 身份证号脱敏
 */
fun identityEncrypt(identity: String): String? {
    var str = ""
    if (identity.isEmpty() || identity.length != 18) {
        str = identity
    } else {
        var sign = ""
        for (i in 0 until identity.length - 7) {
            sign += "*"
        }
        str = identity.replace("(\\w{3})\\w*(\\w{4})".toRegex(), "$1${sign}$2")
    }
    return str
}

/**
 * 姓名脱敏
 */
fun desensitizedName(fullName: String): String? {
    var name = ""
    if (!fullName.isNullOrEmpty() && fullName.length > 1) {
        name = fullName.substring(0, 1)
        for (i in 1 until fullName.length) {
            name += "*"
        }
    } else {
        name = fullName
    }
    return name
}