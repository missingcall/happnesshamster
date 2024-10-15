package com.kissspace.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.kissspace.common.model.RoomListBannerBean
import com.kissspace.module_common.R
import com.kissspace.util.loadImage


class BannerAdapter<T> constructor(mData: List<T>?, private val isCover:Boolean = false) :
    com.youth.banner.adapter.BannerAdapter<T, BannerAdapter.BannerHolder>(mData) {

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerHolder {

        return BannerHolder(LayoutInflater.from(parent?.context).inflate(R.layout.common_layout_banner_item, parent,false))
    }

    override fun onBindView(holder: BannerHolder, data: T, position: Int, size: Int) {
        holder.cover.visibility = if (isCover) View.VISIBLE else View.GONE
        when (data) {
            is String -> {
                holder.imageView.loadImage(data)
            }

            is RoomListBannerBean -> {
                holder.imageView.loadImage(data.url)
            }

            else -> {
                holder.imageView.loadImage(data as Int)
            }
        }
    }

    class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var cover: View
        init {
            imageView = itemView.findViewById<ImageView>(R.id.iv_image)
            cover = itemView.findViewById<ImageView>(R.id.view_cover)
        }
    }
}