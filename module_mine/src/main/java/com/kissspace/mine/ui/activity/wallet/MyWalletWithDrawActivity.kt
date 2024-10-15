package com.kissspace.mine.ui.activity.wallet

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.widget.ImageView
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.model.wallet.PayProductResponses
import com.kissspace.common.model.wallet.WithDrawModel
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.RouterPath.PATH_USER_WITHDRAW_RECODE
import com.kissspace.common.router.jump
import com.kissspace.common.router.parseIntent
import com.kissspace.common.util.customToast
import com.kissspace.common.util.format.Format
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.common.widget.CommonHintDialog
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityWalletWithdrawBinding
import com.kissspace.util.immersiveStatusBar
import com.kissspace.util.isNotEmpty
import com.kissspace.util.isNotEmptyBlank
import com.kissspace.util.orZero
import com.kissspace.util.pxToDp


/**
 * @Author gaohangbo
 * @Date 2023/1/3 09:48.
 * @Describe 收益提现
 */
@Router(path = RouterPath.PATH_USER_WALLET_WITHDRAW)
class MyWalletWithDrawActivity : BaseActivity(R.layout.mine_activity_wallet_withdraw) {

    private val mBinding by viewBinding<MineActivityWalletWithdrawBinding>()

    private val mViewModel by viewModels<WalletViewModel>()

    private var walletType by parseIntent<String>()

    //当前选择的支付方式
    private var currentWalletRechargeList: WithDrawModel? = null

    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(true)
        mBinding.titleBar.setMarginStatusBar()
        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                handleBackPressed()
            }

            override fun onRightClick(titleBar: TitleBar?) {
                super.onRightClick(titleBar)
                jump(
                   PATH_USER_WITHDRAW_RECODE,
                "walletType" to walletType
                )
            }
        }
        )

//        mBinding.titleBar.
        //        mBinding.tvWithDrawRecode.safeClick {

//        }

        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this


        mBinding.tvAllWithDraw.safeClick {
            if (mViewModel.withDrawBalance.value.orZero() > withDrawMaxNumber) {
                mBinding.etWithDraw.setText(withDrawMaxNumber.toString())
            } else {
                mBinding.etWithDraw.setText(Format.E.format(mViewModel.withDrawBalance.value.orZero()))
            }
        }

        mBinding.etWithDraw.onAfterTextChanged = {
            if (it.isNotEmptyBlank()) {
                mViewModel.withDrawNumberContent.value = it
            } else {
                mViewModel.withDrawNumberContent.value = null
            }
        }

        mBinding.rvWithdrawWay.divider {
            setMargin(start = 8, dp = true)
        }
        mBinding.rvWithdrawWay.isNestedScrollingEnabled = false
        mBinding.rvWithdrawWay.linear().setup {
            addType<WithDrawModel>(com.kissspace.module_common.R.layout.common_item_withdraw_type)
            singleMode = true
            onBind {
                val model = getModel<WithDrawModel>()
                val image =
                    this.findView<ImageView>(com.kissspace.module_common.R.id.iv_icon)
                when(model.withDrawType){
                    1->{
                        image.setBackgroundResource(com.kissspace.module_common.R.mipmap.common_ic_withdraw_ali)
                    }
                    2 ->{
                        image.setBackgroundResource(com.kissspace.module_common.R.mipmap.common_ic_withdraw_bank)
                    }
                }
            }
            onChecked { position, isChecked, _ ->
                val model = getModel<WithDrawModel>(position)
                model.isSelected = isChecked
                model.notifyChange()
            }

            onFastClick(com.kissspace.module_common.R.id.cl_pay_type) {
                checkedAll(false)
                setChecked(modelPosition, true)
                val model = getModel<WithDrawModel>(modelPosition)
                currentWalletRechargeList = model
            }
        }.models = mutableListOf()

        mBinding.rvWithdrawWay.bindingAdapter.models= mutableListOf(WithDrawModel(1,"提现到支付宝",false),
            WithDrawModel(2,"提现到银行卡",false))

        mBinding.rvWithdrawWay.bindingAdapter.setChecked(0, true)

        when (walletType) {
            Constants.WalletType.EARNS.type -> {
                mViewModel.withDrawImage.value = R.mipmap.mine_wallet_earns_withdraw
                mViewModel.withDrawType.value = Constants.WalletType.EARNS.type
                mViewModel.withDrawTypeTitle.value = "收益提现"

                mViewModel.withDrawTextHint.value =
                    resources.getString(R.string.mine_wallet_withdraw_earns_hint)
                        .replaceFirst("%d", MMKVProvider.withdrawDepositFee)
                        .replaceFirst("%s", MMKVProvider.wechatPublicAccount)
            }

            Constants.WalletType.DIAMOND.type -> {
                mViewModel.withDrawImage.value = R.mipmap.mine_wallet_diamond_withdraw
                mViewModel.withDrawType.value = Constants.WalletType.DIAMOND.type
                mViewModel.withDrawTypeTitle.value = "钻石提现"
                mViewModel.withDrawTextHint.value =
                    resources.getString(R.string.mine_wallet_withdraw_diamond_hint)
                        .replaceFirst("%s", MMKVProvider.wechatPublicAccount)
            }

            else -> {

            }
        }

        val hint = SpannableString("${mViewModel.withDrawType.value}最低提现10000")

        hint.setSpan(AbsoluteSizeSpan(18, true), 0, hint.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mBinding.etWithDraw.hint = hint

        mBinding.tvConfirm.safeClick {
            mViewModel.withDrawNumber.value = mViewModel.withDrawNumberContent.value?.toDouble()
            var withdrawType: Int? = null
            if (walletType == Constants.WalletType.EARNS.type) {
                withdrawType = 1
            } else if (walletType == Constants.WalletType.DIAMOND.type) {
                withdrawType = 2
            }

            val withdrawalPaymentType =  mBinding.rvWithdrawWay.bindingAdapter.checkedPosition[0]
            LogUtils.e("withdrawalPaymentType:$withdrawalPaymentType")
            if(withdrawalPaymentType == -1){
                ToastUtils.showShort(
                    "请选择提现方式"
                )
            }

            if (mViewModel.withDrawNumber.value != null) {
                if (mViewModel.withDrawNumber.value.orZero() > mViewModel.withDrawBalance.value.orZero()) {
                    ToastUtils.showShort("提现${walletType}余额不足")
                    return@safeClick
                }
                if (mViewModel.withDrawNumber.value.orZero() < withDrawMinNumber) {
                    ToastUtils.showShort(
                        "提现${walletType}收益不能小于${
                            Format.E.format(
                                withDrawMinNumber
                            )
                        }"
                    )
                    return@safeClick
                }
                if (mViewModel.withDrawNumber.value.orZero() > withDrawMaxNumber) {
                    ToastUtils.showShort(
                        "提现${walletType}不能大于${
                            Format.E.format(
                                withDrawMaxNumber
                            )
                        }"
                    )
                    return@safeClick
                }

                if (mViewModel.withDrawNumber.value.orZero() != 0.0) {
                    //是公会成员才能提现
                    getUserInfo(onSuccess = {
                        if (it.family) {
                            mViewModel.withDrawNumber(
                                mViewModel.withDrawNumber.value.orZero(),
                                withdrawType.orZero(),
                                withdrawalPaymentType+1,
                                { isWithdrawSuccess ->
                                    if (isWithdrawSuccess == true) {
                                        ToastUtils.showShort("申请提现成功")
                                        jump(
                                            RouterPath.PATH_USER_WALLET_OPERATE_SUCCESS,
                                            "number" to mViewModel.withDrawNumber.value.orZero(),
                                            "successType" to Constants.SuccessType.WITHDRAW.type,
                                            "walletType" to walletType
                                        )
                                        //设置内容清空
                                        mBinding.etWithDraw.setText("")
                                    }


                                },
                                { s ->
                                    CommonConfirmDialog(this, "提交失败", subTitle = ((s ?: "").toString()), negativeString = "取消"){

                                    }.show()

                                }
                            )
                        } else {
                            customToast("不是公会成员")
                        }
                    })
                } else {
                    ToastUtils.showShort("请输入提现金额")
                }
            }
        }
    }

    private fun changePayTypeList(listPayProductList: List<PayProductResponses>?) {
        mBinding.rvWithdrawWay.bindingAdapter.models=listPayProductList
        mBinding.rvWithdrawWay.bindingAdapter.checkedAll(false)

        listPayProductList?.let {
            if (listPayProductList.isNotEmpty()) {
                //changePayTypeSelect(listPayProductList)
            }
        }
    }

//    private fun changePayTypeSelect(listPayProductList: List<PayProductResponses>?) {
//        listPayProductList.apply {
//            this?.map { it.isSelected = false }
//        }.also { list ->
//            val position = list?.indexOfFirst {
//                it.payProductCash == currentSelectRmb
//            }
//            if (position == -1) {
//                list.size.let {
//                    if (it > 0) {
//                        mBinding.rvWithdrawWay.bindingAdapter.checkedAll(false)
//                        //设置默认选中第一个
//                        mBinding.rvWithdrawWay.bindingAdapter.setChecked(0, true)
//                        mViewModel.mRmb.value = "¥" +
//                                Format.E.format(listPayProductList?.get(0)?.payProductCash)
//                        list[0].isSelected = true
//                        list[0].notifyChange()
//                    }
//                }
//            } else {
//                mBinding.rvWithdrawWay.bindingAdapter.checkedAll(false)
//                if (position != null) {
//                    mBinding.rvWithdrawWay.bindingAdapter.setChecked(position, true)
//                    list[position].isSelected = true
//                    mViewModel.mRmb.value =
//                        "¥" + Format.E.format(list[position].payProductCash)
//                    list[position].notifyChange()
//                }
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        mViewModel.getMyMoneyBag {
            it?.let {
                mViewModel.walletModel.value = it
                when (walletType) {
                    Constants.WalletType.EARNS.type -> {
                        mViewModel.withDrawBalance.value = it.accountBalance.orZero()
                    }

                    Constants.WalletType.DIAMOND.type -> {
                        mViewModel.withDrawBalance.value = it.diamond.orZero()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    companion object {
        const val withDrawMaxNumber: Int = 9000000
        const val withDrawMinNumber: Int = 10000
    }

}