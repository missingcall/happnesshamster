package com.kissspace.common.widget

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blankj.utilcode.util.SizeUtils
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonDialogGameAnimationBinding
import com.kissspace.util.loadImage
import de.hdodenhof.circleimageview.CircleImageView
import org.libpag.PAGImageView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/3/8 19:46
 * @Description: 房间加载弹窗
 *
 */

class GameAnimationDialog(
    private var memberList: List<String> = mutableListOf()
) : BaseDialogFragment<CommonDialogGameAnimationBinding>(CommonDialogGameAnimationBinding::inflate,Gravity.CENTER) {

    override fun getLayoutId() = R.layout.common_dialog_game_animation

    override fun initView() {
        initMembers(mBinding.layoutMembers)
        playGameAnimation()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val attr = attributes
            attr.dimAmount = 0f
            attributes = attr
        }
    }


    private fun initMembers(layout: LinearLayout){
        layout.removeAllViews()
        if(memberList.isNotEmpty()) {
            val margin = SizeUtils.dp2px(20f)
            val size = SizeUtils.dp2px(48f)
            memberList.forEachIndexed { index, model ->
                val imageView = CircleImageView(requireContext())
                val params = LinearLayout.LayoutParams(size, size)
                if (index > 0) {
                    params.setMargins(margin, 0, 0, 0)
                }
                imageView.layoutParams = params
                imageView.loadImage(model)
                layout.addView(imageView)
            }
        }else{
            layout.visibility = View.GONE
        }
    }

    private fun playGameAnimation(){
        mBinding.avGame.setLoopCount(1)
        mBinding.avGame.addListener(object : PAGImageView.PAGImageViewListener{
            override fun onAnimationStart(p0: PAGImageView?) {

            }

            override fun onAnimationEnd(p0: PAGImageView?) {
                dismiss()
            }

            override fun onAnimationCancel(p0: PAGImageView?) {

            }

            override fun onAnimationRepeat(p0: PAGImageView?) {

            }

            override fun onAnimationUpdate(p0: PAGImageView?) {

            }

        })
        var localFile = File(context?.filesDir, "game_guangxiao.pag")
        if(localFile.exists()){
            mBinding.avGame.play(localFile.absolutePath)
        }else{
            val inputStream = context?.assets?.open("game_guangxiao.pag")
            val outputFile = File(context?.filesDir, "game_guangxiao.pag")
            outputFile.createNewFile()
            val outputStream: OutputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var length: Int
            while ((inputStream?.read(buffer).also { length = it!! })!! > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream?.close()
            localFile = File(context?.filesDir, "game_guangxiao.pag")
            if(localFile.exists()){
                mBinding.avGame.play(localFile.absolutePath)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.avGame.clear()
    }
}


