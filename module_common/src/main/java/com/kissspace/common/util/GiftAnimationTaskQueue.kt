package com.kissspace.common.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.kissspace.common.widget.AnimationView
import com.opensource.svgaplayer.*
import com.kissspace.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/22 10:06
 * @Description: 礼物动效任务队列管理
 *
 */
class GiftAnimationTaskQueue(
    private val lifecycleOwner: LifecycleOwner,
    private val animView: AnimationView,
) : DefaultLifecycleObserver {
    private var isFinished = true
    private val queue = ConcurrentLinkedQueue<String>()
    private var job: Job? = null

    init {
        animView.setAnimationListener(object : AnimationView.AnimationListener{
            override fun onStart() {

            }

            override fun onComplete() {
                    isFinished = true
            }

        })
    }

    fun addTask(path: String?) {
        logE("礼物地址----${path}")
        if (path.isNullOrEmpty()) {
            return
        }
        queue.add(path)
        if (job == null) {
            startJob()
        }
    }

    private fun startJob() {
        job = flow {
            for (i in 0 until Int.MAX_VALUE) {
                emit(i)
                delay(1000)
            }
        }.flowOn(Dispatchers.Main).onEach {
            if (queue.isEmpty()) {
                job?.cancel()
                job = null
            } else {
                if (isFinished) {
                    queue.poll()?.let {
                        playAnimation(it)
                    }
                }
            }
        }.launchIn(MainScope())
    }

    private fun playAnimation(path: String) {
        LogUtils.e("path-----${path}")
        lifecycleOwner.lifecycleScope.launch {
            isFinished = false
            animView.play(path)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        job?.cancel()
        queue.clear()
        animView?.onDestroy()
    }
}


