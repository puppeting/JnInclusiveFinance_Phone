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
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentCreditApproval3Binding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
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
 * 征信复核签字
 * */
@AndroidEntryPoint
class CreditApproval3Fragment : MyBaseFragment(), OnRefreshListener, OnLoadMoreListener,
    OnItemChildClickListener {
    private  var orgCode: String = ""
    private var jgList = ArrayList<BaseTypeBean.Enum12>()
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentCreditApproval3Binding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentCreditApproval3Binding.inflate(inflater, container, false).apply {
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
        viewBind.actionBarCustom.mTitle.text = "征信复核签字"
        mAdapter = ItemBaseListCardAdapter(this)
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        mAdapter.setOnItemChildClickListener(this) //
        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
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

        val dataList: MutableList<Filter> = mutableListOf(

        )
        val viewModel = ViewModelProvider(this@CreditApproval3Fragment)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(filters = dataList)
        }
        DataCtrlClass.RCJNet.getJGMCList(requireActivity()) {
            if (it != null) {
                var index = 0
                jgList = arrayListOf()
                it.forEach { jsonObject ->
                    jgList.add(BaseTypeBean.Enum12().apply {
                        keyName = SZWUtils.getJsonObjectString(jsonObject, "orgCode")
                        valueName = SZWUtils.getJsonObjectString(jsonObject, "departName")
                        val elements0 = Filter.TagFilter(Tag("1$index", "orgCode", "机构", valueName = "" + valueName, keyName = "0$keyName", isSingleCheck = true))
                        dataList.add(elements0)
                        index++
                    })
                }

                if (jgList.size > 0) {
                    orgCode = jgList[0].keyName
                }
                val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
                lifecycleScope.launch {
                    delay(500)
                    dataList.let { it1 ->
                        viewModel.setSupportedFilters(it1)
                        viewModel.toggleFilter(dataList[0], enabled = true, singleCheck = true)

                    }
                }
                viewBind.mRefreshLayout.autoRefresh()
            }
        }
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("查看征信授权")
    }
    private var resultCountCallBack: ((count: String) -> Unit)? = null
    override fun initData() {
        DataCtrlClass.SXSPNet.getZXFHQZList(requireActivity(), currentPage, orgCode) {
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
                            IRouter.goF(view, R.id.action_to_navActivity, "征信授权书", SZWUtils.getJsonObjectString(jsonObject, "creditId"), jsonObject, ApplyModel.BUSINESS_TYPE_ZXFHQZ, false)

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
    private var filtersFragment: FiltersFragment? = null

    private fun findFiltersFragment(): FiltersFragment? { //        if (filtersFragment == null) {

        filtersFragment = FiltersFragment(this) { list, str, c ->
            resultCountCallBack = c
            searchStr = str
            admitType = ""
            orgCode = ""
//            list.forEach {
//                val tagFilter = it.filter as Filter.TagFilter
//                when (tagFilter.tag.categoryId) {
//
//                    "orgCode" -> {
//                        orgCode = tagFilter.tag.keyName
//                    }
//
//                }
//            }
            refreshData()
        } //        }

        filtersFragment?.show(childFragmentManager, this.javaClass.name)
        return filtersFragment
    }

}