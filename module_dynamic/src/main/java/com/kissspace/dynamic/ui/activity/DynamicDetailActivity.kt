package com.kissspace.dynamic.ui.activity

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.drake.softinput.hideSoftInput
import com.drake.softinput.setWindowSoftInput
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.CommonApi
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setDrawable
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.checkUserPermission
import com.kissspace.common.model.DynamicsHandlerBean
import com.kissspace.common.model.dynamic.DynamicDetailCommentInfo
import com.kissspace.common.model.dynamic.DynamicDetailLikeInfo
import com.kissspace.common.model.dynamic.DynamicInfoRecord
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.router.parseIntent
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.previewPicture
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.dynamic.http.DynamicApi
import com.kissspace.dynamic.widget.DynamicMenuDialog
import com.kissspace.module_dynamic.R
import com.kissspace.module_dynamic.databinding.DynamicActivityDetailBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.lifecycleOwner
import com.kissspace.util.toast
import java.util.Timer
import java.util.TimerTask


@Router(path = RouterPath.PATH_DYNAMIC_DETAIL)
class DynamicDetailActivity : BaseActivity(R.layout.dynamic_activity_detail) {
    private val mBinding by viewBinding<DynamicActivityDetailBinding>()
    private var dynamicDetail:DynamicInfoRecord?=null // by parseIntent<DynamicInfoRecord>()
    private val dynamicId by parseIntent<String>()
    val videoChanged : MutableLiveData<Int> = MutableLiveData()
//    private val detail:MutableLiveData<DynamicInfoRecord> by lazy { MutableLiveData<DynamicInfoRecord>() }

//    var dynamicDetail : DynamicInfoRecord? = null

//    private var isFollow = false
//    private var isLike = false
    private var isHandleLikeOrFollow = false
//    private var likeNumber = 0

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                handleBackPressed()
            }

            override fun onRightClick(titleBar: TitleBar?) {
                super.onRightClick(titleBar)

                dynamicDetail?.let {
                    checkUserPermission(Constants.UserPermission.PERMISSION_DELETE_DYNAMIC){has->
                        val items = if (it.userId == MMKVProvider.userId) {
                            arrayOf("删除", "取消")
                        } else {
                            if (has) arrayOf("举报", "删除","取消") else arrayOf("举报", "取消")
                        }
                        DynamicMenuDialog(items) { dialog, text ->
                            when (text) {
                                "取消" -> dialog.dismiss()
                                "删除" -> deleteDynamic()
                                "举报" -> {
                                    jump(
                                        RouterPath.PATH_REPORT,
                                        "reportType" to Constants.ReportType.USER.type,
                                        "userId" to it.userId
                                    )
                                }
                            }
                        }.showDialog(supportFragmentManager)
                    }
                }
            }
        })

        showLoading()
        val param = mutableMapOf<String, Any?>().apply {
            put("dynamicId", dynamicId)
        }
        lifecycleOwner.request<DynamicInfoRecord>(DynamicApi.API_DYNAMIC_DETAIL,Method.GET,param,{
            hideLoading()
            bindView(it)
        },{
            hideLoading()
            customToast(it.message)
        })
    }

    private fun bindView(dynamicDetail:DynamicInfoRecord) {
//        detail.postValue(dynamicDetail)
        this.dynamicDetail = dynamicDetail
        mBinding.m = dynamicDetail
//        isLike =dynamicDetail.likeStatus
//        likeNumber = dynamicDetail.numberOfLikes
//        isFollow = dynamicDetail.followStatus
        mBinding.tvLike.text = dynamicDetail.numberOfLikes.toString()
        mBinding.tvComment.text = dynamicDetail.dynamicCommentsList.size.toString()
        mBinding.tvLike.setDrawable(
            if (dynamicDetail.likeStatus) R.mipmap.dynamic_like else R.mipmap.dynamic_unlike_in_list,
            Gravity.START
        )

        mBinding.clVoice.safeClick {
            playMusic(dynamicDetail.voiceDynamicContent)
        }

        mBinding.ivAvatar.safeClick {
            jump(RouterPath.PATH_USER_PROFILE, "userId" to dynamicDetail.userId)
        }

        videoChanged.observe(lifecycleOwner){
             mBinding.msb.setCurrent(it)
             if(it == -1){
                 mBinding.msb.setCurrent(0)
                 mBinding.ivVoice.setImageResource(com.kissspace.module_common.R.mipmap.common_audio_play_icon)
             }
        }



        mBinding.recyclerPicture.grid(3).setup {
            addType<String>(R.layout.dynamic_picture_item)
            onClick(R.id.root) {
                previewPicture(
                    this@DynamicDetailActivity,
                    modelPosition,
                    findView<ImageView>(R.id.root),
                    dynamicDetail.pictureDynamicContent.toMutableList()
                )
            }
        }.models = dynamicDetail.pictureDynamicContent

//        mBinding.tabLayout.configTabLayoutConfig {
//            onSelectItemView = { _, index, selected, _ ->
//                if (selected) {
//                    if (index == 0) {
//                        mBinding.tvAmount.text = "${dynamicDetail.commentAmount}条评论"
//                        requestComment()
//
//                    } else {
//                        mBinding.tvAmount.text = "${dynamicDetail.numberOfLikes}点赞"
//                        requestLike(true)
//                    }
//                }
//                false
//            }
//        }
        mBinding.recyclerView.linear().setup {
            addType<DynamicDetailCommentInfo>(R.layout.dynamic_detail_comment_item)
            addType<DynamicDetailLikeInfo>(R.layout.dynamic_detail_like_item)
        }.mutable = mutableListOf()
        requestComment()

        setWindowSoftInput(float = mBinding.layoutComment, transition = mBinding.layoutComment)


        mBinding.tvLike.safeClick {
            val param = mutableMapOf<String, Any?>()
            param["dynamicId"] = dynamicDetail.dynamicId
            request<Boolean>(
                url = DynamicApi.API_LIKE_DYNAMICS,
                method = Method.POST,
                param = param,
                onSuccess = {
//                    Log.e("likeStatus", "likeStatus:"+it)
//                    Log.e("likeStatus", "likeStatus:"+dynamicDetail.likeStatus)
                      dynamicDetail.likeStatus = !dynamicDetail.likeStatus

                      dynamicDetail.numberOfLikes =dynamicDetail.numberOfLikes + (if (dynamicDetail.likeStatus) 1 else  -1)
                      dynamicDetail.notifyChange()
//                    Log.e("likeStatus", "likeStatus:"+dynamicDetail.likeStatus)
//                    mBinding.tvLike.setDrawable(
//                        if (dynamicDetail.likeStatus) R.mipmap.dynamic_like else R.mipmap.dynamic_unlike_in_list,
//                        Gravity.START
//                    )
                     isHandleLikeOrFollow = true
//                    isLike = !isLike
//                    likeNumber = if (isLike) likeNumber+1 else likeNumber -1
//                    mBinding.tvLike.setDrawable(
//                        if (isLike) R.mipmap.dynamic_like else R.mipmap.dynamic_unlike_in_list,
//                        Gravity.START
//                    )
//                    mBinding.tvLike.text = likeNumber.toString()
                    //requestLike(mBinding.tabLayout.currentItemIndex==1)
                })
        }

        mBinding.ivFollow.safeClick {
                val param = mutableMapOf<String, Any?>("userId" to dynamicDetail.userId)
                request<Int>(if(dynamicDetail.followStatus) CommonApi.API_CANCEL_FOLLOW_USER else CommonApi.API_FOLLOW_USER, if(dynamicDetail.followStatus) Method.GET else Method.POST, param, onSuccess = {
                    isHandleLikeOrFollow = true
//                    isFollow = !isFollow
                    //dynamicDetail.followStatus = !dynamicDetail.followStatus
                    //DynamicBindingAdapter.dynamicFollow(mBinding.ivFollow, isFollow)
                    dynamicDetail.followStatus = !dynamicDetail.followStatus
                    dynamicDetail.notifyChange()
                })
        }

        mBinding.tvSend.safeClick {
            val text = mBinding.editComment.text.toString().trim()
            if (text.isEmpty()) {
                toast("请输入评论内容")
                return@safeClick
            }
            val param = mutableMapOf<String, Any?>(
                "commentContent" to text,
                "dynamicId" to dynamicDetail.dynamicId
            )
            request<Boolean>(DynamicApi.API_ADD_COMMENT, method = Method.POST, param, onSuccess = {
                hideSoftInput()
                mBinding.editComment.setText("")
                toast("评论成功")
                isHandleLikeOrFollow  = true
                requestComment(true)
            })


        }
    }

    private fun getDynamicDetail() : DynamicInfoRecord {
        return dynamicDetail!!
    }

    private fun requestComment(isRefreshRecycler:Boolean = true) {
        val params = mutableMapOf<String, Any?>("dynamicId" to getDynamicDetail().dynamicId)
        request<List<DynamicDetailCommentInfo>>(
            DynamicApi.API_GET_COMMENT_DETAIL,
            method = Method.GET,
            param = params,
            onSuccess = {
                if (isRefreshRecycler){
                    mBinding.recyclerView.mutable.clear()
                    mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
                    mBinding.recyclerView.addModels(it)
                    mBinding.emptyLayout.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                }

                getDynamicDetail().commentAmount = it.size
                getDynamicDetail().notifyChange()
            })
    }

    private fun deleteDynamic() {
        CommonConfirmDialog(this, title = "您确定要删除此动态吗") {
            if (this) {
                val param = mutableMapOf<String, Any?>("dynamicId" to getDynamicDetail().dynamicId)
                request<Boolean>(DynamicApi.API_DELETE_DYNAMIC, Method.POST, param, onSuccess = {
                    customToast("删除成功")
                    finish()
                    FlowBus.post(Event.DynamicsHandlerEvent(DynamicsHandlerBean(type = 2,  getDynamicDetail().dynamicId)))
                })
            }
        }.show()
    }

    private fun requestLike(isRefreshRecycler: Boolean) {
        val params = mutableMapOf<String, Any?>("dynamicId" to getDynamicDetail().dynamicId)
        request<List<DynamicDetailLikeInfo>>(
            DynamicApi.API_GET_DYNAMIC_LILES,
            method = Method.GET,
            param = params,
            onSuccess = {
                if (isRefreshRecycler){
                    mBinding.recyclerView.mutable.clear()
                    mBinding.recyclerView.bindingAdapter.notifyDataSetChanged()
                    mBinding.recyclerView.addModels(it)
                    mBinding.emptyLayout.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                }
                mBinding.tvLike.text = it.size.toString()
            })
    }

    private var timer: Timer? = null
    var mediaPlayer : MediaPlayer? = null

    private fun playMusic(path: String){
        if(mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                timer?.cancel()
                mediaPlayer?.pause()
                mBinding.ivVoice.setImageResource(com.kissspace.module_common.R.mipmap.common_audio_play_icon)
            }else{
                startTime()
                mediaPlayer?.start()
                mBinding.ivVoice.setImageResource(com.kissspace.module_common.R.mipmap.common_audio_pause_icon)
            }
            return
        }
        mBinding.ivVoice.setImageResource(com.kissspace.module_common.R.mipmap.common_audio_pause_icon)
        mediaPlayer = MediaPlayer()
        mediaPlayer?.reset()
        path.apply {
            mediaPlayer?.setDataSource(this)
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener {
                mediaPlayer?.start()
                mediaPlayer?.seekTo(0)
                startTime()
            }
            mediaPlayer?.setOnCompletionListener {
                timer?.cancel()
                videoChanged.postValue(-1)
            }
        }
    }

    private fun startTime(){
        timer?.cancel()
        timer = Timer()
        val duration = mediaPlayer?.duration // 获取视频总时长
        val updateInterval = 500 // 更新间隔，单位为毫秒
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentPosition = mediaPlayer?.currentPosition // 获取当前播放进度
                val  currentProgress = ((currentPosition?.times(100) ?: 0) / duration!!)
                videoChanged.postValue(currentProgress)
            }
        }, 0, updateInterval.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        mediaPlayer?.release()
    }

    override fun handleBackPressed() {
        if(isHandleLikeOrFollow)
           FlowBus.post(Event.DynamicsHandlerEvent(DynamicsHandlerBean(0,getDynamicDetail().dynamicId,getDynamicDetail().userId,getDynamicDetail().likeStatus,getDynamicDetail().followStatus,getDynamicDetail().commentAmount.toString())))
        super.handleBackPressed()
    }
}

