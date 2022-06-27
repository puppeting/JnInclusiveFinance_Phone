package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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
import com.inclusive.finance.jh.databinding.FragmentCreditApprovalBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.ui.filter.Filter
import com.inclusive.finance.jh.ui.filter.FiltersFragment
import com.inclusive.finance.jh.ui.filter.SearchViewModel
import com.inclusive.finance.jh.ui.filter.Tag
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.act

/**
 * 授信审批
 * */
@AndroidEntryPoint
class CreditApprovalFragment : MyBaseFragment(), OnRefreshListener, OnLoadMoreListener,
    OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentCreditApprovalBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    private var businessType: Int = ApplyModel.BUSINESS_TYPE_SXSP
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentCreditApprovalBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
            lifecycleOwner?.lifecycle?.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_CREATE -> {
                            this@CreditApprovalFragment.event = null
                        }
                        Lifecycle.Event.ON_RESUME -> {
                            if (this@CreditApprovalFragment.event != null) {
                                viewBind.mRefreshLayout.autoRefresh()
                            }
                        }
                        Lifecycle.Event.ON_STOP -> {
                            this@CreditApprovalFragment.event = event
                        }
                        else -> {
                        }
                    }
                }

            })
        }
        return viewBind.root
    }

    private var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btSeeOnly?.visibility = View.GONE
        viewBind?.btChange?.text = "签署意见"
    }


    override fun initView() {
        businessType = arguments?.getInt("businessType", ApplyModel.BUSINESS_TYPE_APPLY)
            ?: businessType
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        mAdapter = ItemBaseListCardAdapter(this)
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter.loadMoreModule.setOnLoadMoreListener(this) // 当数据不满一页时，是否继续自动加载（默认为true）
        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRefreshLayout.setOnRefreshListener(this)
        mAdapter.setOnItemChildClickListener(this) //
        mAdapter.loadMoreModule.setOnLoadMoreListener(this)

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
    }


    private var listUrl = ""
    private var getPicUrl = ""
    private var savePicUrl = ""
    private var endPicUrl = ""
    private var getYJUrl = ""
    private var saveYJUrl = ""
    private var saveLCGGUrl = ""
    private var seeDetailDestination = ApplyModel.BUSINESS_TYPE_INVESTIGATE
    val listMenuDatas = mutableListOf<String>().apply {
        add("查看调查")
        add("查看进度")
        add("签批进度")
        add("影像补录")
        add("强制完成补录")
        add("移交")
        add("流程更改")

    }

    override fun initEvent() {
        when (businessType) {
            ApplyModel.BUSINESS_TYPE_SXSP -> {
                viewBind.actionBarCustom.mTitle.text = "授信审批"
                processStatus = "待处理"
                listUrl = Urls.getList_sxsp
                getPicUrl = Urls.get_picAdd_pop
                savePicUrl = Urls.save_picAdd_pop
                endPicUrl = Urls.finish_picAdd_list
                getYJUrl = Urls.get_clr_yj_pop
                saveYJUrl = Urls.save_clr_yj_pop
                saveLCGGUrl = Urls.get_sxsp_changeFlow
            }
            ApplyModel.BUSINESS_TYPE_QPLC -> {
                viewBind.actionBarCustom.mTitle.text = "签批流程"
                processStatus = "流程中"
                listUrl = Urls.getList_sxqp
                listMenuDatas.remove("影像补录")
                listMenuDatas.remove("强制完成补录")
                listMenuDatas.remove("移交")
                listMenuDatas.remove("流程更改")
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC -> {
                viewBind.actionBarCustom.mTitle.text = "签批流程"
                processStatus = "流程中"
                listUrl = Urls.getList_sunshine_sxqp
                listMenuDatas.remove("影像补录")
                listMenuDatas.remove("强制完成补录")
                listMenuDatas.remove("移交")
                listMenuDatas.remove("流程更改")
                seeDetailDestination = ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY
            }
        }
        val dataList: MutableList<Filter>

        val elements5 = Filter.TagFilter(Tag("5", "processStatus", "流程状态", valueName = "流程中", keyName = "流程中", isSingleCheck = true))
        val elements6 = Filter.TagFilter(Tag("6", "processStatus", "流程状态", valueName = "待处理", keyName = "待处理", isSingleCheck = true))
        val elements7 = Filter.TagFilter(Tag("7", "processStatus", "流程状态", valueName = "已完成", keyName = "已完成", isSingleCheck = true))
        val elements8 = Filter.TagFilter(Tag("8", "processStatus", "流程状态", valueName = "已终止", keyName = "已终止", isSingleCheck = true))
        dataList = mutableListOf( //            elements_1,
            elements5, elements6, elements7, elements8
        )
        val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(dataList)
        }
    }
    private var resultCountCallBack: ((count: String) -> Unit)?=null
    override fun initData() {
        DataCtrlClass.SXSPNet.getSXSPList(requireActivity(), listUrl, currentPage, searchStr, processStatus) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.setListData(bean = it, list = it.list)
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
            resultCountCallBack?.invoke(mAdapter.data.size.toString())
        }
    }

    override fun refreshData(type: Int?) {
        viewBind.mRefreshLayout.autoRefresh()
    }

    override fun onLoadMore() {

        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        initData()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")

        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "查看调查" -> {
                            IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "creditId"), seeDetailDestination, true)
//
                        }
                        "签批进度" -> {
                            CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "creditId"), type = "1", businessType = businessType).show(childFragmentManager, this.javaClass.name)

                        }
                        "查看进度" -> {
                            CheckProgressPop(
                                context, SZWUtils.getJsonObjectString(jsonObject, "creditId"), type = when (businessType) {
                                    ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC -> {
                                        SZWUtils.getBusinessType(businessType)
                                    }
                                    else -> {
                                        "0"
                                    }
                                }, businessType = businessType
                            ).show(childFragmentManager, this.javaClass.name)
                        }

                        "影像补录" -> {
                            when {
                                jsonObjectString.contains("0") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                                else ->

                                    BaseTypePop(context, this, "提交补录", getUrl = getPicUrl, saveUrl = savePicUrl, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), json = jsonObject) { adapter, resultStr ->
                                        refreshData()
                                        if (isAdded) (activity as BaseActivity).refreshData()
                                    }.show(childFragmentManager, this.javaClass.name)
                            }
                        }
                        "强制完成补录" -> {
                            ConfirmPop(context, "确认强制结束补录?") { confirm ->
                                if (confirm) {
                                    DataCtrlClass.SXSQNet.picAddFinish(context, endPicUrl, SZWUtils.getJsonObjectString(jsonObject, "creditId")) {
                                        onRefresh(viewBind.mRefreshLayout)
                                    }
                                }
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        "移交" -> {
                            BaseTypePop(context, this, "移交", getUrl = getYJUrl, saveUrl = saveYJUrl, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), json = jsonObject) { adapter, resultStr ->
                                refreshData()
                                if (isAdded) (activity as BaseActivity).refreshData()
                            }.show(childFragmentManager, this.javaClass.name)

                        }
                        "流程更改" -> {
                            ConfirmPop(context, "此操作将流程退回至授信审批部最后一人，流程进度不会有历史体现，确认更改吗？") { confirm ->
                                if (confirm) {
                                    DataCtrlClass.SXSPNet.applyChangeFlow(context, url = saveLCGGUrl, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId")) { str ->
                                        if (str != null) {
                                            refreshData()
                                        }
                                    }
                                }
                            }.show(childFragmentManager, this.javaClass.name)

                        }
                        else -> {
                        }
                    }
                }.showPopupWindow(view)
            }
            R.id.bt_change -> {
                when {
                    jsonObjectString.contains("1") -> DataCtrlClass.SXSPNet.getSXSPById(
                        context, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), businessType = businessType, type = when (businessType) {
                            ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC -> {
                                "1"
                            }
                            else -> {
                                SZWUtils.getBusinessType(businessType)
                            }
                        }
                    ) { configurationBean ->
                        if (configurationBean != null) {
                            ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), businessType = businessType) {
                                refreshData()
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                    }
                    jsonObjectString.contains("2") -> ApprovalSuggestionPop(
                        context, when (businessType) {
                            ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC -> {
                                "1"
                            }
                            else -> {
                                SZWUtils.getBusinessType(businessType)
                            }
                        }, keyId = SZWUtils.getJsonObjectString(jsonObject, "creditId"), businessType = businessType
                    ) {
                        refreshData()
                    }.show(childFragmentManager, this.javaClass.name)
                    else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                }
            }
        }
    }


    private var searchStr = ""
    private var admitType = ""
    private var processStatus = "待处理"
    private var filtersFragment: FiltersFragment? = null

    private fun findFiltersFragment(): FiltersFragment? { //        if (filtersFragment == null) {

        filtersFragment = FiltersFragment(this) { list, str,c ->
            resultCountCallBack=c
            searchStr = str
            admitType = ""
            processStatus = ""
            list.forEach {
                val tagFilter = it.filter as Filter.TagFilter
                when (tagFilter.tag.categoryId) {
                    "admitType" -> {
                        admitType = tagFilter.tag.keyName
                    }
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