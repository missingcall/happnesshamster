package com.kissspace.mine.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.kissspace.common.base.BaseViewModel
import com.kissspace.common.config.CommonApi
import com.kissspace.common.config.Constants
import com.kissspace.common.config.Constants.TypeFaceRecognition
import com.kissspace.common.model.FindPropReceiveListItem
import com.kissspace.common.model.QueryMarketListItem
import com.kissspace.common.model.UserInfoBean
import com.kissspace.common.model.wallet.*
import com.kissspace.common.util.DJSLiveData
import com.kissspace.common.util.customToast
import com.kissspace.common.util.format.Format
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.common.util.setApplicationValue
import com.kissspace.mine.bean.ExpectedTransferAccountsModel
import com.kissspace.mine.bean.ExpectedTransferConversionModel
import com.kissspace.mine.http.MineApi
import com.kissspace.network.exception.AppException
import com.kissspace.network.net.Method
import com.kissspace.network.net.request
import com.kissspace.util.isNotEmptyBlank
import com.kissspace.util.logE
import com.kissspace.util.orZero
import org.json.JSONObject

/**
 * @Author gaohangbo
 * @Date 2022/12/30 10:03.
 * @Describe
 */
class WalletViewModel : BaseViewModel() {

    //兑换余额
    var mExchangeBalance = MutableLiveData<Double>()

    //兑换余额内容
    var mExchangeBalanceContent = MediatorLiveData<String>().apply {
        addSource(mExchangeBalance) {
            setValue(Format.E.format(mExchangeBalance.value.orZero()))
        }
    }
    var mGoldNumber = MutableLiveData<String>()

    var successNumber = MutableLiveData<String>()

    //操作成功提示
    val successMessage = MutableLiveData<String>()

    //是否显示转账成功页面
    val isShowSuccessOperate = MutableLiveData<Boolean>()

    //是否显示提现状态
    val isShowWithDrawStatus = MutableLiveData<Boolean>()

    var mRmb = MutableLiveData<String>()

    var mDiamond = MutableLiveData<String>()

    val walletModel = MutableLiveData<WalletModel>()

    //转账的用户id
    val transferUserId = MutableLiveData<String>()


    val transferSuccessText = MutableLiveData<CharSequence>()

    //转账的金币
    val transferUserNumber = MutableLiveData<Double>()

    //是否显示白色背景
    val isShowWhiteBackground = MutableLiveData<Boolean>()

    //兑换类型
    val exchangeType = MutableLiveData<String>()

    val exchangeTypeTitle = MutableLiveData<String>()

    //实际兑换的金额
    val exchangeRmb = MutableLiveData<Double>()

    //兑换数量
    val exchangeNumber = MutableLiveData<Double>()

    //金币转账权限
    var exchangeCoinPermission = MutableLiveData<Boolean>()


    //是否可兑换
    val enableExchangeNumber = MediatorLiveData<Boolean>().apply {
        addSource(exchangeRmb) {
            setValue(exchangeRmb.value.orZero() != 0.0)
        }
    }

    val exchangeNumberContent = MediatorLiveData<String>().apply {
        addSource(exchangeNumber) {
            if (exchangeNumber.value != 0.0) {
                value = Format.E.format(exchangeNumber.value.orZero())
            }
        }
    }

    //提现图片
    var withDrawImage = MutableLiveData<Int>()

    var withDrawType = MutableLiveData<String>()

    var withDrawTypeTitle = MutableLiveData<String>()

    //提现余额
    var withDrawBalance = MutableLiveData<Double>()

    //提现内容
    var withDrawBalanceContent = MediatorLiveData<String>().apply {
        addSource(withDrawBalance) {
            setValue(Format.E.format(withDrawBalance.value.orZero()))
        }
    }

    //提现数量内容
    var withDrawNumberContent = MutableLiveData<String>()

    //获取领养仓鼠&仓鼠果园-松果银行列表
    var queryMarketItem = MutableLiveData<QueryMarketListItem>()

    //提现数量
    var withDrawNumber = MutableLiveData<Double>()

    var withDrawTextHint = MutableLiveData<String>()

    val walletRechargeHint = MutableLiveData<String>()

    val walletExchangeHint = MutableLiveData<String>()

    //实付金额
    val isTransferCoinEnable = DJSLiveData<Boolean>().apply {
        addSources(transferUserId, transferUserNumber) {
            setValue(transferUserId.value?.isNotEmpty() == true && transferUserNumber.value != 0.0)
        }
    }
    val isWithDrawEnable = MediatorLiveData<Boolean>().apply {
        addSource(withDrawNumberContent) {
            setValue(withDrawNumberContent.value.isNotEmptyBlank())
        }
    }

    //coin 钻石
    val coin = MediatorLiveData<String>().apply {
        addSource(walletModel) { walletModel ->
            setValue(Format.E.format(walletModel.coin.orZero()))
        }
    }

    //diamond 松果
    var diamond = MediatorLiveData<String>().apply {
        // java.lang.IllegalArgumentException: Cannot format given Object as a Number
        addSource(walletModel) { walletModel ->
            logE("walletModel.diamond" + walletModel.diamond)
            setValue(Format.E.format(walletModel.diamond.orZero()))
        }
    }


    //accountBalance 松子
    var accountBalance = MediatorLiveData<String>().apply {
        addSource(walletModel) { walletModel ->
            setValue(
                Format.E.format(walletModel.accountBalance.orZero())
            )
        }
    }

    //medal 勋章
    var medal = MediatorLiveData<String>().apply {
        addSource(walletModel) { walletModel ->
            setValue(
                Format.E.format(walletModel.medal.orZero())
            )
        }
    }

    //普通用户
    var isOrdinaryUser = MediatorLiveData<Boolean>().apply {
        addSource(walletModel) {
            setValue(it.identity == "001")
        }
    }

    var isTransferReward = ObservableField(false)

    //公会长
    var isFamilyHeader = MediatorLiveData<Boolean>().apply {
        addSource(walletModel) {
            setValue(it.identity == "003")
        }
    }

    //获取当前仓鼠信息
    var hmsInfoModel = ObservableField<HmsInfoModel>()

    //获取仓鼠养成培养消费面板
    var cultivationPanelModel = ObservableField<CultivationPanelModel>()

    //获取仓鼠复活消费面板
    var revivePanelModel = ObservableField<RevivePanelModel>()

    //代币转换费率
    var expectedTransferConversionModel = MediatorLiveData<ExpectedTransferConversionModel>().apply {
        addSource(walletModel) {

        }
    }

    //代币转赠费率
    var expectedTransferAccountsModel = MediatorLiveData<ExpectedTransferAccountsModel>().apply {
        addSource(walletModel) {

        }
    }

    //清洁度
    var cleanliness = ObservableField<Int>()

    //饱食度
    var satiety = ObservableField<Int>()

    //仓鼠状态 （001 正常 002 濒死 003 已死亡 004 已到期）
    var hamsterStatus = ObservableField<String>()

    //提现
    fun withDrawNumber(
        withdrawCoin: Double,
        withdrawType: Int,
        withdrawalPaymentType: Int,
        block: ((Boolean?) -> Unit)?,
        error: ((String?) -> Unit)?
    ) {
        val param = mutableMapOf<String, Any?>()
        param["os"] = Constants.OperationSystem
        param["withdrawCoin"] = withdrawCoin
        param["withdrawType"] = withdrawType
        param["withdrawalPaymentType"] = withdrawalPaymentType
        setApplicationValue(
            type = TypeFaceRecognition,
            value = Constants.FaceRecognitionType.WITHDRAW.type.toString()
        )
        request<Boolean>(MineApi.API_WITHDRAW_ITEM, Method.POST, param, onSuccess = {
            block?.invoke(it)
        }, onError = {
            error?.invoke(it.message)
        })
    }

    //金币转账
    fun transferCoin(coin: Double, targetDisplayId: String, block: ((Boolean?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        //coin
        param["coin"] = coin
        //	收款用户
        param["targetDisplayId"] = targetDisplayId
        setApplicationValue(
            type = TypeFaceRecognition,
            value = Constants.FaceRecognitionType.BLIND_BOX.type.toString()
        )
        request<Boolean>(MineApi.API_TRANSFER_COIN, Method.POST, param, onSuccess = {
            block?.invoke(it)
        }, onError = {
            logE(it.message)
            customToast(it.message)
        })
    }

    //钻石转账
    fun transferDiamond(diamond: Double, targetDisplayId: String, block: ((Boolean?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        //coin
        param["diamond"] = diamond
        //	收款用户
        param["targetDisplayId"] = targetDisplayId
        setApplicationValue(
            type = TypeFaceRecognition,
            value = Constants.FaceRecognitionType.BLIND_BOX.type.toString()
        )
        request<Boolean>(MineApi.API_TRANSFER_DIAMOND, Method.POST, param, onSuccess = {
            block?.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }

    //收益兑换金币
    fun exchangeEarns(accountBalance: Double, onSuccess: ((Boolean?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["accountBalance"] = accountBalance
        setApplicationValue(
            type = TypeFaceRecognition,
            value = Constants.FaceRecognitionType.EXCHANGE.type.toString()
        )
        request<Boolean>(MineApi.API_EXCHANGE_EARNS, Method.POST, param, onSuccess = {
            onSuccess?.invoke(it)
        }, onError = {
            customToast(it.message)
            onSuccess?.invoke(false)
        })
    }

    //钻石兑换金币
    fun exchangeDiamond(diamond: Double, block: ((Boolean?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["diamond"] = diamond
        setApplicationValue(
            type = TypeFaceRecognition,
            value = Constants.FaceRecognitionType.EXCHANGE.type.toString()
        )
        request<Boolean>(MineApi.API_EXCHANGE_DIAMOND, Method.POST, param, onSuccess = {
            block?.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }


    //获取钱包兑现记录
    fun getWalletExchangeDetailRecode(
        pageNum: Int, withdrawType: Int, block: ((WalletExchangeRecodeListModel?) -> Unit)?
    ) {
        val param = mutableMapOf<String, Any?>()
        //(1.收益提现/2.钻石提现)
        param["withdrawType"] = withdrawType
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        request<WalletExchangeRecodeListModel?>(MineApi.API_WithDrawList,
            Method.GET,
            param,
            onSuccess = {
                block?.invoke(it)
            },
            onError = {
                customToast(it.message)
            })
    }

    //获取金币流水
    fun getCoinList(coinType: String?, pageNum: Int, onSuccess: ((WalletDetailModel?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["coinType"] = ""
        param["flowKind"] = coinType
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        request<WalletDetailModel?>(MineApi.API_FLOW_RECORD_COIN_LIST,
            Method.GET,
            param,
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onError = {
                customToast(it.message)
            })
    }

    //获取钱包收益流水
    fun getEarnsList(
        profitType: String?, pageNum: Int, onSuccess: ((WalletDetailModel?) -> Unit)?
    ) {
        val param = mutableMapOf<String, Any?>()
        param["profitType"] = ""
        param["flowKind"] = profitType
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        request<WalletDetailModel>(
            MineApi.API_FLOW_RECORD_EARNS_LIST,
            Method.GET,
            param,
            onSuccess = {
                onSuccess?.invoke(it)
            })
    }

    //获取紫币流水
    fun getDiamondList(
        diamondType: String?, pageNum: Int, onSuccess: ((WalletDetailModel?) -> Unit)?
    ) {
        val param = mutableMapOf<String, Any?>()
        param["diamondType"] = diamondType
        param["flowKind"] = ""
        param["pageNum"] = pageNum
        param["pageSize"] = Constants.PageSize
        request<WalletDetailModel>(
            MineApi.API_FLOW_RECORD_DIAMOND_LIST,
            Method.GET,
            param,
            onSuccess = {
                onSuccess?.invoke(it)
            })
    }

    //根据displayid获取用户信息
    fun loadUserByDisplayId(displayId: String, onSuccess: ((UserInfoBean?) -> Unit)?, onError: ((AppException?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["displayId"] = displayId
        request<UserInfoBean>(MineApi.API_QUERY_USER_BY_DISPLAY_ID_RESPONSE,
            Method.GET,
            param,
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onError = {
                onError?.invoke(it)
            })
    }

    //杉德（支付宝）支付
    fun sandPay(sandPayChannelCode: String, payChannelType: String, payProductId: String, block: ((String) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        val jsonObject = JSONObject()
//        jsonObject.put("cardNo","6228480038740595475")
//        jsonObject.put("wx_app_id", ConstantsKey.WECHAT_APPID)
//        jsonObject.put("gh_ori_id","gh_8f69bbed2867")
//        jsonObject.put("path_url","pages/zf/index?")
//        jsonObject.put("miniProgramType","0")
        jsonObject.put("userId", MMKVProvider.displayId)
        param["payExtra"] = jsonObject.toString()
        param["os"] = Constants.OperationSystem
        // param["sandPayChannelCode"] = "05030001"
        param["sandPayChannelCode"] = sandPayChannelCode
        param["payProductId"] = payProductId
        param["payChannelType"] = payChannelType

        request<String>(CommonApi.API_SAND_PAY, Method.POST, param, onSuccess = {
            block?.invoke(it)
        }, onError = {
            customToast(it.message)
        }
        )
    }

    fun sandWechatPay(sandPayChannelCode: String, payChannelType: String, payProductId: String, block: ((String) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        val jsonObject = JSONObject()
        jsonObject.put("wx_app_id", "wx7d77111f5fbb8e45")
        jsonObject.put("gh_ori_id", "gh_99ba4bfcf8b3")
        jsonObject.put("path_url", "pages/zf/index?")
        jsonObject.put("miniProgramType", "2")
        param["payExtra"] = jsonObject.toString()
        param["os"] = Constants.OperationSystem
        // param["sandPayChannelCode"] = "05030001"
        param["sandPayChannelCode"] = sandPayChannelCode
        param["payProductId"] = payProductId
        param["payChannelType"] = payChannelType

        request<String>(CommonApi.API_SAND_PAY, Method.POST, param, onSuccess = {
            block?.invoke(it)
        }, onError = {
            customToast(it.message)
        }
        )
    }

    /**
     * 电报仓鼠 钱包- 松果/松子/钻石
     */

    //我的钱包
    fun getMyMoneyBag(block: ((WalletModel?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        request<WalletModel>(MineApi.API_MY_WALLET, Method.GET, param, onSuccess = {
            walletModel.value = it
            block?.invoke(it)
        })
    }

    //钱包记录 最近5条
    fun queryCollectRecordList(startTime: String?, endTime: String?, pageNum: Int, pageSize: Int, onSuccess: ((CollectRecordModel?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["startTime"] = startTime
        param["endTime"] = endTime
        param["pageNum"] = pageNum
        param["pageSize"] = pageSize
        request<CollectRecordModel?>(MineApi.API_QUERY_COLLECT_RECORD_LIST,
            Method.GET,
            param,
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onError = {
                customToast(it.message)
            })
    }

    /**
     * 记录查询V1.2.0

    currency	币种 002 松果 004 松子 001 钻石
    startTime	开始时间,字符串类型(yyyy-MM-dd HH:mm:ss格式)
    endTime	    结束时间,字符串类型(yyyy-MM-dd HH:mm:ss格式)
    flowKind	收入类型 001 收入 002 支出
    pageNum	    页数,示例值(1)
    pageSize	每页条数,示例值(10)
     */

    fun queryNumChangeRecord(
        currency: String?,
        startTime: String?,
        endTime: String?,
        flowKind: String?,
        pageNum: Int,
        pageSize: Int,
        onSuccess: ((QueryNumChangeRecordModel?) -> Unit)?
    ) {
        val param = mutableMapOf<String, Any?>()
        param["currency"] = currency
        param["startTime"] = startTime
        param["endTime"] = endTime
        param["flowKind"] = flowKind
        param["pageNum"] = pageNum
        param["pageSize"] = pageSize
        request<QueryNumChangeRecordModel?>(MineApi.API_CENTER_HMS_NUM_QUERY_NUM_CHANGE_RECORD,
            Method.GET,
            param,
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onError = {
                customToast(it.message)
            })
    }

    //获取仓鼠养成培养消费面板
    fun queryCultivationPanel(block: ((CultivationPanelModel?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        request<CultivationPanelModel>(MineApi.API_HAMSTER_CULTIVATE_QUERY_CULTIVATION_PANEL, Method.GET, param, onSuccess = {
            cultivationPanelModel.set(it)
            cultivationPanelModel.notifyChange()
            block?.invoke(it)
        }, onError = {
            customToast(it.message)
        }
        )
    }

    //获取仓鼠复活消费面板
    fun queryRevivePanel(block: ((RevivePanelModel?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        request<RevivePanelModel>(MineApi.API_HAMSTER_CULTIVATE_QUERY_REVIVEPANEL, Method.GET, param, onSuccess = {
            revivePanelModel.set(it)
            block?.invoke(it)
        }, onError = {
            customToast(it.message)
        }
        )
    }

    //获取当前仓鼠信息
    fun hmsInfo(onSuccess: ((HmsInfoModel?) -> Unit)?, onError: ((AppException?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        request<HmsInfoModel>(MineApi.API_HAMSTER_CULTIVATION_HMSINFO, Method.GET, param, onSuccess = {
            hmsInfoModel.set(it)
            onSuccess?.invoke(it)
        }, onError = {
            onError?.invoke(it)
        }
        )
    }

    //喂养仓鼠
    fun improveSatiety(costType: String, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["costType"] = costType
        request<Boolean>(MineApi.API_HAMSTER_CULTIVATE_IMPROVE_SATIETY, Method.GET, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }


    //清洗仓鼠
    fun improveCleanliness(costType: String, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["costType"] = costType
        request<Boolean>(MineApi.API_HAMSTER_CULTIVATE_IMPROVE_CLEANLINESS, Method.GET, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }

    //复活仓鼠
    fun cultivateRevive(payType: String, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["payType"] = payType
        request<Boolean>(MineApi.API_HAMSTER_CULTIVATE_REVIVE, Method.GET, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }

    //获取领养仓鼠&仓鼠果园-松果银行列表
    fun queryMarketList(type: String, onSuccess: ((MutableList<QueryMarketListItem>) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["type"] = type
        request<MutableList<QueryMarketListItem>>(MineApi.API_HAMSTER_MARKET_QUERY_MARKET_LIST, Method.GET, param, onSuccess = {
            onSuccess.invoke(it)

        }, onError = {
        })
    }

    //购买商品
    fun buy(extendedFields: String?, goodsId: String?, money: Double?, number: Int?, payType: String?, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["extendedFields"] = extendedFields
        param["goodsId"] = goodsId
        param["money"] = money
        param["number"] = number
        param["payType"] = payType
        request<Boolean>(MineApi.API_HAMSTER_MARKET_BUY, Method.POST, param, onSuccess = {
            onSuccess.invoke(it)

        }, onError = {
            customToast(it.message)
        })
    }

    //背包物品激活
    fun activation(propId: String?, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["propId"] = propId
        request<Boolean>(MineApi.API_HAMSTER_MARKET_ACTIVATION, Method.POST, param, onSuccess = {
            onSuccess.invoke(it)

        }, onError = {
            customToast(it.message)
        })
    }

    //查询所有用户信息
    fun queryUserByUserId(userId: String, onSuccess: ((UserInfoBean) -> Unit) = {}, onError: ((AppException?) -> Unit)) {
        val param = mutableMapOf<String, Any?>()
        param["userId"] = userId
        request<UserInfoBean>(CommonApi.API_GET_USER_INFO_BY_ID, Method.GET, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            onError.invoke(it)
        })
    }


    //代币转换 费率 预计获得
    fun expectedTransferConversion(amount: Double, type: String, onSuccess: ((ExpectedTransferConversionModel) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["amount"] = amount
        param["type"] = type
        request<ExpectedTransferConversionModel>(MineApi.API_USERNUM_EXPECTED_TRANSFER_CONVERSION, Method.POST, param, onSuccess = {
            expectedTransferConversionModel.value = it
            onSuccess.invoke(it)
        }, onError = {
        })
    }

    //代币转换 费率 预计获得
    fun transferConversion(amount: Double, type: String, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["amount"] = amount
        param["type"] = type
        request<Boolean>(MineApi.API_USERNUM_TRANSFER_CONVERSION, Method.POST, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }


    //代币转赠 费率 预计获得
    fun expectedTransferAccounts(
        amount: Double,
        targetUserDisplayId: String?,
        type: String,
        onSuccess: ((ExpectedTransferAccountsModel) -> Unit) = {}
    ) {
        val param = mutableMapOf<String, Any?>()
        param["amount"] = amount
        param["targetUserDisplayId"] = targetUserDisplayId
        param["type"] = type
        request<ExpectedTransferAccountsModel>(MineApi.API_USERNUM_EXPECTED_TRANSFER_ACCOUNTS, Method.POST, param, onSuccess = {
            expectedTransferAccountsModel.value = it
            onSuccess.invoke(it)
        }, onError = {
        })
    }

    //松子转赠
    fun transferPineNuts(smsCode: String?, amount: Double, targetUserDisplayId: String, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["smsCode"] = smsCode
        param["amount"] = amount
        param["targetUserDisplayId"] = targetUserDisplayId
        request<Boolean>(MineApi.API_TRANSFER_PINE_NUTS, Method.POST, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }

    //代币转赠
    fun transferAccounts(/*smsCode: String?,*/ amount: Double, targetUserDisplayId: String, type: String, onSuccess: ((Boolean) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
//        param["smsCode"] = smsCode
        param["amount"] = amount
        param["targetUserDisplayId"] = targetUserDisplayId
        param["type"] = type
        request<Boolean>(MineApi.API_USERNUM_TRANSFER_ACCOUNTS, Method.POST, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
            customToast(it.message)
        })
    }

    //
    fun findPropReceiveList(propId: String?, onSuccess: ((MutableList<FindPropReceiveListItem>) -> Unit) = {}) {
        val param = mutableMapOf<String, Any?>()
        param["propId"] = propId

        request<MutableList<FindPropReceiveListItem>>(MineApi.API_CENTER_HAMSTERMARKET_FINDPROPRECEIVELIST, Method.POST, param, onSuccess = {
            onSuccess.invoke(it)
        }, onError = {
//            customToast(it.message)
        })
    }

    /**
     * 领取松果 领取每日奖励
     */
    fun receivePineCone(day: Int, propId: String, onSuccess: ((String?) -> Unit)?, onError: ((AppException?) -> Unit)?) {
        val param = mutableMapOf<String, Any?>()
        param["day"] = day
        param["propId"] = propId
        request<String?>(MineApi.API_HAMSTER_MARKET_RECEIVE_PINE_CONE,
            Method.POST,
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onError = {
                onError?.invoke(it)
            })
    }
}