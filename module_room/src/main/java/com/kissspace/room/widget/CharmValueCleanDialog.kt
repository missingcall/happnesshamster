package com.kissspace.room.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.drake.brv.utils.divider
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.ext.safeClick
import com.kissspace.common.widget.BaseBottomSheetDialogFragment
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomDialogCharmCleanBinding
import com.kissspace.room.game.bean.CharmValueBean
import com.kissspace.room.viewmodel.CharmValueModel

/**
 * param["chatRoomId"] = chatRoomId
 *         param["microphoneNumber"] = microphoneNumber
 */
class CharmValueCleanDialog(private val chatRoomId:String,private val microphoneNumber:Int,private val block: Boolean.() -> Unit) : BaseDialogFragment<RoomDialogCharmCleanBinding>(RoomDialogCharmCleanBinding::inflate,Gravity.BOTTOM) {
    private val mViewModel by viewModels<CharmValueModel>()
    companion object {
        fun newInstance(chatRoomId:String,microphoneNumber:Int,block: Boolean.() -> Unit) = CharmValueCleanDialog(chatRoomId,microphoneNumber,block)
    }

    override fun getLayoutId(): Int {
        return R.layout.room_dialog_charm_clean
    }



    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.tvCleanAll.safeClick {
            CommonConfirmDialog(
                requireContext(), "是否清除此麦位的魅力值", "魅力值将重置为0"
            ) {
                if (this) {
                    block.invoke(true)
                    dismiss()
                }
            }.show()
//               CommonConfirmDialog(
//                        requireContext(), "是否清除全部麦位的魅力值", "魅力值将重置为0"
//                    ) {
//                        if (this) {
//
//                        }
//                    }.show()
        }

        mBinding.tvCharmRankTitle.text = when(microphoneNumber) {
             0 -> "主持的魅力值"
             in 1..7 -> "${microphoneNumber}麦的魅力值"
             else -> "嘉宾的魅力值"
        }

        mBinding.recyclerView.linear().setup {
            addType<CharmValueBean> { R.layout.room_dialog_charm_value_item }
            onBind {
                val  rank =  findView<TextView>(R.id.tv_charm_rank)
                if(modelPosition>2) {
                    rank.text = "${modelPosition + 1}"
                    rank.background = null
                }else {
                    rank.text = null
                    rank.setBackgroundResource(
                        when (modelPosition) {
                            0 -> R.mipmap.room_ic_charm_rank_1
                            1 -> R.mipmap.room_ic_charm_rank_2
                            else -> R.mipmap.room_ic_charm_rank_3
                        }
                    )
                }
            }
        }.models = mutableListOf()

        mBinding.pageRefreshLayout.apply {
            setEnableLoadMore(false)
            onRefresh {
                getData()
            }
//            onLoadMore {
//                getData()
//            }
        }.autoRefresh()

//        mBinding.pageRefreshLayout.refresh()
    }

    private fun  getData(){
//        val param = mutableMapOf<String, Any?>()
//        param["pageNum"] = mBinding.pageRefreshLayout.index
//        param["pageSize"] = 20
        mViewModel.getCharmValueInfo(chatRoomId,microphoneNumber){
            if(mBinding.pageRefreshLayout.index == 1){
                mBinding.pageRefreshLayout.finishRefresh()
            }else{
                mBinding.pageRefreshLayout.finishLoadMore()
            }
            mBinding.pageRefreshLayout.addData(it, isEmpty = {
                it.isEmpty()
              }
            )
        }
    }
}