package com.kissspace.room.widget

import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.net.utils.scopeNetLife
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.config.CommonApi
import com.kissspace.common.config.Constants
import com.kissspace.common.http.IBaseCommonPage
import com.kissspace.common.model.TeamPKUserBean
import com.kissspace.common.model.wallet.WalletRecord
import com.kissspace.common.model.wallet.WalletDetailModel
import com.kissspace.common.util.customToast
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomFragmentUserInPkBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.room.http.RoomApi
import com.uc.crashsdk.export.LogType.addType


class RoomUserInPKFragment : BaseFragment(R.layout.room_fragment_user_in_pk),IBaseCommonPage {

    private val mBinding by dataBinding<RoomFragmentUserInPkBinding>()

    companion object {
        fun newInstance(roomId:String?,position: Int): RoomUserInPKFragment {
            val args = Bundle()
            args.putInt("position", position+1)
            args.putString("roomId", roomId)
            val fragment = RoomUserInPKFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
         initPageView()
        mBinding.rvCoin.linear().setup {
            addType<TeamPKUserBean> { R.layout.room_rv_user_in_pk }
            onBind {
                findView<TextView>(R.id.tv_top_num).apply {
                     setCompoundDrawablesRelativeWithIntrinsicBounds(
                         when (bindingAdapterPosition){
                                  0-> R.mipmap.ic_pk_top_1
                                  1-> R.mipmap.ic_pk_top_2
                                  2-> R.mipmap.ic_pk_top_3
                                  else -> 0 },0,0,0
                     )
                    text = if(bindingAdapterPosition>2) ""+bindingAdapterPosition else ""
                }
            }
        }.models = mutableListOf()
        getPageRecycleView().refresh()
    }


    override fun getPageRecycleView(): PageRefreshLayout {
        return mBinding.pageRefreshLayout
    }
//(@ApiParam("房间id") @RequestParam String roomId,@ApiParam("队伍类型")@RequestParam String teamType){
    override fun loadData(page: Int, size: Int) {
        val param = mutableMapOf<String, Any?>()
        param["roomId"] = arguments?.getString("roomId")
        param["teamType"] ="00"+arguments?.getInt("position")

        request<List<TeamPKUserBean>>(
            RoomApi.API_USER_IN_PK, Method.GET, param, onSuccess = {
               setListData(it)
            },
            onError = {
                customToast(it.message)
            }
        )
    }
}