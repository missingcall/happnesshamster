package com.hamster.happyness.widget

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import coil.load
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hamster.happyness.R
import com.hamster.happyness.databinding.DialogChangeAccountBinding
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.UserAccountBean
import com.kissspace.common.util.copyClip
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.oldAccountExit
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.network.result.collectData
import com.kissspace.setting.viewmodel.ChangeAccountViewModel
import com.kissspace.util.toast

/**
 * 左边抽屉-账号多身份
 */
class ChangeAccountDialog : BaseDialogFragment<DialogChangeAccountBinding>(DialogChangeAccountBinding::inflate, Gravity.LEFT, true, false) {
    private val mViewModel by viewModels<ChangeAccountViewModel>()
    private var userPhone: String = ""
    private var isCreateAccount = false
    private var loginAccountPosition = 0;

    override fun getLayoutId(): Int {
        return R.layout.dialog_change_account
    }

    companion object {
        fun newInstance() = ChangeAccountDialog().apply {
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.ivAvatar.load(MMKVProvider.userInfo?.profilePath)
        mBinding.tvNickname.text = MMKVProvider.userInfo?.nickname
        mBinding.tvUserId.text = MMKVProvider.userInfo?.displayId

        mBinding.ivCopy.safeClick {
            copyClip(mBinding.tvUserId.text.toString())
        }

        mBinding.conAdd.safeClick {
            createAccount()
        }
        initRecyclerView()
        initData()
    }

    override fun observerData() {
        super.observerData()

        collectData(mViewModel.accounts, onSuccess = {
            it.forEachIndexed { index, userAccountBean ->
                if (userAccountBean.userId == MMKVProvider.userId) {
                    loginAccountPosition = index
                }
            }
            if (it.size < 10) {
                mBinding.conAdd.visibility = View.VISIBLE
            } else {
                mBinding.conAdd.visibility = View.GONE
            }
            mBinding.recycler.bindingAdapter.addModels(it)
            mBinding.recycler.bindingAdapter.setChecked(loginAccountPosition, true)
        })

        collectData(mViewModel.createAccounts, onSuccess = {
            mViewModel.loginByUserId(it.userId)
        }, onError = {
            hideLoading()
            toast("创建账号失败,请稍后重试")
        })

        collectData(mViewModel.token, onSuccess = {
            //先退出之前账号的房间
            oldAccountExit {
                mViewModel.loginIm(it, onSuccess = {
                    if (!isCreateAccount) {
                        isCreateAccount = false
                        changeAccountSuccess()
                    }
                })
            }
        }, onError = {
            hideLoading()
            toast("切换账号失败")
        })
    }

    private fun initData() {
        userPhone = MMKVProvider.userPhone
        mViewModel.requestUserListByPhone(MMKVProvider.userPhone)
    }

    private fun initRecyclerView() {
        mBinding.recycler.linear()
            //.divider(com.kissspace.module_common.R.drawable.common_user_account_divider_item)
            .setup {
                addType<UserAccountBean> {
                    R.layout.layout_change_account_list_item
                }
                onBind {
                    findView<ConstraintLayout>(R.id.root).safeClick {
                        val model = getModel<UserAccountBean>()
                        if (model.checked) {
                            toast("当前账号已登录")
                            return@safeClick
                        }
                        loginAccountPosition = adapterPosition
                        showLoading("切换中...")
                        mViewModel.loginByUserId(model.userId)
                    }
                }
                onChecked { position, isChecked, _ ->
                    val model = getModel<UserAccountBean>(position)
                    model.checked = isChecked
                    model.notifyChange()
                }
                singleMode = true
                clickThrottle = 500
            }.models = mutableListOf()
    }

    private fun changeAccountSuccess() {
        FlowBus.post(Event.RefreshChangeAccountEvent)
        FlowBus.post(Event.CloseRoomFloating)
        mBinding.recycler.bindingAdapter.setChecked(loginAccountPosition, true)
        toast("切换账号成功")
    }

    private fun createAccount() {
        isCreateAccount = true
        showLoading()
        mViewModel.createAccountNew(userPhone)
    }
}