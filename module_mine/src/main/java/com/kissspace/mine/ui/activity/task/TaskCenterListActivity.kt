package com.kissspace.mine.ui.activity.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.kissspace.common.router.jump
import com.kissspace.common.ext.setTitleBarListener
import com.kissspace.common.router.RouterPath
import com.kissspace.mine.ui.activity.task.fragment.TaskFragment
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityTaskCenterBinding

/**
 * @Author gaohangbo
 * @Date 2023/1/9 09:54.
 * @Describe 任务中心
 */
@Router(path = RouterPath.PATH_TASK_CENTER_LIST)
class TaskCenterListActivity : com.kissspace.common.base.BaseActivity(R.layout.mine_activity_task_center) {
    private val mBinding by viewBinding<MineActivityTaskCenterBinding>()
    override fun initView(savedInstanceState: Bundle?) {
        setTitleBarListener(mBinding.titleBar)
        mBinding.titleBar.rightView.setOnClickListener {
           jump(RouterPath.PATH_MY_DRESS_UP)
        }


        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@TaskCenterListActivity) {
                override fun getItemCount(): Int = 3
                override fun createFragment(position: Int): Fragment =
                    if (position == 0) {
                        TaskFragment.newInstance("NEW_PEOPLE")
                    } else if (position == 1) {
                        TaskFragment.newInstance("DAY")
                    }else{
                        TaskFragment.newInstance("Hamster")
                    }
                override fun getItemId(position: Int): Long = position.toLong()
            }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
        mBinding.viewPager.offscreenPageLimit=2
    }

}