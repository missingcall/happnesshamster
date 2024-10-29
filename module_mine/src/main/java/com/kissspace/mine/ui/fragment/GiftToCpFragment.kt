package com.kissspace.mine.ui.fragment


import android.annotation.SuppressLint
import android.view.Gravity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.handleGiftMessage
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.GiftBean
import com.kissspace.common.model.immessage.GiftInfo
import com.kissspace.common.model.immessage.GiftMessage
import com.kissspace.common.ui.BosonFriendDialog
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.setApplicationValue
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.RelationBean
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineGiftToCpBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.netCatch
import com.kissspace.network.net.request
import com.kissspace.util.loadImageWithDefault
import org.json.JSONArray
import org.json.JSONObject

class GiftToCpFragment : BaseDialogFragment<MineGiftToCpBinding>(MineGiftToCpBinding::inflate,Gravity.BOTTOM) {


    private val toUserId  by lazy { arguments?.getString("toUserId")?:"" }

    private val toUserIcon  by lazy { arguments?.getString("toUserIcon")?:"" }

    companion object {
        fun newInstance(toUserId: String,toUserIcon:String?)=GiftToCpFragment().apply {
            arguments = bundleOf("toUserId" to toUserId,"toUserIcon" to toUserIcon)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.mine_gift_to_cp
    }

    override fun initView() {

        mBinding.ivCpLeftIcon.loadImageWithDefault(MMKVProvider.userInfo?.profilePath)
        mBinding.ivCpRightIcon.loadImageWithDefault(toUserIcon)

        mBinding.tvConfirm.safeClick {
            val cpTypes = mBinding.rvCpType.bindingAdapter.getCheckedModels<RelationBean>()
            if(cpTypes.isEmpty()){
                customToast("请选择关系")
                return@safeClick
            }

            val gifts = mBinding.rvCpGift.bindingAdapter.getCheckedModels<GiftBean>()
            if(gifts.isEmpty()){
                customToast("请选择礼物")
                return@safeClick
            }
            sendGift(gifts)
        }

        mBinding.rvCpGift.linear(RecyclerView.HORIZONTAL).setup {
            addType<GiftBean> { R.layout.rv_cp_gift_item }
            singleMode = true
            onClick(R.id.cl_gift){
                checkedAll(false)
                setChecked(modelPosition, true)
            }
            onChecked { position, isChecked, _ ->
                val model = getModel<GiftBean>(position)
                model.select = isChecked
                model.notifyChange()
            }
        }.models = mutableListOf()

        mBinding.rvCpType.grid(4).setup {
            addType<RelationBean> { R.layout.rv_cp_type_item }
            onClick(R.id.cl_pay_type){
                checkedAll(false)
                setChecked(modelPosition, true)
                mBinding.rvCpGift.bindingAdapter.checkedAll(false)
                showGift(getModel(modelPosition))
            }
            onChecked { position, isChecked, _ ->

                val model = getModel<RelationBean>(position)
                model.isSelected = isChecked
                model.notifyChange()
            }
        }.models = mutableListOf()
        scopeNetLife {
            showLoading()
            Get<List<RelationBean>>(MineApi.API_GET_RELATION_LIST).await().let {
                    mBinding.rvCpType.models = it
                    if(it.isNotEmpty()){
                        mBinding.rvCpType.bindingAdapter.setChecked(0,true)
                        showGift(it[0])
                    }
                }
            hideLoading()
        }.netCatch {
            it.printStackTrace()
            customToast(it.message)
            hideLoading()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showGift(relationBean:RelationBean){
        mBinding.rvCpGift.bindingAdapter.models = relationBean.giftList
    }

    private fun sendGift(
        gifts: List<GiftBean>,
    ) {
        val giftSource = "001"
        val targetUserIds = JSONArray()
        targetUserIds.put(toUserId)
        val giveGiftInfo = JSONArray()
        gifts.forEach {
            val json = JSONObject()
            json.put("giftId", it.giftId)
            json.put("giftNum", 1)
            giveGiftInfo.put(json)
        }
        val param = mutableMapOf<String, Any?>()
        param["sourceUserId"] = MMKVProvider.userId
        param["targetUserIds"] = targetUserIds
        param["giveGiftInfos"] = giveGiftInfo
        param["giftSource"] = giftSource
        setApplicationValue(
            type = Constants.TypeFaceRecognition,
            value = Constants.FaceRecognitionType.CONSUMPTION.type.toString()
        )
        showLoading()
        request<GiftMessage>("/hamster-user/giftInfo/giveGiftForSomebody", Method.POST, param,
            onSuccess = {
                handleGiftMessage(it, invoke = {
                        gift, nickName, userIcon ->
                    BosonFriendDialog.newInstance(gift,nickName,userIcon).show(childFragmentManager)
                })
                FlowBus.post(Event.CPChanged)
                hideLoading()
            },
            onError = {
                customToast(it.message)
                hideLoading()
            })
    }

    fun handle(gift: GiftInfo, nickName:String, userIcon:String){

    }

}