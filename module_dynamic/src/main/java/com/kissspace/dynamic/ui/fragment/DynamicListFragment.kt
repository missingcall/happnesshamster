package com.kissspace.dynamic.ui.fragment

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeLife
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.CommonApi
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.dynamic.DynamicInfoRecord
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.previewPicture
import com.kissspace.dynamic.http.DynamicApi
import com.kissspace.dynamic.ui.activity.DynamicDetailActivity
import com.kissspace.dynamic.ui.viewmodel.DynamicViewModel
import com.kissspace.module_dynamic.R
import com.kissspace.module_dynamic.databinding.DynamicFragmentListBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.collectData
import com.qmuiteam.qmui.kotlin.onClick
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import java.util.Timer
import java.util.TimerTask

class DynamicListFragment(private val position:Int = 0):BaseFragment(R.layout.dynamic_fragment_list) {
    private val mBinding by viewBinding<DynamicFragmentListBinding>()
    private val mViewModel by viewModels<DynamicViewModel>()
    val videoChanged : MutableLiveData<Int> = MutableLiveData()
    private var userId: String? = null
    private var nickname: String? = null
    private var profilePath: String? = null
    private var sex: String? = null
    companion object {
        fun newInstance(userId: String?,nickname: String?= null,profilePath: String? = null,sex:String?=null) =
            DynamicListFragment().apply {
                arguments = bundleOf(
                    "userId" to userId,
                    "nickname" to nickname,
                    "profilePath" to profilePath,
                    "sex" to sex
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("userId")
        nickname = arguments?.getString("nickname")
        profilePath = arguments?.getString("profilePath")
        sex = arguments?.getString("sex")
    }

    override fun initView(savedInstanceState: Bundle?) {

        videoChanged.observe(viewLifecycleOwner){
            mBinding.recyclerView.adapter?.notifyItemChanged(it,false)
        }

        mBinding.pageRefreshLayout.apply {
            onRefresh {
                mViewModel.queryDynamicList(mBinding.pageRefreshLayout.index,position,userId,nickname,profilePath,sex)
            }
            onLoadMore {
                mViewModel.queryDynamicList(mBinding.pageRefreshLayout.index,position,userId,nickname,profilePath,sex)
            }
        }.autoRefresh()

        mBinding.recyclerView.linear().setup {
            addType<DynamicInfoRecord>(R.layout.dynamic_item)
            onBind {
                val model = getModel<DynamicInfoRecord>()
                val recyclerPicture = findView<RecyclerView>(R.id.recycler_picture)
                recyclerPicture.grid(3).setup {
                    addType<String>(R.layout.dynamic_picture_item)
                    onClick(R.id.root){
                        previewPicture(requireContext(),modelPosition,findView<ImageView>(R.id.root),model.pictureDynamicContent.toMutableList())
                    }
                }.models = model.pictureDynamicContent

                //val recyclerComment = findView<RecyclerView>(R.id.recycler_comment)
//                recyclerComment.linear().setup {
//                    addType<DynamicCommentModel>(R.layout.dynamic_comment_item)
//                }.models = if (model.dynamicCommentsList.size>2) model.dynamicCommentsList.subList(0,2) else model.dynamicCommentsList

               // findView<ViewGroup>(R.id.layout_comment).visibility = if (model.dynamicCommentsList.isEmpty()) GONE else VISIBLE
                //findView<TextView>(R.id.tv_check_more).visibility = if (model.dynamicCommentsList.size>2) VISIBLE else GONE

                val like = findView<TextView>(R.id.tv_like)
                like.text = model.numberOfLikes.toString()
                findView<TextView>(R.id.iv_follow).safeClick {
                    doFollow(getModel())
//                    model.followStatus = !model.followStatus
//                    notifyItemChanged(modelPosition,false)
                }
            }

            onClick(R.id.layout_voice){
                val model = getModel<DynamicInfoRecord>()
                playMusic(model,modelPosition)
            }

            onClick(R.id.iv_avatar){
                val model = getModel<DynamicInfoRecord>()
                jump(RouterPath.PATH_USER_PROFILE, "userId" to model.userId)
            }
//            onClick(R.id.tv_check_more){
//                val model = getModel<DynamicInfoRecord>()
//                val intent = Intent(requireContext(),DynamicDetailActivity::class.java)
//                intent.putExtra("dynamicDetail",model as Parcelable)
//                startActivity(intent)
//            }
            onClick(R.id.tv_like){
                val model = getModel<DynamicInfoRecord>()
                doLike(model)
            }
//            onClick(R.id.iv_follow){
//                val model = getModel<DynamicInfoRecord>()
//                doFollow(model)
//            }
            onClick(R.id.root){
                val model = getModel<DynamicInfoRecord>()
                val intent = Intent(requireContext(),DynamicDetailActivity::class.java)
                intent.putExtra("dynamicId",model.dynamicId)
                startActivity(intent)
            }
        }.mutable = mutableListOf()

        mBinding.ivGoTop.onClick {
            mBinding.recyclerView.smoothScrollToPosition(0)
        }

        mBinding.ivAdd.onClick {
            jump(RouterPath.PATH_DYNAMIC_SEND_FRIEND)
        }
    }

    private fun doLike(model:DynamicInfoRecord){
        val param = mutableMapOf<String,Any?>()
        param["dynamicId"] = model.dynamicId
        request<Boolean>(url = DynamicApi.API_LIKE_DYNAMICS, method = Method.POST,param=param, onSuccess = {
            model.likeStatus = !model.likeStatus
            model.numberOfLikes = if (model.likeStatus) model.numberOfLikes+1 else model.numberOfLikes -1
            model.notifyChange()
        })
    }


    private var timer:Timer? = null
    val mediaPlayer = MediaPlayer()
    private var currentDynamicInfoRecord:DynamicInfoRecord? = null
    var currenctPosition:Int? = null
    private fun playMusic(model: DynamicInfoRecord,position:Int){

        model.apply {
            mediaPlayer.reset()
            if(currenctPosition != null&& currenctPosition!=-1) {
                if(currenctPosition == position){
                    timer?.cancel()
                    model.currentDuration = 0
                    model.isPlayingVoice = false
                    videoChanged.postValue(currenctPosition)
                    currentDynamicInfoRecord = null
                    currenctPosition = -1
                    return
                }
                currentDynamicInfoRecord?.isPlayingVoice= false
                currentDynamicInfoRecord?.currentDuration = 0
                videoChanged.postValue(currenctPosition)
            }
            currenctPosition = position
            currentDynamicInfoRecord = model
            mediaPlayer.setDataSource(model.voiceDynamicContent)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                mediaPlayer.seekTo(0)
                model.isPlayingVoice = true
                val duration = mediaPlayer.duration // 获取视频总时长
                val updateInterval = 1000 // 更新间隔，单位为毫秒
                timer?.cancel()
                timer = Timer()
                timer?.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        val currentPosition = mediaPlayer.currentPosition // 获取当前播放进度
                        val  currentProgress = (currentPosition*100/duration)
                        model.currentDuration = currentProgress
                        videoChanged.postValue(currenctPosition)

                    }
                }, 0, updateInterval.toLong())
            }
            mediaPlayer.setOnCompletionListener {
                timer?.cancel()
                model.currentDuration = 0
                model.isPlayingVoice = false
                videoChanged.postValue(currenctPosition)
                currentDynamicInfoRecord = null
                currenctPosition = -1
            }
        }

    }


    private fun doFollow(model:DynamicInfoRecord){
        val param = mutableMapOf<String, Any?>("userId" to model.userId)
        request<Int>(if(model.followStatus) CommonApi.API_CANCEL_FOLLOW_USER else CommonApi.API_FOLLOW_USER, if(model.followStatus) Method.GET else Method.POST, param, onSuccess = {
            mBinding.recyclerView.bindingAdapter.mutable.filter {
                (it as DynamicInfoRecord).userId == model.userId
            }.forEach {
                val mo = it as DynamicInfoRecord
                mo.followStatus = !mo.followStatus
                mo.notifyChange()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        mediaPlayer.release()
    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.dynamicList, onSuccess = {
            mBinding.pageRefreshLayout.addData(it.dynamicInfoRecords, hasMore = {
                mBinding.pageRefreshLayout.index * 20 < it.total
            }, isEmpty = {
                it.dynamicInfoRecords.isEmpty()
            })
        }, onError = {
            mBinding.pageRefreshLayout.finishRefresh()
            mBinding.pageRefreshLayout.finishLoadMore()
        })

        FlowBus.observerEvent<Event.DynamicsHandlerEvent>(this) { bean->
            when (bean.dynamicsHandlerBean.type) {
                0 -> {

//                    mBinding.recyclerView.bindingAdapter.mutable.filter {
//                        (it as DynamicInfoRecord).userId == bean.dynamicsHandlerBean.userId
//                    }.forEach {
//                        val mo = it as DynamicInfoRecord
//                        if (mo.followStatus != bean.dynamicsHandlerBean.followStatus) {
//                            mo.followStatus = !mo.followStatus
//                        }
//                        if (mo.dynamicId == bean.dynamicsHandlerBean.id && mo.likeStatus != bean.dynamicsHandlerBean.like) {
//                            mo.likeStatus = !mo.likeStatus
//                            mo.numberOfLikes =
//                                if (mo.likeStatus) mo.numberOfLikes + 1 else mo.numberOfLikes - 1
//                        }
//                        mo.commentAmount = bean.dynamicsHandlerBean.collectNum.toInt()
//                        mo.notifyChange()
//                    }
                    scopeLife {
                        mBinding.recyclerView.bindingAdapter.mutable.asFlow()
                            .filter {
                                (it as DynamicInfoRecord).userId == bean.dynamicsHandlerBean.userId
                            }
                            .onEach {
                                val mo = it as DynamicInfoRecord
                                if (mo.followStatus != bean.dynamicsHandlerBean.followStatus) {
                                    mo.followStatus = !mo.followStatus
                                }
                                if (mo.dynamicId == bean.dynamicsHandlerBean.id ) {
                                    if(mo.likeStatus != bean.dynamicsHandlerBean.like){
                                        mo.likeStatus = !mo.likeStatus
                                        mo.numberOfLikes =
                                            if (mo.likeStatus) mo.numberOfLikes + 1 else mo.numberOfLikes - 1
                                    }
                                    mo.commentAmount = bean.dynamicsHandlerBean.collectNum.toInt()
                                }

                            }.collect{
                                (it as DynamicInfoRecord).notifyChange()
                            }
                    }
                }
                1 -> {
                    mBinding.recyclerView.smoothScrollToPosition(0)
                    mBinding.pageRefreshLayout.refresh()
                }
                2 -> {
                    mBinding.recyclerView.bindingAdapter.mutable.filter {
                        (it as DynamicInfoRecord).dynamicId == bean.dynamicsHandlerBean.id
                    }.forEach {
                        val mo = it as DynamicInfoRecord
                        val index = mBinding.recyclerView.bindingAdapter.mutable.indexOf(mo)
                        if(index>-1){
                            mBinding.recyclerView.bindingAdapter.mutable.remove(mo)
                            mBinding.recyclerView.bindingAdapter.notifyItemRemoved(index)
                            mBinding.recyclerView.bindingAdapter.notifyItemRangeChanged(index, mBinding.recyclerView.bindingAdapter.mutable.size)
                        }
                    }
                }
            }
        }

        mBinding.pageRefreshLayout.refresh()
    }


    override fun onResume() {
        super.onResume()
    }



}