package com.kissspace.mine.adapter

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ResourceUtils
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kissspace.common.config.Constants
import com.kissspace.common.model.*
import com.kissspace.common.util.formatNumCoin
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.UserLevelIconView
import com.kissspace.module_mine.R
import androidx.core.text.buildSpannedString
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.kissspace.common.ext.safeClick
import com.kissspace.common.util.format.DateFormat
import com.ruffian.library.widget.RTextView
import com.kissspace.common.util.format.Format
import com.kissspace.common.util.formatDate
import com.kissspace.common.widget.NiceImageView
import com.kissspace.util.*
import java.time.temporal.TemporalAmount
import kotlin.text.isNotEmpty


object MineBindingAdapter {

    @JvmStatic
    @BindingAdapter("sexImage", requireAll = false)
    fun sexImage(imageView: ImageView, sex: String?) {
        imageView.setImageResource(if (sex == Constants.SEX_MALE) R.mipmap.mine_icon_sex_male else R.mipmap.mine_sex_female)
    }

    @JvmStatic
    @BindingAdapter("coinText", requireAll = false)
    fun coinText(textView: TextView, amount: Double) {
        textView.text = formatNumCoin(amount)
    }

    @JvmStatic
    @BindingAdapter("visitorNewCount", requireAll = false)
    fun visitorNewCount(textView: TextView, count: Int? = 0) {
        if (count == null || count == 0) {
            textView.visibility = View.GONE
        } else {
            textView.visibility =
                if (count - MMKVProvider.currentVisitorCount > 0) View.VISIBLE else View.GONE
            textView.text = "+" + (count - MMKVProvider.currentVisitorCount)
        }
    }

    @JvmStatic
    @BindingAdapter("fansNewCount", requireAll = false)
    fun fansNewCount(textView: TextView, count: Int? = 0) {
        if (count == null || count == 0) {
            textView.visibility = View.GONE
        } else {
            textView.visibility =
                if (count - MMKVProvider.currentFansCount > 0) View.VISIBLE else View.GONE
            textView.text = "+" + (count - MMKVProvider.currentFansCount)
        }
    }

    @JvmStatic
    @BindingAdapter("userProfileEditBtnVisibility", requireAll = false)
    fun userProfileEditBtnVisibility(textView: TextView, userId: String?) {
        textView.visibility = if (MMKVProvider.userId == userId) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("userProfileFollowBtnState", requireAll = false)
    fun userProfileFollowBtnState(textView: TextView, userId: String?) {
        textView.visibility = if (MMKVProvider.userId == userId) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("userProfileFollowText", requireAll = false)
    fun userProfileFollowText(textView: TextView, isFollow: Boolean?) {
//        textView.setBackgroundResource(if (isFollow == true) R.mipmap.mine_info_follow_cancel else R.mipmap.mine_info_follow_plus)
        textView.text = if (isFollow == true) "取消关注" else "关注"
    }

    @JvmStatic
    @BindingAdapter("noCarText", requireAll = false)
    fun noCarText(textView: TextView, userInfo: UserProfileBean?) {
        textView.visibility = if (userInfo?.car?.isEmpty() == true) View.VISIBLE else View.GONE
        textView.text =
            if (userInfo?.userId?.equals(MMKVProvider.userId) == true) "您还没有坐骑，快去购买一辆吧" else "该用户还未拥有坐骑"
    }

    @JvmStatic
    @BindingAdapter("userGiftWallCount", requireAll = false)
    fun userGiftWallCount(textView: TextView, count: Int?) {
        if (count == null || count == 0) {
            textView.visibility = View.INVISIBLE
        } else {
            textView.text = "x${count}"
            textView.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("jumpStoreVisibility", requireAll = false)
    fun jumpStoreVisibility(textView: TextView, userInfo: UserProfileBean?) {
        textView.visibility =
            if (userInfo?.car?.isEmpty() == true && userInfo.userId == MMKVProvider.userId) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("dressUpBtnState", requireAll = false)
    fun dressUpBtnState(imageView: ImageView, state: String?) {
        if (state == "001") {
            imageView.setImageResource(R.mipmap.mine_icon_dress_up_cancel_wear)
        } else {
            imageView.setImageResource(R.mipmap.mine_dress_up_wear)
        }
    }

    //使用rtextView
    @JvmStatic
    @BindingAdapter("dressUpBtnState2", requireAll = false)
    fun dressUpBtnState2(tv: RTextView, state: String?) {
        if (state == "001") {
            tv.helper.backgroundColorNormalArray = intArrayOf(Color.parseColor("#36398A"), Color.parseColor("#36398A"))
            tv.text = "取消佩戴"
        } else {
            tv.helper.backgroundColorNormalArray = intArrayOf(Color.parseColor("#936DDE"), Color.parseColor("#6C74FB"))
            tv.text = "佩戴"
        }
    }


    @JvmStatic
    @BindingAdapter("followBtnState", requireAll = false)
    fun followBtnState(textView: TextView, isFollow: Boolean) {
        textView.setBackgroundResource(if (isFollow) com.kissspace.module_common.R.drawable.common_shape_blue_deep_normal_45 else com.kissspace.module_common.R.drawable.common_shape_blue_normal_45)
        textView.text = (if (isFollow) "取消关注" else "+ 关注")
    }

    @JvmStatic
    @BindingAdapter("followBtnStateText", requireAll = false)
    fun followBtnStateText(textView: TextView, follow: Boolean?) {
        textView.text = (if (follow != null && follow) "已关注" else "+ 关注")
    }

    @JvmStatic
    @BindingAdapter("userCarRecyclerData", requireAll = false)
    fun userCarRecyclerData(recyclerView: RecyclerView, data: List<PersonCar>?) {
        data?.let {
            recyclerView.linear(RecyclerView.HORIZONTAL).setup {
                addType<PersonCar> { R.layout.mine_layout_user_profile_car_item }
            }.models = it
        }
    }

    @JvmStatic
    @BindingAdapter("showUserEditAvatar", requireAll = false)
    fun showUserEditAvatar(imageView: ImageView, bean: UserProfileBean?) {
        imageView.loadImage(bean?.auditingProfilePath?.ifEmpty { bean.profilePath })
    }

    @JvmStatic
    @BindingAdapter("avatarVerifyVisibility", requireAll = false)
    fun avatarVerifyVisibility(imageView: ImageView, bean: UserProfileBean?) {
        imageView.visibility =
            if (bean?.auditingProfilePath?.isNotEmpty() == true) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("userProfileTitle", requireAll = false)
    fun userProfileTitle(textView: TextView, userId: String?) {
        textView.text = if (MMKVProvider.userId == userId) "关于我" else "关于TA"

    }

    @JvmStatic
    @BindingAdapter("userProfileEditBtnVisibility", requireAll = false)
    fun userProfileEditBtnVisibility(textView: View, userId: String?) {
        textView.visibility = if (MMKVProvider.userId == userId) View.VISIBLE else View.GONE
    }


    @JvmStatic
    @BindingAdapter("followRoomInVisible", requireAll = false)
    fun followRoomInVisible(layout: View, model: UserProfileBean?) {
        layout.visibility =
            if (model != null && model.followRoomId.isNotEmpty() && model.userId != MMKVProvider.userId) View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("userProfileFollowBtnState", requireAll = false)
    fun userProfileFollowBtnState(textView: View, userId: String?) {
        textView.visibility = if (MMKVProvider.userId == userId) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("userProfileDynamicTitle", requireAll = false)
    fun userProfileDynamicTitle(textView: TextView, userId: String?) {
        textView.text = if (MMKVProvider.userId == userId) "我的动态" else "TA的动态"
    }

    @JvmStatic
    @BindingAdapter("followRoomIcon", requireAll = false)
    fun followRoomIcon(imageView: ImageView, model: UserProfileBean?) {
        if (model != null && model.followRoomId.isNotEmpty() && model.userId != MMKVProvider.userId) {
            imageView.setBackgroundResource(R.drawable.mine_anim_follow_room)
            val drawable = imageView.background as AnimationDrawable
            drawable.start()
        }
    }

    @JvmStatic
    @BindingAdapter("followRoomVisible", requireAll = false)
    fun followRoomVisible(layout: ConstraintLayout, model: UserProfileBean?) {
        layout.visibility =
            if (model != null && model.followRoomId.isNotEmpty() && model.userId != MMKVProvider.userId) View.VISIBLE else View.GONE
    }


    @JvmStatic
    @BindingAdapter("mineLevelCount", requireAll = false)
    fun mineLevelCount(textView: UserLevelIconView, count: Int) {
        textView.setLeveCount(count)
    }

    @JvmStatic
    @BindingAdapter("storeCarPreviewVisible", requireAll = false)
    fun storeCarPreviewVisible(textView: TextView, id: String) {
        textView.visibility = if (id.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("storeCarBuyVisible", requireAll = false)
    fun storeCarBuyVisible(textView: TextView, id: String) {
        textView.visibility = if (id.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("storeCarSellOutVisible", requireAll = false)
    fun storeCarSellOutVisible(textView: TextView, id: String) {
        textView.visibility = if (id.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("carImage", requireAll = false)
    fun carImage(imageView: ImageView, model: GoodsListBean) {
        if (model.commodityId.isNullOrEmpty()) {
            imageView.setImageResource(R.mipmap.mine_icon_store_car_default)
        } else {
            imageView.loadImage(model.icon)
        }
    }


    @JvmStatic
    @BindingAdapter("userIdText", requireAll = false)
    fun userIdText(textView: TextView, userInfo: UserInfoBean?) {
        userInfo?.let {
            textView.text = "ID:  " + it.beautifulId.ifEmpty { it.displayId }
        }
    }

    @JvmStatic
    @BindingAdapter("profileNickname", requireAll = false)
    fun profileNickname(textView: TextView, userInfo: UserProfileBean?) {
        userInfo?.let {
            textView.text = it.auditingNickname.ifEmpty { it.nickname }
        }
    }

    @JvmStatic
    @BindingAdapter("profileSex", requireAll = false)
    fun profileSex(textView: TextView, userInfo: UserProfileBean?) {
        userInfo?.let {
            textView.text = if (it.sex == Constants.SEX_FEMALE) "女" else "男"
        }
    }

    @JvmStatic
    @BindingAdapter("profileSignInfo", requireAll = false)
    fun profileSignInfo(textView: TextView, userInfo: UserProfileBean?) {
        userInfo?.let {
            textView.text = it.auditingPersonalSignature.ifEmpty { it.personalSignature }
        }
    }

    @JvmStatic
    @BindingAdapter("storeCarDrawableStart", requireAll = false)
    fun storeCarDrawableStart(textView: TextView, id: String) {
        val drawable =
            if (id.isNullOrEmpty()) ResourceUtils.getDrawable(R.mipmap.mine_icon_integral_store) else ResourceUtils.getDrawable(
                R.mipmap.mine_icon_store_coin
            )
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        textView.setCompoundDrawables(drawable, null, null, null)
    }

    @JvmStatic
    @BindingAdapter("incomeColor", requireAll = false)
    fun incomeColor(textView: TextView, incomeNum: String) {
        textView.setTextColor(
            if (!TextUtils.isEmpty(incomeNum) && incomeNum.startsWith("+")) ContextCompat.getColor(
                textView.context,
                com.kissspace.module_common.R.color.color_ui_sub_main
            ) else ContextCompat.getColor(
                textView.context,
                com.kissspace.module_common.R.color.color_ui_main_text
            )
        )
    }


    @JvmStatic
    @BindingAdapter("goodsInvalidDaysVisible", requireAll = false)
    fun goodsInvalidDaysVisible(textView: TextView, attribute: String) {
        textView.visibility = if (attribute == "001") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("loadGoodsBackground", requireAll = false)
    fun loadGoodsBackground(imageView: ImageView, url: String) {
        imageView.loadImage(url, topLeftRadius = 12f, topRightRadius = 12f)
    }

    @JvmStatic
    @BindingAdapter("familyMemberManagerText", requireAll = false)
    fun familyMemberManagerText(textView: TextView, role: String) {
        textView.text = if (role == "003") "取消管理" else "设置管理"
    }

    @JvmStatic
    @BindingAdapter("isShowMemberManager", requireAll = false)
    fun isShowMemberManager(textView: TextView, role: String) {
        textView.visibility = if (role == "003" || role == "004") View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("memberRoleText", requireAll = false)
    fun memberRoleText(textView: TextView, role: String) {
        textView.text = if (role == "003") "管理" else "会长"
    }


    @JvmStatic
    @BindingAdapter("sexSet", requireAll = false)
    fun sexSet(textView: TextView, sex: String?) {
        textView.setBackgroundResource(if (sex == Constants.SEX_MALE) R.drawable.shape_gender_man else R.drawable.shape_gender)
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (sex == Constants.SEX_MALE) com.kissspace.module_common.R.mipmap.common_ic_flag_male else com.kissspace.module_common.R.mipmap.common_ic_flag_female,
            0,
            0,
            0
        )
    }

    @JvmStatic
    @BindingAdapter("showUserMedal")
    fun showUserMedal(layout: com.nex3z.flowlayout.FlowLayout, model: UserProfileBean?) {
        model?.let {
            layout.removeAllViews()
            if (it.consumeLevel.orZero() > 0) {
                val wealthLevel = UserLevelIconView(layout.context)
                wealthLevel.setLeveCount(UserLevelIconView.LEVEL_TYPE_EXPEND, it.consumeLevel)
                layout.addView(wealthLevel)
            }
            if (it.charmLevel.orZero() > 0) {
                val charmLevel = UserLevelIconView(layout.context)
                charmLevel.setLeveCount(UserLevelIconView.LEVEL_TYPE_INCOME, it.charmLevel!!)
                layout.addView(charmLevel)
            }
            val lp =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 19.dp.toInt())
            it.medalList?.forEach { that ->
                val image = ImageView(layout.context)
                image.adjustViewBounds = true
                image.layoutParams = lp
                image.loadImage(that.url)
                layout.addView(image)
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

    @JvmStatic
    @BindingAdapter("wareHouseDayIncome", requireAll = false)
    fun wareHouseDayIncome(textView: TextView, item: QueryMarketListItem) {
        textView.text = SpanUtils()
            .append("${item.buyMoney}松果${item.buyDay}天预计\n额外采集")
            .append("${item.buyMoney * item.windInterest / 100}").setForegroundColor(Color.parseColor("#FDC120"))
            .append("个松果")
            .create()
    }

    /**
     * 商行-果园 日产
     */
    @JvmStatic
    @BindingAdapter("firmDayIncomeOrchard", requireAll = false)
    fun firmDayIncomeOrchard(textView: TextView, dayIncome: Int) {
        textView.text = SpanUtils().append("日产: ")
            .appendImage(R.mipmap.icon_pine_cone_little)
            .append(dayIncome.toString())
            .create()

    }

    /**
     * 商行-银行 收益
     */
    @JvmStatic
    @BindingAdapter("firmDayIncomeBank", requireAll = false)
    fun firmDayIncomeBank(textView: TextView, item: QueryMarketListItem) {
        textView.text = SpanUtils().append("额外赠")
            .append(item.windInterest.toString() + "%")
            .appendImage(
                when (item.payType) {
                    Constants.HamsterPayType.PINE_CONE -> R.mipmap.icon_pine_cone_little
                    Constants.HamsterPayType.PINE_NUT -> R.mipmap.icon_pine_nut_little
                    Constants.HamsterPayType.THREE_PARTY -> R.mipmap.icon_diamond_little
                    Constants.HamsterPayType.MEDAL -> R.mipmap.icon_hamster_medal_little
                    else -> R.mipmap.icon_pine_cone_little
                }
            )
            .create()

    }

    @JvmStatic
    @BindingAdapter("tvCommodityStatus", requireAll = false)
    fun tvCommodityStatus(textView: TextView, item: QueryMarketListItem) {
        when (item.goodsStatue) {
            Constants.HamsterGoodsStatusType.AVAILABLE_FOR_PURCHASE -> {
                if (item.commodityMark.isBlank()) {
                    textView.visibility = View.GONE
                } else {
                    //首发
                    textView.visibility = View.VISIBLE
                    textView.text = item.commodityMark
                    textView.setBackgroundResource(R.drawable.shape_orchard_mark_green)

                }
            }
            Constants.HamsterGoodsStatusType.SOLD_OUT -> textView.visibility = View.GONE
            Constants.HamsterGoodsStatusType.ALREADY_OWNED_PENDING_ACTIVATION -> {
                textView.visibility = View.VISIBLE
                textView.setBackgroundResource(R.drawable.shape_orchard_mark_red)
                textView.text = "待激活"
            }
            Constants.HamsterGoodsStatusType.ALREADY_OWNED_IN_EFFECT -> textView.visibility = View.GONE
        }

    }

    /**
     * 充值-合计金额
     */
    @JvmStatic
    @BindingAdapter(value = ["payProductCash", "payProductGoldCoin"])
    fun totalRechargeAmount(textView: TextView, payProductCash: String, payProductGoldCoin: String) {
        textView.text = SpanUtils().append("合计：")
            .append(payProductCash + "元").setForegroundColor(Color.parseColor("#5A60FF")).setFontSize(20, true)
            .append("（" + payProductGoldCoin + "钻石）")
            .create()
    }

    @JvmStatic
    @BindingAdapter("btnFirmOrchard", requireAll = false)
    fun btnFirmOrchard(button: Button, item: QueryMarketListItem) {
        //001 商品可购买 002 商品已售磬 003 用户未解锁 004 用户已拥有(待激活) 005 用户已拥有(生效中)
        when (item.goodsStatue) {
            "001" -> {
                //支付类型 001 松果支付 002 松子支付 003 第三方支付
                button.text = SpanUtils().append("购买 ")
                    .appendImage(
                        when (item.payType) {
                            Constants.HamsterPayType.PINE_CONE -> R.mipmap.icon_pine_cone_little
                            Constants.HamsterPayType.PINE_NUT -> R.mipmap.icon_pine_nut_little
                            Constants.HamsterPayType.THREE_PARTY -> R.mipmap.icon_diamond_little
                            Constants.HamsterPayType.MEDAL -> R.mipmap.icon_hamster_medal_little
                            else -> R.mipmap.icon_pine_cone_little
                        }
                    )
                    .append(Format.E.format(item.coinPrice))
                    .create()
                button.setBackgroundResource(com.kissspace.module_common.R.drawable.common_btn_selector_purple_black_radius45)
            }
            "002" -> {
                button.text = "已售罄"
                button.setBackgroundResource(com.kissspace.module_common.R.drawable.common_shape_bg_gradient_radius45)
            }
            "004" -> {
                button.text = "去激活"
                button.setBackgroundResource(com.kissspace.module_common.R.drawable.common_btn_selector_purple_black_radius45)
            }
            //后端字段待定
            "005" -> {
                button.text = "领取奖励"
                button.setBackgroundResource(com.kissspace.module_common.R.drawable.common_shape_bg_green_radius45)
            }
        }
        button.isEnabled = item.goodsStatue != "002"
    }


    @JvmStatic
    @BindingAdapter("btnFirmBank", requireAll = false)
    fun btnFirmBank(button: Button, item: QueryMarketListItem) {
        //001 商品可购买 002 商品已售磬 003 用户未解锁 004 用户已拥有(待激活) 005 用户已拥有(生效中)
        when (item.goodsStatue) {
            "001" -> button.text = "去购买"
            "002" -> button.text = "已售罄"
            "004" -> button.text = "去激活"
            "005" -> button.text = "领取奖励"
        }
        button.isEnabled = item.goodsStatue != "002"
    }

    @JvmStatic
    @BindingAdapter("wareHouseTimeLimit", requireAll = false)
    fun wareHouseTimeLimit(textView: TextView, item: QueryMarketListItem) {
        if (item.settleDay > 0) {
            textView.text = "领取剩余天数:${item.settleDay}天"
        } else {
            textView.text = "结算剩余天数:${item.timeLimit}天"
        }

    }

    @JvmStatic
    @BindingAdapter("btnCollectOrClaim", requireAll = false)
    fun btnCollectOrClaim(button: Button, item: QueryMarketListItem) {
        if (item.settleDay > 0) {
            button.text = "领取"
            button.isEnabled = true
            button.safeClick {
                //TODO 存折领取逻辑

            }
        } else {
            button.text = "采集中"
            button.isEnabled = false
        }

    }

    @JvmStatic
    @BindingAdapter("recordsCoinNumber", requireAll = false)
    fun recordsCoinNumber(textView: TextView, coinNumber: Int) {
        textView.text = "" + coinNumber
        textView.setTextColor(
            if (coinNumber > 0) ContextCompat.getColor(
                textView.context,
                com.kissspace.module_common.R.color.color_FDC120
            ) else ContextCompat.getColor(
                textView.context,
                com.kissspace.module_common.R.color.color_FE5F55
            )
        )
    }

    @JvmStatic
    @BindingAdapter("amountColor", requireAll = false)
    fun amountColor(textView: TextView, amount: String) {
        if (amount.toDouble() >= 0) {
            textView.setTextColor(ColorUtils.getColor(com.kissspace.module_common.R.color.color_FDC120))
        } else {
            textView.setTextColor(ColorUtils.getColor(com.kissspace.module_common.R.color.color_FE5F55))
        }
        textView.text = amount
    }

    @JvmStatic
    @BindingAdapter("createTimeFormat", requireAll = false)
    fun createTimeFormat(textView: TextView, createTime: Long) {
        textView.text = createTime.formatDate(DateFormat.YYYY_MM_DD_HH_MM_SLASH)
    }

    @JvmStatic
    @BindingAdapter("productStatusIsVisible")
    fun productStatusIsVisible(view: View, status: String) {
        if (status == Constants.HamsterGoodsStatusType.SOLD_OUT) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    /**
     * 每日奖励领取状态
     * 	领取状态 001 待领取 002 已领取 003 已过期 004可领取
     */
    @JvmStatic
    @BindingAdapter("propReceiveStatus", requireAll = false)
    fun propReceiveStatus(textView: TextView, status: String) {
        textView.text =
            when (status) {
                "001" -> ""
                "002" -> "已领取"
                "003" -> "已过期"
                "004" -> "领取"
                else -> "领取"
            }

        textView.isEnabled = status == "004"
    }

    /**
     * 领取蒙版显示
     * 	领取状态 001 待领取 002 已领取 003 已过期 004可领取
     */
    @JvmStatic
    @BindingAdapter("propReceiveLockStatus", requireAll = false)
    fun propReceiveLockStatus(imageView: ImageView, status: String) {
        if (status == "001") {
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }
    }

    /**
     * 果园购买-payType
     */
    @JvmStatic
    @BindingAdapter("ivConditions", requireAll = false)
    fun ivConditions(imageView: ImageView, payType: String) {
        imageView.loadImage(
            when (payType) {
                "001" -> R.mipmap.icon_pine_cone_small
                "002" -> R.mipmap.icon_pine_nut_small
                "003" -> R.mipmap.icon_pine_cone_small
                else -> R.mipmap.icon_hamster_medal_small
            }
        )
    }

    /**
     * 展示第n天奖励图标
    道具类型(001 松果 002 松子 003 钻石 004 蓝珀 005 金珀 006 元宝 007 直播间礼物 008 清洁道具 009 勋章 010 头像框 011 座驾)
    7,10,11会给图
    清洁道具 icon为空 name给名字
     */
    @JvmStatic
    @BindingAdapter("SetDailyRewardIcon", requireAll = false)
    fun SetDailyRewardIcon(imageView: ImageView, item: FindPropReceiveListItem.Item) {
        imageView.loadImage(
            when (item.type) {
                "001" -> R.mipmap.icon_pine_cone_small
                "002" -> R.mipmap.icon_pine_nut_small
                "003" -> R.mipmap.icon_diamond_small
                "004" -> R.mipmap.mine_icon_blue_pepper
                "005" -> R.mipmap.mine_icon_gold_pepper
                "006" -> R.mipmap.mine_icon_yuan_bao
                "007" -> item.icon
                "008" ->R.mipmap.mine_icon_cleanliness
                "009" -> R.mipmap.icon_diamond_small
                "010" -> R.mipmap.icon_hamster_medal_small
                "011" -> item.icon

                else -> R.mipmap.icon_pine_cone_small
            }
        )
    }

}