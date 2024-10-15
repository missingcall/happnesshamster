package com.kissspace.room.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.contains
import coil.load
import com.drake.net.scope.AndroidScope
import com.drake.net.utils.scope
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.model.immessage.GiftSourceInfo
import com.kissspace.module_room.R
import com.kissspace.util.dp
import com.kissspace.util.isNotEmpty
import com.kissspace.util.logE
import com.kissspace.util.pxToDp
import com.kissspace.util.runOnUi
import com.kissspace.util.screenWidth
import kotlinx.coroutines.delay
import kotlin.math.log


/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/8
 * @Description: 坐骑用户进场消息动画view
 */
class UserLuckyBagNetView : LinearLayout {

    private var hitLayout: LinearLayout
    val giftJob = mutableMapOf<String, Gift>()

    val giftView = mutableMapOf<String,View>()




    constructor(context: Context) : super(context, null) {

    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {

    }


    private var lastAnimator: Animator? = null
    private fun startNumAnim(view: View?) {
        if (lastAnimator != null) {
            lastAnimator?.removeAllListeners()
            lastAnimator?.end()
            lastAnimator?.cancel()
        }
        val anim1 = ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1.5f, 1f)
        val anim2 = ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1.5f, 1f)
        val animSet = AnimatorSet()
        lastAnimator = animSet
        animSet.setDuration(500)
        animSet.interpolator = OvershootInterpolator()
        animSet.playTogether(anim1, anim2)
        animSet.start()
    }


    init {
        View.inflate(context, R.layout.room_view_user_luck_coin_net, this)
        hitLayout = findViewById(R.id.layout_hit)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    @Synchronized
    fun  setNum(source: GiftSourceInfo,tagName:String,userTagId:String, giftInfo: GiftInfo){
        val gift : Gift
        val key = source.userId+userTagId
        if (giftJob.containsKey(key)) {
            // 如果在，则增加其礼物数量
            gift = giftJob[key]!!
            gift.job?.cancel()
        }else{
            gift = Gift(key,null,0).also {
                giftJob[key] = it
            }
        }
        gift.giftNum = gift.giftNum+giftInfo.total
        gift.job = scope {
            val time = System.currentTimeMillis()
            delay(2207L) // 等待2秒
            giftJob.remove(key)
            if(giftView.containsKey(key)){
                removeView(giftView.remove(key))
                logE("time:net:"+(System.currentTimeMillis()-time))
//                    .also {
//                    it?.let {
//                        it1 -> startRemoveAnimation(it1) }
//               })
            }
        }

        val sortedByGiftCountDescending = giftJob.toList().sortedByDescending { it.second.giftNum }.take(3)


        val sortedByGiftMap = sortedByGiftCountDescending.toMap()

        val list = mutableMapOf<String,View>()



        giftView.forEach { (sender, gift) ->
            if(!sortedByGiftMap.containsKey(sender)){
                list[sender] = gift
            }
        }
        logE("childCount:"+(list.size+childCount))
//        if(list.size+childCount>3){
//            val sort =  list.toList().sortedBy { it.second.tag as Int }.take(list.size+childCount-3)
//            sort.forEach {
//                (sender, gift) ->
//                  removeView(giftView.remove(sender))
//            }
//        }

        sortedByGiftCountDescending.forEach { (sender, gift) ->
            if (giftView.containsKey(sender)) {
                val textView = giftView[sender]?.findViewById<TextView>(R.id.tv_gift_num)
                textView?.text = "x"+gift.giftNum
                startNumAnim(textView)
            }else{
                if(childCount>2&&list.isNotEmpty()){
                    val sort =  list.toList().sortedBy { it.second.tag as Int }.take(1)
                    logE("sort:"+sort.isNotEmpty())
                    if(sort.isNotEmpty()){
                        removeView(giftView.remove(sort[0].first))
                    }
                    list.remove(sort[0].first)
                }
                val child = LayoutInflater.from(context).inflate(R.layout.item_room_hit,null)
                val textView = child.findViewById<TextView>(R.id.tv_gift_num)
                child.findViewById<ImageView>(R.id.iv_head)?.load(source.profilePath)
                child.findViewById<TextView>(R.id.tv_nickname)?.text = source.nickName
                child.findViewById<TextView>(R.id.tv_gift_name)?.text = "送给：$tagName"
                child.tag = gift.giftNum
                textView?.text = "x"+gift.giftNum
                startNumAnim(textView)
                addView(child)
                giftView[sender] = child
                startAnimation(child)
            }
        }
    }


    data class Gift(var sender: String,var job:AndroidScope?,var giftNum:Int)

    private fun startAnimation(tageView: View) {
        val animator1 =
            ObjectAnimator.ofFloat(tageView, "translationX", screenWidth.toFloat() + width, 12.dp)
                .apply {
                    duration = 300
                    interpolator = AccelerateInterpolator()
                    start()
                }



//        AnimatorSet().apply {
//            playSequentially(animator1)
//            addListener(onStart = {
//                this@UserLuckyBagNetView.visibility = View.VISIBLE
//            }, onEnd = {
//                removeAllListeners()
//                this@UserLuckyBagNetView.alpha = 1f
//                this@UserLuckyBagNetView.translationX = screenWidth.toFloat() + width
//                this@UserLuckyBagNetView.visibility = View.INVISIBLE
//            })
//        }.start()

    }

    private fun startRemoveAnimation(tageView: View) {
        // 创建向上移动的动画
        // 创建向上移动的动画
        val translateYAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(tageView, "translationY", 0f, (-42).dp)
        // 创建透明度变化的动画
        // 创建透明度变化的动画
        val alphaAnimator = ObjectAnimator.ofFloat(tageView, "alpha", 1f, 0f)
        // 使用AnimatorSet来同时播放这两个动画
        // 使用AnimatorSet来同时播放这两个动画
        AnimatorSet().apply {
            playTogether(translateYAnimator, alphaAnimator)
            duration = 200 // 设置动画持续时间为1000毫秒
//           addListener(onStart = {
//            }, onEnd = {
//               if(contains(tageView))
//                   removeView(tageView)
//            })
        }.start()



    }
}