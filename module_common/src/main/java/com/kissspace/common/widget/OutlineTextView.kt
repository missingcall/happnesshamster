package com.kissspace.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.kissspace.module_common.R

class OutlineTextView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context,attrs,defStyleAttr) {
    private var outlineColor: Int = 0
    private var inlineColor: Int = 0


    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.OutlineTextView)
        outlineColor = a.getColor(R.styleable.OutlineTextView_outlineColor, 0)
        inlineColor = a.getColor(R.styleable.OutlineTextView_inlineColor, 0)
        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        //val paint: TextPaint = paint
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        setTextColor(outlineColor)
        super.onDraw(canvas)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f
        setTextColor(inlineColor)
        super.onDraw(canvas)
    }
}