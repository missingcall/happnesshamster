package com.kissspace.room.widget

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.graphics.Insets
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kissspace.common.ext.safeClick
import com.kissspace.common.model.RoomOnLineUserListBean
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomLayoutDialogInputTextBinding
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.collectData
import com.kissspace.room.http.RoomApi
import com.kissspace.common.model.AitBean
import com.kissspace.room.util.ait.AitManager
import com.kissspace.room.util.ait.AitTextChangeListener
import com.kissspace.room.viewmodel.LiveViewModel
import com.kissspace.util.addAfterTextChanged
import com.kissspace.util.postRunnable
import kotlin.math.abs

class InputTextDialogFragment : BottomSheetDialogFragment() {

    var aitBean:AitBean? = null

    private lateinit var binding: RoomLayoutDialogInputTextBinding

    private val mViewModel: LiveViewModel by lazy { ViewModelProvider(requireActivity())[LiveViewModel::class.java] }

    private var aitManager:AitManager?=null


    private fun getAitManager(): AitManager {
        if(aitManager == null){
           aitManager =  AitManager(requireContext(),"tid",false).apply {
               this.setTextChangeListener(object : AitTextChangeListener{
                   override fun onAitPageOpen() {
                       if(aitBean ==null) {
                           binding.rvAitUser.visibility = View.VISIBLE
                           mViewModel.roomInfoBean.value?.let {
                               getOnLineUsers(it.crId)
                           }
                       }
                       aitBean = null
                   }

                   override fun onAitPageClose() {
                       binding.rvAitUser.visibility = View.GONE
                   }

                   override fun onTextAdd(content: String?, start: Int, length: Int) {
                       binding.etInput.text.let {
                           if(start-1>=0&&start-1<it.length) {
                               val aitChar = it.substring(start - 1, start)
                               if (aitChar == "@") {
                                   it.replace(start - 1, start, "")
                               }
                               it.insert(start-1, buildSpannedString{
                                   color(Color.parseColor("#FFFF8A00")) {
                                       append("@$content")
                                   }
                               })
                           }
                       }
//                    if(binding.etInput.text.substring(start-1, start) == "@"){
//                        binding.etInput.text.replace(start-1,  start, "")
//                    }
//                    binding.etInput.text.insert(start, buildSpannedString{
//                        color(Color.parseColor("#FFFF8A00")) {
//                            append("@$content")
//                        }
//                    })
                   }

                   override fun onTextDelete(start: Int, length: Int) {
                       binding.etInput.text.replace(start,  start +length - 1, "")
                   }
               })
           }
        }
        return aitManager!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = RoomLayoutDialogInputTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var confirmCallback: Function1<String, Unit>? = null
    private var inputText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.kissspace.module_common.R.style.Theme_CustomBottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBottomWindow()
        initCallback()
        initKeySet()
        initView()
    }

    private fun initView() {
        inputText?.let {
            binding.etInput.setText(it)
        }
        binding.rvAitUser.linear(LinearLayout.HORIZONTAL).setup {
            addType<RoomOnLineUserListBean> { R.layout.room_dialog_online_user_item }
            onFastClick(R.id.root) {
                val  model = getModel<RoomOnLineUserListBean>()
                getAitManager().insertAitMember(model.userId,model.nickname)
                binding.rvAitUser.visibility = View.GONE
            }
        }.models = mutableListOf()
        collectData(mViewModel.getOnLineUsersEvent, onSuccess = {
            binding.rvAitUser.models = it
        })

    }

    fun getOnLineUsers(crId: String) {
        val param = mutableMapOf<String, Any?>("chatRoomId" to crId)
        request<List<RoomOnLineUserListBean>>(url = RoomApi.API_GET_ROOM_ONLINE_USER, method = Method.GET,param=param, onSuccess = {
            binding.rvAitUser.models = it.filter { bean -> bean.userId != MMKVProvider.userId }
        })
    }

    private fun initKeySet(){
        binding.root.postRunnable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setWindowInsetsAnimationCallback(
                binding.root,
                object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

                    private var startHeight = 0
                    private var lastDiffH = 0

                    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                        if (startHeight == 0) {
                            startHeight = binding.root.height
                        }
                    }

                    override fun onProgress(
                        insets: WindowInsetsCompat,
                        runningAnimations: MutableList<WindowInsetsAnimationCompat>,
                    ): WindowInsetsCompat {

                        val typesInset = insets.getInsets(WindowInsetsCompat.Type.ime())
                        val otherInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                        val diff = Insets.subtract(typesInset, otherInset).let {
                            Insets.max(it, Insets.NONE)
                        }

                        val diffH = abs(diff.top - diff.bottom)

                        binding.etInput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            bottomMargin = diffH
                        }

                        if (diffH < lastDiffH) {
                            ViewCompat.setWindowInsetsAnimationCallback(binding.root, null)
                            dismissAllowingStateLoss()
                        }

                        lastDiffH = diffH

                        return insets
                    }
                }
            )
        }
        else {
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                var lastBottom = 0
                override fun onGlobalLayout() {
                    ViewCompat.getRootWindowInsets(binding.root)?.let { insets ->
                        val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                        if (lastBottom != 0 && bottom == 0) {
                            // 收起键盘了，可以 dismiss 了
                            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            dismiss()
                        }
                        lastBottom = bottom
                    }
                }
            })
          }
        }
    }


    public fun setConfirmCallback(block: (String) -> Unit) {
        confirmCallback = block
    }

    private fun setInputText(text: String) {
        if (text.isNotBlank()) {
            inputText = text
        }
    }

    private fun initCallback() {

        binding.apply {

            tvConfirm.background.alpha = 125
            tvConfirm.isEnabled = false

            etInput.setOnKeyListener(View.OnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                    tvConfirm.performClick()
                    return@OnKeyListener true
                }
                false
            })


            etInput.addAfterTextChanged {
                if (etInput.text.toString().isEmpty()) {
                    tvConfirm.background.alpha = 125 //(R.mipmap.room_icon_send_chat_normal)
                    tvConfirm.isEnabled = false
                    //tvConfirm.setBackgroundResource(R.mipmap.room_icon_send_chat_normal)
                } else {
                    tvConfirm.background.alpha = 255
                    tvConfirm.isEnabled = true
                    //tvConfirm.setBackgroundResource(R.mipmap.room_icon_send_chat_selected)
                }
            }
            tvConfirm.safeClick {
                val content = etInput.text.toString().trim()
                if (content.isEmpty()) {
                    customToast("请输入聊天内容")
                } else {
                    confirmCallback?.invoke(etInput.text.toString())
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.etInput.addTextChangedListener(getAitManager())
        aitBean?.let {
            binding.etInput.setText("@")
            binding.etInput.setSelection(1)
            getAitManager().reset(1)
            getAitManager().insertAitMember(it.userId,it.nickname)
        }
        binding.etInput.requestFocus()
    }


    override fun onPause() {
        super.onPause()
        binding.etInput.removeTextChangedListener(getAitManager())
    }

    fun getAitList():List<AitBean>{
       return getAitManager().aitTeamMember
    }

    fun clean(){
        getAitManager().reset()
        binding.etInput.text = null
        dismiss()
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        try {
            //由于父类方法中mDismissed，mShownByMe不可直接访问，所以此处采用反射修改他们的值
            val dialogFragmentClass: Class<*> = DialogFragment::class.java
            val mDismissed = dialogFragmentClass.getDeclaredField("mDismissed")
            mDismissed.isAccessible = true
            mDismissed[this] = false
            val mShownByMe = dialogFragmentClass.getDeclaredField("mShownByMe")
            mShownByMe.isAccessible = true
            mShownByMe[this] = true
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitNowAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var bottomSheetBehavior: BottomSheetBehavior<out View>? = null

    private fun initBottomWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(null)
            setSoftInputMode(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                } else {
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                }
            )
            attributes?.apply {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            bottomSheetBehavior = this
            state = BottomSheetBehavior.STATE_EXPANDED
            addBottomSheetCallback(bottomSheetCallback)
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                //判断为向下拖动行为时，则强制设定状态为展开
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}