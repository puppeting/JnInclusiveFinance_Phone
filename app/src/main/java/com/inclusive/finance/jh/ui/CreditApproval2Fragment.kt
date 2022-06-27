package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCreditApproval2Binding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.pop.ApprovalPop
import com.inclusive.finance.jh.pop.BaseListMenuPop
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
 * 征信审批
 * */
@AndroidEntryPoint
class CreditApproval2Fragment : MyBaseFragment(), OnRefreshListener, OnLoadMoreListener,
    OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentCreditApproval2Binding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    private var businessType: Int = ApplyModel.BUSINESS_TYPE_ZXSP
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentCreditApproval2Binding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    private var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btSeeOnly?.visibility = View.GONE
        viewBind?.btChange?.text="审批"
    }

    override fun initView() {
        businessType = arguments?.getInt("businessType", ApplyModel.BUSINESS_TYPE_ZXSP)
            ?: businessType
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "征信审批"
        mAdapter = ItemBaseListCardAdapter(this)
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        mAdapter.setOnItemChildClickListener(this) //
        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
//        // 当数据不满一页时，是否继续自动加载（默认为true）
//        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRefreshLayout.setOnRefreshListener(this)
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


    val listMenuDatas = mutableListOf<String>().apply {
        add("查看征信授权")
    }
    var processStatus = ""
    var getUrl = ""
    var destination = ""
    override fun initEvent() {
        when (businessType) {
            ApplyModel.BUSINESS_TYPE_ZXSP -> {
                getUrl = Urls.getZXSPList
                destination = "征信授权"
            }
            ApplyModel.BUSINESS_TYPE_DH_ZXSP -> {
                getUrl = Urls.getDHZXSPList
                destination = "征信授权-资料上传"
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_ZXSP -> {
                getUrl = Urls.getZXSP_sunshine_List
                destination = "征信授权"
            }
        }
        val dataList: MutableList<Filter>
        val elements5 = Filter.TagFilter(Tag("5", "processStatus", "流程状态", valueName = "全部", keyName = "", isSingleCheck = true))
        val elements6 = Filter.TagFilter(Tag("6", "processStatus", "流程状态", valueName = "待审批", keyName = "1000", isSingleCheck = true))
        val elements7 = Filter.TagFilter(Tag("7", "processStatus", "流程状态", valueName = "审批退回", keyName = "2000", isSingleCheck = true))
        val elements8 = Filter.TagFilter(Tag("8", "processStatus", "流程状态", valueName = "审批通过", keyName = "200", isSingleCheck = true))
        dataList = mutableListOf( //            elements_1,
            elements5, elements6, elements7, elements8
        )
        val viewModel = ViewModelProvider(this@CreditApproval2Fragment)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(dataList)
        }
    }
    private var resultCountCallBack: ((count: String) -> Unit)?=null
    override fun initData() {
        DataCtrlClass.SXSPNet.getZXSPList(requireActivity(), currentPage, url = getUrl, searchStr, processStatus) {
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

        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "查看征信授权" -> {
                            IRouter.goF(view = view, R.id.action_to_navActivity, destination, SZWUtils.getJsonObjectString(jsonObject, "creditId"), jsonObject, businessType, true)

                        }
                    }
                }.showPopupWindow(view)
            }
            R.id.bt_change->{
                ApprovalPop(context, SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = businessType) {
                    refreshData()
                }.show(childFragmentManager, this.javaClass.name)
            }

        }
    }

    private var searchStr = ""
    private var admitType = ""
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