package com.kissspace.common.widget

import android.content.Context
import android.util.AttributeSet
import com.kissspace.util.withStyledAttributes
import com.kissspace.module_common.R
import org.libpag.PAGImageView

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/3/17 14:25
 * @Description: 封装PAGView，提供自动销毁方法
 *
 */

class EasyPagImageView(context: Context, attributeSet: AttributeSet?) :
    PAGImageView(context, attributeSet) {
    private var clearAfterStop = true
    private var loopCount = 1

    init {
        withStyledAttributes(attributeSet, R.styleable.EasyPagView) {
            clearAfterStop = getBoolean(R.styleable.EasyPagView_clearAfterStop, true)
            loopCount = getInteger(R.styleable.EasyPagView_loop_count, 1)
        }
        initView()
    }

    private fun initView() {
        setRepeatCount(loopCount)
        addListener(object : PAGImageViewListener {
            override fun onAnimationStart(p0: PAGImageView?) {
            }

            override fun onAnimationEnd(p0: PAGImageView?) {
                if (clearAfterStop) {
                    composition = null
                    flush()
                }
            }

            override fun onAnimationCancel(p0: PAGImageView?) {
            }

            override fun onAnimationRepeat(p0: PAGImageView?) {
            }

            override fun onAnimationUpdate(p0: PAGImageView?) {
            }

        })
    }

    fun setLoopCount(loopCount: Int) {
        this.loopCount = loopCount
        setRepeatCount(loopCount)
    }

    fun play(url: String) {
        if (isPlaying) {
            pause()
        }
        path = url
        play()
    }

    fun clear() {
        try {
            flush()
        }catch (e:Exception){
            e.printStackTrace()
        }
        composition = null
    }

    override fun flush(): Boolean {
        try {
            return super.flush()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return false
    }
}