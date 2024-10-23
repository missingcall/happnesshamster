package com.kissspace.common.base

/**
 * @description:懒加载fragment 适用于ViewPager2
 * @author: yxt
 * @create: 2024-10-22 17:31
 **/
abstract class BaseLazyFragment(layoutId: Int) :BaseFragment(layoutId) {

    // 懒加载
    private var isLoaded = false
    /**
     * 懒加载，只有在Fragment第一次创建且第一次对用户可见
     */
    protected abstract fun lazyInitView()
    protected abstract fun lazyLoadData()
    //View点击事件
    protected open fun lazyClickListener(){}
    //数据变化监听
    protected open fun lazyDataChangeListener() {}
    //总线事件监听
    protected open fun lazyEventListener() {}



    override fun onResume() {
        super.onResume()
        if (!isLoaded&&!isHidden) {
            lazyInitView()
            lazyLoadData()
            lazyClickListener()
            lazyDataChangeListener()
            lazyEventListener()
            isLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }

}