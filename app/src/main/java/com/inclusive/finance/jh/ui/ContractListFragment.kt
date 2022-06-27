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
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentApplyListBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.ui.filter.Filter
import com.inclusive.finance.jh.ui.filter.FiltersFragment
import com.inclusive.finance.jh.ui.filter.SearchViewModel
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.act


/**
 * 合同签约列表
 * */
@AndroidEntryPoint
class ContractListFragment : MyBaseFragment(), OnRefreshLoadMoreListener, OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentApplyListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    private var getListUrl = ""
    private var xdtbUrl = ""
    private var businessType: Int = ApplyModel.BUSINESS_TYPE_APPLY
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]
        viewBind = FragmentApplyListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        businessType = arguments?.getInt("businessType", ApplyModel.BUSINESS_TYPE_APPLY)
            ?: businessType
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
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
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)


        //        val mainData = SZWUtils.getJson(context, "listData.json")
        //        val data = Gson().fromJson<BaseListBean>(mainData, BaseListBean::class.java)
        //        adapter.titleList = data.titleList
        //        adapter.setNewInstance(data.list)

        //                val mainData = SZWUtils.getJson(context, "待办事项.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //                mAdapter.titleList = list[0].listBean?.titleList
        //                mAdapter.setNewInstance(list[0].listBean?.list)
    }

    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btMore?.visibility = View.GONE
        viewBind?.btSeeOnly?.visibility = View.GONE
        viewBind?.btChange?.visibility = View.VISIBLE
        viewBind?.btChange?.text="合同"
    }
    val listMenuDatas = mutableListOf<String>().apply {

    }

    override fun initEvent() {


        getListUrl = Urls.htqy_sxlisApp
        xdtbUrl = Urls.khglxdtb
        viewBind.actionBarCustom.mTitle.text = "合同签约"


//        val dataList: MutableList<Filter> //        val elements_1 = Filter.SearchFilter( //            Tag( //                "9",
//        //                "客户姓名",
//        //                hint = "请输入客户姓名或身份证号",
//        //                valueName = "0",
//        //                keyName = "0"
//        //            )
//        //        )
//        val elements0 = Filter.TagFilter(Tag("0", "applyProcessStatus", "是否本人发起", "0", valueName = "本人发起", keyName = "本人发起", isSingleCheck = false))
//        val elements0_1 = Filter.TagFilter(Tag("10", "applyProcessStatus", "是否本人发起", "0", valueName = "协办复核", keyName = "协办复核", isSingleCheck = false))
//        val elements2 = Filter.TagFilter(Tag("2", "admitType", "是否签批", valueName = "是", keyName = "1", isSingleCheck = false))
//        val elements3 = Filter.TagFilter(Tag("3", "admitType", "是否签批", valueName = "否", keyName = "0", isSingleCheck = false))
//        val elements4 = Filter.TagFilter(Tag("4", "processStatus", "流程状态", valueName = "全部", keyName = "", isSingleCheck = true))
//        val elements5 = Filter.TagFilter(Tag("5", "processStatus", "流程状态", valueName = "流程中", keyName = "流程中", isSingleCheck = true))
//        val elements6 = Filter.TagFilter(Tag("6", "processStatus", "流程状态", valueName = "待处理", keyName = "待处理", isSingleCheck = true))
//        val elements7 = Filter.TagFilter(Tag("7", "processStatus", "流程状态", valueName = "已完成", keyName = "已完成", isSingleCheck = true))
//        val elements8 = Filter.TagFilter(Tag("8", "processStatus", "流程状态", valueName = "已终止", keyName = "已终止", isSingleCheck = true))
//        dataList = mutableListOf( //            elements_1,
//            elements0, elements0_1, elements2, elements3, elements4, elements5, elements6, elements7, elements8
//        )
        val viewModel = ViewModelProvider(this@ContractListFragment)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
//            viewModel.setSupportedFilters(dataList)
//            viewModel.toggleFilter(elements0, enabled = true, singleCheck = true)
//            viewModel.toggleFilter(elements6, enabled = true, singleCheck = true)
        }

    }

    private var searchStr = ""
    private var admitType = ""
    private var processStatus = "待处理"
    private var applyProcessStatus = "本人发起"
    var resultCountCallBack: ((count: String) -> Unit)? = null
    override fun initData() {

        DataCtrlClass.HTQYNet.getHTQYList(
            requireActivity(), url = getListUrl, currentPage, idenNo = searchStr
        ) {
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
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {

                }.showPopupWindow(view)
            }
            R.id.bt_seeOnly -> {
                IRouter.goF(
                    view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), businessType, true
                )
            }
            R.id.bt_change -> {
                when {
                    jsonObjectString.contains("1") ->
//                        IRouter.goF(
//                        view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), businessType, false
//                    )
                        IRouter.goF(view, R.id.action_to_navActivity, "合同签约", SZWUtils.getJsonObjectString(jsonObject, "id"), jsonObject, ApplyModel.BUSINESS_TYPE_HTQY, false)

                    else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                }

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
            admitType = ""
            processStatus = ""
            applyProcessStatus = ""
            list.forEach {
                val tagFilter = it.filter as Filter.TagFilter
                when (tagFilter.tag.categoryId) {
                    "admitType" -> {
                        admitType = tagFilter.tag.keyName
                    }
                    "processStatus" -> {
                        processStatus = tagFilter.tag.keyName
                    }
                    "applyProcessStatus" -> {
                        applyProcessStatus = tagFilter.tag.keyName
                    }
                }
            }
            refreshData()
        } //        }

        filtersFragment?.show(childFragmentManager, this.javaClass.name)
        return filtersFragment
    }
}