package com.kissspace.common.widget

import android.util.Log
import android.view.Gravity
import com.kissspace.module_common.R
import com.kissspace.module_common.databinding.CommonDialogLoadingMp4Binding
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

class AnimationLoadDialog() : BaseDialogFragment<CommonDialogLoadingMp4Binding>(CommonDialogLoadingMp4Binding::inflate,Gravity.CENTER) {

    override fun getLayoutId() = R.layout.common_dialog_loading_mp4

    override fun initView() {
        mBinding.pivLoading.setRepeatCount(Int.MAX_VALUE)
        var localFile = File(context?.filesDir, "loading.pag")
        if(localFile.exists()){
            mBinding.pivLoading.play(localFile.absolutePath)
        }else{
            val inputStream = context?.assets?.open("loading.pag")
            val outputFile = File(context?.filesDir, "loading.pag")
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
            localFile = File(context?.filesDir, "loading.pag")
            if(localFile.exists()){
                mBinding.pivLoading.play(localFile.absolutePath)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.pivLoading.clear()
    }
}

