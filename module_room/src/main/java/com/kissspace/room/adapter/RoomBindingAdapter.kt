package com.kissspace.room.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannedString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.animation.content.Content
import com.angcyo.tablayout.DslTabLayout
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kissspace.common.config.Constants
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.GiftModel
import com.kissspace.common.model.MicUserModel
import com.kissspace.common.model.NormalGiftModel
import com.kissspace.common.model.PredictionHistoryBean
import com.kissspace.common.model.PredictionListBean
import com.kissspace.common.model.immessage.RoomChatMessageModel
import com.kissspace.common.util.*
import com.kissspace.common.util.format.Format
import com.kissspace.common.util.glide.GlideApp
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.EasyPagImageView
import com.kissspace.common.widget.UserLevelIconView
import com.kissspace.module_room.R
import com.kissspace.room.widget.PredictionProgressView
import com.kissspace.room.widget.RoomSettingDialogV2
import com.kissspace.util.dp
import com.kissspace.util.ellipsizeString
import com.kissspace.util.loadImage
import com.kissspace.util.loadImageCircle
import com.kissspace.util.loadImageWithDefault
import com.kissspace.util.logE
import com.kissspace.util.orZero
import com.kissspace.util.resToColor
import org.libpag.PAGImageView
import org.libpag.PAGImageView.PAGImageViewListener
import java.util.regex.Pattern


object RoomBindingAdapter {

    @JvmStatic
    @BindingAdapter("giftChecked", requireAll = false)
    fun giftChecked(root: ConstraintLayout, checked: Boolean = false) {
        root.setBackgroundResource(if (checked) R.drawable.room_bg_gift_selected else R.drawable.room_bg_gift_normal)
    }

    @JvmStatic
    @BindingAdapter("micIncomeVisibility", requireAll = false)
    fun micIncomeVisibility(layout: LinearLayout, isShow: String) {
        layout.visibility = if (isShow == "001") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("roomSendChatBtn", requireAll = false)
    fun roomSendChatBtn(imageView: ImageView, enable: Boolean) {
        imageView.setImageResource(if (enable) R.mipmap.room_icon_send_chat_selected else R.mipmap.room_icon_send_chat_normal)
    }

    @JvmStatic
    @BindingAdapter("micPKValueVisibility", requireAll = false)
    fun micPKValueVisibility(layout: LinearLayout, isShow: Boolean) {
        layout.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("giftLock", requireAll = false)
    fun giftLock(imageView: ImageView, model: NormalGiftModel) {
        imageView.setImageResource(if (model.isDark) R.mipmap.room_icon_gift_lock_white else R.mipmap.room_icon_gift_lock_black)
        imageView.visibility =
            if (model.isPrivilegeGift && MMKVProvider.privilege == "001") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("giftLockDark", requireAll = false)
    fun giftLockDark(imageView: ImageView, model: GiftModel) {
        logE("giftName:"+model.name+"  lockFlag:"+model.lockFlag)
        imageView.setImageResource(R.mipmap.room_icon_gift_lock_white)
        imageView.visibility =
            if ((model.isPrivilegeGift && MMKVProvider.privilege == "001")|| model.locked || model.isGiftLock()) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("giftLockLight", requireAll = false)
    fun giftLockLight(imageView: ImageView, model: GiftModel) {
        logE("giftName:"+model.name+"  lockFlag:"+model.lockFlag)
        imageView.setImageResource(R.mipmap.room_icon_gift_lock_black)
        imageView.visibility =
            if ((model.isPrivilegeGift && MMKVProvider.privilege == "001") || model.locked || model.isGiftLock()) View.VISIBLE else View.GONE
    }


    @JvmStatic
    @BindingAdapter("micIncomeValue", requireAll = false)
    fun micIncomeValue(textView: TextView, value: Long) {
        val str = when (value) {
            0L -> "0"
            in 1..9999 -> value.toString()
            in 10000..99999999 -> {
                Format.O_OO.format(value.toDouble() / 10000).toString() + "万"
            }

            else -> {
                Format.O_OO.format(value.toDouble() / 100000000).toString() + "亿"
            }
        }
        textView.text = str
    }

    @JvmStatic
    @BindingAdapter("isShowManagerIcon", requireAll = false)
    fun isShowManagerIcon(imageView: ImageView, role: String) {
        imageView.visibility =
            if (role != Constants.ROOM_USER_TYPE_NORMAL) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("userIsOnMic", requireAll = false)
    fun userIsOnMic(imageView: TextView, isOnMic: Boolean = false) {
        imageView.visibility = if (isOnMic) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("checkBoxState", requireAll = false)
    fun checkBoxState(imageView: ImageView, checked: Boolean = false) {
        imageView.setImageResource(if (checked) R.mipmap.room_icon_checkbox_checked else R.mipmap.room_icon_checkbox_normal)
    }

    @JvmStatic
    @BindingAdapter("sendAllChecked", requireAll = false)
    fun sendAllChecked(textView: TextView, checked: Boolean = false) {
        textView.setBackgroundResource(if (checked) R.mipmap.room_bg_gift_send_all_checked else R.mipmap.room_bg_gift_send_all_normal)
    }

    @JvmStatic
    @BindingAdapter("leftWinVisibility", requireAll = false)
    fun leftWinVisibility(imageView: ImageView, whichWin: String) {
        imageView.visibility = if (whichWin == "001") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("rightWinVisibility", requireAll = false)
    fun rightWinVisibility(imageView: ImageView, whichWin: String) {
        imageView.visibility = if (whichWin == "002") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("invalidVisibility", requireAll = false)
    fun invalidVisibility(imageView: ImageView, model: PredictionListBean) {
        imageView.visibility =
            if (model.state == "004" || model.whichWin == "003") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("predictionListTitle", requireAll = false)
    fun predictionListTitle(textView: TextView, predictionListBean: PredictionListBean) {
        textView.text = predictionListBean.title
        val lp = textView.layoutParams as LinearLayout.LayoutParams
        if (predictionListBean.isShowTitle) {
            lp.height = 54.dp.toInt()
        } else {
            lp.height = 16.dp.toInt()
        }
        textView.layoutParams = lp

    }

    @JvmStatic
    @BindingAdapter("betItemBackGround", requireAll = false)
    fun betItemBackGround(layout: LinearLayout, isChecked: Boolean = false) {
        layout.setBackgroundResource(if (isChecked) R.drawable.room_bg_integral_item_selected else R.drawable.room_bg_integral_item_normal)
    }

    @JvmStatic
    @BindingAdapter("betItemText", requireAll = false)
    fun betItemText(textView: TextView, amount: Long) {
        textView.text = if (amount == 0L) "全部积分" else amount.toString()
    }

    @JvmStatic
    @BindingAdapter("leftQuestionTextColor", requireAll = false)
    fun leftQuestionTextColor(textView: TextView, model: PredictionListBean) {
        val color = when (model.state) {
            "001" -> {
                if (model.userBetOption == "001" || model.userBetOption.isEmpty()) {
                    com.kissspace.module_common.R.color.common_white.resToColor()
                } else com.kissspace.module_common.R.color.color_80FFFFFF.resToColor()
            }

            else -> {
                if (model.userBetOption == "001") {
                    com.kissspace.module_common.R.color.common_white.resToColor()
                } else com.kissspace.module_common.R.color.color_80FFFFFF.resToColor()
            }
        }
        textView.setTextColor(color)
    }

    @JvmStatic
    @BindingAdapter("rightQuestionTextColor", requireAll = false)
    fun rightQuestionTextColor(textView: TextView, model: PredictionListBean) {
        val color = when (model.state) {
            "001" -> {
                if (model.userBetOption == "002" || model.userBetOption.isEmpty()) {
                    com.kissspace.module_common.R.color.common_white.resToColor()
                } else com.kissspace.module_common.R.color.color_80FFFFFF.resToColor()
            }

            else -> {
                if (model.userBetOption == "002") {
                    com.kissspace.module_common.R.color.common_white.resToColor()
                } else com.kissspace.module_common.R.color.color_80FFFFFF.resToColor()
            }
        }
        textView.setTextColor(color)
    }

    @JvmStatic
    @BindingAdapter("userLeftBetEnable", requireAll = false)
    fun userLeftBetEnable(layout: LinearLayout, item: PredictionListBean) {
        val resource = when (item.state) {
            "001" -> {
                if (item.userBetOption == "001" || item.userBetOption.isEmpty()) {
                    R.drawable.room_bg_prediction_question_left_normal
                } else R.drawable.room_bg_prediction_question_left_disable
            }

            else -> {
                if (item.userBetOption == "001") {
                    R.drawable.room_bg_prediction_question_left_normal
                } else R.drawable.room_bg_prediction_question_left_disable
            }
        }
        layout.setBackgroundResource(resource)

    }


    @JvmStatic
    @BindingAdapter("userRightBetEnable", requireAll = false)
    fun userRightBetEnable(layout: LinearLayout, item: PredictionListBean) {
        val resource = when (item.state) {
            "001" -> {
                if (item.userBetOption == "002" || item.userBetOption.isEmpty()) {
                    R.drawable.room_bg_prediction_question_right_normal
                } else R.drawable.room_bg_prediction_question_right_disable
            }

            else -> {
                if (item.userBetOption == "002") {
                    R.drawable.room_bg_prediction_question_right_normal
                } else R.drawable.room_bg_prediction_question_right_disable
            }
        }
        layout.setBackgroundResource(resource)

    }

    @JvmStatic
    @BindingAdapter(value = ["status", "isLeft"])
    fun questionBackground(
        layout: LinearLayout, status: String, isLeft: Boolean = true,
    ) {
        val resource = if (isLeft) {
            if (status == "001") R.drawable.room_bg_prediction_question_left_normal else R.drawable.room_bg_prediction_question_left_disable
        } else {
            if (status == "001") R.drawable.room_bg_prediction_question_right_normal else R.drawable.room_bg_prediction_question_right_disable
        }
        layout.setBackgroundResource(resource)
    }


    @JvmStatic
    @BindingAdapter("loadRoomMicAvatar", requireAll = false)
    fun loadRoomMicAvatar(imageView: ImageView, model: MicUserModel) {
        if (model.wheatPositionId.isEmpty()) {
            if (model.lockWheat) {
                imageView.loadImage(R.mipmap.room_icon_microphone_lock)
            }else if(model.onMicroPhoneNumber==8){
                imageView.loadImage(R.mipmap.room_icon_microphone_guest)
            } else {
                imageView.loadImage(R.mipmap.room_icon_microphone_normal)
            }
        } else {
            imageView.loadImageWithDefault(model.wheatPositionIdHeadPortrait)
        }
    }

    @JvmStatic
    @BindingAdapter("loadVideoRoomMicAvatar", requireAll = false)
    fun loadVideoRoomMicAvatar(imageView: ImageView, model: MicUserModel) {
        if (model.wheatPositionId.isEmpty()) {
            if (model.lockWheat) {
                imageView.loadImage(R.mipmap.room_icon_video_mic_lock)
            } else {
                imageView.loadImage(R.mipmap.room_icon_video_mic_default)
            }
        } else {
            imageView.loadImageCircle(model.wheatPositionIdHeadPortrait)
        }
    }

    @JvmStatic
    @BindingAdapter("videoRoomMicName", requireAll = false)
    fun videoRoomMicName(textView: TextView, model: MicUserModel) {
        if (model.wheatPositionId.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = model.wheatPositionIdName.ellipsizeString(5)
        }
    }

    @JvmStatic
    @BindingAdapter("micIndexImage", requireAll = false)
    fun micIndexImage(imageView: ImageView, bean: MicUserModel) {
        val resource = when (bean.onMicroPhoneNumber) {
            0 -> R.mipmap.room_icon_mic_type_host
            1 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_1
            2 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_2
            3 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_3
            4 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_4
            5 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_5
            6 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_6
            7 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_7
            else -> R.mipmap.room_icon_mic_index_8
        }
        imageView.setImageResource(resource)
    }

    @JvmStatic
    @BindingAdapter("micIndexText", requireAll = false)
    fun micIndexText(textView: TextView, bean: MicUserModel) {
         when (bean.onMicroPhoneNumber) {
            0 -> {
                textView.text = "主持"
                textView.setBackgroundResource(R.drawable.room_shape_bg_zhuchi)
            }
            in 1..7 -> {
                if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR){
                    textView.setBackgroundResource(R.drawable.room_shape_bg_mic)
                    textView.text = "房主"
                }  else{
                    textView.setBackgroundResource(R.drawable.room_shape_bg_mic_nor)
                    textView.text = bean.onMicroPhoneNumber.toString()
                }

            }
//                if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_1
//            2 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_2
//            3 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_3
//            4 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_4
//            5 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_5
//            6 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_6
//            7 -> if (bean.userRole == Constants.ROOM_USER_TYPE_ANCHOR) R.mipmap.room_icon_mic_index_owner else R.mipmap.room_icon_mic_index_7
            else -> {
                textView.text = "嘉宾位"
                textView.setBackgroundResource(R.drawable.room_shape_bg_mic_nor)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("giftUserIndexIcon", requireAll = false)
    fun giftUserIndexIcon(imageView: ImageView, model: MicUserModel) {
        if (model.onMicroPhoneNumber < 0) {
            imageView.visibility = View.GONE
        } else {
            val resource = when (model.onMicroPhoneNumber) {
                0 -> if (model.checked) R.mipmap.room_icon_gift_user_index_host_checked else R.mipmap.room_icon_gift_user_index_host_normal
                1 -> {
                    if (model.userRole == Constants.ROOM_USER_TYPE_ANCHOR) {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_owner_checked else R.mipmap.room_icon_gift_user_index_owner_normal
                    } else {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_1_checked else R.mipmap.room_icon_gift_user_index_1_normal
                    }
                }

                2 -> {
                    if (model.userRole == Constants.ROOM_USER_TYPE_ANCHOR) {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_owner_checked else R.mipmap.room_icon_gift_user_index_owner_normal
                    } else {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_2_checked else R.mipmap.room_icon_gift_user_index_2_normal
                    }
                }

                3 -> {
                    if (model.userRole == Constants.ROOM_USER_TYPE_ANCHOR) {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_owner_checked else R.mipmap.room_icon_gift_user_index_owner_normal
                    } else {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_3_checked else R.mipmap.room_icon_gift_user_index_3_normal
                    }
                }

                4 -> {
                    if (model.userRole == Constants.ROOM_USER_TYPE_ANCHOR) {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_owner_checked else R.mipmap.room_icon_gift_user_index_owner_normal
                    } else {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_4_checked else R.mipmap.room_icon_gift_user_index_4_normal
                    }
                }

                5 -> {
                    if (model.userRole == Constants.ROOM_USER_TYPE_ANCHOR) {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_owner_checked else R.mipmap.room_icon_gift_user_index_owner_normal
                    } else {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_5_checked else R.mipmap.room_icon_gift_user_index_5_normal
                    }
                }

                6 -> {
                    if (model.userRole == Constants.ROOM_USER_TYPE_ANCHOR) {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_owner_checked else R.mipmap.room_icon_gift_user_index_owner_normal
                    } else {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_6_checked else R.mipmap.room_icon_gift_user_index_6_normal
                    }
                }

                7 -> {
                    if (model.userRole == Constants.ROOM_USER_TYPE_ANCHOR) {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_owner_checked else R.mipmap.room_icon_gift_user_index_owner_normal
                    } else {
                        if (model.checked) R.mipmap.room_icon_gift_user_index_7_checked else R.mipmap.room_icon_gift_user_index_7_normal
                    }
                }

                else -> {
                    if (model.checked) R.mipmap.room_icon_gift_user_index_boss_checked else R.mipmap.room_icon_gift_user_index_boss_normal
                }
            }
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(resource)
        }


    }

    @JvmStatic
    @BindingAdapter("micUserName", requireAll = false)
    fun micUserName(textView: TextView, text: String) {
        textView.text = text
        textView.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
    }


    @JvmStatic
    @BindingAdapter("giftUserCheckedVisibly", requireAll = false)
    fun giftUserCheckedVisibly(imageView: ImageView, checked: Boolean) {
        imageView.visibility = if (checked) View.VISIBLE else View.INVISIBLE
    }

//    @JvmStatic
//    @BindingAdapter("roomChatContext", requireAll = false)
//    fun roomChatContext(textView: TextView, content: SpannedString) {
//        textView.text = content
//        textView.movementMethod = LinkMovementMethod.getInstance()
//    }

    @JvmStatic
    @BindingAdapter("roomChatContext", requireAll = false)
    fun roomChatContext(textView: TextView, content: RoomChatMessageModel) {
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        if(content.aitList.isNullOrEmpty()){
            textView.text = content.contentSpan
        }else {
            val spannableString = SpannableString(content.content!!)
            content.aitList?.let {
                //val span = SpanUtils()
                val aitColor = Color.parseColor("#FF8A00")
                val colorSpan =
                    it.forEach { ait ->
                        //val regex = "@[^@\\s]*\\s"
                        val pattern = Pattern.compile(ait.getAitName())
                        val matcher = pattern.matcher(content.contentSpan!!)
                        while (matcher.find()) {
                            val start = matcher.start()
                            val end = matcher.end()
                            LogUtils.e("start:$start, end:$end")
                            spannableString.setSpan(object :ClickableSpan(){
                                override fun onClick(widget: View) {
                                    FlowBus.post(Event.OpenUserInfoDialog(ait.userId))
                                }

                                override fun updateDrawState(ds: TextPaint) {
                                    super.updateDrawState(ds)
                                    ds.color = ds.linkColor
                                    ds.isUnderlineText = false
                                }

                            }, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                            spannableString.setSpan(ForegroundColorSpan(aitColor), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
//                        spannableString.setSpan()
//                        if (start > findEnd) {
//                            content.contentSpan?.substring(findEnd, start)
//                                ?.let { it1 -> span.append(it1) }
//                        }
//                        span.append(it.substring(start, end))
//                            .setForegroundColor().setClickSpan(object :
//                                ClickableSpan() {
//                                override fun onClick(widget: View) {
//                                    FlowBus.post(Event.OpenUserInfoDialog(userId))
//                                }
//
//                                override fun updateDrawState(ds: TextPaint) {
//                                    super.updateDrawState(ds)
//                                    ds.color = ds.linkColor
//                                    ds.isUnderlineText = false
//                                }
//                            })
//                        position += 1
//                        findEnd = end
                        }
                    }
//                content.contentSpan?.let {
//                    val matcher = pattern.matcher(it)
//                    var findEnd = 0
//                    var position = 0
//                    while (matcher.find()) {
//                        val userId = content.aitList!![position]
//                        val start = matcher.start()
//                        val end = matcher.end()
//                        if (start > findEnd) {
//                            content.contentSpan?.substring(findEnd, start)
//                                ?.let { it1 -> span.append(it1) }
//                        }
//                        span.append(it.substring(start, end))
//                            .setForegroundColor(Color.parseColor("#FFFF8A00")).setClickSpan(object :
//                            ClickableSpan() {
//                            override fun onClick(widget: View) {
//                                FlowBus.post(Event.OpenUserInfoDialog(userId))
//                            }
//
//                                override fun updateDrawState(ds: TextPaint) {
//                                    super.updateDrawState(ds)
//                                    ds.color = ds.linkColor
//                                    ds.isUnderlineText = false
//                                }
//                        })
//                        position += 1
//                        findEnd = end
//                    }
//                    content.contentSpan?.substring(findEnd)?.let { it1 -> span.append(it1) }
//                    textView.text = span.create()
//                }
            }
            textView.text =spannableString
        }
    }

    @JvmStatic
    @BindingAdapter("roomChatNickname", requireAll = false)
    fun roomChatNickname(textView: TextView, nickname: String) {
        textView.text = nickname.ellipsizeString(7)
    }

    @JvmStatic
    @BindingAdapter("roomQueueText", requireAll = false)
    fun roomQueueText(textView: TextView, count: Int) {
        textView.text = if (count == 0) "排麦" else count.toString()
    }

    @JvmStatic
    @BindingAdapter("coinBalanceText", requireAll = false)
    fun coinBalanceText(textView: TextView, amount: Double) {
        textView.text = formatNum(amount)
    }

    @JvmStatic
    @BindingAdapter("coinText", requireAll = false)
    fun coinText(textView: TextView, amount: Double) {
        textView.text = formatNumCoin(amount)
    }

    @JvmStatic
    @BindingAdapter("coinIntegralText", requireAll = false)
    fun coinIntegralText(textView: TextView, amount: Double) {
        textView.text = formatNumIntegral(amount)
    }


    @JvmStatic
    @BindingAdapter("managerBadgeVisibility", requireAll = false)
    fun managerBadgeVisibility(imageView: ImageView, role: String?) {
        imageView.visibility =
            if (role == Constants.ROOM_USER_TYPE_MANAGER) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("roomBgCheckedStatus", requireAll = false)
    fun roomBgCheckedStatus(imageView: ImageView, isChecked: Boolean) {
        if (isChecked) {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.mipmap.room_icon_background_checked)
        } else {
            imageView.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("giftCheckedAllStatus", requireAll = false)
    fun giftCheckedAllStatus(textView: TextView, isChecked: Boolean) {
        if (isChecked) {
            textView.text = StringUtils.getString(R.string.room_gift_pack_select_all_cancel)
        } else {
            textView.text = StringUtils.getString(R.string.room_gift_pack_select_all)
        }
    }

    @JvmStatic
    @BindingAdapter("predictionTimeChecked", requireAll = false)
    fun predictionTimeChecked(textView: TextView, checked: Boolean) {
        textView.setTextColor(if (checked) com.kissspace.module_common.R.color.common_white.resToColor() else com.kissspace.module_common.R.color.color_82FFFFFF.resToColor())
        textView.setBackgroundResource(if (checked) R.drawable.room_bg_add_prediciton_time_checked else R.drawable.room_bg_add_prediciton_time_normal)
    }

    @JvmStatic
    @BindingAdapter("predictionButtonEnable", requireAll = false)
    fun predictionButtonEnable(textView: TextView, enable: Boolean) {
        textView.setBackgroundResource(if (enable) R.drawable.room_bg_btn_add_prediction_enable else R.drawable.room_bg_btn_add_prediction_disable)
    }

    @JvmStatic
    @BindingAdapter("finishBetVisibility", requireAll = false)
    fun finishBetVisibility(view: TextView, state: String) {
        view.visibility = if (state == "001") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("settleBetVisibility", requireAll = false)
    fun settleBetVisibility(view: TextView, state: String) {
        view.visibility = if (state == "002") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("deleteBetVisibility", requireAll = false)
    fun deleteBetVisibility(view: TextView, state: String) {
        view.visibility = if (state == "003" || state == "004") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("betAmount", requireAll = false)
    fun betAmount(view: TextView, amount: Double) {
        view.text = formatNum(amount)
    }

    @JvmStatic
    @BindingAdapter("giftNameTextColor", requireAll = false)
    fun giftNameTextColor(textView: TextView, isDark: Boolean) {
        textView.setTextColor(
            if (isDark) com.kissspace.module_common.R.color.common_white.resToColor() else com.kissspace.module_common.R.color.color_313133.resToColor()
        )
    }


    @JvmStatic
    @BindingAdapter("giftPriceTextColor", requireAll = false)
    fun giftPriceTextColor(textView: TextView, isDark: Boolean) {
        textView.setTextColor(
            if (isDark) com.kissspace.module_common.R.color.color_B3FFFFFF.resToColor() else com.kissspace.module_common.R.color.color_949499.resToColor()
        )
    }

    @JvmStatic
    @BindingAdapter("giftPriceVisible", requireAll = false)
    fun giftPriceVisible(textView: TextView, number: Int) {
        textView.visibility = if (number > 0) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("giftFreeCountVisible", requireAll = false)
    fun giftFreeCountVisible(textView: TextView, number: Int) {
        textView.visibility = if (number > 0) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("roomMessageUnReadCount", requireAll = false)
    fun roomMessageUnReadCount(textView: TextView, count: Int) {
        val text = if (count > 99) "99+" else count.toString()
        textView.text = text
        textView.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("predictionIncomeText", requireAll = false)
    fun predictionIncomeText(textView: TextView, bean: PredictionHistoryBean) {
        textView.text = if (bean.state != "004") bean.outputIntegral.toString() else "失效"
        textView.setTextColor(if (bean.outputIntegral > 0) com.kissspace.module_common.R.color.color_FEC238.resToColor() else com.kissspace.module_common.R.color.color_FF0026.resToColor())
    }

    @JvmStatic
    @BindingAdapter("predictionCreateTime", requireAll = false)
    fun predictionCreateTime(textView: TextView, data: PredictionListBean) {
        textView.text = TimeUtils.millis2String(data.createTime)
        textView.visibility =
            if (data.state == "003" || data.state == "004") View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("predictionState", requireAll = false)
    fun predictionState(textView: TextView, state: PredictionListBean) {
        textView.text = when (state.state) {
            "001" -> {
                state.leftTime.parse2CountDown() + "截止"
            }

            "002" -> {
                "等待结算"
            }

            "003" -> {
                if (state.creatorId == MMKVProvider.userId) "已结束" else if (state.userBetOption.isEmpty()) "未参与" else "已结束"
            }

            else -> {
                "已失效"
            }
        }
    }


    @JvmStatic
    @BindingAdapter("predictionProgress", requireAll = false)
    fun predictionProgress(progress: PredictionProgressView, model: PredictionListBean) {
        when {
            model.leftBetAmount == 0 && model.rightBetAmount == 0 -> {
                progress.postDelayed({
                    progress.setProgress(0.5f, 0.5f)
                }, 100)
            }

            model.leftBetAmount > 0 && model.rightBetAmount == 0 -> {
                progress.postDelayed({
                    progress.showOnlyLeftProgress()
                }, 100)
            }

            model.leftBetAmount == 0 && model.rightBetAmount > 0 -> {
                progress.postDelayed({
                    progress.showOnlyRightProgress()
                }, 100)
            }

            else -> {
                val total = model.leftBetAmount + model.rightBetAmount
                progress.postDelayed({
                    progress.setProgress(
                        model.leftBetAmount / total.toFloat(),
                        model.rightBetAmount / total.toFloat()
                    )
                }, 100)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("roomRankIndex", requireAll = false)
    fun roomRankIndex(textView: TextView, index: Int) {
        when (index) {
            0 -> textView.setBackgroundResource(R.mipmap.room_icon_ranking_first)
            1 -> textView.setBackgroundResource(R.mipmap.room_icon_ranking_second)
            2 -> textView.setBackgroundResource(R.mipmap.room_icon_ranking_third)
            else -> textView.text = index.toString()
        }
    }

    @JvmStatic
    @BindingAdapter("roomRankIndex", requireAll = false)
    fun gift(textView: TextView, index: Int) {
        when (index) {
            0 -> textView.setBackgroundResource(R.mipmap.room_icon_ranking_first)
            1 -> textView.setBackgroundResource(R.mipmap.room_icon_ranking_second)
            2 -> textView.setBackgroundResource(R.mipmap.room_icon_ranking_third)
            else -> textView.text = index.toString()
        }
    }

    @JvmStatic
    @BindingAdapter("giftDialogBackground", requireAll = false)
    fun giftDialogBackground(layout: ConstraintLayout, isDark: Boolean) {
        layout.setBackgroundResource(if (isDark) R.drawable.room_bg_gift_dialog_black else R.drawable.room_bg_gift_dialog_white)
    }

    @JvmStatic
    @BindingAdapter("giftDialogUserVisible", requireAll = false)
    fun giftDialogUserVisible(layout: ConstraintLayout, isDark: Boolean) {
        layout.visibility = if (isDark) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("giftDialogBalanceBackground", requireAll = false)
    fun giftDialogBalanceBackground(layout: ViewGroup, isDark: Boolean) {
        layout.setBackgroundResource(if (isDark) R.drawable.room_bg_gift_dialog_balance_dark else R.drawable.room_bg_gift_dialog_balance_light)
    }

    @JvmStatic
    @BindingAdapter("giftDialogBalanceColor", requireAll = false)
    fun giftDialogBalanceColor(textView: TextView, isDark: Boolean) {
        textView.setTextColor(if (isDark) com.kissspace.module_common.R.color.common_white.resToColor() else com.kissspace.module_common.R.color.color_313133.resToColor())
    }

    @JvmStatic
    @BindingAdapter("giftDialogCountImage", requireAll = false)
    fun giftDialogCountImage(imageView: ImageView, isDark: Boolean) {
        imageView.setImageResource(if (isDark) R.mipmap.room_icon_gift_count_dark else R.mipmap.room_icon_gift_count_light)
    }

    @JvmStatic
    @BindingAdapter("giftTabTextColor", requireAll = false)
    fun giftTabTextColor(tabLayout: DslTabLayout, isDark: Boolean) {
        tabLayout.tabLayoutConfig?.tabDeselectColor =
            com.kissspace.module_common.R.color.color_949499.resToColor()
        tabLayout.tabLayoutConfig?.tabSelectColor =
            if (isDark) com.kissspace.module_common.R.color.common_white.resToColor() else com.kissspace.module_common.R.color.color_313133.resToColor()
    }

    @JvmStatic
    @BindingAdapter("roomChatOfficialVisible", requireAll = false)
    fun roomChatOfficialVisible(imageView: ImageView, state: String) {
        imageView.visibility = if (state == "002") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("packGiftCountVisible", requireAll = false)
    fun packGiftCountVisible(textView: TextView, isPack: Boolean) {
        textView.visibility = if (isPack) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("micTalking", requireAll = false)
    fun micTalking(lottie: LottieAnimationView, isTalk: Boolean) {
        if (isTalk && !lottie.isAnimating) {
            lottie.visibility = View.VISIBLE
            lottie.playAnimation()
        } else {
            lottie.visibility = View.INVISIBLE
            lottie.pauseAnimation()
        }
    }

    @JvmStatic
    @BindingAdapter("musicPlay", requireAll = false)
    fun musicPlay(lottie: LottieAnimationView, isTalk: Boolean) {
        if (isTalk && !lottie.isAnimating) {
            lottie.visibility = View.VISIBLE
            lottie.playAnimation()
        } else {
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
        }
    }

    @JvmStatic
    @BindingAdapter("giftUserNicknameVisible", requireAll = false)
    fun giftUserNicknameVisible(textView: TextView, position: Int) {
        textView.visibility = if (position == -1) View.VISIBLE else View.GONE
    }


    @JvmStatic
    @BindingAdapter("anchorPredictionLeftOption", requireAll = false)
    fun anchorPredictionLeftOption(textView: TextView, model: PredictionListBean) {
        textView.text = "${model.leftOption}(${model.leftTimes}倍)"
    }

    @JvmStatic
    @BindingAdapter("anchorPredictionRightOption", requireAll = false)
    fun anchorPredictionRightOption(textView: TextView, model: PredictionListBean) {
        textView.text = "${model.rightOption}(${model.rightTimes}倍)"
    }

    @JvmStatic
    @BindingAdapter("roomSettingStatus", requireAll = false)
    fun roomSettingStatus(textView: ImageView, model: RoomSettingDialogV2.SettingItem) {
        textView.visibility = if (model.isShowStatus) View.VISIBLE else View.GONE
        textView.setImageResource(if (model.isOpen) R.mipmap.room_icon_setting_status_enable else R.mipmap.room_icon_setting_status_disable)
    }


    @JvmStatic
    @BindingAdapter("loadEmoji", requireAll = false)
    fun loadEmoji(pagFrameLayout: FrameLayout, roomChatMessageModel: RoomChatMessageModel) {
        val pagView = pagFrameLayout.findViewById<EasyPagImageView>(R.id.room_pag_view)
        val pagEndView = pagFrameLayout.findViewById<ImageView>(R.id.room_iv_pag_end)
        val emojiUrl = roomChatMessageModel.emojiUrl

        if(!emojiUrl.isNullOrEmpty()){
            val extension = emojiUrl.substringAfterLast(".", "")
            if(extension.equals("gif", ignoreCase = true)){
                pagEndView.visibility = View.VISIBLE
                pagView.visibility = View.GONE
                GlideApp.with(pagEndView).load(emojiUrl).into(pagEndView)
                return
            }
        }

        if (roomChatMessageModel.isEmojiEnd) {
            pagView.visibility = View.INVISIBLE
            pagEndView.visibility = View.VISIBLE
            pagEndView.setImageResource(roomChatMessageModel.isEmojiImage)
        } else {
            pagView.visibility = View.VISIBLE
            pagEndView.visibility = View.INVISIBLE
            pagView.setRepeatCount(if (roomChatMessageModel.isEmojiLoop) Int.MAX_VALUE else 1)

            if (!emojiUrl.isNullOrEmpty()) {
                getPagPath(pagView,emojiUrl) {
                    if (!roomChatMessageModel.isEmojiLoop) {
                        pagView.addListener(object : PAGImageViewListener {
                            override fun onAnimationStart(p0: PAGImageView?) {
                            }

                            override fun onAnimationEnd(p0: PAGImageView?) {
                                pagView.visibility = View.INVISIBLE
                                pagEndView.visibility = View.VISIBLE
                                roomChatMessageModel.isEmojiEnd = true
                                pagEndView.setImageResource(roomChatMessageModel.isEmojiImage)
                            }

                            override fun onAnimationCancel(p0: PAGImageView?) {
                            }

                            override fun onAnimationRepeat(p0: PAGImageView?) {
                            }

                            override fun onAnimationUpdate(p0: PAGImageView?) {
                            }

                        })
                    }
                    pagView.play(it)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("isShowStartMusic")
    fun isShowStartMusic(imageView: ImageView, isShow: Boolean) {
        imageView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("isShowCollectMusic")
    fun isShowCollectMusic(imageView: ImageView, isShow: Boolean) {
        imageView.visibility = View.GONE ;// if (isShow) View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("isShowCancelMusic")
    fun isShowCancelMusic(imageView: ImageView, isShow: Boolean) {
        imageView.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("playChangeStatus")
    fun playChangeStatus(textView: TextView, isChecked: Boolean) {
        textView.setTextColor(if (isChecked) com.kissspace.module_common.R.color.color_FFFD62.resToColor() else com.kissspace.module_common.R.color.common_white.resToColor())
    }

    @JvmStatic
    @BindingAdapter("loadPAGAvatar")
    fun loadPAGAvatar(pagView: EasyPagImageView, url: String) {
        pagView.visibility = if (url.isNullOrEmpty()) View.GONE else View.VISIBLE
        getPagPath(pagView,url) {
            pagView.play(it)
        }
    }


    @JvmStatic
    @BindingAdapter("showUserMedal")
    fun showUserMedal(layout: com.nex3z.flowlayout.FlowLayout, model: RoomChatMessageModel) {
        layout.removeAllViews()
        if (model.privilege == "002") {
            val image = ImageView(layout.context)
            image.setImageResource(R.mipmap.room_icon_chat_official)
            layout.addView(image)
        }

        if (model.wealthLevel.orZero() > 0) {
            val wealthLevel = UserLevelIconView(layout.context)
            wealthLevel.setLeveCount(UserLevelIconView.LEVEL_TYPE_EXPEND, model.wealthLevel!!)
            layout.addView(wealthLevel)
        }
        if (model.charmLevel.orZero() > 0) {
            val charmLevel = UserLevelIconView(layout.context)
            charmLevel.setLeveCount(UserLevelIconView.LEVEL_TYPE_INCOME, model.charmLevel!!)
            layout.addView(charmLevel)
        }
        model.medalList?.forEach {
            val image = ImageView(layout.context)
            val lp =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 19.dp.toInt())
            lp.marginEnd = 2.dp.toInt()
            image.layoutParams = lp
            image.adjustViewBounds = true
            image.loadImage(it.url)
            layout.addView(image)
        }

        val nickname = TextView(layout.context).apply {
            textSize = 14f
            text = model.nickname
            setTextColor(Color.WHITE)
        }
        layout.addView(nickname)
    }

    @JvmStatic
    @BindingAdapter("chatMessageVisible")
    fun chatMessageVisible(layout: ViewGroup, model: RoomChatMessageModel) {
        layout.visibility =
            if (model.messageKindList.contains(model.chatTabIndex)) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("imageResourceAndNet", requireAll = false)
    fun imageResourceAndNet(imageView: ImageView, item: RoomSettingDialogV2.SettingItem) {
        if (item.icon != 0 || item.iconPath.isEmpty()){
            imageView.setImageResource(item.icon)
        }else{
            imageView.loadImage(item.iconPath)
        }
    }

    @JvmStatic
    @BindingAdapter("pdContent", requireAll = false)
    fun pdContent(textView: TextView, content: String) {
        textView.text = SpanUtils().append(content).append(" 前往").setForegroundColor(ContextCompat.getColor(textView.context,
            com.kissspace.module_common.R.color.color_FE7A67)).create()
    }


}



