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
import com.kissspace.util.dp
import com.kissspace.common.config.Constants
import com.kissspace.common.model.*
import com.kissspace.common.util.formatNumCoin
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.widget.UserLevelIconView
import com.kissspace.module_mine.R
import com.kissspace.util.loadImage
import com.kissspace.util.orZero
import com.kissspace.util.resToColor
import androidx.core.text.buildSpannedString
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.StringUtils


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
     * 我的仓库-仓鼠果园/松果银行
     */

    @JvmStatic
    @BindingAdapter("wareHouseCommodityIcon", requireAll = false)
    fun wareHouseCommodityIcon(imageView: ImageView, commodityIcon: String) {
        imageView.loadImage(commodityIcon, com.kissspace.module_common.R.mipmap.common_app_logo)
    }

    @JvmStatic
    @BindingAdapter("wareHouseDayIncome", requireAll = false)
    fun wareHouseDayIncome(textView: TextView, dayIncome: Int) {
        textView.text = buildSpannedString {
            color(com.kissspace.module_common.R.color.color_999999.resToColor()) {
                append("每天可以产出\n")
            }
            color(com.kissspace.module_common.R.color.color_FDC120.resToColor()) {
                append("" + dayIncome)
            }
            color(com.kissspace.module_common.R.color.color_999999.resToColor()) {
                append("松果")
            }
        }
    }

    /**
     * 商行-果园 日产
     */
    @JvmStatic
    @BindingAdapter("firmDayIncomeOrchard", requireAll = false)
    fun firmDayIncomeOrchard(textView: TextView, dayIncome: Int) {
        textView.text = SpanUtils().append("日产: ")
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_little)
            .append(dayIncome.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .create()

    }

    /**
     * 商行-银行 收益
     */
    @JvmStatic
    @BindingAdapter("firmDayIncomeBank", requireAll = false)
    fun firmDayIncomeBank(textView: TextView, dayIncome: Int) {
        textView.text = SpanUtils().append("额外赠")
            .append(dayIncome.toString()).setForegroundColor(Color.parseColor("#FDC120"))
            .appendImage(com.kissspace.module_mine.R.mipmap.icon_pine_cone_little)
            .create()

    }

    @JvmStatic
    @BindingAdapter("tvCommodityStatus", requireAll = false)
    fun tvCommodityStatus(textView: TextView, item: QueryMarketListItem) {
        when (item.goodsStatue) {
            "001" -> {
                if (item.commodityMark == "") {
                    textView.visibility = View.GONE
                } else {
                    //首发
                    textView.visibility = View.VISIBLE
                    textView.text = item.commodityMark
                    textView.setBackgroundColor(Color.parseColor("#00AC13"))

                }
            }
            "002" -> textView.visibility = View.GONE
            "004" -> {
                textView.visibility = View.VISIBLE
                textView.setBackgroundColor(Color.parseColor("#FA3127"))
                textView.text = "待激活"
            }
            "005" -> textView.visibility = View.GONE
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
                            "001" -> R.mipmap.icon_pine_cone_little
                            "002" -> R.mipmap.icon_pine_nut_little
                            "003" -> R.mipmap.icon_hamster_coin_little
                            else -> R.mipmap.icon_pine_cone_little
                        }
                    )
                    .append(item.coinPrice.toString())
                    .create()
            }
            "002" -> button.text = "已售罄"
            "004" -> button.text = "去激活"
            "005" -> button.text = "领取奖励"
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
    fun wareHouseTimeLimit(textView: TextView, timeLimit: Int) {
        textView.text = "剩余天数: " + timeLimit + " 天"
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
    @BindingAdapter("flowKindCoinType", requireAll = false)
    fun flowKindCoinType(textView: TextView, coinType: String) {
        var s = ""
        when (coinType) {
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_ALL -> s = StringUtils.getString(R.string.coinType_all)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_001 -> s = StringUtils.getString(R.string.coinType_001)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_002 -> s = StringUtils.getString(R.string.coinType_002)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_003 -> s = StringUtils.getString(R.string.coinType_003)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_004 -> s = StringUtils.getString(R.string.coinType_004)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_005 -> s = StringUtils.getString(R.string.coinType_005)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_006 -> s = StringUtils.getString(R.string.coinType_006)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_007 -> s = StringUtils.getString(R.string.coinType_007)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_008 -> s = StringUtils.getString(R.string.coinType_008)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_009 -> s = StringUtils.getString(R.string.coinType_009)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_010 -> s = StringUtils.getString(R.string.coinType_010)
            Constants.CollectRecordMode.API_HAMSTER_MARKET_RECORD_LIST_TYPE_011 -> s = StringUtils.getString(R.string.coinType_011)

        }
        textView.text = s
    }


}