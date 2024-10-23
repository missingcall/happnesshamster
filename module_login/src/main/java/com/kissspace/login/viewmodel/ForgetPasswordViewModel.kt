package com.kissspace.login.viewmodel

import android.text.Editable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.DeviceUtils
import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.model.LoginResultBean
import com.kissspace.common.util.DJSLiveData
import com.kissspace.common.util.customToast
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.login.http.LoginApi
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.network.result.ResultState
import com.kissspace.util.isNotEmptyBlank

class ForgetPasswordViewModel : BaseViewModel() {
    val phoneNumber = ObservableField<String>()

    val verificationCode = ObservableField<String>()

    val password = ObservableField<String>()

    val confirmPwd = ObservableField<String>()

    val sendSmsEnable = ObservableField(false)

    val btnEnable = ObservableField(false)


    fun onPswTextChange(editable: Editable) {
        btnEnable.set(
            password.get().isNotEmptyBlank() && confirmPwd.get()
                .isNotEmptyBlank()
        )
    }

    fun onVerifyTextChange(editable: Editable) {
        btnEnable.set(
            phoneNumber.get().toString().isNotEmptyBlank() && verificationCode.get()
                .isNotEmptyBlank()
        )
    }

    fun onPhoneTextChange(editable: Editable) {
        sendSmsEnable.set(
            phoneNumber.get().toString().isNotEmptyBlank()
        )
        btnEnable.set(
            phoneNumber.get().toString().isNotEmptyBlank()
        )
    }

    fun phoneCodeLogin(phoneNumber: String, verifyCode: String, onSuccess: (LoginResultBean) -> Unit) {
        val map = mutableMapOf<String, Any?>()
        map["mobile"] = phoneNumber
        map["smsCode"] = verifyCode
        map["accessFlags"] = "0"
        map["deviceId"] = MMKVProvider.sm_deviceId
        map["machineCode"] = DeviceUtils.getUniqueDeviceId()
        request<LoginResultBean>(LoginApi.API_SMS_CODE_LOGIN, Method.POST, map, onSuccess = {
            onSuccess.invoke(it)
        })
    }

}