 package com.hamster.happyness.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentExplorePageListBinding
import com.hamster.happyness.ui.activity.MainActivity
import com.hamster.happyness.viewmodel.ExploreViewModel
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.RoomListBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.getMutable
import com.kissspace.common.util.jumpRoom
import com.kissspace.network.result.collectData
import me.jessyan.autosize.AutoSize

/**
 *
 * @author xhj
 * @CreateDate: 2022/11/24
 * @Description: 探索列表fragment
 *
 */
class ExplorePageListFragment : BaseFragment(R.layout.fragment_explore_page_list) {
    private val mBinding by viewBinding<FragmentExplorePageListBinding>()
    private val mViewModel by viewModels<ExploreViewModel>()
    private lateinit var mTageId: String
    private var position = 0
    private var isRefresh = true

    companion object {
        fun newInstance(position: Int, tagId: String) = ExplorePageListFragment().apply {
            arguments = bundleOf("position" to position, "tagId" to tagId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AutoSize.autoConvertDensity(requireActivity(), 375f, true);
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            mTageId = it.getString("tagId")!!
            position = it.getInt("position", 0)!!
        }
        initData()
        initRecyclerView()
    }

    private fun initData() {
        mBinding.refreshLayout.apply {
            onLoadMore {
                isRefresh = false
                getData( )
            }

            onRefresh {
                getData()
            }
        }

    }

    private fun getData(){
        mViewModel.getExploreData(mBinding.refreshLayout.index);
    }


    private fun initRecyclerView() {
        val gridLayoutManager = LinearLayoutManager(context)
        mBinding.recyclerView.setup {
            addType<RoomListBean> {
                when (it) {
                    0 -> {
                        R.layout.view_item_explore_head
                    }
                    1 -> {
                        R.layout.rv_explore_item_title
                    }
                    else -> {
                        R.layout.rv_item_hot_recommend
                    }
                }
            }
            onBind {

                if (itemViewType == R.layout.view_item_explore_head) {
                    findView<ConstraintLayout>(R.id.layout_explore_chat).safeClick {
                       //getRandomRoom("90e811129b62f088f2029b092d1ecc69")
//                        getRandomRoom()
                        jump(path = RouterPath.PATH_NEWBIE)

                    }

                    findView<ConstraintLayout>(R.id.layout_explore_live).safeClick {
                        getRandomRoom() //jumpRoom("90e811129b62f088f2029b092d1ecc69", Constants.ROOM_TYPE_PARTY)
                    }

                    findView<ConstraintLayout>(R.id.layout_explore_heartbeat_murmurs).safeClick {
                        getRandomRoom() //jumpRoom("8a72944df746d47ef02ffe72b1cc00e8", Constants.ROOM_TYPE_PARTY)
                    }

                    findView<ConstraintLayout>(R.id.layout_explore_make_friend).safeClick {
                        if(requireActivity() is MainActivity){
                            (requireActivity() as MainActivity).makeFriend()
                        }
                    }

                    findView<ConstraintLayout>(R.id.layout_explore_wait_for_you).safeClick {
                        getRandomRoom() // jumpRoom("71d6c217101f49781c816b535f62cbc1", Constants.ROOM_TYPE_PARTY)
                        //jump(path = RouterPath.PATH_POINT_EXCHANGE)
                    }
                }

                else if (itemViewType == R.layout.rv_item_hot_recommend){
                        findView<ConstraintLayout>(R.id.layout).safeClick {
                            val model = getModel<RoomListBean>()
                            jumpRoom(model.crId, Constants.ROOM_TYPE_PARTY, roomList = mBinding.recyclerView.getMutable())
                        }
                }
            }
        }.models = mutableListOf()
        mBinding.recyclerView.layoutManager = gridLayoutManager
    }

    private fun getRandomRoom(){
        jumpRoom(roomType = Constants.ROOM_TYPE_PARTY, stochastic = "001")
//        showLoading()
//        val param = mutableMapOf<String, Any?>()
//        request<String>(CommonApi.API_GET_RANDOM_ROOM, Method.GET,param, onSuccess = {
//            jumpRoom(it, Constants.ROOM_TYPE_PARTY)
//            hideLoading()
//        }, onError = {
//            hideLoading()
//            customToast(it.message)
//        })
    }

    fun onRefresh() {
        isRefresh = true
        mBinding.refreshLayout.index = 1
        mBinding.refreshLayout.refresh()
    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.roomListEvent, onSuccess = {
            val data = mutableListOf<RoomListBean>()
            if (mBinding.refreshLayout.index == 1) {
                 data.add(RoomListBean("-1"))

                 if(it.records.isNotEmpty()) {
                     data.add(RoomListBean("-2"))
                     data.addAll(it.records)
                 }
            } else {
                 data.addAll(it.records)
            }

            if (!isRefresh) {
                for (record in it.records) {
                    val roomBean = mBinding.recyclerView.mutable.find { any ->
                        record.crId == (any as RoomListBean).crId
                    }
                    if (roomBean != null) {
                        val index = mBinding.recyclerView.mutable.indexOf(roomBean)
                        mBinding.recyclerView.mutable.removeAt(index)
                        mBinding.recyclerView.bindingAdapter.notifyItemRemoved(index)
                    }
                }
            }
            mBinding.refreshLayout.addData(data, hasMore = {
                mBinding.refreshLayout.index * 20 < it.total
            }, isEmpty = {
                it.records.isEmpty()
            })
        })
//        scopeLife {
//            mViewModel.exploreListEvent.collect{
//                mBinding.refreshLayout.addData(it,hasMore = {
//                    it.size == 20
//                }, isEmpty = {
//                    it.isEmpty()
//                })
//            }
//        }
        getData()
    }
}


