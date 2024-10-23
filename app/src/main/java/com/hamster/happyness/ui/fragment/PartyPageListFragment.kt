 package com.hamster.happyness.ui.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.DefaultDecoration
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.divider
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.hamster.happyness.R
import com.hamster.happyness.databinding.FragmentPartyPageListBinding
import com.hamster.happyness.viewmodel.PartyViewModel
import com.kissspace.common.adapter.BannerAdapter
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.base.BaseLazyFragment
import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.model.RoomListBannerBean
import com.kissspace.common.model.RoomListBean
import com.kissspace.common.model.immessage.BaseAttachment
import com.kissspace.common.util.getMutable
import com.kissspace.common.util.handleSchema
import com.kissspace.common.util.jumpRoom
import com.kissspace.network.result.collectData
import com.kissspace.util.dp
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.listener.OnBannerListener

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/24
 * @Description: 派对列表fragment
 *
 */
class PartyPageListFragment : BaseLazyFragment(R.layout.fragment_party_page_list) {
    private val mBinding by viewBinding<FragmentPartyPageListBinding>()
    private val mViewModel by viewModels<PartyViewModel>()
    private lateinit var mTageId: String
    private var position = 0
    private val mPageSize = 20
    private var isRefresh = true

    companion object {
        fun newInstance(position: Int, tagId: String) = PartyPageListFragment().apply {
            arguments = bundleOf("position" to position, "tagId" to tagId)
        }
    }

    override fun lazyInitView() {
    }

    override fun lazyLoadData() {

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
                mViewModel.getRoomList(mTageId, mBinding.refreshLayout.index, mPageSize)
            }

            onRefresh {
                mViewModel.getRoomList(mTageId, mBinding.refreshLayout.index, mPageSize)
            }
        }
    }


    private fun initRecyclerView() {
//        val gridLayoutManager = GridLayoutManager(context, 2)
//        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                var viewType = -1
//                mBinding.recyclerView.adapter?.let {
//                    if(position < it.itemCount){
//                         viewType = it.getItemViewType(position)
//                    }
//                }
//                return if (viewType == R.layout.layout_party_page_list_item) 1 else 2
//            }
//        }

//        mBinding.recyclerView.divider {
//            orientation = DividerOrientation.GRID
//            setMargin( start = 10,dp = true)
//        }

        mBinding.recyclerView.linear().setup {
            addType<RoomListBean> {
//                R.layout.layout_party_page_list_item
                R.layout.rv_item_hot_recommend_old
            }
            onBind {
                findView<ConstraintLayout>(R.id.layout).safeClick {
                    val model = getModel<RoomListBean>()
                    jumpRoom(model.crId, Constants.ROOM_TYPE_PARTY, roomList = mBinding.recyclerView.getMutable())
                }
            }
        }.models = mutableListOf()

//        mBinding.recyclerView.layoutManager = gridLayoutManager
    }

    fun onRefresh() {
        isRefresh = true
        mBinding.refreshLayout.index = 1
        mBinding.refreshLayout.refresh()
    }

    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.roomListEvent, onSuccess = {
            if (mBinding.refreshLayout.index == 1 && position == 0) {
                mViewModel.getRoomListBanner()
            }
            (parentFragment as PartyV2Fragment).finishRefresh()
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
            mBinding.refreshLayout.addData(it.records, hasMore = {
                mBinding.refreshLayout.index * 20 < it.total
            }, isEmpty = {
                it.records.isEmpty()
            })
        })

        collectData(mViewModel.roomListBannerEvent, onSuccess = {
            val banner =
                it.filter { item -> item.state == "001" && item.os == "001" }
            if (banner.isNotEmpty()) {
                mBinding.banner.apply {
                    visibility = View.VISIBLE
                    if(adapter == null) {
                        setAdapter(BannerAdapter(banner))
                        indicator = RectangleIndicator(requireActivity())
                        setIndicatorSelectedColorRes(com.kissspace.module_common.R.color.common_white)
                        setIndicatorNormalColorRes(com.kissspace.module_common.R.color.color_80FFFFFF)
                        setIndicatorWidth(12.dp.toInt(), 12.dp.toInt())
                        setIndicatorHeight(4.dp.toInt())
                        setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, 10.dp.toInt()))
                        setOnBannerListener(object : OnBannerListener<RoomListBannerBean> {
                            override fun OnBannerClick(data: RoomListBannerBean?, position: Int) {
                                 handleSchema(data?.schema)
                            }
                        })
                        addBannerLifecycleObserver(this@PartyPageListFragment)
                    }else{
                        adapter.setDatas(banner)
                    }
                }
            }
        })

        mViewModel.getRoomList(mTageId, mBinding.refreshLayout.index, mPageSize)

    }
}


