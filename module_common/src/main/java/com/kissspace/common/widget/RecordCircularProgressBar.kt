package com.kissspace.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.kissspace.module_common.R

/**
 * @Author gaohangbo
 * @Date 2023/7/26 21:56.
 * @Describe 录音进度
 */
class RecordCircularProgressBar constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyle: Int = 0) : View(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val circlePaint = Paint().apply {
        color = Color.parseColor("#FFF8F5FA")
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias=true
    }

    private val progressPaint = Paint().apply {
        color = Color.parseColor("#FFFC22B8")
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias=true
    }

    private val innerCirclePaint = Paint().apply {
        // 创建渐变效果
        shader = LinearGradient(
            0f, 0f, 0f, this.textSize,
            intArrayOf(Color.RED, Color.GREEN, Color.BLUE), null, Shader.TileMode.CLAMP
        )
//        color = Color.parseColor("#FFFC22B8")
        style = Paint.Style.FILL
        strokeWidth = 10f
        isAntiAlias=true
    }

    //内圆和外圆的间隙
    private var circleGap= resources.getDimension(R.dimen.dp_50)

    private var MAX_PROGRESS = 60

    private var progress: Int = 0

    private val circleRect = RectF()

    private val innerCircleRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        val viewWidth = width
        val viewHeight = height

        val diameter = minOf(viewWidth, viewHeight) - 10
        val left = (viewWidth - diameter) / 2
        val top = (viewHeight - diameter) / 2
        val right = left + diameter
        val bottom = top + diameter
        circleRect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        // 绘制内圆
        val innerDiameter = diameter - circleGap // 这里可以调整内圆的大小，增加或减少内外圆之间的间距
        val innerLeft = (viewWidth - innerDiameter) / 2
        val innerTop = (viewHeight - innerDiameter) / 2
        val innerRight = innerLeft + innerDiameter
        val innerBottom = innerTop + innerDiameter
        innerCircleRect.set(innerLeft.toFloat(), innerTop.toFloat(), innerRight.toFloat(), innerBottom.toFloat())
        // 绘制内圆
        //canvas.drawArc(innerCircleRect, 0f, 360f, false, innerCirclePaint)

        // 绘制完整的圆形
        canvas.drawArc(circleRect, 0f, 360f, false, circlePaint)

        // 绘制进度弧线
        val sweepAngle = progress.toFloat() / MAX_PROGRESS * 360f
        canvas.drawArc(circleRect, -90f, sweepAngle, false, progressPaint)
    }

    fun setProgress(progress: Int) {
        if (progress in 0..MAX_PROGRESS) {
            this.progress = progress
            invalidate() // 重绘View
        }
    }

    fun setGapDistance(isCloseGap: Boolean){
        if(isCloseGap){
            circleGap=  0f
          //  progressPaint.color =resources.getColor(R.color.color_50336C)
        }else{
            circleGap = 20f
            //progressPaint.color =resources.getColor(R.color.color_ffeb71)
        }
        invalidate()
    }

    fun changeCircleProgressBar(maxProgress:Int){
        circleGap=  20f
//        progressPaint.color =resources.getColor(R.color.color_ffeb71)
//        circlePaint.color = resources.getColor(R.color.color_302D45)
        MAX_PROGRESS=maxProgress
        //重置1进度
        progress=0
        invalidate()
    }


    fun resetCircleProgressBar(){
        circleGap=  20f
//        progressPaint.color =resources.getColor(R.color.color_ffeb71)
//        circlePaint.color = resources.getColor(R.color.color_302D45)
        MAX_PROGRESS=60
        //重置1进度
        progress=0
        invalidate()
    }


}