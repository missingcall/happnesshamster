package com.kissspace.common.util

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.ColorUtils
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hjq.bar.TitleBar
import com.kissspace.common.util.format.DateFormat
import com.kissspace.common.util.glide.GlideApp
import com.kissspace.common.widget.AnimationView
import com.kissspace.common.widget.UserLevelIconView
import com.kissspace.module_common.R
import com.kissspace.util.*
import java.io.File

object CommonBindingAdapter {
    @JvmStatic
    @BindingAdapter("titleText", requireAll = false)
    fun titleText(title: TitleBar, text: String?) {
        title.title = text
    }

    @JvmStatic
    @BindingAdapter("colorEnable", requireAll = false)
    fun colorEnable(textView: TextView, isEnable: Boolean) {
        textView.setTextColor(if(isEnable){ColorUtils.getColor(R.color.color_ui_main_text)}else ColorUtils.getColor(R.color.color_ui_sub_text_middle))
    }

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "imageRadius"])
    fun loadImage(imageView: ImageView, imageUrl: String?, imageRadius: Float = 0f) {
//        imageView.loadImage(imageUrl, radius = imageRadius)
        imageUrlDefault(imageView,imageUrl,imageRadius)
    }

    @JvmStatic
    @BindingAdapter("loadImageCircle",requireAll = false)
    fun loadImageCircle(imageView: ImageView, imageUrl: String?) {
        imageView.loadImageCircle(imageUrl)
    }

    @JvmStatic
    @BindingAdapter(value = ["imageUrlDefault", "imageRadius"])
    fun imageUrlDefault(imageView: ImageView, imageUrl: String?, imageRadius: Float = 0f) {
        var  options = RequestOptions().placeholder(com.kissspace.module_util.R.drawable.common_ic_default)
        if(imageRadius>0){
            options = options.transform(RoundedCorners(imageRadius.toInt()))
        }
        GlideApp.with(imageView)
            .load(imageUrl)
            .apply(options)
            .into(imageView)
    }


    @JvmStatic
    @BindingAdapter("loadImage", requireAll = false)
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        GlideApp.with(imageView)
            .load(imageUrl)
            .placeholder(com.kissspace.module_util.R.drawable.common_ic_default)
            .into(imageView)
//        imageView.loadImage(imageUrl)
    }

    @JvmStatic
    @BindingAdapter("loadImageNoPlace", requireAll = false)
    fun loadImageNoPlace(imageView: ImageView, imageUrl: String?) {
        GlideApp.with(imageView)
            .load(imageUrl)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("loadGif", requireAll = false)
    fun loadGif(imageView: ImageView, imageUrl: String?) {
        GlideApp.with(imageView).load(imageUrl).into(imageView)
    }

    @JvmStatic
    @BindingAdapter("loadImageWithDefault", requireAll = false)
    fun loadImageWithDefault(imageView: ImageView, imageUrl: String?) {
        loadImage(imageView,imageUrl)
//        imageView.loadImageWithDefault(imageUrl)
    }

    @JvmStatic
    @BindingAdapter("loadImageOrGone", requireAll = false)
    fun loadImageOrGone(imageView: ImageView, imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) {
            imageView.visibility = View.GONE
        } else {
            imageView.visibility = View.VISIBLE
            imageView.loadImage(imageUrl)
        }
    }

    @JvmStatic
    @BindingAdapter("goneIf")
    fun viewVisibleIf(
        view: View, gone: Boolean?
    ) {
        view.visibility = if (gone == true) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("goneIfByContent")
    fun goneIfByContent(
        view: View, string: String?
    ) {
        view.visibility = if (TextUtils.isEmpty(string)) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("showIf")
    fun showIf(
        view: View, isShow: Boolean?
    ) {
        view.visibility = if (isShow == true) View.VISIBLE else View.GONE
    }


    @JvmStatic
    @BindingAdapter("loadChatImage")
    fun loadChatImage(imageView: ImageView, url: String) {
        if (url.startsWith("http") || url.startsWith("https")) {
            imageView.loadImage(url)
        } else {
            imageView.loadImage(File(url))
        }
    }

    @JvmStatic
    @BindingAdapter("goneUnless")
    fun viewGoneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

//    @JvmStatic
//    @BindingAdapter("clIsSelected")
//    fun clIsSelected(constraintLayout: ConstraintLayout, isSelected: Boolean) {
//        constraintLayout.isSelected = isSelected
//    }

    @JvmStatic
    @BindingAdapter("clIsSelected")
    fun clIsSelected(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }


    @JvmStatic
    @BindingAdapter("setImageBackground")
    fun setImageBackground(imageView: ImageView, resource: Int) {
        imageView.setBackgroundResource(resource)
    }

    @JvmStatic
    @BindingAdapter("loadImageResource")
    fun loadImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }


    @JvmStatic
    @BindingAdapter("commonBtnState", requireAll = false)
    fun commonBtnState(textView: TextView, enable: Boolean = false) {
        if (enable) {
            textView.setBackgroundResource(R.mipmap.common_button_bg)
            textView.setTextColor(ColorUtils.getColor(R.color.color_FFFD62))
        } else {
            textView.setBackgroundResource(R.mipmap.common_button_bg_no_check)
            textView.setTextColor(ColorUtils.getColor(R.color.color_50FFFD62))
        }
        textView.isEnabled = enable
    }

    @JvmStatic
    @BindingAdapter("commonBtnGradientState", requireAll = false)
    fun commonBtnGradientState(textView: TextView, enable: Boolean = false) {
        if (enable) {
            textView.setBackgroundResource(R.drawable.common_shape_bg_gradient)
        } else {
            textView.setBackgroundResource(R.drawable.common_shape_bg_gray_28)
        }
        textView.isEnabled = enable
    }

    @JvmStatic
    @BindingAdapter("loadCircleImage", requireAll = false)
    fun loadCircleImage(imageView: ImageView, imageUrl: String?) {
        imageView.loadImageCircle(imageUrl)
    }


    @JvmStatic
    @BindingAdapter("alphaByEnable", requireAll = false)
    fun alphaByEnable(view: View, enable: Boolean?) {
        view.alpha = if(view.isEnabled&&enable != false) 1f else 0.5f
    }

    @JvmStatic
    @BindingAdapter("userLevelCount", requireAll = false)
    fun userLevelCount(textView: UserLevelIconView, count: Int) {
        textView.setLeveCount(count)
    }

    @JvmStatic
    @BindingAdapter("imageResource", requireAll = false)
    fun imageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @JvmStatic
    @BindingAdapter("longToTime", requireAll = false)
    fun longToTime(textView: TextView, time: Long) {
        textView.text = time.formatDate(DateFormat.YYYY_MM_DD_HH_MM_SS)
    }

    @BindingAdapter("setRightDrawable")
    fun setRightDrawable(textView: TextView, drawable: Drawable?) {
        if (drawable == null) {
            textView.setCompoundDrawables(null, null, null, null)
        } else {
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            textView.setCompoundDrawables(null, null, drawable, null)
        }
    }

    @BindingAdapter("setLeftDrawable")
    fun setLeftDrawable(textView: TextView, drawable: Drawable?) {
        if (drawable == null) {
            textView.setCompoundDrawables(null, null, null, null)
        } else {
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            textView.setCompoundDrawables(drawable, null, null, null)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["animationUrl", "animationLoop"])
    fun loadAnimation(animationView: AnimationView,  animationUrl: String?,animationLoop: Boolean?) {
         if(animationUrl.isNullOrEmpty()){
             animationView.visibility = View.GONE
             animationView?.onDestroy()
         }else{
             animationView.visibility = View.VISIBLE
             animationView.play(animationUrl, if(animationLoop!=null&&animationLoop)  Int.MAX_VALUE else 1)
         }
    }

    @JvmStatic
    @BindingAdapter("imageViewSelected", requireAll = false)
    fun imageViewSelected(imageView: ImageView, imageViewSelected: Boolean) {
        imageView.isSelected=imageViewSelected
    }

    @JvmStatic
    @BindingAdapter("urlChecked", requireAll = false)
    fun urlChecked(imageView: ImageView, isChecked: Boolean) {
        imageView.setImageResource(if (isChecked) com.kissspace.module_common.R.mipmap.common_icon_checkbox_selected else com.kissspace.module_common.R.mipmap.common_icon_check_normal)
    }

    @JvmStatic
    @BindingAdapter("showWalletType")
    fun showWalletType(textView: TextView, text: String?) {
        textView.text = text
    }

}