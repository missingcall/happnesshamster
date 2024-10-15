package com.kissspace.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.kissspace.common.util.getAnimationPath
import com.kissspace.common.util.glide.GlideRequests
import com.kissspace.module_common.R
import com.kissspace.util.runOnUi
import com.kissspace.util.withStyledAttributes
import com.opensource.svgaplayer.SVGACallback2
import com.opensource.svgaplayer.SVGAImageView
import com.svga.glide.SVGAImageViewDrawableTarget
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.util.ScaleType
import org.libpag.PAGImageView
import java.io.File


/**
 *
 */

class AnimationView(context: Context, attributeSet: AttributeSet?) :
    FrameLayout(context, attributeSet),DefaultLifecycleObserver {

    private var url:String? = null

    private var count = 0

    private  var view:View? = null

    private var animationListener:AnimationListener? = null

    private var svgaImageViewDrawableTarget:SVGAImageViewDrawableTarget? = null
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    private  val attachListen:OnAttachStateChangeListener by lazy {
         object : OnAttachStateChangeListener{
             override fun onViewAttachedToWindow(v: View) {
                 view?.let {
                     when (it) {
                         is AnimView -> {

                         }

                         is EasyPagImageView -> {
                             it.let { animView ->
                                 if(!animView.isPlaying){
                                     animView.play()
                                 }
                             }
                         }

                         is SVGAImageView -> {
                             it.startAnimation()
                         }

                         else -> {

                         }
                     }
                 }
             }

             override fun onViewDetachedFromWindow(v: View) {
                 view?.let {
                     when (it) {
                         is AnimView -> {

                         }
                         is EasyPagImageView -> {
                             it.let { animView ->
                                 if(animView.isPlaying){
                                     animView.pause()
                                 }
                             }
                         }
                         is SVGAImageView -> {
                             it.pauseAnimation()
                         }
                         else -> {

                         }
                     }
                 }
             }

         }
    }


    init {
        withStyledAttributes(attributeSet, R.styleable.AnimationView) {
            url = getString(R.styleable.AnimationView_animation_url)
        }
        initView()
        startAnimation()
    }

    private fun initView() {
          getViewByUrl(url)
    }

    fun play(url:String){
        play(url,1)
    }

    @Synchronized
    fun play(url:String,count:Int){
        this.url?.let {
            if(it ==url)
                return
            }
        if(TextUtils.isEmpty(url)) {
            animationListener?.onComplete()
            return
        }
        onFinish(true)
        runOnUi {
            this.count = count
            this.url = url
            getViewByUrl(url)
            initListener()
            startAnimation()
        }
    }

    fun setAnimationListener(listener: AnimationListener) {
        this.animationListener = listener
    }

    @Synchronized
   private fun onFinish(isForced: Boolean = false){
        try {
            if(isForced) {
                runOnUi {
                    if(childCount>0) {
                        onDestroy()
                        if (view != null) {
                            removeView(view)
                        }
                    }
                }
            }
        }catch (_:Exception){

        }

    }

    private fun initListener(){
        view?.let {
            when (it) {
                is AnimView -> {
                    (view as AnimView).setAnimListener(object : IAnimListener {
                        override fun onFailed(errorType: Int, errorMsg: String?) {
                        }

                        override fun onVideoComplete() {
                            animationListener?.onComplete()
                            onFinish(count == 1)


                        }

                        override fun onVideoDestroy() {

                        }

                        override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
                        }

                        override fun onVideoStart() {
                            animationListener?.onStart()
                        }
                    })
                }

                is EasyPagImageView -> {
                    (view as EasyPagImageView).addListener(object :
                        PAGImageView.PAGImageViewListener {
                        override fun onAnimationStart(p0: PAGImageView?) {
                            animationListener?.onStart()
                        }

                        override fun onAnimationEnd(p0: PAGImageView?) {
                            animationListener?.onComplete()
                            onFinish(count == 1)
                        }

                        override fun onAnimationCancel(p0: PAGImageView?) {

                        }

                        override fun onAnimationRepeat(p0: PAGImageView?) {
                        }

                        override fun onAnimationUpdate(p0: PAGImageView?) {

                        }

                    })
                }

                is ImageView -> {

                }

                else -> {

                }
            }
        }
    }

    override fun isAttachedToWindow(): Boolean {
        return super.isAttachedToWindow()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun getViewByUrl(url: String?){
        url?.let {
            view?.let {
                it.visibility = View.GONE
            }
            view = null
            when (url.substringAfterLast(".")) {
                "mp4" -> {
                    view =  AnimView(context)
                    (view as AnimView).setScaleType(ScaleType.CENTER_CROP)
                }
                "pag" -> {
                    view = EasyPagImageView(context,null)

                }
                "svga" -> {
                    view = ImageView(context)
                }
            }
            view?.let {
                it.isClickable = false
                it.setOnTouchListener { _, _ -> false }
                addView(it, LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT))
            }
        }
    }

    private fun startAnimation(){

        if(view == null){
            animationListener?.onComplete()
            return
        }
        view?.let {
            addOnAttachStateChangeListener(attachListen)
            when (it) {
                is AnimView -> {
                    url?.let {
                        getAnimationPath(view,url!!){ url ->
                            (view as AnimView).let { animView ->
                                animView.setLoop(count)
                                animView.enableVersion1(true)
                                animView.supportMask(isSupport = true, isEdgeBlur = true)
                                animView.startPlay(File(url))
                            }
                        }
                    }
                }

                is EasyPagImageView -> {
                    url?.let {
                        getAnimationPath(view,url!!){ url ->
                            (view as EasyPagImageView).let { easyPagImageView ->
                                easyPagImageView.setLoopCount(count)
                                easyPagImageView.play(url)
                            }
                        }
                    }
                }


                is ImageView -> {
                    url?.let {
                        (Glide.with(this) as GlideRequests).asSVGAResource().load(url).into(
                            SVGAImageViewDrawableTarget(view as ImageView,count, svgaCallback = object:
                                SVGACallback2 {
                                override fun onPause() {
                                }

                                override fun onFinished() {
                                    animationListener?.onComplete()
                                    onFinish(count == 1)
                                }

                                override fun onRepeat() {
                                }

                                override fun onStep(frame: Int, percentage: Double) {

                                }


                            }).apply {
                                svgaImageViewDrawableTarget = this
                            }
                        )
                         //Glide.with(this).`as`(SVG).load(url).into(view as SVGAImageView)
//                        getAnimationPath(view,url!!){ url ->
////                            (view as EasyPagImageView).play(url)
//
//                            Glide.with(this).load(myurl).into(animationView!!)
//
//                            val file = File(url)
//                            inputStream = FileInputStream(file)
//                            val parser = SVGAParser(context)
//                            inputStream?.let { stream ->
//                                parser.decodeFromInputStream(stream,file.name,object : SVGAParser.ParseCompletion {
//                                    override fun onComplete(videoItem: SVGAVideoEntity) {
//                                        val drawable = SVGADrawable(videoItem)
//                                        (view as SVGAImageView).let { svgaImageView ->
//                                            svgaImageView.loops = count
//                                            svgaImageView.setImageDrawable(drawable)
//                                            svgaImageView.startAnimation()
//                                            animationListener?.onStart()
//                                        }
//                                    }
//                                    override fun onError() {
//
//                                    }
//                                })
//                            }
//
//                        }
                    }
                }

                else -> {
                    animationListener?.onComplete()
                }
            }
        }
    }

    @Synchronized
    fun onDestroy() {
        try {
            url?.let {
                url = null
                view?.let { view ->
                    when (view) {
                        is AnimView -> {
                            (this.view as AnimView).let {
                                it.stopPlay()
                                it.getSurfaceTexture()?.release()
                            }
                        }


                        is EasyPagImageView -> {
                            (this.view as EasyPagImageView).clear()
                        }


                        is ImageView -> {
                            svgaImageViewDrawableTarget?.onDestroy()
                            svgaImageViewDrawableTarget = null
                        }

                        else -> {

                        }
                    }
                }
                removeOnAttachStateChangeListener(attachListen)
            }
        }catch (_:Exception){

        }
    }


    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        try {
            onDestroy()
        }catch (_:Exception){

        }

    }

//    fun isRunning(): Boolean {
//
//    }


    interface AnimationListener{
         fun onStart()

         fun onComplete()
    }

}