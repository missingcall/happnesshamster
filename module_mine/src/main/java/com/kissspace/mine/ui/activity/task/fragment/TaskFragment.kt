package com.kissspace.mine.ui.activity.task.fragment

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.base.BaseFragment
import com.kissspace.common.base.BaseLazyFragment
import com.kissspace.common.binding.dataBinding
import com.kissspace.common.config.Constants.TASK_FINISH
import com.kissspace.common.config.Constants.TASK_NOT_FINISH
import com.kissspace.common.config.Constants.TASK_WAIT_TO_REWARD
import com.kissspace.common.http.getReceiveTaskReward
import com.kissspace.common.model.task.DailyTaskInfo
import com.kissspace.common.model.task.HamsterTaskInfo
import com.kissspace.common.model.task.NoviceTaskInfo
import com.kissspace.common.util.*
import com.kissspace.common.util.glide.loadwithGlide
import com.kissspace.common.widget.Footer
import com.kissspace.mine.viewmodel.TaskCenterViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFragmentTaskDayBinding
import com.kissspace.module_mine.databinding.MineItemTaskDayCenterBinding
import com.kissspace.module_mine.databinding.MineItemTaskHamsterBinding
import com.kissspace.module_mine.databinding.MineItemTaskNewpeopleCenterBinding
import com.kissspace.util.loadImage
import com.kissspace.util.logE
import com.ruffian.library.widget.RTextView

/**
 * @Author gaohangbo
 * @Date 2023/1/9 13:06.
 * @Describe
 */
class TaskFragment : BaseLazyFragment(R.layout.mine_fragment_task_day) {

    private val mBinding by dataBinding<MineFragmentTaskDayBinding>()
    private val mViewModel by viewModels<TaskCenterViewModel>()
    private var taskType: String? = null

    companion object {
        fun newInstance(tag: String) = TaskFragment().apply {
            arguments = bundleOf("taskType" to tag)
        }
    }

    override fun lazyInitView() {
        taskType = arguments?.getString("taskType")
        mBinding.pageRefreshLayout.apply {
            onRefresh {
                initData()
                mBinding.pageRefreshLayout.finishRefresh()
            }
        }
        initData()
    }

    override fun lazyLoadData() {
    }

    @Deprecated("懒加载不重写该方法")
    override fun initView(savedInstanceState: Bundle?) {
    }

    private fun initData() {
        if (taskType=="DAY"||taskType=="NEW_PEOPLE"){
            mViewModel.requestTaskList(onSuccess = {
                if (taskType.equals("DAY")) {
                    initDayTask(it.dailyTaskInfoList)
                } else if (taskType.equals("NEW_PEOPLE")) {
                    initNewPeopleTask(it.noviceTaskInfoList)
                } else {
                    // 其他任务
                    //  initHamsterTask(it.dailyTaskInfoList)
                }
            })
        }else{//仓鼠任务
            mViewModel.requestHamsterTaskList(onSuccess = {
                initHamsterTaskList(it)
            })
        }


    }

    /**
     *  初始化新人任务列表
     *
     */
    private fun initNewPeopleTask(noviceTaskInfoList: List<NoviceTaskInfo>) {
        mBinding.rvTask.linear().setup {
            addType<NoviceTaskInfo>(R.layout.mine_item_task_newpeople_center)
            addType<Footer>(R.layout.mine_task_bottom_view)
            onBind {
                when (val viewBinding = getBinding<ViewDataBinding>()) {
                    is MineItemTaskNewpeopleCenterBinding -> {
                        val model = getModel<NoviceTaskInfo>()
                        //	任务状态 001 未完成 002 待领取奖励 003 已完成
                        when (model.finishStatus) {
                            TASK_NOT_FINISH -> {
                                viewBinding.tvButton.isEnabled = true
                                viewBinding.tvButton.text = "前往"

                            }

                            TASK_WAIT_TO_REWARD -> {
                                viewBinding.tvButton.isEnabled = true
                                viewBinding.tvButton.text = "领取奖励"

                            }

                            TASK_FINISH -> {
                                viewBinding.tvButton.isEnabled = false
                                viewBinding.tvButton.text = "已完成"
                            }
                        }
                        setBtnBg(viewBinding.tvButton, model.finishStatus)
                        model.rewardType?.let {
                            val pair = getFirstGiftTypeImgText(it)
                            var i = 0
                            for (item in pair) {
                                if (i == 0) {
                                    setGift(
                                        viewBinding.ivGift, viewBinding.tvTaskReward, item.first,
                                        item.second as String
                                    )
                                } else if (i == 1) {
                                    setGift(
                                        viewBinding.ivGift1, viewBinding.tvTaskReward1, item.first,
                                        item.second as String
                                    )
                                }
                                i += 1
                            }
                        }
//                        viewBinding.tvTaskProgress.text = "0/1"
//                        if(modelPosition==1){
//                            viewBinding.tvTaskProgress.visibility= View.GONE
//                        }else{
//                            viewBinding.tvTaskProgress.visibility= View.VISIBLE
//                        }
                    }
                }
            }
            onFastClick(R.id.tv_button) {
                val model = getModel<NoviceTaskInfo>()
                if (model.finishStatus == TASK_WAIT_TO_REWARD) {
                    getReceiveTaskReward(model.taskId) {
                        taskType = arguments?.getString("taskType")
                        mViewModel.requestTaskList(onSuccess = {
                            val tvButton = findView<RTextView>(R.id.tv_button)
                            tvButton.isEnabled = false
                            tvButton.text = "已完成"
                            setBtnBg(tvButton, TASK_FINISH)
                            //tvButton.setBackgroundResource(R.mipmap.mine_task_bg_completed)
                        })
                    }
                } else if (model.finishStatus == TASK_NOT_FINISH) {
                    logE("model.schema" + model.schema)
                    handleSchema(model.schema)
                }

            }
        }.models = noviceTaskInfoList
        mBinding.rvTask.bindingAdapter.addFooter(Footer(), animation = true)
    }

    /**
     * 初始化每日任务
     */
    private fun initDayTask(dailyTaskInfoList: List<DailyTaskInfo>) {
        mBinding.rvTask.linear().setup {
            addType<DailyTaskInfo>(R.layout.mine_item_task_day_center)
            addType<Footer>(R.layout.mine_task_bottom_view)
            onBind {
                when (val viewBinding = getBinding<ViewDataBinding>()) {
                    is MineItemTaskDayCenterBinding -> {
                        val model = getModel<DailyTaskInfo>()
                        //	任务状态 001 未完成 002 待领取奖励 003 已完成
                        when (model.finishStatus) {
                            TASK_NOT_FINISH -> {
                                viewBinding.tvButton.isEnabled = true
                                viewBinding.tvButton.text = "前往"

                            }

                            TASK_WAIT_TO_REWARD -> {
                                viewBinding.tvButton.isEnabled = true
                                viewBinding.tvButton.text = "领取奖励"

                            }

                            TASK_FINISH -> {
                                viewBinding.tvButton.isEnabled = false
                                viewBinding.tvButton.text = "已完成"

                            }
                        }
                        setBtnBg(viewBinding.tvButton, model.finishStatus)
//                        viewBinding.tvTaskProgress.text = "0/1"
//                        if(modelPosition==1){
//                            viewBinding.tvTaskProgress.visibility= View.GONE
//                        }else{
//                            viewBinding.tvTaskProgress.visibility= View.VISIBLE
//                        }
                        model.rewardType?.let {
                            val pair = getFirstGiftTypeImgText(it)
                            logE("pair$pair")
                            var i = 0
                            for (item in pair) {
                                if (i == 0) {
                                    setGift(
                                        viewBinding.ivGift, viewBinding.tvTaskReward, item.first,
                                        item.second as String
                                    )
                                } else if (i == 1) {
                                    setGift(
                                        viewBinding.ivGift1, viewBinding.tvTaskReward1, item.first,
                                        item.second as String
                                    )
                                }
                                i += 1
                            }
                        }
                    }
                }
            }
            onFastClick(R.id.tv_button) {
                val model = getModel<DailyTaskInfo>()
                if (model.finishStatus == TASK_WAIT_TO_REWARD) {
                    getReceiveTaskReward(model.taskId) {
                        val tvButton = findView<RTextView>(R.id.tv_button)
                        tvButton.isEnabled = false
                        tvButton.text = "已完成"
                        setBtnBg(tvButton, TASK_FINISH)
                        //tvButton.setBackgroundResource(R.mipmap.mine_task_bg_completed)
                    }
                } else if (model.finishStatus == TASK_NOT_FINISH) {
                    logE("model.schema" + model.schema)
                    handleSchema(model.schema)
                }

            }
        }.models = dailyTaskInfoList
        mBinding.rvTask.bindingAdapter.addFooter(Footer(), animation = true)
    }

    /**
     * 初始化仓鼠任务列表
     */

    private fun initHamsterTaskList(list:List<HamsterTaskInfo>) {
        mBinding.rvTask.linear().setup {
            addType<HamsterTaskInfo>(R.layout.mine_item_task_hamster)
            addType<Footer>(R.layout.mine_task_bottom_view)
            onBind {

                when (val viewBinding = getBinding<ViewDataBinding>()) {
                    is MineItemTaskHamsterBinding -> {
                        val binding = getBinding<MineItemTaskHamsterBinding>()
                        val model = getModel<HamsterTaskInfo>()
                        //binding.ivTask.loadwithGlide(model.icon)
                        binding.tvTask.text = model.remark
                        binding.tvTaskProgress.text="(${model.currentValue}/${model.targetValue})"

                        //是否可领取 001：待解锁 002：进行中 003：待领取 004：已领取 参考 TaskStatusForIntegralEnum
                        when(model.status){
                            "001","002"->{
                                binding.tvButton.isEnabled = true
                                binding.tvButton.text="前往"

                                binding.tvButton.helper.apply {
                                    setTextColorNormal(
                                        ContextCompat.getColor(
                                            requireActivity(),
                                            com.kissspace.module_common.R.color.white
                                        )
                                    )
                                    setBackgroundColorNormalArray(
                                        intArrayOf(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                com.kissspace.module_common.R.color.color_36398A
                                            ),
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                com.kissspace.module_common.R.color.color_36398A
                                            )
                                        )
                                    )
                                }
                            }
                            "003"->{
                                binding.tvButton.isEnabled = true
                                binding.tvButton.text="领取奖励"
                                binding.tvButton.helper.apply {
                                    setTextColorNormal(
                                        ContextCompat.getColor(
                                            requireActivity(),
                                            com.kissspace.module_common.R.color.white
                                        )
                                    )
                                    setBackgroundColorNormalArray(
                                        intArrayOf(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                com.kissspace.module_common.R.color.color_936DDE
                                            ),
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                com.kissspace.module_common.R.color.color_6C74FB
                                            )
                                        )
                                    )
                                }
                            }
                            "004"->{
                                binding.tvButton.isEnabled = false
                                binding.tvButton.text="已完成"
                                binding.tvButton.helper.apply {
                                    setTextColorNormal(
                                        ContextCompat.getColor(
                                            requireActivity(),
                                            com.kissspace.module_common.R.color.color_949499
                                        )
                                    )
                                    setBackgroundColorNormalArray(
                                        intArrayOf(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                com.kissspace.module_common.R.color.color_2C2C2C
                                            ),
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                com.kissspace.module_common.R.color.color_2C2C2C
                                            )
                                        )
                                    )


                                }
                            }

                        }

                    }}





            }
        }.mutable = list.toMutableList()
        mBinding.rvTask.bindingAdapter.addFooter(Footer(), animation = true)
    }


    private fun setGift(imageView: ImageView, textView: TextView, image: Any?, text: String) {
        if (image is String) {
            imageView.loadImage(image)
        } else if (image is Int) {
            imageView.setImageResource(image)
        }
        textView.text = text
    }

    private fun setBtnBg(view: RTextView, status: String) {
        //做任务
        when (status) {
            TASK_NOT_FINISH -> {

                view.helper.apply {
                    setTextColorNormal(
                        ContextCompat.getColor(
                            requireActivity(),
                            com.kissspace.module_common.R.color.white
                        )
                    )
                    setBackgroundColorNormalArray(
                        intArrayOf(
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_36398A
                            ),
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_36398A
                            )
                        )
                    )
                }
            }

            //领取奖励
            TASK_WAIT_TO_REWARD -> {
                view.helper.apply {
                    setTextColorNormal(
                        ContextCompat.getColor(
                            requireActivity(),
                            com.kissspace.module_common.R.color.white
                        )
                    )
                    setBackgroundColorNormalArray(
                        intArrayOf(
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_936DDE
                            ),
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_6C74FB
                            )
                        )
                    )
                }
            }
            //已完成
            TASK_FINISH -> {
                view.helper.apply {
                    setTextColorNormal(
                        ContextCompat.getColor(
                            requireActivity(),
                            com.kissspace.module_common.R.color.color_949499
                        )
                    )
                    setBackgroundColorNormalArray(
                        intArrayOf(
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_2C2C2C
                            ),
                            ContextCompat.getColor(
                                requireActivity(),
                                com.kissspace.module_common.R.color.color_2C2C2C
                            )
                        )
                    )
                }
            }
        }

    }

}