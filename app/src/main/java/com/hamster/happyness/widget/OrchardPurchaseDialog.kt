package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.drake.brv.utils.*
import com.hamster.happyness.databinding.DialogOrchardPurchaseBinding
import com.hamster.happyness.viewmodel.HamsterViewModel
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.InfoListModel
import com.kissspace.common.model.QueryBaseInfoList
import com.kissspace.common.util.customToast
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.mine.viewmodel.WalletViewModel

/**
 * 底部弹窗-果园购买/激活
 */
class OrchardPurchaseDialog : BaseDialogFragment<DialogOrchardPurchaseBinding>(DialogOrchardPurchaseBinding::inflate, Gravity.BOTTOM) {
    private val mViewModel by activityViewModels<WalletViewModel>()
    private val mHamsterViewModel by activityViewModels<HamsterViewModel>()
    private var mType = 1 //1.购买 2.激活
    private var mIsBaseHamster = false //是否是基础仓鼠
    private var mBaseId: String? = null

    companion object {
        fun newInstance(type: Int, isBaseHamster: Boolean) = OrchardPurchaseDialog().apply {
            arguments = bundleOf("type" to type, "isBaseHamster" to isBaseHamster)
        }
    }


    override fun getLayoutId(): Int {
        return com.hamster.happyness.R.layout.dialog_orchard_purchase
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.m = mViewModel

        val item = mViewModel.queryMarketItem.value

        //是否是基础仓鼠
        if (mIsBaseHamster) {
            mBinding.tvSkinSelection.visibility = View.VISIBLE
            mBinding.clSkinSelection.visibility = View.VISIBLE

            initRecyclerView()
            initData()
        } else {
            mBinding.tvSkinSelection.visibility = View.GONE
            mBinding.clSkinSelection.visibility = View.GONE
        }

        //购买
        if (mType == 1) {
            mBinding.tvConditionsDetail.text = "消耗" + when (item?.payType) {
                "001" -> "松果"
                "002" -> "松子"
                "003" -> "三方"
                else -> "勋章"
            } + item?.coinPrice + "/" + mViewModel.walletModel.value?.diamond

            mBinding.ivConditionsAvailable.visibility = if (item?.coinPrice!! <= mViewModel.walletModel.value?.diamond!!) View.VISIBLE else View.GONE

            mBinding.ibBack.safeClick {
                dismiss()
            }

            mBinding.btnCancel.safeClick {
                dismiss()
            }

            mBinding.btnConfirm.safeClick {
                CommonConfirmDialog(
                    requireContext(),
                    "您确定要消耗${item.coinPrice}个" + when (item.payType) {
                        "001" -> "松果"
                        "002" -> "松子"
                        "003" -> "三方"
                        else -> "勋章"
                    } + "购买${item.commodityName}吗？",
                    ""
                ) {
                    if (this) {
                        mViewModel.buy(mBaseId, item.commodityInfoId, item.coinPrice, 1, Constants.HamsterPayType.PINE_CONE, onSuccess = {
                            //购买成功
                            customToast("购买成功")
                            dismiss()
                            FlowBus.post(Event.OrchardPurchaseEvent)
                        })
                    }
                }.show()


            }
            //激活
        } else {
            mBinding.tvSkinSelection.visibility = View.GONE
            mBinding.clSkinSelection.visibility = View.GONE
            mBinding.tvConditions.visibility = View.GONE
            mBinding.clConditions.visibility = View.GONE

            mBinding.btnConfirm.text = "立即激活"
            mBinding.btnConfirm.safeClick {

                CommonConfirmDialog(
                    requireContext(),
                    "您确定要激活${item?.commodityName}吗？",
                    ""
                ) {
                    if (this) {
                        mViewModel.activation(item?.propId, onSuccess = {
                            //购买成功
                            customToast("激活成功")
                            dismiss()
                            FlowBus.post(Event.OrchardActivationEvent)
                        })
                    }
                }.show()


            }
        }


    }

    private fun initData() {
        mHamsterViewModel.queryBaseInfoList {
            mBinding.recyclerView.models = it
        }
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.linear().setup {
            addType<QueryBaseInfoList.QueryBaseInfoListItem> { com.hamster.happyness.R.layout.rv_item_basic_hamster_skin }

            onFastClick(com.hamster.happyness.R.id.item) {
                val model = getModel<QueryBaseInfoList.QueryBaseInfoListItem>()
                //设置选中状态
                val checked = model.checked
                if (!checked) {
                    setChecked(adapterPosition, !checked)
                }

            }

            onChecked { position, isChecked, _ ->
                val model = getModel<QueryBaseInfoList.QueryBaseInfoListItem>(position)
                mHamsterViewModel.baseInfoListItem.set(model)
                model.checked = isChecked
                model.notifyChange()

                mBaseId = model.id.toString()
            }
            //单选模式
            singleMode = true
        }.models = mutableListOf()

        //默认选中第一条
        mBinding.recyclerView.bindingAdapter.setChecked(0, true)
    }

    override fun observerData() {
        super.observerData()


    }
}