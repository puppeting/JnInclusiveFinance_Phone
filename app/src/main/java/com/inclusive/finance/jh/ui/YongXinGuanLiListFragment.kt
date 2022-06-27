package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentYxglListBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.pop.BaseListMenuPop
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.CheckProgressPop
import com.inclusive.finance.jh.pop.ProcessProcessingPop
import com.inclusive.finance.jh.ui.filter.Filter
import com.inclusive.finance.jh.ui.filter.Filter.TagFilter
import com.inclusive.finance.jh.ui.filter.FiltersFragment
import com.inclusive.finance.jh.ui.filter.SearchViewModel
import com.inclusive.finance.jh.ui.filter.Tag
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.act


/**
 * 用信管理列表
 * */
@AndroidEntryPoint
class YongXinGuanLiListFragment : MyBaseFragment(), OnRefreshLoadMoreListener,
    OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentYxglListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentYxglListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btMore?.visibility = View.VISIBLE
        viewBind?.btSeeOnly?.visibility = View.GONE
        viewBind?.btChange?.visibility = View.GONE

    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "用信管理"
        viewBind.actionBarCustom.toolbar.apply {
            inflateMenu(R.menu.add_search_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_open_filters -> {
                        findFiltersFragment()?.showFiltersSheet()
                        true
                    }
                    R.id.action_add -> {
                        BaseTypePop(context, this@YongXinGuanLiListFragment, "新增", getUrl = Urls.get_yxgl_pop_new, saveUrl = Urls.save_yxgl_pop_new) { adapter, resultStr ->
                            refreshData()
                            if (isAdded) (activity as BaseActivity).refreshData()
                        }.show(childFragmentManager, this.javaClass.name)

                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }

        mAdapter = ItemBaseListCardAdapter(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        mAdapter.setOnItemChildClickListener(this)
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this) //        val mainData = SZWUtils.getJson(context, "listData.json")

        initStatusView()
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("用信")
        add("查看详情")
        add("查看进度")
        add("提交")
        add("离柜放款验证单")
    }

    private fun initStatusView() {

        val dataList: MutableList<Filter>
        val elements1 = TagFilter(Tag("0", "checkStatus", "流程状态", valueName = "全部", keyName = "", isSingleCheck = true))
        val elements2 = TagFilter(Tag("2", "checkStatus", "流程状态", valueName = "申请中", keyName = "1", isSingleCheck = true))
        val elements3 = TagFilter(Tag("3", "checkStatus", "流程状态", valueName = "已超时", keyName = "2", isSingleCheck = true))
        val elements4 = TagFilter(Tag("4", "checkStatus", "流程状态", valueName = "待审批", keyName = "3", isSingleCheck = true))

        dataList = mutableListOf(
            elements1, elements2, elements3, elements4
        )
        val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(dataList)
        }
    }

    private var searchStr = ""
    private var processStatus = ""
    private var resultCountCallBack: ((count: String) -> Unit)? = null
    override fun initData() {

        DataCtrlClass.CreditManagementNet.getYXGLList(requireActivity(), currentPage, searchStr, ""/*"搜索待合一"*/, processStatus) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.setListData(bean = it, list = it.list)
                } else {
                    mAdapter.addData(it.list)

                }
                if (!it.list.isNullOrEmpty()) {
                    viewBind.mRefreshLayout.finishLoadMore()
                    currentPage++
                } else {
                    viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
                }
            } else {
                viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
            }
            resultCountCallBack?.invoke(mAdapter.data.size.toString())
        }
    }

    override fun refreshData(type: Int?) {
        viewBind.mRefreshLayout.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        viewBind.root.postDelayed({ initData() }, 0)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val flag = SZWUtils.getJsonObjectString(jsonObject, "flag")
        val status = SZWUtils.getJsonObjectString(jsonObject, "status")
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "用信" -> {
                            when {
                                flag == "1" && status == "申请中" -> {
                                    IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER, false)
                                }
                                else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                            }

                        }
                        "查看详情" -> {
                            IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER, true)
                        }
                        "查看进度" -> {
                            CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "id"), type = "7", businessType = ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER).show(childFragmentManager, this.javaClass.name)

                        }
                        "提交" -> {
                            when {
                                flag.contains("0") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                                else -> DataCtrlClass.SXSPNet.getSXSPById(context, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER, type = "7") { configurationBean ->
                                    if (configurationBean != null) {
                                        ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER) {
                                            viewBind.mRefreshLayout.autoRefresh()
                                        }.show(childFragmentManager, this.javaClass.name)
                                    }
                                }
                            }
                        }
                        "离柜放款验证单" -> {
                            when (status) {
                                "已审批" -> {
                                    IRouter.goF(view, R.id.action_to_navActivity, "离柜放款验证单", SZWUtils.getJsonObjectString(jsonObject, "id"), jsonObject, ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK, true)
                                }
                                else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                            }
                        }
                        else -> {
                        }
                    }
                }.showPopupWindow(view)
            }

        }
    }

    /**
    返回后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [com.hwangjr.rxbus.annotation.Tag(Constants.BusAction.Bus_Refresh_List)])
    fun backRefresh(str: String) {
        viewBind.mRefreshLayout.autoRefresh()
    }


    private var filtersFragment: FiltersFragment? = null
    private fun findFiltersFragment(): FiltersFragment? { //        if (filtersFragment == null) {

        filtersFragment = FiltersFragment(this) { list, str, c ->
            resultCountCallBack = c
            searchStr = str
            processStatus = ""
            list.forEach {
                val tagFilter = it.filter as TagFilter
                when (tagFilter.tag.categoryId) {
                    "processStatus" -> {
                        processStatus = tagFilter.tag.keyName
                    }
                }
            }
            refreshData()
        } //        }
        filtersFragment?.show(childFragmentManager, this.javaClass.name)

        return filtersFragment
    }
}