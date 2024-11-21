package com.kissspace.common.util.glide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.kissspace.module_util.R

/**
 * 文件名：glideExt <br/>
 *
 * @author yxt
 * @since 2022/07/06 15:25
 *
 * @param isTopLeftRound 左上角圆弧的半径
 * @param isTopLeftRound 右上角圆弧的半径
 * @param bottomLeftRound  左下角圆弧的半径
 * @param bottomRightRound 右下角圆弧的半径
 *
 * 优先级: isCircle>round>各个圆角的设置
 *
 * 如果要设置 每个角的圆弧 不要设置round属性
 */
@SuppressLint("CheckResult")
fun ImageView.loadwithGlide(
    path: Any?,
    isCircle: Boolean = false,
    round: Int? = null,
    topLeftRound: Float = 0f,
    topRightRound: Float = 0f,
    bottomLeftRound: Float = 0f,
    bottomRightRound: Float = 0f,
    @DrawableRes placeholderId: Int = R.drawable.common_ic_default,
    @DrawableRes errorId: Int = R.drawable.common_ic_default,
) {

    val options: RequestOptions = RequestOptions().apply {
        diskCacheStrategy(DiskCacheStrategy.ALL)
        placeholder(placeholderId)
        error(errorId)
        if (isCircle) {
            transform(CircleCrop())
        } else {
            if (round != null) {
                transform(CenterCrop(), RoundedCorners(round!!))
            } else {
                val transform = RoundedCornersTransform(
                    topLeftRound,
                    topRightRound,
                    bottomLeftRound,
                    bottomRightRound
                )
                transform(CenterCrop(), transform)
            }
        }
    }

    GlideApp.with(context)
        .load(path)
        .apply(options)
        .transition(DrawableTransitionOptions.with(DrawableCrossFadeFactory.Builder(200).setCrossFadeEnabled(true).build()))
        .thumbnail(
            loadTransform(
                context,
                placeholderId,
                isCircle,
                round,
                topLeftRound,
                topRightRound,
                bottomLeftRound,
                bottomRightRound
            )
        )
        .thumbnail(
            loadTransform(
                context,
                errorId,
                isCircle,
                round,
                topLeftRound,
                topRightRound,
                bottomLeftRound,
                bottomRightRound
            )
        )
        .into(this)
}

@SuppressLint("CheckResult")
private fun loadTransform(
    context: Context,
    @DrawableRes placeholderId: Int,
    isCircle: Boolean,
    round: Int?,
    topLeftRound: Float,
    topRightRound: Float,
    bottomLeftRound: Float,
    bottomRightRound: Float,
): RequestBuilder<Drawable> {
    return GlideApp.with(context).load(placeholderId)
        .apply(
            RequestOptions().apply {
                if (isCircle) {
                    transform(CircleCrop())
                }else{
                    if (round != null) {
                        transform(CenterCrop(), RoundedCorners(round!!))
                    } else {
                        val transform = RoundedCornersTransform(
                            topLeftRound,
                            topRightRound,
                            bottomLeftRound,
                            bottomRightRound
                        )
                        transform(CenterCrop(), transform)
                    }
                }
            }
        )
}


