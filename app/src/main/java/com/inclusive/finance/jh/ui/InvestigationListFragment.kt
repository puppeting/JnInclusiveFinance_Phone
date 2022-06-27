package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.google.gson.JsonObject
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
import com.inclusive.finance.jh.databinding.FragmentInvestigationListBinding
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.ui.filter.Filter
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
 * 授信调查列表
 * */
@AndroidEntryPoint
class InvestigationListFragment : MyBaseFragment(), OnRefreshLoadMoreListener,
    OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentInvestigationListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentInvestigationListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("申请进度")
        add("签批进度")
        add("提交")
        add("影像补录")
        add("强制完成补录")
        add("移交")
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "授信调查"
        viewBind.actionBarCustom.toolbar.apply {
            inflateMenu(R.menu.add_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_open_filters -> {
                        findFiltersFragment()?.showFiltersSheet()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
        mAdapter = ItemBaseListCardAdapter(this)
        mAdapter.setOnItemChildClickListener(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    override fun initEvent() {
        val dataList: MutableList<Filter> //        val elements_1 = Filter.SearchFilter( //            Tag( //                "9",
        //                "客户姓名",
        //                hint = "请输入客户姓名或身份证号",
        //                valueName = "0",
        //                keyName = "0"
        //            )
        //        )
        val elements1 = Filter.TagFilter(Tag("1", "processStatus", "流程状态", valueName = "全部", keyName = "", isSingleCheck = true))
        val elements2 = Filter.TagFilter(Tag("2", "processStatus", "流程状态", valueName = "流程中", keyName = "流程中", isSingleCheck = true))
        val elements3 = Filter.TagFilter(Tag("3", "processStatus", "流程状态", valueName = "待处理", keyName = "待处理", isSingleCheck = true))
        val elements4 = Filter.TagFilter(Tag("4", "processStatus", "流程状态", valueName = "已完成", keyName = "已完成", isSingleCheck = true))
        val elements5 = Filter.TagFilter(Tag("5", "processStatus", "流程状态", valueName = "已终止", keyName = "已终止", isSingleCheck = true))
        dataList = mutableListOf(elements1, elements2, elements3, elements4, elements5)
        val viewModel = ViewModelProvider(this@InvestigationListFragment)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(dataList)
            viewModel.toggleFilter(elements3, enabled = true, singleCheck = true)
        }
    }

    var searchStr = ""
    var processStatus = "待处理"
    private var resultCountCallBack: ((count: String) -> Unit)?=null
    override fun initData() {
        DataCtrlClass.SXDCNet.getSXDCList(requireActivity(), currentPage, searchStr, processStatus) {
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
        viewBind.root.postDelayed({ initData() }, 200)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")
        val investigateBusinessesType = when (SZWUtils.getJsonObjectString(jsonObject, "creditModel")) {
            "通用简化类" -> {
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE
            }
            "经营类" -> {
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE
            }
            "消费类" -> {
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE
            }
            else -> {
                ApplyModel.BUSINESS_TYPE_INVESTIGATE
            }
        }
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "申请进度" -> {
                            CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "creditId"), type = "0", businessType = ApplyModel.BUSINESS_TYPE_INVESTIGATE).show(childFragmentManager, this.javaClass.name)
                        }
                        "签批进度" -> {
                            CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "creditId"), type = "1", businessType = ApplyModel.BUSINESS_TYPE_INVESTIGATE).show(childFragmentManager, this.javaClass.name)
                        }
                        "提交" -> {
                            when {
                                jsonObjectString.contains("0") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                                else -> DataCtrlClass.SXSPNet.getSXSPById(context, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), businessType = ApplyModel.BUSINESS_TYPE_INVESTIGATE, type = "0") { configurationBean ->
                                    if (configurationBean != null) {
                                        ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), businessType = ApplyModel.BUSINESS_TYPE_INVESTIGATE) {
                                            refreshData()
                                            if (isAdded) (activity as BaseActivity).refreshData()
                                        }.show(childFragmentManager, this.javaClass.name)
                                    }
                                }
                            }
                        }
                        "影像补录" -> {
                            when {
                                jsonObjectString.contains("0") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                                else ->

                                    BaseTypePop(context, this, "提交补录", getUrl = Urls.get_picAdd_pop, saveUrl = Urls.save_picAdd_pop, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), json = jsonObject) { adapter, resultStr ->
                                        refreshData()
                                        if (isAdded) (activity as BaseActivity).refreshData()
                                    }.show(childFragmentManager, this.javaClass.name)
                            }

                        }
                        "强制完成补录" -> {
                            ConfirmPop(context, "确认强制结束补录?") { confirm ->
                                if (confirm) {
                                    DataCtrlClass.SXSQNet.picAddFinish(context, Urls.finish_picAdd_list, SZWUtils.getJsonObjectString(jsonObject, "creditId")) {
                                        onRefresh(viewBind.mRefreshLayout)
                                    }
                                }
                            }.show(childFragmentManager, this.javaClass.name)

                        }
                        "移交" -> {
                            BaseTypePop(context, this, "移交", getUrl = Urls.get_clr_yj_pop, saveUrl = Urls.save_clr_yj_pop, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), json = jsonObject) { adapter, resultStr ->
                                refreshData()
                                if (isAdded) (activity as BaseActivity).refreshData()
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        else -> {}
                    }
                }.showPopupWindow(view)
            }
            R.id.bt_seeOnly -> {
                IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "creditId"), investigateBusinessesType, true)
            }
            R.id.bt_change -> {
                when {
                    jsonObjectString.contains("2") -> IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "creditId"), investigateBusinessesType, false)
                    else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                }

            }
        }
    }

    private var filtersFragment: FiltersFragment? = null
    private fun findFiltersFragment(): FiltersFragment? { //        if (filtersFragment == null) {

        filtersFragment = FiltersFragment(this) { list, str,c ->
            resultCountCallBack=c
            searchStr = str
            processStatus = ""
            list.forEach {
                val tagFilter = it.filter as Filter.TagFilter
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