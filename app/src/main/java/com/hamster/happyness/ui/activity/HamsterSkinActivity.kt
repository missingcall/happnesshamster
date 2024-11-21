package com.hamster.happyness.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import com.blankj.utilcode.util.SpanUtils
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.hamster.happyness.databinding.ActivityHamsterSkinBinding
import com.kissspace.mine.viewmodel.HamsterViewModel
import com.hamster.happyness.widget.HomeSkinUnlockDialog
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.InfoListModel
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.customToast
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.util.logE

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/17
 * @Description: 手机验证码登录页面
 *
 */
@Router(path = RouterPath.PATH_HAMSTER_SKIN)
class HamsterSkinActivity : BaseActivity(com.hamster.happyness.R.layout.activity_hamster_skin) {
    private val mBinding by dataBinding<ActivityHamsterSkinBinding>()
    private val mViewModel by viewModels<HamsterViewModel>()
    private val mWalletViewModel by viewModels<WalletViewModel>()


    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this
        mBinding.pageRefreshLayout.apply {
            onRefresh {
                initData(true)
            }
            onLoadMore {
                initData(false)
            }
        }.refresh()
        initRecyclerView()

    }

    private fun initData(isRefresh: Boolean) {
        if (isRefresh) {
            mBinding.pageRefreshLayout.index = 1
        }
        mViewModel.requestInfoList(mBinding.pageRefreshLayout.index) {
            if (isRefresh) {
                mBinding.recyclerView.bindingAdapter.mutable.clear()
                if (it?.records?.size == 0) {
                    mBinding.pageRefreshLayout.showEmpty()
                } else {
                    mBinding.pageRefreshLayout.addData(
                        it?.records
                    )
                    mBinding.pageRefreshLayout.showContent()
                    //默认选中第一条
                    mBinding.recyclerView.bindingAdapter.setChecked(0, true)
                }
                mBinding.pageRefreshLayout.finishRefresh()
            } else {
                mBinding.pageRefreshLayout.addData(
                    it?.records
                )
                mBinding.pageRefreshLayout.finishLoadMore()
            }
            logE("it.total" + it?.total)
            if (mBinding.recyclerView.bindingAdapter.models?.size == it?.total) {
                mBinding.pageRefreshLayout.finishLoadMoreWithNoMoreData()
            } else {
                mBinding.pageRefreshLayout.setNoMoreData(false)
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.grid(4).setup {
            addType<InfoListModel.Record> { com.hamster.happyness.R.layout.rv_item_skin }

            onFastClick(com.hamster.happyness.R.id.clSkin) {
                val model = getModel<InfoListModel.Record>()
                //设置选中状态
                val checked = model.checked
                if (!checked) {
                    setChecked(adapterPosition, !checked)
                }

            }

            onChecked { position, isChecked, _ ->
                val model = getModel<InfoListModel.Record>(position)
                mViewModel.record.set(model)
                model.checked = isChecked
                model.notifyChange()

//                mBinding.ivImageDisplay.loadImage(model.icon)
//                mBinding.tvName.text = model.name
//                mBinding.tvDescribe.text = model.remark
                setBtnSkinStatus(position, mBinding.btn, model)

            }
            //单选模式
            singleMode = true
        }.models = mutableListOf()
    }


    override fun createDataObserver() {
        super.createDataObserver()

        FlowBus.observerEvent<Event.WearSkinEvent>(this) {
            mBinding.pageRefreshLayout.autoRefresh()
        }

        //解锁成功
        FlowBus.observerEvent<Event.UnLockSkinEvent>(this) {
            getMoney()
            mBinding.pageRefreshLayout.autoRefresh()
        }

    }

    /**
     * 皮肤状态-按钮 (已使用、立即使用、购买)
     */
    private fun setBtnSkinStatus(position: Int, button: Button, record: InfoListModel.Record) {
        //已解锁
        if (record.unlockStatus) {
            //已配带
            if (record.wearStatus) {
                button.text = "已使用"
                button.isEnabled = false
                //未佩戴
            } else {
                button.text = "立即使用"
                button.isEnabled = true
                button.safeClick {
                    mViewModel.wearSkin(position / Constants.PageSize + 1, record.id, onSuccess = {
                        customToast("使用成功")
                        FlowBus.post(Event.WearSkinEvent)
                    })
                }
            }
        } else {
            button.isEnabled = true
            //确认购买
            val spanString = SpanUtils()
                .append("确认购买 ")
                .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
                .append(record.price.toString()).setForegroundColor(Color.parseColor("#FDC120"))
                .create()
            button.text = spanString

            button.safeClick {
                HomeSkinUnlockDialog.newInstance(position / Constants.PageSize + 1, record.id ,mWalletViewModel.walletModel.value?.diamond).show(supportFragmentManager)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getMoney()
    }

    private fun getMoney() {
        mWalletViewModel.getMyMoneyBag {
            it?.let {
                mWalletViewModel.walletModel.value = it
            }
        }
    }

}