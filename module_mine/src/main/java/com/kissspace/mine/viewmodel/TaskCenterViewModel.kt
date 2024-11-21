package com.kissspace.mine.viewmodel

import androidx.databinding.ObservableField
import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.model.UserProfileBean
import com.kissspace.common.model.task.HamsterTaskInfo
import com.kissspace.common.model.task.TaskCenterListModel
import com.kissspace.common.util.customToast
import com.kissspace.mine.http.MineApi
import com.kissspace.network.net.Method
import com.kissspace.network.net.request

/**
 * @Author gaohangbo
 * @Date 2023/1/9 19:44.
 * @Describe
 */
class TaskCenterViewModel : BaseViewModel() {
    val userInfo = ObservableField<UserProfileBean>()

    fun requestTaskList(onSuccess: ((TaskCenterListModel) -> Unit)?,onError: (() -> Unit)?=null) {
        request<TaskCenterListModel>(MineApi.API_TASK_CENTER, Method.GET, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            onError?.invoke()
        })
    }



    fun requestHamsterTaskList(onSuccess: ((List<HamsterTaskInfo>) -> Unit)?, onError: (() -> Unit)?=null) {
        request<List<HamsterTaskInfo>>(MineApi.API_TASK_HAMSTER, Method.GET, param = mutableMapOf("os" to "android"), onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            onError?.invoke()
        })
    }

    /**
     * 领取仓鼠任务奖励
     */
    fun receiveRewardHamsterTask(type:String,success:()->Unit){
        request<Boolean>(MineApi.API_TASK_HAMSTER_REWARD, Method.GET,param = mutableMapOf("type" to type), onSuccess = {
            customToast("领取成功")
            success.invoke()
        }, onError = {
           // onError?.invoke()
         customToast(it.errorMsg)
        })
    }

}