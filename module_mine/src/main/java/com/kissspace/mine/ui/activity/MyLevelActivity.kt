package com.kissspace.mine.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.common.adapter.BannerAdapter
import com.kissspace.common.base.BaseActivity
import com.kissspace.common.config.Constants
import com.kissspace.common.router.jump
import com.kissspace.common.model.LevelModel
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.getH5Url
import com.kissspace.mine.viewmodel.LevelViewModel
import com.kissspace.module_mine.R
import com.kissspace.module_mine.databinding.MineActivityMyLevelBinding
import com.kissspace.common.http.getUserInfo
import com.kissspace.common.model.RoomListBannerBean
import com.kissspace.common.util.handleSchema
import com.kissspace.network.result.collectData
import com.kissspace.util.dp
import kotlinx.coroutines.*
import com.kissspace.util.logE
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.listener.OnBannerListener

/**
 *
 * @Author: nicko
 * @CreateDate: 2023/1/11 11:18
 * @Description: 我的等级页面
 *
 */
@Router(path = RouterPath.PATH_MY_LEVEL)
class MyLevelActivity : com.kissspace.common.base.BaseActivity(R.layout.mine_activity_my_level) {
    private val mBinding by viewBinding<MineActivityMyLevelBinding>()
    private val mViewModel by viewModels<LevelViewModel>()


    var charmLevel:Int=0
    var consumeLevel:Int=0
    var charmLevelCount:Double=0.0
    var consumeLevelCount:Double=0.0
    var charmListLevelModel:List<LevelModel>?=null
    var consumeLevelModel:List<LevelModel>?=null
    override fun initView(savedInstanceState: Bundle?) {

        getUserInfo(onSuccess = {
            //设置魅力等级
            mBinding.iconUserCharm.setLeveCount(it.charmLevel)
            mBinding.iconUserCharm.visibility = View.VISIBLE
            charmLevel=it.charmLevel
            charmLevelCount=it.charmTotal

            //设置财富等级
            mBinding.iconUserConsume.setLeveCount(it.consumeLevel)
            mBinding.iconUserConsume.visibility = View.VISIBLE
            consumeLevel=it.consumeLevel
            consumeLevelCount =it.consumeTotal
            getLevelInfo()
        })

        mBinding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }

            override fun onRightClick(titleBar: TitleBar?) {
                jump(
                    RouterPath.PATH_WEBVIEW,
                    "url" to getH5Url(Constants.H5.consumeUrl),
                    "showTitle" to true
                )
            }
        })

    }
    private fun getLevelInfo(){
        mViewModel.queryUserConsume {
            consumeLevelModel = it
            logE("AAAconsumeLevelModel"+consumeLevelModel.toString())
        }
        mViewModel.getUserCharm {
            charmListLevelModel = it
            logE("AAAcharmListLevelModel" + charmListLevelModel.toString())
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setData(){

        val charmLevelModel:LevelModel? = charmListLevelModel?.firstOrNull { it.num > charmLevelCount }

        val consumeLevelModel:LevelModel? = consumeLevelModel?.firstOrNull { it.num > consumeLevelCount }

        if(charmLevelModel!=null){
            mBinding.tvConsumeNext.text ="距离升级还需要收到："+com.kissspace.common.util.format.Format.E.format(charmLevelModel.num-charmLevelCount)+"钻石"
            mBinding.tvConsumeValue.text="当前魅力值:${com.kissspace.common.util.format.Format.E.format(charmLevelCount)}"
        }else{
            mBinding.tvConsumeNext.text ="已是最高等级"
            mBinding.tvConsumeValue.text="当前魅力值:${com.kissspace.common.util.format.Format.E.format(charmLevelCount)}"
            mBinding.incomeProgressInner.progress = 100
            mBinding.incomeProgressOuter.progress = 100
        }

        if(consumeLevelModel!=null){
            mBinding.tvCharmNext .text = "距离升级还需要消耗："+com.kissspace.common.util.format.Format.E.format(consumeLevelModel.num-consumeLevelCount)+"经验"
            mBinding.tvCharmValue.text="当前经验值:${com.kissspace.common.util.format.Format.E.format(consumeLevelCount)}"
        }else{
            mBinding.tvCharmNext .text = "已是最高等级"
            mBinding.tvCharmValue.text="当前经验值:${com.kissspace.common.util.format.Format.E.format(consumeLevelCount)}"
            mBinding.expendProgressInner.progress = 100
            mBinding.expendProgressOuter.progress = 100
        }

        charmLevelModel?.let {
            val charmProgress = ((charmLevelCount / charmLevelModel.num) * 100).toInt()
            mBinding.incomeProgressInner.progress = charmProgress
            mBinding.incomeProgressOuter.progress = charmProgress
        }

        consumeLevelModel?.let {
            val consumeProgress = ((consumeLevelCount / consumeLevelModel.num) * 100).toInt()
            mBinding.expendProgressInner.progress = consumeProgress
            mBinding.expendProgressOuter.progress = consumeProgress
        }
    }

    override fun createDataObserver() {
        super.createDataObserver()
        this.lifecycleScope.launch {
             var count = 0
            mViewModel.getLevelEventCount.collect {
                count += it
               if(count==2){
                   setData()
               }
            }
        }

        collectData(mViewModel.roomListBannerEvent, onSuccess = {
            val banner =
                it.filter { item -> item.state == "001" && item.os == "001" }
            if (banner.isNotEmpty()) {
                mBinding.banner.apply {
                    visibility = View.VISIBLE
                    if(adapter == null) {
                        setAdapter(BannerAdapter(banner))
                        indicator = RectangleIndicator(this@MyLevelActivity)
                        setIndicatorSelectedColorRes(com.kissspace.module_common.R.color.common_white)
                        setIndicatorNormalColorRes(com.kissspace.module_common.R.color.color_80FFFFFF)
                        setIndicatorWidth(12.dp.toInt(), 12.dp.toInt())
                        setIndicatorHeight(4.dp.toInt())
                        setIndicatorMargins(IndicatorConfig.Margins(0, 0, 0, 10.dp.toInt()))
                        setOnBannerListener(object : OnBannerListener<RoomListBannerBean> {
                            override fun OnBannerClick(data: RoomListBannerBean?, position: Int) {
                                handleSchema(data?.schema)
                            }
                        })
                        addBannerLifecycleObserver(this@MyLevelActivity)
                    }else{
                        adapter.setDatas(banner)
                    }
                }
            }
        })

        mViewModel.getRoomListBanner()
    }

}