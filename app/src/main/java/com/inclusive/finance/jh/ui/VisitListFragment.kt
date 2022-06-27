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
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentVisitListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * 走访列表
 * */
class VisitListFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentVisitListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentVisitListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@VisitListFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {

        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "走访列表"


        mAdapter = ItemBaseListAdapter(this)
        mAdapter.singleCheck = false //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        initStatusView() //        val mainData = SZWUtils.getJson(context, "listData.json")
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


        DataCtrlClass.VisitNet.getVisitRWMC(context) {
            if (it != null) {
                rwmcList = it
                if (it.size > 0) {
                    status_rwmc = it[0].keyName
                    viewBind.downRwmc.text = it[0].valueName
                }
                viewBind.mRefreshLayout.autoRefresh()
            }
        }

        DataCtrlClass.VisitNet.getVisitKHJL(context) {
            if (it != null) {
                viewBind.downKhjlLay.visibility = View.VISIBLE
                khjlList = it
                if (it.size > 0) {
                    status_khjl = it[0].keyName
                    viewBind.downKhjl.text = it[0].valueName
                }
                viewBind.mRefreshLayout.autoRefresh()
            } else {
                viewBind.downKhjlLay.visibility = View.GONE
            }
        }
    }

    val tjztList = ArrayList<BaseTypeBean.Enum12>()
    var rwmcList = ArrayList<BaseTypeBean.Enum12>()
    var khjlList = ArrayList<BaseTypeBean.Enum12>()
    private fun initStatusView() {
        tjztList.clear()
        tjztList.add(BaseTypeBean.Enum12().apply {
            valueName = "全部"
            keyName = ""
        })
        tjztList.add(BaseTypeBean.Enum12().apply {
            valueName = "待走访"
            keyName = "01"
        })
        tjztList.add(BaseTypeBean.Enum12().apply {
            valueName = "走访完成"
            keyName = "02"
        })
        val listener: (v: View) -> Unit = {
            DownPop(context, enums12 = when (it) {
                viewBind.downTjzt -> tjztList
                viewBind.downRwmc -> rwmcList
                viewBind.downKhjl -> khjlList
                else -> arrayListOf()
            }, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                when (it) {
                    viewBind.downTjzt -> status_tjzt = k
                    viewBind.downRwmc -> status_rwmc = k
                    viewBind.downKhjl -> status_khjl = k
                }
            }.showPopupWindow(it)
        }
        viewBind.downTjzt.text = "待走访"
        viewBind.downTjzt.setOnClickListener(listener)
        viewBind.downRwmc.setOnClickListener(listener)
        viewBind.downKhjl.setOnClickListener(listener)
    }

    private var status_tjzt = "01"
    private var status_rwmc = ""
    private var status_khjl = ""
    override fun initData() {

        DataCtrlClass.VisitNet.getVisitList(requireActivity(), currentPage, viewBind.etSearch.text.toString(), viewBind.etKhdz.text.toString(), status_tjzt, status_rwmc, status_khjl) {
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
        if (v == viewBind.chipNew) {
            IRouter.goF(v, R.id.action_to_applyActivity, "", ApplyModel.BUSINESS_TYPE_VISIT_NEW, false)
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanListFromList(mAdapter.data) { jsonObjects ->
            val tjzt = jsonObjects.firstOrNull { SZWUtils.getJsonObjectString(it, "tjzt") == "走访完成" }

            when (v) {
                viewBind.chipEdit -> {
                    if (SZWUtils.isSingleCheck(jsonObjects)) {
                        if (SZWUtils.getJsonObjectString(jsonObjects[0], "flag") == "0") {
                            DataCtrlClass.VisitNet.getVisitCheckJbxx(context, SZWUtils.getJsonObjectString(jsonObjects[0], "zfId")) {
                                if (it != null) {
                                    IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObjects[0], "zfId"), ApplyModel.BUSINESS_TYPE_VISIT_EDIT, false)
                                } else {
                                    IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObjects[0], "zfId"), ApplyModel.BUSINESS_TYPE_VISIT_NEW, false)
                                }
                            }
                        } else {
                            SZWUtils.showSnakeBarMsg("该条数据无法操作")
                        }

                    }
                }
                viewBind.chipSee-> {
                    if (SZWUtils.isSingleCheck(jsonObjects)) {
                        DataCtrlClass.VisitNet.getVisitCheckJbxx(context, SZWUtils.getJsonObjectString(jsonObjects[0], "zfId")) {
                            if (it != null) {
                                IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObjects[0], "zfId"), ApplyModel.BUSINESS_TYPE_VISIT_EDIT, true)
                            } else {
                                IRouter.goF(v, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObjects[0], "zfId"), ApplyModel.BUSINESS_TYPE_VISIT_NEW, true)
                            }
                        }
                    }
                }

                viewBind.chipKhfp -> {
                    when {
                        tjzt != null -> context?.let { SZWUtils.showSnakeBarMsg("该走访状态下无法操作") }
                        else -> KHFPPop(context, jsonObjects, ApplyModel.BUSINESS_TYPE_VISIT_NEW or ApplyModel.BUSINESS_TYPE_VISIT_EDIT, false).show(childFragmentManager, this.javaClass.name)
                    }
                }
                viewBind.chipGhqyj -> {
                    KHFPPop(context, jsonObjects, ApplyModel.BUSINESS_TYPE_VISIT_NEW or ApplyModel.BUSINESS_TYPE_VISIT_EDIT, true).show(childFragmentManager, this.javaClass.name)
                }
                viewBind.chipSubmit -> {
                    val flag = jsonObjects.firstOrNull { SZWUtils.getJsonObjectString(it, "flag") != "0" }
                    if (flag != null) {
                        SZWUtils.showSnakeBarMsg("存在无法操作数据")
                    } else {
                        ConfirmPop(context, "确认提交?") {
                            if (it) {
                                DataCtrlClass.VisitNet.visitSubmit(context, Urls.get_visit_submit, SZWUtils.getJsonObjectStringList(jsonObjects, "zfId")
                                    .joinToString(",")) {
                                    onRefresh(viewBind.mRefreshLayout)
                                    GoldenPop(context).show(childFragmentManager, this.javaClass.name)
                                }
                            }
                        }.show(childFragmentManager, this.javaClass.name)
                    }
                }
                viewBind.chipYsx -> {
                    if (SZWUtils.isSingleCheck(jsonObjects)) {
                        if (SZWUtils.getJsonObjectString(jsonObjects[0], "flag") == "0") {
                            ApplyCheckPop(context, this, businessType = ApplyModel.BUSINESS_TYPE_VISIT, SZWUtils.getJsonObjectStringList(jsonObjects, "zfId")[0]) {
                                refreshData()
                                IRouter.goF(v, R.id.action_to_applyActivity, it, ApplyModel.BUSINESS_TYPE_PRECREDIT, false)
                            }.show(childFragmentManager, this.javaClass.name)
                        } else {
                            SZWUtils.showSnakeBarMsg("该条数据无法操作")
                        }
                    }
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