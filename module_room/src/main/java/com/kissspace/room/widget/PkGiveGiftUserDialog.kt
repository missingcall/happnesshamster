package com.kissspace.room.widget

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.angcyo.tablayout.delegate2.ViewPager2Delegate
import com.kissspace.common.widget.BaseDialogFragment
import com.kissspace.module_room.R
import com.kissspace.module_room.databinding.RoomDialogUserPkBinding


class PkGiveGiftUserDialog : BaseDialogFragment<RoomDialogUserPkBinding>(RoomDialogUserPkBinding::inflate,Gravity.BOTTOM) {

    companion object {
        fun newInstance(roomId:String) = PkGiveGiftUserDialog().apply {
            arguments = bundleOf("roomId" to roomId)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.room_dialog_user_pk
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBinding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@PkGiveGiftUserDialog) {
                override fun getItemCount(): Int = 2
                override fun createFragment(position: Int): Fragment = RoomUserInPKFragment.newInstance(arguments?.getString("roomId"),position)
                override fun getItemId(position: Int): Long = position.toLong()
                override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                    super.onAttachedToRecyclerView(recyclerView)
                    //不懒加载
                    recyclerView.setItemViewCacheSize(0)
                }
            }
        }
        ViewPager2Delegate.install(mBinding.viewPager, mBinding.tabLayout)

    }

}