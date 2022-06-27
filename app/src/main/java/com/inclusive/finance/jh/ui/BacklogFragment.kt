package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.adapter.ItemBacklogAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentBacklogBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 *待办事项
 * */
class BacklogFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    private var viewBind: FragmentBacklogBinding? = null
    lateinit var mAdapter: ItemBacklogAdapter<JsonObject>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)
        if (viewBind == null) {
            viewBind = FragmentBacklogBinding.inflate(inflater, container, false).apply {
                presenterClick = this@BacklogFragment
                lifecycleOwner = viewLifecycleOwner
                mAdapter = ItemBacklogAdapter()
                mRecyclerView.adapter = mAdapter
            }
        }

        return viewBind?.root ?: View(context)
    }

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 500L
    override fun initView() {
//        StatusBarUtil.setPaddingSmart(act, viewBind?.actionBarCustom?.appBar)
//        viewBind?.actionBarCustom?.toolbar?.title = "个人中心"

        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                IRouter.goF(view, SZWUtils.getJsonObjectString(mAdapter.data[position], "ANDROID"))
                mLastClickTime = System.currentTimeMillis()
            }
        }
    }

    override fun initData() {
        DataCtrlClass.MainNet.get_main_dbsx_list(requireActivity()) {
            viewBind?.mRefreshLayout?.finishRefresh()
            if (it != null) {
                mAdapter.setNewInstance(it)
            }
        }
    }
//
//    /**
//    返回后刷新数据，
//     */
//    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_Refresh_List)])
//    fun backRefresh(str: String) {
//       viewBind?.userName?.text= (MyApplication.user as User).userInfo?.realname?:""
//    }

    /**
    登录后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_LoginSuccess)])
    fun loginSuccess(str: String) {
        initView()
    }

    override fun onClick(v: View?) {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }


}