package com.kissspace.room.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.GsonUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants.MusicPlayMode.Companion.CONCENTRATION_PLAY_LIST
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.MusicListModel
import com.kissspace.common.model.MusicSongsInfoModel
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomFragmentMusicListBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.downloadFile
import com.kissspace.network.net.request
import com.kissspace.network.result.collectData
import com.kissspace.room.manager.RoomMusicManager
import com.kissspace.room.viewmodel.MusicViewModel
import com.kissspace.util.toast
import im.zego.zegoexpress.ZegoExpressEngine
import im.zego.zegoexpress.entity.ZegoAudioEffectPlayConfig
import org.json.JSONArray
import org.json.JSONObject


/**
 * @CreateDate: 2023-03-16 10:56:49
 * @Description: 音乐列表
 */
class MusicListFragment : BaseFragment(R.layout.room_fragment_music_list) {

    private val mBinding by viewBinding<RoomFragmentMusicListBinding>()
    private val mViewModel by viewModels<MusicViewModel>()

    private var collectModel: MusicSongsInfoModel? = null
    public var currentModel:MusicSongsInfoModel? =null

    companion object {
        fun newInstance() =
            MusicListFragment()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.tvSearch.safeClick {
            searchMusic()
        }
        mBinding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMusic()
            }
            true
        }
        mBinding.recyclerView.linear().setup {
            addType<MusicSongsInfoModel> {
                R.layout.room_dialog_music_list_recycler_item
            }
            onClick(R.id.root){
                if(!RoomMusicManager.instance.checkSameSong(getModel<MusicSongsInfoModel>().getSongsId())) {
                    refreshModel(false)
                    currentModel = getModel<MusicSongsInfoModel>()
                    refreshModel(true)
                    startPlayMusicSongs(bindingAdapterPosition)
                }
            }
            onClick(R.id.iv_collect){
                val model = getModel<MusicSongsInfoModel>()
                collectModel = model
                mViewModel.collectMusic(model.song_id,model.song_name,model.singer_name,model.duration,true)
            }
        }.models = RoomMusicManager.instance.concentrationMusicList
//        RoomMusicManager.instance.requestMusicList(page = 1){ result->
//            mBinding.recyclerView.models = result
//        }
//        RoomMusicManager.instance.requestMusicNewList(page = 1){ result->
//           Log.e("requestMusicNewList", "requestMusicNewList:$result")
//        }


    }

    private fun searchMusic(){
        val searchMessage = mBinding.etSearch.text.toString().trim()
        if(TextUtils.isEmpty(searchMessage)){
            return
        }
        val json : MutableMap<String, Any?> = mutableMapOf()
        json["s"] = searchMessage
        json["type"] = "1"
        json["offset"] = "1"
        json["limit"] = "20"
        request<String>(
            "https://music.163.com/api/cloudsearch/pc",
            Method.GET,
            json,
            onSuccess = {
                val json = JSONObject(/* json = */ it)
                if(json["code"]==200) {
                    val result = json["result"] as JSONObject
                    val songs = result["songs"] as JSONArray
                    val list = mutableListOf<MusicSongsInfoModel>()
                    for (i in 0 until songs.length()) {
                        val song = songs.getJSONObject(i)
                        val musicSongsInfoModel = MusicSongsInfoModel()
                        musicSongsInfoModel.song_id = song.optString("id")
                        musicSongsInfoModel.songName = song.optString("name")
                        musicSongsInfoModel.song_name =  song.optString("name")
                        song.optJSONArray("ar")?.getJSONObject(0)?.optString("name").let { name ->
                            musicSongsInfoModel.singer_name = name.toString()
                        }
                        list.add(musicSongsInfoModel )
                    }

                    RoomMusicManager.instance.concentrationMusicList.clear()
                    RoomMusicManager.instance.concentrationMusicList.addAll(list)

                    if (RoomMusicManager.instance.isPlayConcentrationList()){
                        list.forEachIndexed { index, musicSongsInfoModel ->
                    if (RoomMusicManager.instance.isPlayMusicInfo(musicSongsInfoModel.getSongsId())){
                        musicSongsInfoModel.checked = true
                        currentModel = musicSongsInfoModel
                        RoomMusicManager.instance.updateCurrentIndex(index)
                        }
                     }
                   }
                    mBinding.recyclerView.models = list
                    mBinding.recyclerView.scrollToPosition(0)

//                    mBinding.recyclerView.models = list
//                    mBinding.recyclerView.scrollToPosition(0)
                }
            },
            onError = {
            }
        )

//        RoomMusicManager.instance.requestMusicList(searchMessage.isNotEmpty(),searchMessage,page = 1){result->
//            if (RoomMusicManager.instance.isPlayConcentrationList()){
//                result.forEachIndexed { index, it ->
//                    if (RoomMusicManager.instance.isPlayMusicInfo(it.getSongsId())){
//                        it.checked = true
//                        currentModel = it
//                        RoomMusicManager.instance.updateCurrentIndex(index)
//                    }
//                }
//            }
//            mBinding.recyclerView.models = result
//            mBinding.recyclerView.scrollToPosition(0)
//        }
    }

    private fun startPlayMusicSongs(position:Int) {
       RoomMusicManager.instance.switchPlayList(CONCENTRATION_PLAY_LIST,position)
    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.collectEvent, onSuccess = {
            toast("收藏成功")
            collectModel?.isCollect = false
            collectModel?.notifyChange()
        }, onError = {
            toast("收藏失败")
        })

        FlowBus.observerEvent<Event.ChangePlayMusicList>(this) {
            refreshModel(false)
            val models = mBinding.recyclerView.models
            if (!models.isNullOrEmpty() && it.currentIndex < models.size){
                currentModel = models[it.currentIndex] as MusicSongsInfoModel
                refreshModel(true)
            }
        }

        FlowBus.observerEvent<Event.MusicListChangeEvent>(this){
            if (RoomMusicManager.instance.playListType != CONCENTRATION_PLAY_LIST){
                refreshModel(false)
                currentModel = null
            }
        }
    }

    /**
     * 刷新状态
     */
    private fun refreshModel(check:Boolean){
        if (currentModel != null){
            currentModel?.checked = check
            currentModel?.notifyChange()
        }
    }
}