package com.kissspace.common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.AdjustedCornerSize
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.CornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import com.kissspace.module_common.R
import com.kissspace.util.withStyledAttributes
import org.apmem.tools.layouts.FlowLayout

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {


      init {
//          withStyledAttributes(attrs, R.styleable.CircleImageView) {
//              mBorderWidth = getDimensionPixelSize(
//                  R.styleable.CircleImageView_civ_border_width,
//                  0
//              )
//
//              mBorderColor = getColor(
//                  R.styleable.CircleImageView_civ_border_color,
//                  ContextCompat.getColor(context,R.color.black)
//              )
//          }
          shapeAppearanceModel = ShapeAppearanceModel.builder()
              .setAllCornerSizes(ShapeAppearanceModel.PILL)
              .build()
          setPadding(strokeWidth.toInt())
//          if(mBorderWidth>0) {
//              strokeColor = ColorStateList.valueOf(mBorderColor)
//              strokeWidth = mBorderWidth.toFloat()
//              setPadding(mBorderWidth)
//          }
      }
}