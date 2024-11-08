package com.kissspace.common.ext

import android.app.Activity
import android.content.Context
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import cc.shinichi.library.ImagePreview
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.drake.brv.utils.bindingAdapter
import com.drake.net.utils.scope
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.NewBieBean
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.model.immessage.GiftMessage
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.util.getDrawable
import com.kissspace.util.isNotEmpty
import com.kissspace.util.statusBarHeight
import com.qmuiteam.qmui.widget.QMUIRadiusImageView2
import kotlinx.coroutines.Dispatchers

object ViewClickDelay {
    var lastClickTime: Long = 0
    var SPACE_TIME: Long = 500
}


//点击防抖
fun View.safeClick(spaceTime:Long =ViewClickDelay.SPACE_TIME ,action: () -> Unit) {
    this.setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - ViewClickDelay.lastClickTime > spaceTime) {
            ViewClickDelay.lastClickTime = System.currentTimeMillis()
            action()
        }
    }
}

fun canSendMsgToOther():Boolean{
    return true
   /* return MMKVProvider.userInfo?.let {
        if (!it.family || it.consumeLevel <= 1) {
            customToast("财富等级达到2级并且加入公会后才可以发送私信哦~")
            false
        }else{
            true
        }
    }?:false*/

}

@Synchronized
fun handleGiftMessage(giftMessage: GiftMessage, invoke: ((gift:GiftInfo,nickName:String,userIcon:String) -> Unit)?,  fdCallBack: (() -> Unit)? = null){
    giftMessage.luckyBagGoldCoins?.let {
        customToast("获得金币$it")
    }

    scope (Dispatchers.IO) {
        val tagsId = mutableListOf<String>()
        var fDGift: GiftInfo? = null
        var giftType = "005"
        fDGift = null
        giftMessage.targetUsers.let {
            if (it.isNotEmpty()) {
                for (giftTargetInfo in it) {
                    if (giftMessage.sourceUser.userId == MMKVProvider.userId) {
                        tagsId.add(giftTargetInfo.userId)
                    }

                    if (fDGift == null) {
                        giftTargetInfo.giftInfos[0].let { giftInfo ->
                            if (giftInfo.giftType == "106" || giftMessage.giftSource == "005") {
                                fDGift = giftInfo
                                giftType = if (giftInfo.giftType == "106") "001" else "005"
                            }
                        }
                    }

                    val gift =
                        giftTargetInfo.giftInfos.firstOrNull { giftInfo -> giftInfo.relationTypeName.isNotEmpty() }
                    gift?.let { giftInfo ->
                        {
                            if (giftInfo.firstBind == true)
                                invoke?.invoke(
                                    giftInfo,
                                    giftTargetInfo.nickName,
                                    giftTargetInfo.avatar
                                )
                            else {
                                customToast("${giftInfo.relationTypeName}羁绊时间增加（${giftInfo.expireDate}天）")
                            }
                        }
                    }
                }

                fDGift?.let { giftInfo ->
                    FlowBus.post(
                        Event.GiveGiftByCoinLocal(
                            tagsId,
                            giftInfo.total,
                            giftInfo,
                            giftType
                        )
                    )
                    fdCallBack?.invoke()
                }
            }
        }
    }
}

fun RecyclerView.scrollToBottom() {
    val models = this.bindingAdapter.models
    this.scrollToPosition(models?.size?.minus(1) ?: 0)
}


fun TextView.setDrawable(resourceId: Int, gravity: Int) {
    val drawable = getDrawable(resourceId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    when (gravity) {
        Gravity.TOP -> this.setCompoundDrawables(null, drawable, null, null)
        Gravity.START -> this.setCompoundDrawables(drawable, null, null, null)
        Gravity.END -> this.setCompoundDrawables(null, null, drawable, null)
        Gravity.BOTTOM -> this.setCompoundDrawables(null, null, null, drawable)
    }
}

fun View.setMarginStatusBar() {
    val lp = this.layoutParams as ViewGroup.MarginLayoutParams
    lp.setMargins(0, statusBarHeight, 0, 0)
    this.layoutParams = lp
}

fun TitleBar.setTitleBarListener(
    onLeftClick: (() -> Unit)? = null,
    onRightClick: (() -> Unit)? = null
) {
    this.setOnTitleBarListener(object : OnTitleBarListener {
        override fun onLeftClick(titleBar: TitleBar?) {
            onLeftClick?.invoke()
        }

        override fun onRightClick(titleBar: TitleBar?) {
            onRightClick?.invoke()
        }
    })
}

fun Activity.setTitleBarListener(
    titleBar: TitleBar,
    leftClick: (() -> Unit) = {},
    rightClick: (() -> Unit) = {}
) {
    titleBar.setOnTitleBarListener(object : OnTitleBarListener {
        override fun onLeftClick(titleBar: TitleBar?) {
            super.onLeftClick(titleBar)
            leftClick.invoke()
            finish()
        }

        override fun onRightClick(titleBar: TitleBar?) {
            super.onRightClick(titleBar)
            rightClick.invoke()
        }
    })
}


fun RecyclerView.isScroll2End(): Boolean {
    if (this.layoutManager is LinearLayoutManager) {
        val linearLayoutManager = this.layoutManager as LinearLayoutManager
        val firstItem = linearLayoutManager.findFirstVisibleItemPosition()
        return this.getChildAt(0).y == 0f && firstItem == 0
    } else if (this.layoutManager is StaggeredGridLayoutManager) {
        val aa =
            (this.layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(null)
        return this.getChildAt(0).y == 0f && aa[0] == 0
    }
    return false
}

fun DslTabLayout.setUpViewPager2(viewPager2: ViewPager2, onPageChanged: (Int) -> Unit) {
    ViewPager2Delegate.install(viewPager2, this)
    this.configTabLayoutConfig {
        onSelectItemView = { _, index, selected, _ ->
            if (selected) {
                onPageChanged(index)
            }
            false
        }
    }
}


fun EditText.showSoftInput() {
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    if (isSystemInsetsAnimationSupport()) {
        ViewCompat.getWindowInsetsController(this)?.show(WindowInsetsCompat.Type.ime())
    } else {
        postDelayed({
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, 0)
        }, 300)
    }
}

/** 判断系统是否支持[WindowInsetsAnimationCompat] */
internal fun View.isSystemInsetsAnimationSupport(): Boolean {
    val windowInsetsController = ViewCompat.getWindowInsetsController(this)
    return !(windowInsetsController == null || windowInsetsController.systemBarsBehavior == 0)
}

fun previewPicture(
    activity: Activity,
    modelPosition: Int,
    target: QMUIRadiusImageView2?,
    imageList: MutableList<String>
) {
    ImagePreview.instance
        .setContext(activity)
        .setIndex(modelPosition)
        .setImageList(imageList)
        .setTransitionView(target)
        .setTransitionShareElementName("shared_element_container")
        .setEnableUpDragClose(true)
        .setEnableDragClose(true)
        .setEnableDragCloseIgnoreScale(true)
        .setShowDownButton(false)
        .setShowIndicator(false)
        .start()
}




