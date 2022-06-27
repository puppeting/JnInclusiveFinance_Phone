package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentKhglListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act

/**
 * 客户管理列表
 * */
@Deprecated("暂时不用")
class KeHuGuanLiFragment : MyBaseFragment(), PresenterClick, OnRefreshListener, OnLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentKhglListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentKhglListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@KeHuGuanLiFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "客户管理"


        mAdapter = ItemBaseListAdapter(this)

        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRefreshLayout.setOnRefreshListener(this)
        //        val mainData = SZWUtils.getJson(context, "listData.json")
        //        val data = Gson().fromJson<BaseListBean>(mainData, BaseListBean::class.java)
        //        adapter.titleList = data.titleList
        //        adapter.setNewInstance(data.list)

    }

    override fun initData() {
        DataCtrlClass.KHGLNet.getKHGLList(requireActivity(), currentPage, viewBind.etSearch.text.toString()) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    viewBind.mRefreshLayout.setNoMoreData(false)
                    mAdapter.initTitleLay(context, viewBind.layoutBaseList.root, it) {
                        mAdapter.setNewInstance(it.list)
                    }
                } else {
                    mAdapter.addData(it.list)

                }
                if (!it.list.isNullOrEmpty()) {
                    mAdapter.loadMoreModule.loadMoreComplete()
                    currentPage++
                } else {
                    mAdapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                mAdapter.loadMoreModule.loadMoreFail()
            }


        }
    }

    override fun refreshData(type: Int?) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        super.refreshData(type)
    }

    override fun onLoadMore() {

        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshData()
    }

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 500L
    override fun onClick(v: View?) {
        if (v == viewBind.btSearch) {
            if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                viewBind.mRefreshLayout.autoRefresh()
                mLastClickTime = System.currentTimeMillis()
            }
            return
        }
        when (v) {

            viewBind.chipNew -> {
                //                context?.let { BaseTypePop(it, this, "新增", "", "save",mAdapter.data.firstOrNull {b-> SZWUtils.getJsonObjectBoolean(b, "isCheck") }).showPopupWindow() }
                BaseTypePop(context, this, "新增", Urls.khglGetNewList, Urls.khglAdd) {adapter,resultStr->
                    onRefresh(viewBind.mRefreshLayout)
                }.show(childFragmentManager, this.javaClass.name)
            }
            viewBind.chipDelete -> {
                SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
                    ConfirmPop(context, "确认删除该用户?") {
                        if (it) {
                            DataCtrlClass.KHGLNet.khglDeleteById(context, SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                onRefresh(viewBind.mRefreshLayout)
                            }
                        }
                    }.show(childFragmentManager,this.javaClass.name)
                }


            }
            viewBind.chipConfirm -> {
                SZWUtils.getJsonObjectBeanFromList(mAdapter.data) {
                    viewModel.idCardNum = SZWUtils.getJsonObjectString(it, "id")
                    DataCtrlClass.KHGLNet.khglGetById(context, viewModel.idCardNum) { kehuBean ->
                        if (kehuBean != null) {
                            viewModel.kehuBean.value = kehuBean
                            Navigation.findNavController(act, R.id.my_nav_host_fragment)
                                .navigateUp()
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

}