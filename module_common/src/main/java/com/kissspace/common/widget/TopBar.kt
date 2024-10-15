package com.kissspace.common.widget

import android.content.Context
import android.util.AttributeSet
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.base.v2.IBack

class TopBar(context: Context?, attrs: AttributeSet?) : TitleBar(context, attrs) {
    init {
        setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                if(context is IBack){
                    context.handleBackPressed()
                }
            }
        })
    }
}