package com.kissspace.dynamic.binding

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.kissspace.common.ext.setDrawable
import com.kissspace.common.model.dynamic.DynamicInfoRecord
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.music.MusicSpectrumBar
import com.kissspace.module_dynamic.R

object DynamicBindingAdapter {

    @JvmStatic
    @BindingAdapter("dynamicSex", requireAll = false)
    fun dynamicSex(imageView: ImageView, sex: String?) {
        imageView.setImageResource(if (sex=="001") R.mipmap.dynamic_icon_sex_female else R.mipmap.dynamic_icon_sex_male)
    }


    @JvmStatic
    @BindingAdapter("btnEnable", requireAll = false)
    fun btnEnable(view: View, isEnable: Boolean?) {
        view.alpha = if(isEnable!=null&&isEnable) 1f else 0.5f
    }

    @JvmStatic
    @BindingAdapter("progressChanged", requireAll = false)
    fun progressChanged(musicSpectrumBar: MusicSpectrumBar, progress: Int) {
        musicSpectrumBar.setCurrent(progress)
    }

    @JvmStatic
    @BindingAdapter("playVoiceStatusChanged", requireAll = false)
    fun playVoiceStatusChanged(imageView: ImageView, isPlay: Boolean) {
        imageView.setImageResource(if (isPlay) com.kissspace.module_common.R.mipmap.common_audio_pause_icon else com.kissspace.module_common.R.mipmap.common_audio_play_icon)
    }



    @JvmStatic
    @BindingAdapter("dynamicFollow", requireAll = false)
    fun dynamicFollow(textView: TextView, follow: Boolean?) {
        textView.setTextColor(if(follow!=null&&follow) ContextCompat.getColor(textView.context,
            com.kissspace.module_common.R.color.color_ui_main_text) else Color.WHITE)
        textView.setBackgroundResource( if(follow!=null&&follow) com.kissspace.module_common.R.drawable.common_shape_bg_gray_28 else com.kissspace.module_common.R.drawable.common_shape_bg_gradient )
        textView.text =  ( if(follow!=null&&follow)"已关注" else "+ 关注")
    }

    @JvmStatic
    @BindingAdapter("dynamicLikeStatus", requireAll = false)
    fun dynamicLikeStatus(textView: TextView, status:Boolean) {
        textView.setDrawable(if (status) R.mipmap.dynamic_like else R.mipmap.dynamic_unlike_in_list,
            Gravity.START)
    }

    @JvmStatic
    @BindingAdapter("dynamicLikeAmount", requireAll = false)
    fun dynamicLikeAmount(textView: TextView, amount:Int) {
        textView.text = amount.toString()
    }

    @JvmStatic
    @BindingAdapter("dynamicFollowVisible", requireAll = false)
    fun dynamicFollowVisible(textView: TextView, model:DynamicInfoRecord?) {
        textView.visibility = if (model?.userId==MMKVProvider.userId) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("isReviewVisible", requireAll = false)
    fun isReviewVisible(view: View, model:DynamicInfoRecord?) {
        view.visibility = if (model!=null&&model.userId==MMKVProvider.userId &&
            (model.pictureContentAuditStatus  == "002"||
                    model.textContentAuditStatus  == "002"||
                    model.voiceContentAuditStatus  == "002"
                    )
            ) View.VISIBLE else View.GONE
    }





}