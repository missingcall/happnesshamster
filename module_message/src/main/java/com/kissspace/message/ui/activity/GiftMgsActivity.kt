package com.kissspace.message.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.didi.drouter.annotation.Router
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.ext.safeClick
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.FollowListBean
import com.kissspace.common.model.GiftEmailMessageModel
import com.kissspace.common.model.GiftEmailReceiveState
import com.kissspace.common.router.RouterPath
import com.kissspace.common.router.jump
import com.kissspace.common.util.DJSDate
import com.kissspace.common.util.customToast
import com.kissspace.common.util.formatExpireTime
import com.kissspace.common.util.getFriendlyTimeSpanByNow
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.message.viewmodel.GiftEmailViewModel
import com.kissspace.message.widget.GiftMailDialog
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.MessageActivityGiftBinding
import com.kissspace.module_message.databinding.MessageItemGiftBinding
import com.kissspace.network.result.collectData
import java.text.SimpleDateFormat

/**
 * @description:礼物邮件页面
 * @author: yxt
 * @create: 2024-10-28 11:38
 **/
@Router(path = RouterPath.PATH_GIFT_MAIL)
class GiftMgsActivity :BaseActivity(R.layout.message_activity_gift){
    private val mBinding by viewBinding<MessageActivityGiftBinding>()
    private val mViewModel by viewModels<GiftEmailViewModel>()

    private val pageSize = 20

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.pageRefreshLayout.apply {
            onRefresh {
              mViewModel.requestGiftEmailMessage(mBinding.pageRefreshLayout.index,pageSize)
            }
            onLoadMore {
              mViewModel.requestGiftEmailMessage(mBinding.pageRefreshLayout.index,pageSize)
            }
        }.autoRefresh()


        mBinding.rvGift.linear().setup {
            addType<GiftEmailMessageModel> { R.layout.message_item_gift }

            onBind {
                val binding = getBinding<MessageItemGiftBinding>()
                val model = getModel<GiftEmailMessageModel>()

                binding.tvGiftName.text = "${model.title}"
                binding.tvGiftContent.text =model.remark.replace("\n\n","")

                //过期时间
                binding.tvGiftTime.text = getExpireTime(model.expireTime)

                when(model.receiveState){
                    GiftEmailReceiveState.WAIT->{//待领取
                        binding.imgHasGift.visibility =View.VISIBLE
                        binding.tvGiftStatus.visibility =View.GONE
                    }
                    GiftEmailReceiveState.RECEIVE->{//已领取
                        binding.imgHasGift.visibility =View.GONE
                        binding.tvGiftStatus.visibility=View.VISIBLE
                    }
                    else->{//已失效
                        binding.imgHasGift.visibility=View.GONE
                        binding.tvGiftStatus.visibility =View.GONE
                    }
                }

            }

            onClick(R.id.clGiftRoot){
                val model = getModel<GiftEmailMessageModel>()
                GiftMailDialog.newInstance(model, callback = {
                    mBinding.pageRefreshLayout.refresh()
                }).show(supportFragmentManager)

            }
        }.models = mutableListOf()



        //删除所有已领取 (删除已读)
        mBinding.tvDeleteRead.safeClick {
            mViewModel.deleteAllReceivedGiftEmail(success = {
                mBinding.pageRefreshLayout.refresh()
            })
        }

        //一键领取所有礼物邮件
        mBinding.tvAllReceive.safeClick {
            mViewModel.receiveGiftEmailAll(success = {
                mBinding.pageRefreshLayout.refresh()
            })
        }
    }


    override fun createDataObserver() {
        super.createDataObserver()
        //礼物邮件
        collectData(mViewModel.giftemailMessageEvent, onSuccess = {
                mBinding.pageRefreshLayout.addData(it.list, hasMore = {
                    mBinding.pageRefreshLayout.index * pageSize < it.total
                }, isEmpty = {
                    it.list.isNullOrEmpty()
                })
        }, onError = {
            customToast(it.errorMsg)
        })
    }

    private fun getExpireTime(data:String?):String{
        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val time = data?.substring(0,19)?.replace("T"," ")
            val d = format.parse(time)
            val timestamp = d?.time
            DJSDate.getDays(DJSDate.now, timestamp).let {
                return when {
                    it <= 0 -> "已到期"
                    it < 30 -> "${it}天后过期"
                    else -> ""
                }
            }
        }catch (ex:Exception){
            return ""
        }
        return ""
    }



}