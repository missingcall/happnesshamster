package com.kissspace.room.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.fragment.app.viewModels
import com.drake.net.scope.AndroidScope
import com.drake.net.utils.scope
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.handleGiftMessage
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.model.GiftBean
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.model.immessage.GiftMessage
import com.kissspace.common.model.immessage.GiftSourceInfo
import com.kissspace.util.dp
import com.kissspace.common.model.immessage.UserEnterMessage
import com.kissspace.common.ui.BosonFriendDialog
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.setApplicationValue
import com.kissspace.common.widget.CircularProgressView
import com.kissspace.common.widget.UserLevelIconView
import com.kissspace.module_room.R
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.room.viewmodel.GiftViewModel
import com.kissspace.util.ellipsizeString
import com.kissspace.util.loadImage
import com.kissspace.util.loadImageCircle
import com.kissspace.util.logE
import com.kissspace.util.screenWidth
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.exp

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/8
 * @Description: 坐骑用户进场消息动画view
 *
 */
class UserLuckyBagView : LinearLayout {
    private var avatarImage: ImageView
    //    private var incomeLeve: UserLevelIconView
//    private var expendLevel: UserLevelIconView
    private var nicknameText: TextView

    private var pb_fudai: CircularProgressView
    private var crId :String ? = null
    constructor(context: Context) : super(context, null) {

    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {

    }

    var count = 0L
    var job: AndroidScope?=null
    var tags : List<String> ? = null

    private var giftInfo : GiftInfo ?= null
    private var gitNum:Int?=null
    private var giftType = "005"
    @SuppressLint("SetTextI18n")
    fun setNum(tags:List<String>,number:Int, giftInfo: GiftInfo,crId :String ?,giftType:String){

        visibility = VISIBLE
        logE("tags："+tags)
        this.giftType = giftType
        this.tags = tags
        this.crId = crId
        this.giftInfo = giftInfo
        this.
        job?.cancel()
        gitNum = number
        count += number
        nicknameText.text = "x$count"
        startNumAnim(nicknameText)
        job = scope {
            //delay(2000L) // 等待2秒
            val time = System.currentTimeMillis()
            for (i in 100 downTo 1) {
                pb_fudai.setProgress(i)
                // 延迟20毫秒，以模拟进度条的逐渐减少（总共2秒）
                delay(20L)
            }
            count = 0L // 如果还活着，就将count清零
            visibility = GONE
            logE("time::"+(System.currentTimeMillis()-time))
        }
    }

    init {
        View.inflate(context, R.layout.room_view_user_luck_coin, this)
        avatarImage = findViewById(R.id.iv_luck_coin)
        nicknameText = findViewById(R.id.tv_luck_coin)
        pb_fudai = findViewById(R.id.pb_fudai)
        avatarImage.safeClick(200) {
            scope {
                sendGift()
            }
        }
    }

    private fun sendGift() {
        val giftSource = giftType
        val targetUserIds = JSONArray()
        tags?.forEach {
            targetUserIds.put(it)
        }
        val giveGiftInfo = JSONArray()
        val json = JSONObject()
        json.put("giftId", giftInfo?.giftId)
        json.put("giftNum", gitNum?:1)
        giveGiftInfo.put(json)

        val param = mutableMapOf<String, Any?>()
        crId?.let {
            param["chatRoomId"] = it
        }
        param["sourceUserId"] = MMKVProvider.userId
        param["targetUserIds"] = targetUserIds
        param["giveGiftInfos"] = giveGiftInfo
        param["giftSource"] = giftSource
        setApplicationValue(
            type = Constants.TypeFaceRecognition,
            value = Constants.FaceRecognitionType.CONSUMPTION.type.toString()
        )
        request<GiftMessage>("/hamster-user/giftInfo/giveGiftForSomebody", Method.POST, param,
            onSuccess = {
                getUserInfo(onSuccess = {})
                handleGiftMessage(it,null)
            },
            onError = {
                customToast(it.message)
            })
    }

    private var lastAnimator: Animator? = null
    private fun startNumAnim(view: View?) {
        if (lastAnimator != null) {
            lastAnimator?.removeAllListeners()
            lastAnimator?.end()
            lastAnimator?.cancel()

        }
        val anim1 = ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1.3f, 1f)
        val anim2 = ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1.3f, 1f)
        val animSet = AnimatorSet()
        lastAnimator = animSet
        animSet.setDuration(400)
        animSet.interpolator = OvershootInterpolator()
        animSet.playTogether(anim1, anim2)
        animSet.start()
    }
    fun initData(message: UserEnterMessage) {
        avatarImage.loadImage(message.profilePath)
        nicknameText.text = message.nickname.ellipsizeString(6)
    }

}