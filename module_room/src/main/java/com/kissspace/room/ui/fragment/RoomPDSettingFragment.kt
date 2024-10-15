package com.kissspace.room.ui.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.core.os.bundleOf
import com.kissspace.common.config.AppConfigKey
import com.kissspace.common.config.Constants
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.flowbus.Event
import com.kissspace.common.flowbus.FlowBus
import com.kissspace.common.http.getAppConfigByKey
import com.kissspace.common.model.PDModel
import com.kissspace.common.model.immessage.BaseAttachment
import com.kissspace.common.util.customToast
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.module_room.databinding.RoomFragmentPdSettingBinding
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.ChatRoomService

/**
 * @Description: PK设置
 */
class RoomPDSettingFragment : BaseDialogFragment<RoomFragmentPdSettingBinding>(RoomFragmentPdSettingBinding::inflate,Gravity.CENTER, withScreen = false) {
    private lateinit var roomId: String
    private lateinit var roomTitle: String

    companion  object {
        fun newInstance(roomId: String,roomTitle: String) =
            RoomPDSettingFragment().apply {
                arguments = bundleOf( "roomId" to roomId,"roomTitle" to roomTitle)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = arguments?.getString("roomId")!!
        roomTitle = arguments?.getString("roomTitle")!!
    }

//    override fun getViewBinding(): RoomFragmentPdSettingBinding {
//        return RoomFragmentPdSettingBinding.inflate(layoutInflater)
//    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun initView() {

        mBinding.titleBar.setTitleBarListener(onRightClick = {
            dismiss()
        })

        mBinding.tvCommit.setOnClickListener {
            startPD()
        }
    }

    private fun startPD() {
        mBinding.etPdContent.text.toString().let {
            if(it.isEmpty()){
                customToast("派单内容不能为空")
            }else{
                getAppConfigByKey<String>(AppConfigKey.DAEMON_NETEASE_ROOM_ID) { roomNeteaseId ->
                    val pdModel = PDModel(
                        roomId,
                        roomTitle,
                        mBinding.etPdContent.text.toString(),
                        mutableListOf("001", "002")
                    )
                    val attr = BaseAttachment(Constants.IMMessageType.MSG_ROOM_PD, pdModel)
                    val message = ChatRoomMessageBuilder.createChatRoomCustomMessage(roomNeteaseId, attr)
                    NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
                    FlowBus.post(Event.MsgPDEvent(pdModel))
                    dismiss()
                }
            }
        }
    }
}