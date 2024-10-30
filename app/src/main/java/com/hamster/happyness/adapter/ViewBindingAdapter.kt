 package com.hamster.happyness.adapter

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.model.RoomListBannerBean
import com.kissspace.common.model.RoomListBean
import com.kissspace.common.model.wallet.CultivationPanelModel
import com.kissspace.common.model.wallet.RevivePanelModel
import com.kissspace.common.util.format.Format
import com.kissspace.common.util.handleSchema
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_room.R
import com.kissspace.util.loadImage
import com.kissspace.util.loadImageCircle
import com.kissspace.util.logD

 object ViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("isShowBeautyId")
    fun isShowBeautyId(imageView: ImageView, isShow: Boolean) {
        imageView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("isShowLock")
    fun isShowLock(imageView: ImageView, isShow: Boolean) {
        imageView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("onLineCount")
    fun onLineCount(textView: TextView, count: Int) {
        textView.text = count.toString()
        textView.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("roomListHostVisible", requireAll = false)
    fun roomListHostVisible(root: LinearLayout, room: RoomListBean) {
        root.visibility =
            if (room.wheatPositionList.isEmpty() || room.wheatPositionList[0].wheatPositionId.isEmpty()) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("roomListHostImage", requireAll = false)
    fun roomListHostImage(imageView: ImageView, room: RoomListBean) {
        if (room.wheatPositionList.isNotEmpty() && room.wheatPositionList[0].wheatPositionIdHeadPortrait.isNotEmpty()) {
            imageView.loadImageCircle(room.wheatPositionList[0].wheatPositionIdHeadPortrait)
        }
    }

    @JvmStatic
    @BindingAdapter("roomListBanner", requireAll = false)
    fun roomListBanner(
        banner: RecyclerView,
        data: MutableList<RoomListBannerBean>
    ) {
        banner.linear(orientation = HORIZONTAL).setup {
            addType<RoomListBannerBean> { com.hamster.happyness.R.layout.app_item_party_banner_list }
            onBind {
                val model = getModel<RoomListBannerBean>()
                val imageView = findView<ImageView>(com.hamster.happyness.R.id.iv_banner)
                imageView.loadImage(model.url,com.hamster.happyness.R.mipmap.app_party_banner_rank_bg,8f,8f,8f,8f)
            }
            onFastClick(R.id.root) {
                val model = getModel<RoomListBannerBean>()
                handleSchema(model.schema)
            }
        }.models = data
    }

    @JvmStatic
    @BindingAdapter("roomListMicMembers", requireAll = false)
    fun roomListMicMembers(layout: LinearLayout, room: RoomListBean) {
        layout.removeAllViews()
        val padding = SizeUtils.dp2px(1f)
        val margin = SizeUtils.dp2px(-3f)
        val size = SizeUtils.dp2px(21f)
        var userList =
            room.wheatPositionList.filter { it.wheatPositionId.isNotEmpty() }
        if (userList.isNotEmpty() && userList.size > 4) {
            userList = userList.subList(0, 4)
        }
        userList.forEachIndexed { index, model ->
            val imageView = ImageView(layout.context)
            imageView.background =
                ResourceUtils.getDrawable(com.hamster.happyness.R.drawable.bg_party_members)
            val params = LinearLayout.LayoutParams(size, size)
            if (index > 0) {
                params.setMargins(margin, 0, 0, 0)
            }
            imageView.layoutParams = params
            imageView.loadImageCircle(model.wheatPositionIdHeadPortrait)
            layout.addView(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("roomHeat", requireAll = false)
    fun roomHeat(textView: TextView, value: Long) {
        textView.visibility = if (value > 0) View.VISIBLE else View.GONE
        textView.text = when (value) {
            in 0 until 10000 -> value.toString()
            in 10000 until 100000000 -> Format.O_O_DOWN.format(value.toDouble() / 10000)
                .removeSuffix(".0") + "万"

            else -> Format.O_O_DOWN.format(value.toDouble() / 100000000).removeSuffix(".0") + "亿"
        }
    }

     /**
      * 可用喂食道具
      */
    @JvmStatic
    @BindingAdapter("availableFood", requireAll = false)
    fun availableFood(textView: TextView, vm: WalletViewModel) {
        val spanStringAvailable = SpanUtils().append("我可用的")
            .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_satiety_small)
            .append(vm.hmsInfoModel.get()?.satiety.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
            .append(vm.walletModel.value?.diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()
        textView.text = spanStringAvailable
    }

     /**
      * 可用清洁道具
      */
     @JvmStatic
     @BindingAdapter("availableClean", requireAll = false)
     fun availableClean(textView: TextView, vm: WalletViewModel) {
         val spanStringAvailable = SpanUtils().append("我可用的")
             .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_cleanliness_small)
             .append(vm.hmsInfoModel.get()?.cleanliness.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
             .append(vm.walletModel.value?.diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .create()
         textView.text = spanStringAvailable
     }

     @JvmStatic
     @BindingAdapter("availableReborn", requireAll = false)
     fun availableReborn(textView: TextView, vm: WalletViewModel) {
         val spanStringAvailable = SpanUtils().append("我可用的")
             .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_reborn_small)
             .append(vm.revivePanelModel.get()?.pineCone.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
             .append(vm.walletModel.value?.diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .create()
         textView.text = spanStringAvailable
     }


    /**
     * 道具喂养-确认
     */
    @JvmStatic
    @BindingAdapter("feedingPropConfirmation", requireAll = false)
    fun feedingPropConfirmation(button: Button, cultivationPanelModel: CultivationPanelModel?) {
        val spanString = SpanUtils()
            .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_satiety_small)
            .append(cultivationPanelModel?.satiety?.prop.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .append("\n确认喂养")
            .create()
        button.text = spanString
    }

    /**
     * 松果喂养-确认
     */
    @JvmStatic
    @BindingAdapter("feedingPineConeConfirmation", requireAll = false)
    fun feedingPineConeConfirmation(button: Button, cultivationPanelModel: CultivationPanelModel?) {
        val spanString = SpanUtils()
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
            .append(cultivationPanelModel?.satiety?.consumption.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .append("\n确认喂养")
            .create()
        button.text = spanString

    }

     /**
      * 道具清洗-确认
      */
     @JvmStatic
     @BindingAdapter("cleaningPropConfirmation", requireAll = false)
     fun cleaningPropConfirmation(button: Button, cultivationPanelModel: CultivationPanelModel?) {
         val spanString = SpanUtils()
             .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_cleanliness_small)
             .append(cultivationPanelModel?.cleanliness?.prop.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .append("\n确认清洁")
             .create()
         button.text = spanString
     }

     /**
      * 松果清洗-确认
      */
     @JvmStatic
     @BindingAdapter("cleaningPineConeConfirmation", requireAll = false)
     fun cleaningPineConeConfirmation(button: Button, cultivationPanelModel: CultivationPanelModel?) {
         val spanString = SpanUtils()
             .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
             .append(cultivationPanelModel?.cleanliness?.consumption.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .append("\n确认清洁")
             .create()
         button.text = spanString

     }


     /**
      * 松果重生-确认
      */
     @JvmStatic
     @BindingAdapter("rebornConeConfirmation", requireAll = false)
     fun rebornConeConfirmation(button: Button, revivePanelModel: RevivePanelModel?) {
         val spanString = SpanUtils()
             .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
             .append(revivePanelModel?.pineCone.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .append("\n确定复活")
             .create()
         button.text = spanString
     }

     /**
      * 松子重生-确认
      */
     @JvmStatic
     @BindingAdapter("rebornNutConfirmation", requireAll = false)
     fun rebornNutConfirmation(button: Button, revivePanelModel: RevivePanelModel?) {
         val spanString = SpanUtils()
             .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_nut)
             .append(revivePanelModel?.pineNuts.toString()).setForegroundColor(Color.parseColor("#FDC120"))
             .append("\n确定复活")
             .create()
         button.text = spanString

     }
}