package com.kissspace.room.ui.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.bean.LocalItem
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.util.customToast
import com.kissspace.common.widget.BaseBottomSheetDialogFragment
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomFragmentPkSettingBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.room.http.RoomApi
import com.kissspace.util.toast

/**
 * @Description: PK设置
 */
class RoomPkSettingFragment : BaseBottomSheetDialogFragment<RoomFragmentPkSettingBinding>() {
    private lateinit var roomId: String

    companion  object {
        fun newInstance(roomId: String) =
            RoomPkSettingFragment().apply {
                arguments = bundleOf( "roomId" to roomId)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = arguments?.getString("roomId")!!
    }

    override fun getViewBinding(): RoomFragmentPkSettingBinding {
        return RoomFragmentPkSettingBinding.inflate(layoutInflater)
    }

    override fun initView() {

        mBinding.titleBar.setTitleBarListener(onRightClick = {
            dismiss()
        })

        mBinding.rvPkTime.linear(RecyclerView.HORIZONTAL).setup {
            singleMode = true
            addType<LocalItem> { R.layout.room_pk_time_item }
            onClick(R.id.tv_content){
                checkedAll(false)
                setChecked(modelPosition, true)
            }
            onChecked { position, isChecked, _ ->
                val model = getModel<LocalItem>(position)
                model.select = isChecked
                model.notifyChange()
            }
        }.models = mutableListOf(LocalItem("3分钟"),LocalItem("5分钟"),LocalItem("10分钟"))

        mBinding.rvPkTime.bindingAdapter.setChecked(0,true)


        mBinding.rvPkAfterTime.linear(RecyclerView.HORIZONTAL).setup {
            singleMode = true
            addType<LocalItem> { R.layout.room_pk_time_item }
            onClick(R.id.tv_content){
                checkedAll(false)
                setChecked(modelPosition, true)
            }
            onChecked { position, isChecked, _ ->
                val model = getModel<LocalItem>(position)
                model.select = isChecked
                model.notifyChange()
            }
        }.models = mutableListOf(LocalItem("1分钟"),LocalItem("3分钟"),LocalItem("5分钟"))
        mBinding.rvPkAfterTime.bindingAdapter.setChecked(0,true)

        mBinding.tvCommit.setOnClickListener {
            startPK(mBinding.rvPkTime.bindingAdapter.checkedPosition[0].let {
                  when(it){
                       0->   180L
                       1->   300L
                       2->   600L
                       else -> 180L
                  }
            },mBinding.rvPkAfterTime.bindingAdapter.checkedPosition[0].let {
                when(it){
                    0->   60L
                    1->   180L
                    2->   300L
                    else -> 60L
                }
            })
        }
    }


    private fun startPK(pkTime:Long, punishTime:Long) {
        mBinding.rvPkTime.bindingAdapter.getCheckedModels<LocalItem>().let {
             if(it.isEmpty()){
                 customToast("请选择PK时长")
                 return
             }else{
                 val param = mutableMapOf<String, Any?>()
                 param["roomId"] = roomId
                 param["pkTimeCountdown"] =  pkTime
                 param["pkTimePunish"] = punishTime
                 request<Any?>(RoomApi.API_START_PK, Method.POST, param, onSuccess = {
                     dismiss()
                 }, onError = { exception ->
                     toast(exception.errorMsg)
                 })
             }
        }

    }



}