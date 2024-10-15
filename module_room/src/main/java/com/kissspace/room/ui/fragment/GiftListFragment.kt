package com.kissspace.room.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.config.Constants
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.BasePageResponse
import com.kissspace.common.model.GiftModel
import com.kissspace.common.model.PackGiftModel
import com.kissspace.common.model.immessage.GiftMessage
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.getMutable
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomFragmentGiftListBinding
import com.kissspace.network.net.Method
import com.kissspace.network.result.collectData
import com.kissspace.network.net.request
import com.kissspace.room.http.RoomApi
import com.kissspace.room.viewmodel.GiftViewModel
import com.kissspace.room.widget.GiftDialog

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/2/18 14:22
 * @Description: 礼物列表
 *
 */
class GiftListFragment : BaseFragment(R.layout.room_fragment_gift_list) {
    private lateinit var tabId: String
    private lateinit var tabName: String
    private var position: Int = 0
    private var isDarkMode = true
    private var isOpenBox = false
    private var isPackGift = false
    private val mBinding by viewBinding<RoomFragmentGiftListBinding>()
    private val mViewModel by viewModels<GiftViewModel>()

    private var isLockStatus = false

    companion object {
        fun newInstance(
            tabName:String,
            tabId: String,
            position: Int,
            isOpenBox: Boolean,
            isDarkMode: Boolean,
            isPackGift: Boolean
        ) = GiftListFragment().apply {
            arguments = bundleOf(
                "tabName" to tabName,
                "tabId" to tabId,
                "position" to position,
                "isDarkMode" to isDarkMode,
                "isOpenBox" to isOpenBox,
                "isPackGift" to isPackGift
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabName =  it.getString("tabName")!!
            tabId = it.getString("tabId")!!
            position = it.getInt("position")
            isDarkMode = it.getBoolean("isDarkMode")
            isOpenBox = it.getBoolean("isOpenBox")
            isPackGift = it.getBoolean("isPackGift")
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.vm = mViewModel
        initRecyclerView()

    }

    private fun initRecyclerView() {
        mBinding.stateLayout.emptyLayout =
            if (isDarkMode) R.layout.room_gift_empty_dark else R.layout.room_gift_empty_light

        mBinding.recyclerView.grid(4).setup {
            singleMode = true
            addType<GiftModel> {
                if (isDarkMode) R.layout.room_dialog_gift_recycler_item_dark else R.layout.room_dialog_gift_recycler_item_light
            }
            onBind {
                findView<TextView>(R.id.tv_gift_price).apply {
                     setCompoundDrawablesRelativeWithIntrinsicBounds(if("人气" == tabName)com.kissspace.module_common.R.mipmap.ic_diamond_small_s else 0,0,0,0)
                }
            }
            onChecked { position, isChecked, _ ->
                val model = getModel<GiftModel>(position)
                model.checked = isChecked
                model.notifyChange()
                if (isChecked) {
                    val imageView =
                        mBinding.recyclerView.layoutManager?.findViewByPosition(position)
                            ?.findViewById<ImageView>(R.id.iv_gift)
                    imageView?.let { playChooseAnimation(it) }
                }
            }
            onFastClick(R.id.root) {
                if(!isLockStatus) {
                    val model = getModel<GiftModel>()
                    if(!model.isGiftLock()) {
                        if (isCheckedAll()) {
                            singleMode = true
                            checkedAll(false)
                            (requireParentFragment() as GiftDialog).checkAllGift(false)
                        } else {
                            checkedSwitch(modelPosition)
                            FlowBus.post(Event.ClearGiftChecked(tabId))
                            (requireParentFragment() as GiftDialog).onGiftClick(model)
                        }
                    }
                }else{
                    checkedSwitch(modelPosition)
                }
            }
        }.models = mutableListOf()
    }

    @SuppressLint("SetTextI18n")
    override fun createDataObserver() {
        super.createDataObserver()
        collectData(mViewModel.giftListEvent, onSuccess = {
            mBinding.recyclerView.bindingAdapter.addModels(it)
            //恢复上次选中的礼物，并滚动到该位置
            if (tabId != Constants.GIFT_TAB_ID_PACKAGE) {
                mBinding.tvAllGiftValue.visibility = View.GONE
                var position = 0
                it.forEachIndexed { index, model ->
                    if (isOpenBox) {
                        if (model.giftOrBox == "002" && model.boxType == "002") {
                            mBinding.recyclerView.bindingAdapter.setChecked(index, true)
                            (requireParentFragment() as GiftDialog).onGiftClick(model)
                        }
                    } else if (model.id == MMKVProvider.lastGiftIds) {
                        position = index
                        mBinding.recyclerView.bindingAdapter.setChecked(index, true)
                        (requireParentFragment() as GiftDialog).onGiftClick(model)
                    }
                }
                mBinding.recyclerView.scrollToPosition(position)
            }else{
                mViewModel.giftInBagPriceAll.value?.let { prices ->
                    mBinding.tvAllGiftValue.visibility = View.VISIBLE
                    mBinding.tvAllGiftValue.text = "背包礼物总价值：${prices}金币"
                }?:run {
                    mBinding.tvAllGiftValue.visibility = View.GONE
                }
            }
        }, onEmpty = {
            mBinding.tvAllGiftValue.visibility = View.GONE
            mBinding.stateLayout.showEmpty()
        })

        FlowBus.observerEvent<Event.ClearGiftChecked>(this) {
            if (tabId != it.tabId) {
                checkAllGiftInPag(false)
                (requireParentFragment() as GiftDialog).checkAllGift(false)
            }
        }

        FlowBus.observerEvent<Event.RefreshIntegralEvent>(this) {
            val params = mutableMapOf<String, Any?>()
            params["giftTabId"] = tabId
            if (tabId != Constants.GIFT_TAB_ID_PACKAGE) {
                scopeNetLife {
                    val data = mutableListOf<GiftModel>()
                    val giftModel = Get<MutableList<GiftModel>>(RoomApi.API_GET_GIFT_BY_ID){
                        param("giftTabId",tabId)
                    }.await()

                    val giftModel_fd = Get<MutableList<GiftModel>>(RoomApi.API_GET_FD_GIFT_BY_ID){
                        param("tagId",tabId)
                    }.await()
                    data.addAll(giftModel_fd)
                    data.addAll(giftModel)
                    data.let {
                        val model =
                            it.find { that -> that.giftOrBox == "002" && that.boxType == "002" }
                        if (model != null) {
                            val box = mBinding.recyclerView.getMutable<GiftModel>().find { that -> that.id == model.id }
                            box?.freeNumber = model.freeNumber
                            box?.notifyChange()
                        }
                    }
                }
            }
        }

        mViewModel.requestGiftList(tabId)
    }

    fun getCheckedGift() = mBinding.recyclerView.bindingAdapter.getCheckedModels<GiftModel>()


    fun getAllGift() = mBinding.recyclerView.bindingAdapter.models

    fun checkAllGift(isCheckedAll: Boolean) {
        mBinding.recyclerView.bindingAdapter.singleMode = !isCheckedAll
        checkAllGiftInPag(isCheckedAll)
    }

    private fun checkAllGiftInPag(checked:Boolean){
        mBinding.recyclerView.bindingAdapter.apply {
            if (checked) {
                if (singleMode) return
                for (i in 0 until itemCount) {
                    val  model  = getModel<GiftModel>(i)
                    if(model.isGiftLock()){
                        continue
                    }
                    if (!checkedPosition.contains(i)) {
                        setChecked(i, true)
                    }
                }
            } else {
                for (i in 0 until itemCount) {
                    if (checkedPosition.contains(i)) setChecked(i, false)
                }
            }
        }
    }

    private fun checkAllLockGift(){
        mBinding.recyclerView.bindingAdapter.apply {
            for (i in 0 until itemCount) {
                val  model  = getModel<GiftModel>(i)
                if (!checkedPosition.contains(i)&&model.isGiftLock()) {
                    setChecked(i, true)
                }else{
                    setChecked(i, false)
                }
            }
        }
    }

    fun checkModeChanged(isAll: Boolean) {
        isLockStatus = isAll
        mBinding.recyclerView.bindingAdapter.singleMode = !isAll
        if(isAll){
            checkAllLockGift()
        }else {
            clearChecked()
        }
    }


    fun clearChecked(){
        mBinding.recyclerView.bindingAdapter.checkedAll(false)
    }

    private fun playChooseAnimation(imageView: ImageView) {
        val scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.5f, 1f, 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.5f, 1f, 0.8f, 1f)
        val animatorSet = AnimatorSet().apply {
            duration = 500
            playTogether(scaleX, scaleY)
        }
        animatorSet.start()
    }

    @SuppressLint("SetTextI18n")
    fun updateGiftList(giftMessage: GiftMessage):Int{
        giftMessage.targetUsers.forEach {
            var giftValues =  mViewModel.giftInBagPriceAll.value
            it.giftInfos.forEach { gift ->
                val targetGift = mBinding.recyclerView.getMutable<GiftModel>().find { t -> t.id == gift.giftId }
                if (targetGift != null) {
                    giftValues = giftValues?.minus(targetGift.price *gift.total)
                    targetGift.num -= gift.total
                    if (targetGift.num <= 0) {
                        val index = mBinding.recyclerView.getMutable<GiftModel>().indexOfFirst { t->t.id ==targetGift.id }
                        mBinding.recyclerView.mutable.removeAt(index)
                        mBinding.recyclerView.bindingAdapter.checkedPosition.remove(index)
                        mBinding.recyclerView.bindingAdapter.notifyItemRemoved(index)
                    } else {
                        targetGift.notifyChange()
                    }
                }
            }
            mViewModel.giftInBagPriceAll.value = giftValues
            mBinding.tvAllGiftValue.text = "背包礼物总价值：${giftValues}金币"
        }
        return mBinding.recyclerView.models?.size ?: 0
    }

    fun updateLockGiftList(giftList: MutableList<PackGiftModel>):Int{
        giftList.forEach { gift ->
            val targetGift = mBinding.recyclerView.getMutable<GiftModel>().find { t -> t.id == gift.id }
            if (targetGift != null) {
                targetGift.lockFlag = gift.lockFlag
                targetGift.notifyChange()
            }
        }
        return mBinding.recyclerView.models?.size ?: 0
    }
}