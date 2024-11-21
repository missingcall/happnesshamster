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
import com.kissspace.common.config.Constants
import com.kissspace.common.model.InfoListModel
import com.kissspace.common.model.RoomListBannerBean
import com.kissspace.common.model.RoomListBean
import com.kissspace.common.model.wallet.CultivationPanelModel
import com.kissspace.common.util.format.Format
import com.kissspace.common.util.handleSchema
import com.kissspace.common.widget.NiceImageView
import com.kissspace.mine.viewmodel.WalletViewModel
import com.kissspace.module_room.R
import com.kissspace.util.loadImage
import com.kissspace.util.loadImageCircle

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
                imageView.loadImage(model.url, com.hamster.happyness.R.mipmap.app_party_banner_rank_bg, 8f, 8f, 8f, 8f)
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
     * 皮肤背景
     */
    @JvmStatic
    @BindingAdapter("skinBackground", requireAll = false)
    fun skinBackground(view: View, m: InfoListModel.Record) {
        //已选中
        if (m.checked) {
            //已解锁
            if (m.unlockStatus) {
                view.setBackgroundResource(com.kissspace.module_common.R.drawable.common_skin_item_selector_selected)
            } else {
                //未解锁
                view.setBackgroundResource(com.kissspace.module_common.R.drawable.common_skin_item_selector_selected_locked)

            }


            //未选中
        } else {
            //已解锁
            if (m.unlockStatus) {
                //已佩戴
                if (m.wearStatus) {
                    view.setBackgroundResource(com.kissspace.module_common.R.drawable.common_skin_item_not_selected)
                } else {
                    view.setBackgroundResource(com.kissspace.module_common.R.drawable.common_skin_item_not_selected)
                }
            } else {
                //未解锁
                view.setBackgroundResource(com.kissspace.module_common.R.drawable.common_skin_item_locked)
            }
        }


    }


    /**
     * 设置NiceImageView边框
     */
    @JvmStatic
    @BindingAdapter("setBorderBackground")
    fun setBorderBackground(niceImageView: NiceImageView, isBorder: Boolean) {
        if (niceImageView.isSelected) {
            niceImageView.setBorderWidth(2)
        } else {
            niceImageView.setBorderWidth(0)
        }
    }

    /**
     * 可用松果
     */
    @JvmStatic
    @BindingAdapter("availablePineCones", requireAll = false)
    fun availablePineCones(textView: TextView, vm: WalletViewModel) {
        val spanStringAvailable = SpanUtils().append("我可用的")
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
            .append(vm.walletModel.value?.diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()
        textView.text = spanStringAvailable
    }

    /**
     * 可用松果数量
     */
    @JvmStatic
    @BindingAdapter("availablePineConesNum", requireAll = false)
    fun availablePineConesNum(textView: TextView, diamond: String?) {
        val spanStringAvailable = SpanUtils()
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
            .append(diamond.toString())
            .create()
        textView.text = spanStringAvailable
    }

    /**
     * 可用松子数量
     */
    @JvmStatic
    @BindingAdapter("availablePineNutsNum", requireAll = false)
    fun availablePineNutsNum(textView: TextView, accountBalance: String?) {
        val spanStringAvailable = SpanUtils()
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_nut_small)
            .append(accountBalance.toString())
            .create()
        textView.text = spanStringAvailable
    }

    /**
     * 可用喂食道具v1.1.0
     */
    @JvmStatic
    @BindingAdapter(value = ["propFood", "diamond"])
    fun availableFood(textView: TextView, propFood: Int, diamond: Double) {
        val spanStringAvailable = SpanUtils().append("我可用的")
            .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_satiety)
            .append(propFood.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
            .append(diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()
        textView.text = spanStringAvailable
    }

    /**
     * 可用喂食道具v1.2.0
     */
    @JvmStatic
    @BindingAdapter("availableFoodProp", requireAll = false)
    fun availableFoodProp(textView: TextView, propFood: Int) {
        val spanStringAvailable = SpanUtils()
            .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_satiety_small)
            .append(propFood.toString())//.setForegroundColor(Color.parseColor("#FDC120"))
            .create()
        textView.text = spanStringAvailable
    }

    /**
     * 可用清洁道具v1.1.0
     */
    @JvmStatic
    @BindingAdapter(value = ["propClean", "diamond"])
    fun availableClean(textView: TextView, propClean: Int, diamond: Double) {
        val spanStringAvailable = SpanUtils().append("我可用的")
            .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_cleanliness)
            .append(propClean.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone)
            .append(diamond.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()
        textView.text = spanStringAvailable
    }

    /**
     * 可用清洁道具v1.2.0
     */
    @JvmStatic
    @BindingAdapter("availableCleanProp", requireAll = false)
    fun availableCleanProp(textView: TextView, propClean: Int) {
        val spanStringAvailable = SpanUtils()
            .appendImage(com.hamster.happyness.R.mipmap.app_icon_home_cleanliness_small)
            .append(propClean.toString())//.setForegroundColor(Color.parseColor("#FDC120"))
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
            .append(cultivationPanelModel?.satiety?.costPropCount.toString())
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
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
            .append(cultivationPanelModel?.satiety?.consumption.toString())
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
            .append(cultivationPanelModel?.cleanliness?.costPropCount.toString())//.setForegroundColor(Color.parseColor("#FDC120"))
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
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
            .append(cultivationPanelModel?.cleanliness?.consumption.toString())//.setForegroundColor(Color.parseColor("#FDC120"))
            .create()
        button.text = spanString

    }


    /**
     * 松子重生-确认
     */
    @JvmStatic
    @BindingAdapter("rebornNutConfirmation", requireAll = false)
    fun rebornNutConfirmation(button: Button, pineNuts: Double) {
        val spanString = SpanUtils()
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_nut_small)
            .append(pineNuts.toString())
            .create()
        button.text = spanString
    }


    /**
     * 松果重生-确认
     */
    @JvmStatic
    @BindingAdapter("rebornConeConfirmation", requireAll = false)
    fun rebornConeConfirmation(button: Button, pineCone: Double) {
        val spanString = SpanUtils()
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
            .append(pineCone.toString())
            .create()
        button.text = spanString
    }


    /**
     * 皮肤状态 (已拥有，未解锁，已使用)
     */
    @JvmStatic
    @BindingAdapter("setSkinStatus", requireAll = false)
    fun setSkinStatus(imageView: ImageView, record: InfoListModel.Record) {
        //已解锁
        if (record.unlockStatus) {
            //已配带
            if (record.wearStatus) {
                imageView.visibility = View.VISIBLE
                imageView.loadImage(com.hamster.happyness.R.mipmap.app_icon_home_wear)
            } else {
                imageView.visibility = View.INVISIBLE
            }
        } else {
            imageView.visibility = View.VISIBLE
            imageView.loadImage(com.hamster.happyness.R.mipmap.app_icon_home_lock)

        }

    }

    /**
     * 皮肤购买弹窗 确认
     */
    @JvmStatic
    @BindingAdapter("skinPurchase", requireAll = false)
    fun skinPurchase(button: Button, record: InfoListModel.Record) {
        val spanString = SpanUtils()
            .append("确认购买 ")
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_small)
            .append(record.price.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()
        button.text = spanString
    }

    /**
     * 果园购买-payType
     */
    @JvmStatic
    @BindingAdapter("ivConditions", requireAll = false)
    fun ivConditions(imageView: ImageView, payType: String) {
        imageView.loadImage(
            when (payType) {
                "001" -> com.kissspace.module_mine.R.mipmap.icon_pine_cone_small
                "002" -> com.kissspace.module_mine.R.mipmap.icon_pine_nut_small
                "003" -> com.kissspace.module_mine.R.mipmap.icon_pine_cone_small
                else -> com.kissspace.module_mine.R.mipmap.icon_hamster_medal_small
            }
        )
    }


    /**
     * 首页快捷跳转
     * 状态 是否应用 001 启用 002 未启用 003 删除
     */
    @JvmStatic
    @BindingAdapter("quickJumpStatus", requireAll = false)
    fun quickJumpStatus(view: View, status: String) {
        when (status) {
            "001" -> view.visibility = View.VISIBLE
            "002" -> view.visibility = View.GONE
            "003" -> view.visibility = View.GONE
            else -> view.visibility = View.VISIBLE
        }
    }


}