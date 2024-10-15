package com.kissspace.room.manager

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.didi.drouter.api.DRouter
import com.kissspace.common.config.Constants
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.MusicListModel
import com.kissspace.common.model.MusicResourceModel
import com.kissspace.common.model.MusicSongsInfoModel
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.customToast
import com.kissspace.common.util.randomRange
import com.kissspace.network.net.Method
import com.kissspace.network.net.downloadFile
import com.kissspace.network.net.request
import com.kissspace.room.interfaces.MusicPlayListener
import com.kissspace.room.viewmodel.NetEaseMusicResponse
import com.nirvana.tools.core.annotations.NetUtils
import im.zego.zegoexpress.ZegoCopyrightedMusic
import im.zego.zegoexpress.ZegoExpressEngine
import im.zego.zegoexpress.ZegoMediaPlayer
import im.zego.zegoexpress.callback.IZegoMediaPlayerEventHandler
import im.zego.zegoexpress.constants.ZegoCopyrightedMusicBillingMode
import im.zego.zegoexpress.constants.ZegoCopyrightedMusicResourceType
import im.zego.zegoexpress.constants.ZegoCopyrightedMusicVendorID
import im.zego.zegoexpress.constants.ZegoMediaPlayerState
import im.zego.zegoexpress.entity.ZegoCopyrightedMusicConfig
import im.zego.zegoexpress.entity.ZegoCopyrightedMusicRequestConfig
import im.zego.zegoexpress.entity.ZegoMediaPlayerResource
import im.zego.zegoexpress.entity.ZegoUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 *@author: adan
 *@date: 2023/3/22
 *@Description:房间音乐管理类
 */
class RoomMusicManager private constructor(){

    companion object {
        val instance: RoomMusicManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RoomMusicManager()
        }
    }

    private var mCopyrightedMusic : ZegoCopyrightedMusic? = null
    private var mMediaPlayer : ZegoMediaPlayer? = null

    /**
     * 当前播放歌曲信息
     */
    var musicInfo: MusicSongsInfoModel? = null
    /**
     * 是否在播放歌曲中
     */
    var isMusicPlay = false

    /**
     * 当前播放的模式
     * 0 列表循环 1 单曲循环 2 随机播放
     */
    var playMode = Constants.MusicPlayMode.ORDER_PLAY_MODE

    /**
     * 当前播放列表
     */
//    private var currentMusicList = mutableListOf<MusicSongsInfoModel>()

    /**
     * 收藏
     */
    private var collectMusicList = mutableListOf<MusicSongsInfoModel>()

    /**
     * 默认播放列表,精选
     */
    var concentrationMusicList = mutableListOf<MusicSongsInfoModel>()

    /**
     * 当前播放音频对象在对应播放列表的角标
     */
    private var currentIndex = 0

    /**
     * 当前播放列表类型
     */
    var playListType = Constants.MusicPlayMode.CONCENTRATION_PLAY_LIST

    private var musicCallback: MusicPlayListener? = null

    /**
     * 播放器播放进度回调
     */
    private val mediaPlayerEventHandler = object : IZegoMediaPlayerEventHandler() {
        override fun onMediaPlayerPlayingProgress(
            mediaPlayer: ZegoMediaPlayer?, millisecond: Long
        ) {
            super.onMediaPlayerPlayingProgress(mediaPlayer, millisecond)
            musicCallback?.onProgressUpdate(millisecond.toInt())
        }

        override fun onMediaPlayerStateUpdate(
            mediaPlayer: ZegoMediaPlayer?, state: ZegoMediaPlayerState?, errorCode: Int
        ) {
            super.onMediaPlayerStateUpdate(mediaPlayer, state, errorCode)
            if (state == ZegoMediaPlayerState.PLAY_ENDED) {
                //播放结束
                currentIndex = getCurrentMusicList().indexOfFirst {
                    musicInfo?.getSongsId() == it.getSongsId()
                }
                nextAudio()
            }
        }
    }


    fun init(engine: ZegoExpressEngine?){
        mCopyrightedMusic = engine?.createCopyrightedMusic()
        val musicConfig = ZegoCopyrightedMusicConfig().apply {
            user = ZegoUser(MMKVProvider.userId)
        }
        mCopyrightedMusic?.initCopyrightedMusic(musicConfig) {}
        mMediaPlayer = engine?.createMediaPlayer()
        mMediaPlayer?.setEventHandler(mediaPlayerEventHandler)
    }


    /**
     * 查询音乐歌曲列表
     */
    fun requestMusicList(
        isSearch: Boolean = false,
        keyword: String = "",
        page: Int,
        block: (List<MusicSongsInfoModel>) -> Unit
    ) {
        val json = JSONObject()
        json.put("page", page)
        if (isSearch) {
            json.put("keyword", keyword)
        } else {
            json.put("tag_id", "571")
        }
        json.put("vendor_id", 2)
        mCopyrightedMusic?.sendExtendedRequest(
            if (isSearch) "/search/song" else "/tag/song", json.toString()
        ) { code, _, result ->
            if (code == 0) {
                val mMusicListModel = GsonUtils.fromJson(result, MusicListModel::class.java)
                var musicList = mMusicListModel.data.songs
                if (musicList.isNotEmpty() && musicList.size > 20) {
                    musicList = musicList.subList(0, 20)
                }
                concentrationMusicList.clear()
                concentrationMusicList.addAll(musicList)
//                if (isPlayConcentrationList()){
//                    currentMusicList.clear()
//                    currentMusicList.addAll(concentrationMusicList)
//                }
                block(musicList)
            }else{
                customToast("获取列表失败,请稍后重试")
            }
        }
    }

    /**
     * 获取歌曲资源
     */
    private fun requestMusicResource(musicModel: MusicSongsInfoModel) {
        request<String>(
            "https://music.163.com/api/song/enhance/player/url?ids=["+musicModel.song_id+"]&br=320000",
            Method.GET,
            onSuccess = {
                val json = JSONObject(it)
                if(json["code"]==200){
                    val path =  ((json["data"] as JSONArray)[0] as JSONObject)["url"]
                    downloadFile(path.toString(), progress = {}, onSuccess = { file ->
                        LogUtils.e("downloadFile","downloadFile"+file.absolutePath)
                        musicModel.duration = getMusicDuration(file.absolutePath)
                        musicInfo = musicModel
                        musicCallback?.onPlayMusicUpdate(musicModel)
                        startPlayMusicWithLocalMusic(file.absolutePath)
                    }, onError = {
                        customToast("播放失败")
                    })
                }
            },
            onError = {
                customToast("播放失败")
            }
        )
//        val config = ZegoCopyrightedMusicRequestConfig()
//        config.songID = musicModel.getSongsId()
//        config.mode = ZegoCopyrightedMusicBillingMode.getZegoCopyrightedMusicBillingMode(0)
//        config.vendorID = ZegoCopyrightedMusicVendorID.ZEGO_COPYRIGHTED_MUSIC_VENDOR2
//        val resourceType = ZegoCopyrightedMusicResourceType.ZEGO_COPYRIGHTED_MUSIC_RESOURCE_SONG
//        mCopyrightedMusic?.requestResource(config, resourceType) { i, s ->
//            if (i == 0) {
//                val mMusicResourceModel = GsonUtils.fromJson(s, MusicResourceModel::class.java)
//                downLoadMusic(mMusicResourceModel.data.resources[0].resource_id)
//            } else {
//                customToast("播放失败,请重试")
//            }
//        }
    }

    private fun getMusicDuration(filePath: String): Int {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
            return mediaPlayer.duration
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            mediaPlayer.release()
        }
        return 0
    }
    /**
     * 下载资源
     */
    private fun downLoadMusic(resourceID: String) {
        mCopyrightedMusic?.download(resourceID) {
            if (it == 0) {
                //歌曲下载成功
                startPlayMusic(resourceID)
            } else {
                customToast("播放失败,请重试")
            }
        }
    }

    private fun startPlayMusic(resourceID: String) {
        if (isMusicPlay) {
            mMediaPlayer?.stop()
        }
        mMediaPlayer?.loadCopyrightedMusicResourceWithPosition(resourceID, 0) {
            isMusicPlay = it == 0
        }
        mMediaPlayer?.start()
        mMediaPlayer?.enableAux(true)
    }

    //加载本地资源路径
    private fun startPlayMusicWithLocalMusic(filepath: String) {
        val zegoMediaPlayerResource = ZegoMediaPlayerResource()
        zegoMediaPlayerResource.filePath = filepath
        mMediaPlayer?.loadResourceWithConfig(zegoMediaPlayerResource) {
            isMusicPlay = it == 0
        }
        mMediaPlayer?.start()
        mMediaPlayer?.enableAux(true)
    }

    /**
     * 播放暂停
     */
    fun resumePlayMusic(isResume: Boolean) {
        if (isResume) {
            mMediaPlayer?.resume()
        } else {
            mMediaPlayer?.pause()
        }
    }

    /**
     * 更新当前播放音乐index
     */
    fun updateCurrentIndex(index: Int) {
        currentIndex = index
    }

    /**
     * 设置音量
     */
    fun setVolumeNumber(volume: Int) {
        mMediaPlayer?.setVolume(volume)
    }

    /**
     * 获取当前推流音量
     */
    fun getVolumeNumber(): Int {
        return if (mMediaPlayer != null) {
            mMediaPlayer?.publishVolume!!
        } else {
            0
        }
    }

    /**
     * 当前播放的是否为精选列表
     */
    fun isPlayConcentrationList():Boolean{
        return isMusicPlay && playListType == Constants.MusicPlayMode.CONCENTRATION_PLAY_LIST
    }

    /**
     * 当前播放的是否为收藏列表
     */
    fun isPlayCollectList():Boolean{
        return isMusicPlay && playListType == Constants.MusicPlayMode.COLLECT_PLAY_LIST
    }

    /**
     * 是否是当前播放的歌曲
     */
    fun isPlayMusicInfo(songId:String):Boolean{
        return songId == musicInfo?.getSongsId()
    }

    /**
     * 设置歌曲进度 信息回调
     */
    fun setMediaPalyerEventHandler(mediaPlayerEventHandler: MusicPlayListener) {
        musicCallback = mediaPlayerEventHandler
    }

    fun setCollectList(model: List<MusicSongsInfoModel>) {
        collectMusicList.clear()
        collectMusicList.addAll(model)
    }

    /**
     * 切换播放模式
     */
    fun changePlayMode() {
        when (playMode) {
            Constants.MusicPlayMode.ORDER_PLAY_MODE -> {
                playMode = Constants.MusicPlayMode.SINGLE_PLAY_MODE
            }

            Constants.MusicPlayMode.SINGLE_PLAY_MODE -> {
                playMode = Constants.MusicPlayMode.RANDOM_PLAY_MODE
            }

            Constants.MusicPlayMode.RANDOM_PLAY_MODE -> {
                playMode = Constants.MusicPlayMode.ORDER_PLAY_MODE
            }
        }
    }

    /**
     * 切换播放列表,并播放歌曲
     */
//    fun switchPlayList(type: Int, index: Int) {
//        playListType = type
//        currentMusicList.clear()
//        if (type == Constants.MusicPlayMode.CONCENTRATION_PLAY_LIST) {
//            currentMusicList.addAll(concentrationMusicList)
//        } else {
//            currentMusicList.addAll(collectMusicList)
//        }
//        currentIndex = index
//        val musicSongsInfoModel = currentMusicList[index]
//        //点击播放同一首歌不处理
//        if (type == playMode) {
//            if (isMusicPlay && musicSongsInfoModel.getSongsId() == musicInfo?.getSongsId()) {
//                return
//            }
//        } else {
//            playListType = type
//            FlowBus.post(Event.MusicListChangeEvent)
//        }
//        requestMusicResource(musicSongsInfoModel)
//    }


    fun getCurrentMusicList():List<MusicSongsInfoModel>{
        return if(playListType ==  Constants.MusicPlayMode.CONCENTRATION_PLAY_LIST) concentrationMusicList else concentrationMusicList
    }

    fun checkSameSong(songId: String) : Boolean{
         return musicInfo?.let {
             it.getSongsId() == songId
         } == true
    }
    /**
     * 切换播放列表,并播放歌曲
     */
    fun switchPlayList(playListType:Int,index: Int) {
        this.playListType = playListType
        val musicSongsInfoModel = getCurrentMusicList()[index]
        if(checkSameSong(musicSongsInfoModel.getSongsId())){
            return
        }
        if (isMusicPlay) {
            isMusicPlay = false
            mMediaPlayer?.stop()
        }
        musicInfo = musicSongsInfoModel
        musicCallback?.onPlayMusicUpdate(musicSongsInfoModel)
        requestMusicResource(musicSongsInfoModel)
    }

    fun nextAudio() {
        if (!getCurrentMusicList().isNullOrEmpty()) {
            when (playMode) {
                //顺序
                Constants.MusicPlayMode.ORDER_PLAY_MODE -> {
                    currentIndex = if (currentIndex < getCurrentMusicList().size - 1) {
                        currentIndex + 1
                    } else {
                        0
                    }
                }
                //单曲(不做处理)
                Constants.MusicPlayMode.SINGLE_PLAY_MODE -> {
                }
                //随机
                Constants.MusicPlayMode.RANDOM_PLAY_MODE -> {
                    currentIndex = randomRange(0, getCurrentMusicList().size - 1)
                }
            }
            if (currentIndex < 0){
                currentIndex = 0
            }
            val musicSongsInfoModel = getCurrentMusicList()[currentIndex]
            requestMusicResource(musicSongsInfoModel)
            resetMusicListStatus()
        }
    }

    private fun resetMusicListStatus() {
        if (playListType == Constants.MusicPlayMode.CONCENTRATION_PLAY_LIST) {
            FlowBus.post(
                Event.ChangePlayMusicList(
                    currentIndex
                ))
        } else {
            FlowBus.post(
                Event.ChangePlayMusicCollect(
                    currentIndex
                ))
        }
    }

    fun previousAudio() {
        if (!getCurrentMusicList().isNullOrEmpty()) {
            when (playMode) {
                //顺序
                Constants.MusicPlayMode.ORDER_PLAY_MODE -> {
                    currentIndex = if (currentIndex > 0) {
                        currentIndex - 1
                    } else {
                        getCurrentMusicList().size - 1
                    }
                }
                //单曲(不做处理)
                Constants.MusicPlayMode.SINGLE_PLAY_MODE -> {
                }
                //随机
                Constants.MusicPlayMode.RANDOM_PLAY_MODE -> {
                    currentIndex = randomRange(0, getCurrentMusicList().size - 1)
                }
            }
            if (currentIndex < 0){
                currentIndex = 0
            }
            val musicSongsInfoModel = getCurrentMusicList()[currentIndex]
            requestMusicResource(musicSongsInfoModel)
            resetMusicListStatus()
        }
    }

    fun getMediaPlayStatus(): Boolean {
        return mMediaPlayer?.currentState == ZegoMediaPlayerState.PLAYING
    }


    fun release(){
        mCopyrightedMusic = null
        mMediaPlayer = null
        collectMusicList.clear()
        concentrationMusicList.clear()
        musicInfo = null
        musicCallback = null
    }
}