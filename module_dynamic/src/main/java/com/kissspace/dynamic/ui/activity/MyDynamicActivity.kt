package com.kissspace.dynamic.ui.activity

import android.os.Bundle
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import com.didi.drouter.annotation.Router
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.kissspace.util.*
import com.kissspace.common.router.parseIntent
import com.kissspace.common.router.RouterPath
import com.kissspace.common.util.mmkv.MMKVProvider
import com.kissspace.dynamic.ui.fragment.DynamicListFragment
import com.kissspace.module_common.databinding.CommonFragmentContainBinding

/**
 *
 * @Author: nicko
 * @CreateDate: 2022/12/16 11:08
 * @Description: 聊天页面
 *
 */
@Router(uri = RouterPath.PATH_My_DYNAMIC)
class MyDynamicActivity : com.kissspace.common.base.BaseActivity(com.kissspace.module_common.R.layout.common_fragment_contain) {
    private val userId by parseIntent<String>()
    private val nickname by parseIntent<String>()
    private val profilePath by parseIntent<String>()
    private val sex by parseIntent<String>()
    private val mBinding by viewBinding<CommonFragmentContainBinding>()
    override fun initView(savedInstanceState: Bundle?) {
        immersiveStatusBar(true)
        supportFragmentManager.commit (true){
            replace(com.kissspace.module_common.R.id.container,DynamicListFragment.newInstance(userId,nickname,profilePath,sex))
        }
        mBinding.topBar.title =(if(userId == MMKVProvider.userId){"我"} else nickname)+"的动态"
        mBinding.topBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                finish()
            }
        })

    }

}