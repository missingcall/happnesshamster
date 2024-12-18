package com.kissspace.message.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.didi.drouter.api.DRouter
import com.drake.brv.BindingAdapter
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.BroadcastMessage
import com.kissspace.common.base.BaseLazyFragment
import com.kissspace.common.router.jump
import com.kissspace.common.ext.safeClick
import com.kissspace.common.ext.setMarginStatusBar
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.model.ChatListModel
import com.kissspace.common.model.GiftEmailMessageModel
import com.kissspace.common.provider.IRoomProvider
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.jumpRoom
import com.kissspace.common.util.customToast
import com.kissspace.common.widget.CommonConfirmDialog
import com.kissspace.message.viewmodel.MessageViewModel
import com.kissspace.network.result.collectData
import com.kissspace.common.model.SystemMessageModel
import com.kissspace.message.widget.ChatDialog
import com.kissspace.module_message.R
import com.kissspace.module_message.databinding.FragmentMessageV3Binding
import com.kissspace.module_message.databinding.MessageChatListItemGiftemailBinding
import com.kissspace.util.hasNotificationPermission
import com.kissspace.util.isAppDebug
import com.kissspace.util.logE
import com.kissspace.util.requestNotificationPermission
import com.kissspace.util.swapWithHead

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/11/3
 * @Description: 消息fragment
 *
 */
class MessageFragmentV3 : BaseLazyFragment(R.layout.fragment_message_v3) {
    private val mBinding by viewBinding<FragmentMessageV3Binding>()
    private val mViewModel by viewModels<MessageViewModel>()

    //聊天消息adapter
    private lateinit var mRecentContactAdapter: BindingAdapter

    //系统消息adapter
    private lateinit var mSysMsgAdapter: BindingAdapter

    //礼物邮件adapter
    private lateinit var mGiftMailAdapter: BindingAdapter


    //private var menuList: MutableList<ItemMessageMenu> = ArrayList()

    /**
     * 系统广播监听
     */
    private val broadcastObserver = Observer<BroadcastMessage> {
        mViewModel.requestSystemMessage()
        mViewModel.requestGiftEmailMessage()
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun lazyInitView() {
        mBinding.vm = mViewModel
        viewLifecycleOwner.lifecycle.addObserver(mViewModel)
        initTitleBar()
        //initTopItemList()
        initRecyclerView()
        initRefreshLayout()
        registerObserver()
        showNotificationView()
    }

    override fun lazyLoadData() {
        initData()
        //查询历史回话列表
        mViewModel.queryRecentMessage()
    }

    override fun lazyClickListener() {
        super.lazyClickListener()


        //关闭通知提示
        mBinding.ivClose.safeClick {
            MMKVProvider.lastShowNotificationPermission = System.currentTimeMillis()
            mBinding.layoutNotification.visibility = View.GONE
        }

        //去开启通知
        mBinding.tvOpenNotification.safeClick {
            requestNotificationPermission(fragment = this) { isOpenNotification ->
                if (isOpenNotification) {
                    mBinding.layoutNotification.visibility = View.GONE
                }
            }
        }
        // jump(RouterPath.PATH_GIFT_MAIL)
        //  jump(RouterPath.PATH_CHAT, "account" to "djs6719efc8e4b02a6942fa1289", "userId" to "6719efc8e4b02a6942fa1289")

        /*getSelectPayChannelList { list ->
            DRouter.build(IPayProvider::class.java).getService()
                .showPayDialogFragment(childFragmentManager, list)
        }*/


        //清空消息
        mBinding.ivClearMessage.safeClick {
            CommonConfirmDialog(
                requireContext(), "消息清空", "清空全部消息，消息将不在保留"
            ) {
                if (this) {
                    NIMClient.getService(MsgService::class.java).clearMsgDatabase(true)
                    NIMClient.getService(MsgService::class.java).clearAllUnreadCount()
                    mRecentContactAdapter.mutable.clear()
                    mRecentContactAdapter.notifyDataSetChanged()
                    FlowBus.post(Event.RefreshUnReadMsgCount, delay = 100)
                    showEmptyContent()

                }
            }.show()


        }
        mBinding.ivClearUnread.safeClick {
            CommonConfirmDialog(
                requireContext(), "忽略未读", "消息气泡会删除掉，但仍然保留消息"
            ) {
                if (this) {
                    NIMClient.getService(MsgService::class.java).clearAllUnreadCount()
                    (mRecentContactAdapter.mutable as MutableList<ChatListModel>).forEach {
                        it.unReadCount = 0
                        it.notifyChange()
                    }
                }
            }.show()


        }


    }


    override fun lazyDataChangeListener() {
        super.lazyDataChangeListener()


        //获得消息
        collectData(mViewModel.getRecentMsgListEvent, onSuccess = {
            if (it.isEmpty() || it.size < 80) {
                mBinding.pageRefreshLayout.finishLoadMoreWithNoMoreData()
            } else {
                mBinding.pageRefreshLayout.finishLoadMore()
            }
            mRecentContactAdapter.models = it
            FlowBus.post(Event.RefreshUnReadMsgCount)
            showEmptyContent()
        }, onEmpty = {
            mBinding.pageRefreshLayout.finishLoadMoreWithNoMoreData()
            FlowBus.post(Event.RefreshUnReadMsgCount)
            showEmptyContent()
        })


        collectData(mViewModel.updateRecentEvent, onSuccess = {
            //从历史消息里面获取
            val history = mRecentContactAdapter.mutable as MutableList<ChatListModel>
            it.forEach { model ->
                var existModel: ChatListModel? = null
                var postion = -1
                history.forEachIndexed { index, chatListModel ->
                    if (chatListModel.fromAccount == model.fromAccount) {
                        existModel = chatListModel
                        postion = index
                    }
                }
                if (existModel == null) {
                    mRecentContactAdapter.addModels(mutableListOf(model), index = 0)
                } else {
                    //还是老的昵称和头像
                    existModel?.nickname = model.nickname
                    //显示的昵称
                    logE("model.nickname" + model.nickname)
                    existModel?.avatar = model.avatar
                    existModel?.content = model.content
                    existModel?.unReadCount = model.unReadCount
                    existModel?.date = model.date
                    existModel?.followRoomId = model.followRoomId
                    //交换位置
                    mRecentContactAdapter._data =
                        swapWithHead(mRecentContactAdapter.models?.toMutableList(), postion)
                    mRecentContactAdapter.notifyDataSetChanged()
                }
            }
            showEmptyContent()
        })






        collectData(mViewModel.bannerEvent, onSuccess = {
            mBinding.banner.visibility = View.VISIBLE
            mBinding.m = it
        })


        //系统消息
        collectData(mViewModel.systemMessageEvent, onSuccess = {

            if (it.records.isNotEmpty()) {
                //  data.unReadCount = it.total - MMKVProvider.systemMessageLastReadCount
                // MMKVProvider.systemMessageUnReadCount = data.unReadCount
              //  val list = mutableListOf<SystemMessageModel>()
              //  repeat(30) { t ->
              //      list.add(it.records[0])
              //  }
              //  mSysMsgAdapter.models = list

                mSysMsgAdapter.models = listOf(it.records[0])

            } else {
                if (mSysMsgAdapter.mutable.isEmpty()) return@collectData
                mSysMsgAdapter.mutable.clear()
                mSysMsgAdapter.notifyDataSetChanged()
            }
            showEmptyContent()
            FlowBus.post(Event.RefreshUnReadMsgCount)
        })
        //礼物邮件
        collectData(mViewModel.giftemailMessageEvent, onSuccess = {
            if (!it.list.isNullOrEmpty()) {
                mGiftMailAdapter.models = listOf(it.list!![0])
            } else {
                if (mGiftMailAdapter.mutable.isEmpty()) return@collectData
                mGiftMailAdapter.mutable.clear()
                mGiftMailAdapter.notifyDataSetChanged()
            }
            showEmptyContent()
            FlowBus.post(Event.RefreshUnReadMsgCount)
        }, onError = {
            customToast(it.errorMsg)
        })


    }


    override fun lazyEventListener() {
        super.lazyEventListener()

        FlowBus.observerEvent<Event.RefreshUnReadMsgCount>(this) {
            mViewModel.updateUnReadCount()
        }

        FlowBus.observerEvent<Event.RefreshChangeAccountEvent>(this) {
            mRecentContactAdapter.mutable.clear()
            mRecentContactAdapter.notifyDataSetChanged()
            mViewModel.anchor = null
            mViewModel.queryRecentMessage()
        }

        FlowBus.observerEvent<Event.MsgSystemEvent>(this) {
            mViewModel.requestSystemMessage()
        }

        //   FlowBus.observerEvent<Event.MsgRefreshDynamicNoticeEvent>(this) {
        //       mViewModel.requestDynamicMessageCount()
        //   }

        FlowBus.observerEvent<Event.NotificationEventOpen>(this) {
            mBinding.layoutNotification.visibility = View.GONE
        }

    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (isAppDebug) {
            LogUtils.e("onHiddenChanged:$hidden")
        }
    }

    private fun initTitleBar() {
        if (!isFromDialog()) {
            mBinding.titleBar.setMarginStatusBar()
        } else {
            // mBinding.tvTitle.gravity = Gravity.CENTER
        }

    }


    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mSysMsgAdapter = BindingAdapter().apply {
            addType<SystemMessageModel> { R.layout.message_chat_list_item_system }
            mutable = arrayListOf()
            onClick(R.id.root_system) {
                jump(RouterPath.PATH_SYSTEM_MESSAGE)
            }
        }
        mGiftMailAdapter = BindingAdapter().apply {
            addType<GiftEmailMessageModel>(R.layout.message_chat_list_item_giftemail)
            onBind {
                val binding = getBinding<MessageChatListItemGiftemailBinding>()
                val model = getModel<GiftEmailMessageModel>()
                binding.tvContent.text = model.remark.replace("\n\n","")
            }
            mutable = arrayListOf()
            onClick(R.id.root_giftemail) {
                jump(RouterPath.PATH_GIFT_MAIL)
            }
        }



        mRecentContactAdapter = BindingAdapter().apply {
            addType<ChatListModel> { R.layout.message_chat_list_item }


            onClick(R.id.root_chat) {
                val model = getModel<ChatListModel>()
                if (parentFragment is ChatDialog) {
                    (parentFragment as ChatDialog).addFragment(
                        ChatFragment.newInstance(
                            model.fromAccount!!,
                            model.fromAccount!!.substring(3, model.fromAccount!!.length),
                            true
                        )
                    )
                } else {
                    jump(
                        RouterPath.PATH_CHAT,
                        "account" to model.fromAccount!!,
                        "userId" to model.fromAccount!!.substring(3, model.fromAccount!!.length)
                    )
                }
            }

            onClick(R.id.tvDelete) {
                val model = getModel<ChatListModel>()
                val index = (mutable as MutableList<ChatListModel>).indexOf(model)
                CommonConfirmDialog(requireContext(), "确定要删除该条会话?") {
                    if (this) {
                        NIMClient.getService(MsgService::class.java)
                            .deleteRecentContact2(model.fromAccount, SessionTypeEnum.P2P)
                        NIMClient.getService(MsgService::class.java)
                            .clearChattingHistory(model.fromAccount, SessionTypeEnum.P2P, true)
                        mRecentContactAdapter.mutable.remove(model)
                        mRecentContactAdapter.notifyItemRemoved(index)
                        showEmptyContent()
                        FlowBus.post(Event.RefreshUnReadMsgCount)
                    }
                }.show()

            }

            onClick(R.id.layout_follow_room) {
                val model = getModel<ChatListModel>()
                val service = DRouter.build(IRoomProvider::class.java).getService()
                if (service.getRoomId() != model.followRoomId || !isFromDialog()) {
                    jumpRoom(crId = model.followRoomId)
                } else {
                    customToast("您已在当前房间")
                }
            }



            mutable = arrayListOf()
        }

        val adapter =
            ConcatAdapter(mSysMsgAdapter,mGiftMailAdapter,mRecentContactAdapter)
        mBinding.recyclerView.adapter = adapter
    }

    /**
     * 注册云信监听
     */
    private fun registerObserver() {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeBroadcastMessage(broadcastObserver, true)
    }

    private fun initRefreshLayout() {
        mBinding.pageRefreshLayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
            setOnLoadMoreListener {
                mViewModel.queryRecentMessage()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //请求礼物消息
        mViewModel.requestGiftEmailMessage()
    }

    private fun initData() {
        if (!isFromDialog()) mViewModel.requestBannerData()
        //请求系统消息
        mViewModel.requestSystemMessage()
    }


    /**
     * 显示或隐藏空布局
     */
    private fun showEmptyContent() {
        mBinding.stateLayout.let {
            if (mRecentContactAdapter.models.isNullOrEmpty() && mSysMsgAdapter.models.isNullOrEmpty() &&mGiftMailAdapter.models.isNullOrEmpty()) {
                it.showEmpty()
            } else {
                it.showContent()
            }
        }
    }

    private fun isFromDialog() = parentFragment != null && parentFragment is ChatDialog


    /**
     * 显示通知View
     */
    private fun showNotificationView() {
        if (isAdded) {
            if (context?.let { hasNotificationPermission(it) } != true) {
                if (!TimeUtils.isToday(MMKVProvider.lastShowNotificationPermission)) {
                    mBinding.layoutNotification.visibility = View.VISIBLE
                }
            } else {
                mBinding.layoutNotification.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //注销监听
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeBroadcastMessage(broadcastObserver, false)

    }

}

