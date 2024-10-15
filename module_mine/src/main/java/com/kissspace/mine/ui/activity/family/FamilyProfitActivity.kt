package com.kissspace.mine.ui.activity.family

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.didi.drouter.annotation.Router
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.Constants
import com.kissspace.common.router.RouterPath
import com.kissspace.mine.ui.fragment.FamilyRoomEarnsFragment
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineFamilyProfitBinding

/**
 * @Author gaohangbo
 * @Date 2022/12/29 08:38.
 * @Describe
 */
@Router(path = RouterPath.PATH_FAMILY_PROFIT)
class FamilyProfitActivity : BaseActivity(R.layout.mine_family_profit) {
        val mBinding by viewBinding<MineFamilyProfitBinding>()
    private var familyId:String?=null
    private var isToday:Boolean = true
    override fun initView(savedInstanceState: Bundle?) {
        familyId= intent.getStringExtra("familyId")
        isToday = intent.getBooleanExtra("isToday",true)
        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                finish()
            }
        })
        addTab()
        initViewPager()
    }

    private fun addTab() {

    }

    private fun initViewPager(){
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@FamilyProfitActivity) {
                override fun getItemCount(): Int = 3
                override fun createFragment(position: Int): Fragment = when (position) {
                    0 -> { FamilyRoomEarnsFragment.newInstance(Constants.FamilyEarnsType.FamilyInToady.type,familyId) }
                    1 -> {FamilyRoomEarnsFragment.newInstance(Constants.FamilyEarnsType.FamilyInSevenToady.type,familyId)}
                    else -> { FamilyRoomEarnsFragment.newInstance(Constants.FamilyEarnsType.FamilyInLastWeek.type,familyId) }
                }
            }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)
        if(!isToday){
            mBinding.viewPager.currentItem =1
        }
    }
}