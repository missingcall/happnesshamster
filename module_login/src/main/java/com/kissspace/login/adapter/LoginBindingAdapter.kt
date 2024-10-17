package com.kissspace.login.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ResourceUtils
import com.kissspace.common.config.Constants
import com.kissspace.module_login.R

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/17
 * @Description: 登录模块的BindingAdapter
 *
 */
object LoginBindingAdapter {

    @JvmStatic
    @BindingAdapter("phoneIconStatus", requireAll = false)
    fun phoneIconStatus(imageView: ImageView, isChecked: Boolean = false) {
        imageView.setImageResource(if (isChecked) R.mipmap.login_icon_phone_selected else R.mipmap.login_icon_phone_normal)
    }

    @JvmStatic
    @BindingAdapter("userMaleBg", requireAll = false)
    fun userMaleBg(imageView: ImageView, sex: String? = Constants.SEX_MALE) {
        imageView.setImageResource(if (sex == Constants.SEX_MALE) com.kissspace.module_common.R.drawable.common_shape_blue_selected_15 else com.kissspace.module_common.R.drawable.common_shape_blue_normal_15)
    }

    @JvmStatic
    @BindingAdapter("userFemaleBg", requireAll = false)
    fun userFemaleBg(imageView: ImageView, sex: String? = Constants.SEX_MALE) {
        imageView.setImageResource(if (sex == Constants.SEX_MALE) com.kissspace.module_common.R.drawable.common_shape_blue_normal_15 else com.kissspace.module_common.R.drawable.common_shape_blue_selected_15)
    }

    @JvmStatic
    @BindingAdapter("userMaleIconBg", requireAll = false)
    fun userMaleIconBg(textView: TextView, sex: String? = Constants.SEX_MALE) {
        var drawableLeft = if (sex == Constants.SEX_MALE) {
            ResourceUtils.getDrawable(R.mipmap.login_icon_sex_male)
        } else {
            ResourceUtils.getDrawable(R.mipmap.login_icon_sex_male)
        }
        var textColor = if (sex == Constants.SEX_MALE) {
            ColorUtils.getColor(com.kissspace.module_common.R.color.white)
        } else {
            ColorUtils.getColor(com.kissspace.module_common.R.color.white)
        }
        drawableLeft.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
        textView.setCompoundDrawables(drawableLeft, null, null, null)
        textView.compoundDrawablePadding = 10
        textView.setTextColor(textColor)
    }

    @JvmStatic
    @BindingAdapter("userFemaleIconBg", requireAll = false)
    fun userFemaleIconBg(textView: TextView, sex: String? = Constants.SEX_MALE) {
        var drawableLeft = if (sex == Constants.SEX_MALE) {
            ResourceUtils.getDrawable(R.mipmap.login_icon_sex_female)
        } else {
            ResourceUtils.getDrawable(R.mipmap.login_icon_sex_female)
        }
        var textColor = if (sex == Constants.SEX_MALE) {
            ColorUtils.getColor(com.kissspace.module_common.R.color.white)
        } else {
            ColorUtils.getColor(com.kissspace.module_common.R.color.white)
        }
        drawableLeft.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
        textView.setCompoundDrawables(drawableLeft, null, null, null)
        textView.compoundDrawablePadding = 10
        textView.setTextColor(textColor)
    }

    @JvmStatic
    @BindingAdapter("agreementState", requireAll = false)
    fun agreementState(imageView: ImageView, isAgree: Boolean) {
        imageView.setImageResource(if (isAgree) R.mipmap.login_icon_agreement_selected else R.mipmap.login_icon_agreement_normal)
    }


}