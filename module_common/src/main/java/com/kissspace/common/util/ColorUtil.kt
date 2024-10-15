package com.kissspace.common.util

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

class ColorUtil {

    companion object{
        fun setCommonTextColorWithGradientShade(textView: TextView) {
            val x1 = textView.paint.measureText(textView.text.toString()) //测量文本 宽度
            val y1 = textView.paint.textSize //测量文本 高度
            val c1 = Color.parseColor("#FC22B8") //初始颜色值
            val c2 = Color.parseColor("#FD2894") //初始颜
            val c3 = Color.parseColor("#FF9365") //结束颜色值
            val leftToRightLG = LinearGradient(0f, 0f, x1, y1, c1, c3, Shader.TileMode.CLAMP) //从左到右渐变
            // val topToBottomLG = LinearGradient(0f, 0f, 0f, y1, c1, c2, Shader.TileMode.CLAMP) //从上到下渐变
            textView.paint.shader = leftToRightLG
            textView.invalidate()
        }
    }


}