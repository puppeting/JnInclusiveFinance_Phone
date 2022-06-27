package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentSunprecredittaskListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 阳光预授信任务列表
 * */
class SunPreCreditTaskFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentSunprecredittaskListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentSunprecredittaskListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@SunPreCreditTaskFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "阳光预授信任务"


        mAdapter = ItemBaseListAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        initStatusView()
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
        DataCtrlClass.RCJNet.getJGMCList(requireActivity()) {
            if (it != null) {
                jgList = arrayListOf()
                it.forEach { jsonObject ->
                    jgList.add(BaseTypeBean.Enum12().apply {
                        keyName = SZWUtils.getJsonObjectString(jsonObject, "orgCode")
                        valueName = SZWUtils.getJsonObjectString(jsonObject, "departName")

                    })
                }
                if (jgList.size > 0) {
                    status_jg = jgList[0].keyName
                    viewBind.downJg.text = jgList[0].valueName
                    getRWMCList()
                }

            }
        }
    }

    private fun getRWMCList() {
        status_rwmc=""
        rwmcList.clear()
        viewBind.downRwmc.text = ""
        DataCtrlClass.YXNet.getYGYSXRWMC(requireActivity(), status_jg) { it1 ->
            if (it1 != null) {
                rwmcList = it1
                if (it1.size > 0) {
                    status_rwmc = it1[0].keyName
                    viewBind.downRwmc.text = it1[0].valueName
                }
                viewBind.mRefreshLayout.autoRefresh()
            }
        }
    }

    var jgList = ArrayList<BaseTypeBean.Enum12>()
    var rwmcList = ArrayList<BaseTypeBean.Enum12>()
    val statusList = ArrayList<BaseTypeBean.Enum12>()
    private fun initStatusView() {
        jgList.clear()
        rwmcList.clear()
        statusList.clear()
        //        是否结束，0:否  |  1:是 |  全部：空，默认全部
        statusList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        statusList.add(BaseTypeBean.Enum12().apply {
            valueName = "否"
            keyName = "0"
        })
        statusList.add(BaseTypeBean.Enum12().apply {
            valueName = "是"
            keyName = "1"
        })


        val listener: (v: View) -> Unit = {
            DownPop(context, enums12 = when (it) {
                viewBind.downJg -> jgList
                viewBind.downZt -> statusList
                viewBind.downRwmc -> rwmcList
                else -> arrayListOf()
            }, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                when (it) {
                    viewBind.downJg -> {
                        status_jg = k
                        getRWMCList()
                    }
                    viewBind.downRwmc -> status_rwmc = k
                    viewBind.downZt -> status = k
                }
            }.showPopupWindow(it)
        }
        viewBind.downJg.text=""
        viewBind.downRwmc.text=""
        viewBind.downZt.text="全部"
        viewBind.downJg.setOnClickListener(listener)
        viewBind.downRwmc.setOnClickListener(listener)
        viewBind.downZt.setOnClickListener(listener)
    }

    private var status_jg = ""
    private var status_rwmc = ""
    private var status = ""
    override fun initData() {

        DataCtrlClass.YXNet.getYGYSXRWList(requireActivity(), currentPage, custName=viewBind.etSearch.text.toString(), orgCode =status_jg,rwid = status_rwmc ,status=status) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.initTitleLay(context, viewBind.layoutBaseList.root, it) {
                        mAdapter.setNewInstance(it.list)
                    }
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

        }
    }

    override fun refreshData(type: Int?) {

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

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 500L
    override fun onClick(v: View?) {

        //        PictureSelector.create(this@ApplyListFragment).externalPictureVideo("http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4");

        if (v == viewBind.btSearch) {
            if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                viewBind.mRefreshLayout.autoRefresh()
                mLastClickTime = System.currentTimeMillis()
            }
            return
        }
        if (v ==  viewBind.chipNew) {
            BaseTypePop(context, this, "新增", getUrl = Urls.get_ygysxrw_pop, saveUrl = Urls.save_ygysxrw_pop) {adapter,resultStr->
                getRWMCList()
            }.show(childFragmentManager, this.javaClass.name)
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
//            val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")
            when (v) {
                viewBind.chipRemake -> {
                    ConfirmPop(context, "确定重置?") {
                        if (it) DataCtrlClass.YXNet.remarkById(requireActivity(), Urls.remake_ygysxrw, SZWUtils.getJsonObjectString(jsonObject, "id")) {str->
                            viewBind.mRefreshLayout.autoRefresh()
                        }
                    }.show(childFragmentManager, this.javaClass.name)
                }
                viewBind.chipDelete -> {
                    ConfirmPop(context, "确定删除?") {
                        if (it) DataCtrlClass.YXNet.deleteYGYSX(requireActivity(), Urls.delete_ygysxrw, SZWUtils.getJsonObjectString(jsonObject, "id")) {str->
                            viewBind.mRefreshLayout.autoRefresh()
                        }
                    }.show(childFragmentManager, this.javaClass.name)
                }
            }
        }

    }

    /**
    返回后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_Refresh_List)])
    fun backRefresh(str: String) {
        viewBind.mRefreshLayout.autoRefresh()
    }


}