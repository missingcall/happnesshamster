package com.kissspace.message.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.kissspace.common.router.jump
import com.kissspace.common.model.LoveWallListBean
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.glide.loadwithGlide
import com.kissspace.util.ellipsizeString
import com.kissspace.util.resToColor
import com.kissspace.module_message.R
import com.kissspace.util.loadImage
import com.kissspace.util.loadImageCircle
import com.youth.banner.adapter.BannerAdapter

class MessageBannerAdapter(data: List<LoveWallListBean>) :
    BannerAdapter<LoveWallListBean, MessageBannerAdapter.BannerViewHolder>(data) {

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mRootView: ConstraintLayout
        var mAvatarLeft: ImageView
        var mAvatarRight: ImageView
        var mInfo: TextView
        var mGiftImage: ImageView
        var mtvEnterWall:TextView
       // var mSourceName: TextView
       // var mTagName: TextView

        init {
            mRootView = itemView.findViewById(R.id.root)
            mAvatarLeft = itemView.findViewById(R.id.iv_avatar_left)
            mAvatarRight = itemView.findViewById(R.id.iv_avatar_right)
            mInfo = itemView.findViewById(R.id.tv_gift_info)
            mGiftImage = itemView.findViewById(R.id.iv_gift)
            mtvEnterWall = itemView.findViewById(R.id.tv_enter_wall)
          //  mSourceName = itemView.findViewById(R.id.iv_name_left)
           // mTagName = itemView.findViewById(R.id.iv_name_right)

        }
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.message_layout_love_wall_item_banner, parent, false)
        return BannerViewHolder(itemView)
    }

    override fun onBindView(
        holder: BannerViewHolder,
        data: LoveWallListBean,
        position: Int,
        size: Int
    ) {


        val isTextWhite = position % 3 == 0

        val resource = when (position % 3) {
                0 -> R.mipmap.message_bg_love_wall_blue
                1 -> R.mipmap.message_bg_love_wall_second
                else -> R.mipmap.message_bg_love_wall_third
//            0 -> R.mipmap.message_bg_love_wall_first
//            else -> R.mipmap.message_bg_love_wall_blue
        }
        holder.mRootView.setBackgroundResource(resource)
        holder.mAvatarLeft.loadwithGlide(data.sourceUserProfilePath,isCircle = true)
        holder.mAvatarRight.loadwithGlide(data.targetUserProfilePath,isCircle = true)
        holder.mGiftImage.loadwithGlide(data.url)
  //      holder.mSourceName.text = data.sourceUserNickname
   //     holder.mTagName.text = data.targetUserNickname
        holder.mInfo.text = buildSpannedString {

            color(if (isTextWhite)R.color.white.resToColor() else  R.color.black.resToColor()) {
                bold {
                    append(data.sourceUserNickname.ellipsizeString(4))
                }
            }
            color(if (isTextWhite)R.color.white.resToColor() else  R.color.black.resToColor()) {
                append(" 对 ")
            }
            color(if (isTextWhite)R.color.white.resToColor() else  R.color.black.resToColor()) {
                bold {
                    append(data.targetUserNickname.ellipsizeString(4))
                }
            }

            color(if (isTextWhite)R.color.white.resToColor() else  R.color.black.resToColor()) {
                append("壕刷")
            }

            color(R.color.color_FA3127.resToColor())
            {
                bold {
                    append(data.giftName.ellipsizeString(4) + "x" + data.number)
                }
            }
        }

        if (isTextWhite) {
            holder.mtvEnterWall.setTextColor(R.color.white.resToColor())
        }else{
            holder.mtvEnterWall.setTextColor(R.color.black.resToColor())
        }

        holder.mRootView.setOnClickListener {
            jump(RouterPath.PATH_LOVE_WALL)
        }
    }
}


