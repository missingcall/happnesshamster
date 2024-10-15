package com.kissspace.mine.adapter

import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemAttached
import com.kissspace.common.widget.AnimationView
import com.kissspace.module_mine.R

class AnimationAdapter : BindingAdapter() , ItemAttached {

    override fun onViewDetachedFromWindow(holder: BindingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val  animationView = holder.findView<AnimationView>(R.id.animation_view)
        if(animationView != null) animationView.onDestroy()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }


}