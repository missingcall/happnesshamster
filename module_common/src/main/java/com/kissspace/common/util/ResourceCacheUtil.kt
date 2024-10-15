package com.kissspace.common.util

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import coil.request.ImageRequest
import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.drake.net.Get
import com.drake.net.utils.scopeNet
import com.kissspace.common.util.glide.GlideApp
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.network.net.downPag
import com.kissspace.util.application
import com.kissspace.util.isNotEmpty
import com.kissspace.util.md516
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private val pageCachePath = File(application.cacheDir, "pag").absolutePath
private val mp4CachePath = File(application.cacheDir, "mp4").absolutePath
private val audioCachePath = File(application.cacheDir, "audio").absolutePath

/**
 * 获取本地PAG文件路径
 * @param url 文件url
 * @param block 返回路口
 */

fun getPagPath(view: View?,url: String?, block: (String) -> Unit) {
    (url?:"").let {
        if (url.isNullOrEmpty()||url.startsWith("assets")) {
            block(it)
            return
        }else{
            getAnimationPath(view,url,block)
//            val path = CacheDiskUtils.getInstance("pag").getString(url.md516())
//            if (path.isNullOrEmpty() || !path.endsWith(".pag")) {
//                val tempUrl = url.md516()
//                LogUtils.e("tempUrl:$tempUrl")
//                scopeNet {
//                    val file = Get<File>(url) {
//                        setDownloadTempFile(true)
//                        setDownloadFileName("${tempUrl}.pag")
//                        setDownloadDir(pageCachePath)
//                    }.await()
//                    CacheDiskUtils.getInstance("pag").put(tempUrl, file.absolutePath)
//                    CacheDiskUtils.getInstance("pag").getString(tempUrl).let { url ->
//                        if(url.isNotEmpty()){
//                            block(url)
//                        }else{
//                            block(file.absolutePath)
//                        }
//                    }
//                }
//            } else {
//                block(path)
//            }
        }
    }
}


fun getAnimationPath(view: View?,url: String?, block: (String) -> Unit) {
    view?.let {
        GlideApp.with(view).asFile().load(url).listener(object :RequestListener<File>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<File>?,
                isFirstResource: Boolean
            ): Boolean {
                block("")
                return false
            }

            override fun onResourceReady(
                resource: File?,
                model: Any?,
                target: Target<File>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource?.let {
                    block(it.absolutePath)
                }
                return false
            }
        }).submit()
    }
//    url?.let {
//        val path = CacheDiskUtils.getInstance("animation").getString(url.md516())
//        if(path.isNullOrEmpty()) {
//            scopeNet {
//                val file = Get<File>(url) {
//                    setDownloadTempFile(true)
//                    setDownloadFileName("${url.md516()}."+url.substringAfterLast("."))
//                    setDownloadDir(mp4CachePath)
//                }.await()
//                CacheDiskUtils.getInstance("animation").put(url.md516(), file.absolutePath)
//                (CacheDiskUtils.getInstance("animation").getString(url.md516())?:file.absolutePath).let(block)
//            }
//        }
//    }
}

fun getMP4Path(view: View?,url: String, block: (String) -> Unit) {
    if (url.isNullOrEmpty()) {
        block("")
        return
    }

    getAnimationPath(view,url,block)
//    val path = CacheDiskUtils.getInstance("mp4").getString(url.md516())
//    if (path.isNullOrEmpty() || !path.endsWith(".mp4")) {
//        scopeNet {
//            val file = Get<File>(url) {
//                setDownloadTempFile(true)
//                setDownloadFileName("${url.md516()}.mp4")
//                setDownloadDir(mp4CachePath)
//            }.await()
//            CacheDiskUtils.getInstance("mp4").put(url.md516(), file.absolutePath)
//            CacheDiskUtils.getInstance("mp4").getString(url.md516()).let {
//                if(url.isNotEmpty()){
//                    block(url)
//                }else{
//                    block(file.absolutePath)
//                }
//            }
//        }
//    } else {
//        block(path)
//    }
}

fun getAudioPath(url: String, block: (String) -> Unit) {
    if (url.isNullOrEmpty()) {
        block("")
        return
    }
    val path = CacheDiskUtils.getInstance("audio").getString(url.md516())
    if (path.isNullOrEmpty() || !path.endsWith(".aac")) {
        scopeNet {
            val file = Get<File>(url) {
                setDownloadTempFile(true)
                setDownloadFileName("${url.md516()}.aac")
                setDownloadDir(audioCachePath)
            }.await()
            CacheDiskUtils.getInstance("audio").put(url.md516(), file.absolutePath)
            block(CacheDiskUtils.getInstance("audio").getString(url.md516()))
        }
    } else {
        block(path)
    }
}

fun getAssertMP4Path(url: String, block: (String,Boolean) -> Unit) {
    if (url.isNullOrEmpty()) {
        block("",false)
        return
    }
    val path = CacheDiskUtils.getInstance("mp4").getString(url.md516())
    if (path.isNullOrEmpty() || !path.endsWith(".mp4")) {
        block(url,true)
    } else {
        block(CacheDiskUtils.getInstance("mp4").getString(url.md516()),false)
    }
}

fun copyAssetsToStorage(context: Context,files: Array<String>) {
    Thread {
        var outputStream: OutputStream
        var inputStream: InputStream
        val buf = ByteArray(4096)
        files.forEach {
            try {
                val path = CacheDiskUtils.getInstance("mp4").getString(it.md516())
                if (!path.isNullOrEmpty() && File(path).exists()) {
                    return@forEach
                }
                inputStream = context.assets.open(it)
                outputStream = FileOutputStream("$mp4CachePath/$it")
                var length = inputStream.read(buf)
                while (length > 0) {
                    outputStream.write(buf, 0, length)
                    length = inputStream.read(buf)
                }
                outputStream.close()
                inputStream.close()
                CacheDiskUtils.getInstance("mp4").put(it.md516(),"$mp4CachePath/$it")
            } catch (e: IOException) {
                e.printStackTrace()
                return@Thread
            }
        }
    }.start()
}


