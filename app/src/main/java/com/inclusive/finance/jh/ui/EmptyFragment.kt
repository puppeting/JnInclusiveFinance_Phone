package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.databinding.FragmentEmptyBinding
import com.inclusive.finance.jh.interfaces.PresenterClick

/**
 * 暂未开发
 *
 * */
class EmptyFragment : MyBaseFragment(), PresenterClick {
    lateinit var viewBind: FragmentEmptyBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentEmptyBinding.inflate(inflater, container, false).apply {
            presenterClick = this@EmptyFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
    }
    override fun initData() {
    }


    override fun onClick(v: View?) {
    }

}