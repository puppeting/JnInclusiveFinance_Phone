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
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentCreditListBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.pop.BaseListMenuPop
import com.inclusive.finance.jh.ui.filter.Filter
import com.inclusive.finance.jh.ui.filter.Filter.TagFilter
import com.inclusive.finance.jh.ui.filter.FiltersFragment
import com.inclusive.finance.jh.ui.filter.SearchViewModel
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.act


/**
 * 征信查询记录
 * */
@AndroidEntryPoint
class CreditListFragment : MyBaseFragment(), OnLoadMoreListener, OnRefreshListener,
    OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentCreditListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentCreditListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
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
        viewBind.actionBarCustom.mTitle.text = "征信查询记录"
        mAdapter = ItemBaseListCardAdapter(this)
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        mAdapter.setOnItemChildClickListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRefreshLayout.setOnRefreshListener(this)

        val dataList: MutableList<Filter> = mutableListOf( //            elements_1,
        )
        val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(dataList)
        }
    }

    private var resultCountCallBack: ((count: String) -> Unit)? = null
    override fun initData() {
        DataCtrlClass.SXDCNet.getSXDCList(requireActivity(), currentPage, searchStr) {
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

    val listMenuDatas = mutableListOf<String>().apply {
        add("征信查询记录")
        add("查看征信解析")
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")

        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "征信查询记录" -> {
                            when {
                                jsonObjectString.contains("11") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                                else -> IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "creditId"), ApplyModel.BUSINESS_TYPE_INVESTIGATE, false)
                            }

                        }
                        "查看征信解析" -> {
                            IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "creditId"), ApplyModel.BUSINESS_TYPE_INVESTIGATE, true)

                        }

                        else -> {
                        }
                    }
                }.showPopupWindow(view)
            }

        }
    }


    var searchStr = ""
    var admitType = ""
    var processStatus = "待处理"
    private var filtersFragment: FiltersFragment? = null

    private fun findFiltersFragment(): FiltersFragment? { //        if (filtersFragment == null) {

        filtersFragment = FiltersFragment(this) { list, str, c ->
            resultCountCallBack = c
            searchStr = str
            admitType = ""
            processStatus = ""
            list.forEach {
                val tagFilter = it.filter as TagFilter
                when (tagFilter.tag.categoryId) {
                    "admitType" -> {
                        admitType = tagFilter.tag.keyName
                    }
                }
            }
            refreshData()
        } //        }

        filtersFragment?.show(childFragmentManager, this.javaClass.name)
        return filtersFragment
    }


}