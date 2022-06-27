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
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentPicAddListBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.pop.BaseListMenuPop
import com.inclusive.finance.jh.pop.ConfirmPop
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
 * 影像资料补录列表
 * */
@AndroidEntryPoint
class PicAddListFragment : MyBaseFragment(), OnRefreshLoadMoreListener, OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentPicAddListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]
        viewBind = FragmentPicAddListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btChange?.visibility = View.GONE
        viewBind?.btSeeOnly?.visibility = View.GONE
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "影像资料补录"


        mAdapter = ItemBaseListCardAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mAdapter.setOnItemChildClickListener(this)
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
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
        val dataList: MutableList<Filter>

        val elements1 = TagFilter(Tag("1", "processStatus", "流程状态", valueName = "全部", keyName = "", isSingleCheck = true))
        val elements2 = TagFilter(Tag("2", "processStatus", "流程状态", valueName = "待补录", keyName = "0", isSingleCheck = true))
        val elements3 = TagFilter(Tag("3", "processStatus", "流程状态", valueName = "已补录", keyName = "1", isSingleCheck = true))
        dataList = mutableListOf( //            elements_1,
            elements1, elements2, elements3
        )
        val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(dataList)
            viewModel.toggleFilter(elements2, enabled = true, singleCheck = true)
        }
    }


    private var processStatus = "0"
    private var resultCountCallBack: ((count: String) -> Unit)? = null
    override fun initData() {

        DataCtrlClass.SXSQNet.getPicAddList(requireActivity(), currentPage, searchStr, processStatus) {
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

    val listMenuDatas = mutableListOf<String>().apply {
        add("补录")
        add("结束补录")
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val flag = SZWUtils.getJsonObjectString(jsonObject, "flag")
        if (flag != "1") {
            SZWUtils.showSnakeBarError("您不是补录人，无权操作")
            return
        }
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {

                        "补录" -> {
                            IRouter.goF(view, R.id.action_to_navActivity, "影像资料", SZWUtils.getJsonObjectString(jsonObject, "creditId"), jsonObject, ApplyModel.BUSINESS_TYPE_PICADD, false)
//
                        }
                        "结束补录" -> {
                            ConfirmPop(context, "确认结束补录?") {confirm->
                                if (confirm) {
                                    DataCtrlClass.SXSQNet.picAddEnd(context, SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                        onRefresh(viewBind.mRefreshLayout)
                                    }
                                }
                            }.show(childFragmentManager, this.javaClass.name)
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

    var searchStr = ""
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