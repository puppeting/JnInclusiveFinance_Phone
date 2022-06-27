package com.inclusive.finance.jh.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.inclusive.finance.jh.widget.CustomProgress
import com.hwangjr.rxbus.RxBus


abstract class MyBaseFragment : Fragment() {
    lateinit var rootView: View
    override fun onDestroy() {
        try {
            RxBus.get().unregister(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        CustomProgress.disMiss()
        super.onDestroy()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        view.postDelayed({if (isAdded)refreshData()},200)
        try {
            RxBus.get().register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    open fun initView(){}
    open fun initEvent() {}
    open fun initData() {}
    open fun refreshData(type:Int?=-1) {initData()}
    open fun saveData() {}
}
