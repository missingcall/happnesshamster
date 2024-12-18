package com.kissspace.mine.ui.activity.wallet

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.blankj.utilcode.util.KeyboardUtils
import com.didi.drouter.annotation.Router
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.parseIntent
import com.kissspace.common.widget.CommonBottomHintDialog
import com.kissspace.mine.ui.fragment.WalletTransferFragment
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityWalletTransferHamsterBinding

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/17
 * @Description: 松子/钻石 转赠页面
 *
 */
@Router(path = RouterPath.PATH_WALLET_TRANSFER)
class WalletTransferActivity : BaseActivity(R.layout.mine_activity_wallet_transfer_hamster) {
    private val type by parseIntent<String>()
    private val mBinding by dataBinding<MineActivityWalletTransferHamsterBinding>()
    private val mViewModel by viewModels<WalletViewModel>()


    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.m = mViewModel
        mBinding.lifecycleOwner = this

        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int) = when (position) {
                0 -> WalletTransferFragment.newInstance(Constants.HamsterTransferAccountsType.PINE_NUTS)
                else -> WalletTransferFragment.newInstance(Constants.HamsterTransferAccountsType.DIAMONDS)
            }
        }

        ViewPager2Delegate.install(mBinding.viewPager, mBinding.dslTabLayout)


        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                initData(position)
                /*when (position) {
                    0 -> pineConesToPineNuts()
                    else -> pineNutsToDiamonds()
                }*/
            }
        })

        mBinding.viewPager.currentItem = type.toInt() - 2

        //获取钱包
        getMoney()

        //安全提示弹窗
        when (type) {
            Constants.HamsterWalletType.PINE_NUT.type -> CommonBottomHintDialog.newInstance(
                getString(R.string.mine_transfer_tips_pine_nut_dialog),
                true
            )
                .show(supportFragmentManager)
            Constants.HamsterWalletType.DIAMONDS.type -> CommonBottomHintDialog.newInstance(
                getString(R.string.mine_transfer_tips_diamonds_dialog),
                true
            )
                .show(supportFragmentManager)
        }
    }

    private fun getMoney() {
        mViewModel.getMyMoneyBag {
            it?.let {
                mViewModel.walletModel.value = it
            }
        }
    }


    private fun initData(position: Int) {
        //获取兑换比例 放到WalletViewModel中 下级fragment直接取 逻辑是滑动到那个fragment就获取哪个 这样不会提前保存3个且实时
        when (position) {
            0 -> mViewModel.expectedTransferAccounts(1.0, null, Constants.HamsterTransferAccountsType.PINE_NUTS)
            1 -> mViewModel.expectedTransferAccounts(1.0, null, Constants.HamsterTransferAccountsType.DIAMONDS)

            else -> {}
        }


    }


    override fun createDataObserver() {
        super.createDataObserver()

        FlowBus.observerEvent<Event.RefreshCoin>(this) {
            getMoney()
        }
    }


    override fun onResume() {
        super.onResume()

    }

    //点击空白区域隐藏软件盘并清除焦点
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this)
                v?.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val l = IntArray(2)
            v.getLocationOnScreen(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.rawX > left && event.rawX < right && event.rawY > top && event.rawY < bottom)
        }
        return false
    }

}