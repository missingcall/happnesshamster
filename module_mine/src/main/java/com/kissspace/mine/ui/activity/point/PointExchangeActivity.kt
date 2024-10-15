package com.kissspace.mine.ui.activity.point

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.kissspace.common.base.v2.BaseMVVMActivity
import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.mine.bean.AccountInfoBean
import com.kissspace.mine.bean.AccountToDK
import com.kissspace.mine.http.MineApi
import com.kissspace.mine.viewmodel.PointViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.ActivityPointExchangeBinding
import com.kissspace.network.net.netCatch
import com.kissspace.network.result.collectData
import java.text.DecimalFormat


@Router(uri = RouterPath.PATH_POINT_EXCHANGE)
class PointExchangeActivity : BaseMVVMActivity<PointViewModel, ActivityPointExchangeBinding>(ActivityPointExchangeBinding::inflate){


    private var accountInfoBean: AccountToDK? = null
    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mViewBinding.titleBar.setMarginStatusBar()
//        mViewBinding.recyclerView.linear().setup {
//            singleMode = true
//            addType<AccountInfoBean> {  R.layout.rv_account }
//            singleMode = true
//            onChecked { position, isChecked, _ ->
//                val model = getModel<AccountInfoBean>(position)
//                model.check = isChecked
//                model.notifyChange()
//                accountInfoBean = model
//                mViewBinding.llLayout.visibility = View.VISIBLE
//                mViewBinding.tvChangeAccountName.text = model.nickname
//                mViewBinding.tvChangeAccountCanUserPoint.text ="可用宝石:"+ model.accountBalance
//                mViewBinding.tvChangeAccountId.text ="ID:"+ model.displayId
//                mViewBinding.tvChangeAccountMain.visibility = if(position == 0) View.VISIBLE else View.GONE
//            }
//
//
//            onBind {
//                findView<TextView>(R.id.tv_account_main).visibility = if(modelPosition == 0) View.VISIBLE else View.GONE
//            }
//
//            onFastClick(R.id.ll_layout) {
//                checkedAll(false)
//                setChecked(modelPosition, true)
//            }
//        }.models  = mutableListOf()

        mViewBinding.tvCurrentPhone.text = "当前登录手机号："+ (MMKVProvider.userInfo?.mobile ?: "")

        mViewBinding.tvPointExchangeRecord.setOnClickListener {
            jump(path = RouterPath.PATH_POINT_RECORD)
        }

        mViewBinding.tvCommit.setOnClickListener {

            if(mViewBinding.etDiamondAmount.text.isEmpty()){
                customToast("请输入兑换的宝石数量")
                return@setOnClickListener
            }
                scopeNetLife {
                    accountInfoBean?.let {
                        showLoading()
                        Post<Boolean>(MineApi.API_POINT_EXCHANGED) {
                        json(
                            "userId" to MMKVProvider.userId,
                            "stoneNum" to mViewBinding.etDiamondAmount.text.toString(),
                            "displayId" to it.displayId,
                        )
                      }.await()
                        bindData()
                        mViewBinding.etDiamondAmount.text = null
                        customToast("兑换成功")
                        hideLoading()
                    }?:run {
                        customToast("请选择需要兑换的账号")
                    }
            }.netCatch {
                customToast(it.message)
                    hideLoading()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun createDataObserver() {
        collectData(mViewModel.accountList, onSuccess = {
            accountInfoBean = it
            mViewBinding.llLayout.visibility = View.VISIBLE
            mViewBinding.tvChangeAccountName.text = it.nickname
            mViewBinding.tvChangeAccountCanUserPoint.text ="可用钻石:"+ it.accountBalance
            mViewBinding.tvChangeAccountId.text ="ID:"+ it.displayId
            mViewBinding.tvChangeAccountMain.visibility =  View.VISIBLE
//            mViewBinding.recyclerView.models = it.list
//            if(it.list.isNotEmpty()) {
//                mViewBinding.tvExchangeRate.text = "兑换比例："+it.scale+"积分=1钻石"
//                mViewBinding.recyclerView.bindingAdapter.setChecked(0,true)
//            }else{
//                customToast("未查询到趣玩平台账号")
//            }
        }, onEmpty = {
            customToast("未查询到迪乐工坊平台账号")

        }, onError = {
            customToast(it.message)
        })
    }

    override fun isStatusBarHide(): Boolean {
        window.statusBarColor = Color.TRANSPARENT
        return true
    }



    @SuppressLint("SetTextI18n")
    override fun bindData() {
        MMKVProvider.userInfo?.let { mViewModel.getPointRecord(it.mobile) }

        getAppConfigByKey<String>(AppConfigKey.ExchangedRate) {
            try {
                mViewBinding.tvExchangeRate.text = "兑换比例：1钻石="+it+"积分"
            }catch (_:Exception){

            }
        }
    }
}